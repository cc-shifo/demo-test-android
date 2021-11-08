/*
 * = COPYRIGHT
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20211104 	         LiuJian                  Create
 */

package com.example.demoarcgis;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.GzipSink;
import okio.GzipSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public class TrackRecUtil {
    private static final String TAG = "TrackRecUtil";
    private static final int CACHE_SIZE = 4096;

    private static TrackRecUtil mInstance;
    private String mPath;
    private BufferedSink mBufferedSink;
    private BufferedSource mBufferedSource;
    private final byte[] mPointCache = new byte[CACHE_SIZE];
    private int mOffset;
    private boolean mIsCached;

    private TrackRecUtil() {
        // nothing
    }

    public static synchronized TrackRecUtil getInstance() {
        if (mInstance == null) {
            mInstance = new TrackRecUtil();
        }

        return mInstance;
    }

    /**
     * yyyy-MM-dd-HH-mm-ss-SSS-Country
     *
     * @param context application context
     * @return the path of track file.
     */
    private String generatePath(@NonNull Context context, String path) {
        if (path == null || path.isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS",
                    Locale.getDefault());
            Date date = new Date(System.currentTimeMillis());
            String dir = context.getExternalFilesDir(null).getAbsolutePath() + File.separator
                    + "track";
            return dir + File.separator + dateFormat.format(date) + "-" + Locale.getDefault()
                    .getCountry();
        }

        int index = path.lastIndexOf('/');
        if (index == -1 || index == path.length() - 1) {
            Log.e(TAG, "generatePath: invalid path: " + path);
            return null;
        }

        String dir = path.substring(0, index);
        File file = new File(dir);
        if (!file.exists() && !file.mkdirs()) {
            Log.e(TAG, "generatePath: make" + dir + " failed.");
            return null;
        }

        return path;
    }

    public String getPath() {
        return mPath;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean create(@NonNull Context context, String path,
                          boolean read, boolean append) {
        return open(generatePath(context, path), read, append);
    }

    /**
     * Create an {@link BufferedSink}, remember to call {@link #close()}} to
     * close this buffer when it is useless.
     *
     * @param path   file absolute path of track record
     * @param rw     read if true, otherwise write.
     * @param append append mode when opening for writing if true.
     * @return return true if create BufferedSink successfully, otherwise return false.
     */
    public boolean open(String path, boolean rw, boolean append) {
        mPath = path;
        mOffset = 0;
        if (rw) {
            Source source;
            try {
                source = Okio.source(new File(path));
            } catch (Exception e) {
                Log.e(TAG, "openTrackRecord: path isn't exists");
                return false;
            }
            mBufferedSource = Okio.buffer(new GzipSource(source));

            return true;
        }

        Sink sink;
        try {
            sink = Okio.sink(new File(path), append);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }
        mBufferedSink = Okio.buffer(new GzipSink(sink));
        return true;
    }

    /**
     * Write line into buffer. one line format must be: 8 bytes latitude + 8 bytes longitude
     *
     * @param latitude  latitude
     * @param longitude longitude
     * @param height    reserved.
     * @return return true if write successfully., otherwise return false.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean write(double latitude, double longitude, double height) {
        assert (mOffset & 0x0f) == 0;
        if (mOffset < mPointCache.length) {
            long v = Double.doubleToLongBits(latitude);
            // BigDecimal bigDecimal = BigDecimal.valueOf(latitude);
            // bigDecimal.setScale(10, BigDecimal.ROUND_HALF_DOWN);
            // bigDecimal.toString();
            mPointCache[mOffset++] = (byte) ((v & 0xff00000000000000L) >>> 56);
            mPointCache[mOffset++] = (byte) ((v & 0x00ff000000000000L) >>> 48);
            mPointCache[mOffset++] = (byte) ((v & 0x0000ff0000000000L) >>> 40);
            mPointCache[mOffset++] = (byte) ((v & 0x000000ff00000000L) >>> 32);
            mPointCache[mOffset++] = (byte) ((v & 0x00000000ff000000L) >>> 24);
            mPointCache[mOffset++] = (byte) ((v & 0x0000000000ff0000L) >>> 16);
            mPointCache[mOffset++] = (byte) ((v & 0x000000000000ff00L) >>> 8);
            mPointCache[mOffset++] = (byte) (v & 0x00000000000000ffL);
            v = Double.doubleToLongBits(longitude);
            mPointCache[mOffset++] = (byte) ((v & 0xff00000000000000L) >>> 56);
            mPointCache[mOffset++] = (byte) ((v & 0x00ff000000000000L) >>> 48);
            mPointCache[mOffset++] = (byte) ((v & 0x0000ff0000000000L) >>> 40);
            mPointCache[mOffset++] = (byte) ((v & 0x000000ff00000000L) >>> 32);
            mPointCache[mOffset++] = (byte) ((v & 0x00000000ff000000L) >>> 24);
            mPointCache[mOffset++] = (byte) ((v & 0x0000000000ff0000L) >>> 16);
            mPointCache[mOffset++] = (byte) ((v & 0x000000000000ff00L) >>> 8);
            mPointCache[mOffset++] = (byte) (v & 0x00000000000000ffL);
        }

        // 复杂度8192次比较
        if (mOffset == mPointCache.length) {
            try {
                mBufferedSink.write(mPointCache, 0, mPointCache.length);
                mBufferedSink.flush();
            } catch (IOException e) {
                Log.e(TAG, "writeToBuf: ", e);
                return false;
            } finally {
                mOffset = 0;
            }

            mIsCached = false;
            return true;
        }

        mIsCached = true;
        return true;
    }

    public boolean isCached() {
        return mIsCached;
    }

    public boolean isExhausted() {
        try {
            return mBufferedSource.exhausted();
        } catch (IOException e) {
            Log.e(TAG, "isExhausted: ", e);
        }
        return true;
    }

    /**
     * Read points of track from historical record.
     *
     * @return an NonNull object. 2-dimension array. Latitude and longitude is stored in the 1st
     * demision in sequence.
     */
    @NonNull
    public double[][] read() {
        double[][] cache = new double[0][2];

        mOffset = 0;
        try {
            while (!mBufferedSource.exhausted()) {
                int n = mBufferedSource.read(mPointCache, mOffset, mPointCache.length - mOffset);
                mOffset += n;
                if ((mOffset & 0x0f) == 0) {
                    cache = new double[mOffset / 16][2];
                    // 读取数据不全，读取的不一定是8的倍数。
                    // 如果调用了，当遇到读取数据长度不是8的倍数时就会出现数据丢失情况。
                    // rewind:position ->0 mark discard
                    // 读取数据不全，读取的不一定是8的倍数。
                    for (int pti = 0; pti < mOffset; pti += 16) {
                        // 读取不全，缓存里不一定有第二个数
                        long l = ((mPointCache[pti] & 0xffL) << 56)
                                | ((mPointCache[pti + 1] & 0xffL) << 48)
                                | ((mPointCache[pti + 2] & 0xffL) << 40)
                                | ((mPointCache[pti + 3] & 0xffL) << 32)
                                | ((mPointCache[pti + 4] & 0xffL) << 24)
                                | ((mPointCache[pti + 5] & 0xffL) << 16)
                                | ((mPointCache[pti + 6] & 0xffL) << 8)
                                | (mPointCache[pti + 7] & 0xffL);
                        cache[pti>>>4][0] = Double.longBitsToDouble(l);
                        l = ((mPointCache[pti + 8] & 0xffL) << 56)
                                | ((mPointCache[pti + 9] & 0xffL) << 48)
                                | ((mPointCache[pti + 10] & 0xffL) << 40)
                                | ((mPointCache[pti + 11] & 0xffL) << 32)
                                | ((mPointCache[pti + 12] & 0xffL) << 24)
                                | ((mPointCache[pti + 13] & 0xffL) << 16)
                                | ((mPointCache[pti + 14] & 0xffL) << 8)
                                | (mPointCache[pti + 15] & 0xffL);
                        cache[pti>>>4][1] = Double.longBitsToDouble(l);
                    }
                    // mOffset = 0;
                    break;
                }
                Log.d(TAG, "readTrackRecord mOffset=" + mOffset + ", n=" + n);
            }
        } catch (IOException e) {
            mOffset = 0;
            cache = new double[0][2];
            Log.e(TAG, "read: ", e);
        }

        if ((mOffset & 0x0f) != 0) {
            Log.e(TAG, "!!!Error readTrackRecord mOffset=" + mOffset);
            cache = new double[0][2];
        }

        return cache;
    }


    /**
     * flush data in buffer to underlying sink.
     */
    public void flush() {
        try {
            mBufferedSink.flush();
        } catch (IOException e) {
            Log.e(TAG, "flushTrackBuffer: ", e);
        }
    }

    /**
     * close the internal buffer {@link BufferedSink} or {@link BufferedSource}
     */
    public void close() {
        if (mBufferedSink == null || !mBufferedSink.isOpen()) {
            return;
        }

        try {
            mBufferedSink.close();
        } catch (IOException e) {
            Log.e(TAG, "close: ", e);
        }
        mBufferedSink = null;
        mPath = null;

        if (mBufferedSource == null || !mBufferedSource.isOpen()) {
            return;
        }

        try {
            mBufferedSource.close();
        } catch (IOException e) {
            Log.e(TAG, "close: ", e);
        }

        mOffset = 0;
    }
}