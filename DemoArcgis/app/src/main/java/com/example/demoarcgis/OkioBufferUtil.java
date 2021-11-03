package com.example.demoarcgis;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

public class OkioBufferUtil {
    private static final String TAG = "OkioBufferUtil";
    private static OkioBufferUtil mInstance;
    private String mPath;
    /**
     * the buffer created by
     * {@link OkioBufferUtil##openBuffedTrackRecord(String)}.
     */
    private BufferedSink mBufferedSink;

    private OkioBufferUtil() {
        mBufferedSink = null;
    }

    public static synchronized OkioBufferUtil getInstance() {
        if (mInstance == null) {
            mInstance = new OkioBufferUtil();
        }

        return mInstance;
    }

    /**
     * yyyy-MM-dd-HH-mm-ss-SSS-Country
     * @param context application context
     * @return the path of track file.
     */
    private String generatePath(@NonNull Context context) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS",
                Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        String dir = context.getExternalFilesDir(null).getAbsolutePath() + File.separator
                + "track";

        File file = new File(dir);
        if (!file.exists() && !file.mkdirs()) {
            Log.e(TAG, "generatePath: make" + dir + " failed.");
            return null;
        }

        return dir + File.separator + "TEST";

        // TODO TEST
        // return dir + File.separator + dateFormat.format(date) + "-" + Locale.getDefault()
        //         .getCountry();
    }

    public String getTrackRecordPath() {
        return mPath;
    }

    public boolean createBuffedTrackRecord(@NonNull Context context) {
        String path = OkioBufferUtil.getInstance().generatePath(context);
        return path != null && openBuffedTrackRecord(path);
    }

    public boolean createBuffedTrackRecord(@NonNull Context context, String path) {
        return openBuffedTrackRecord(path);
    }


    /**
     * Create an {@link BufferedSink}, remember to call {@link #closeTrackBuffer()}
     * close this buffer when it is useless.
     *
     * @param path file absolute path of track record
     * @return return true if create BufferedSink successfully, otherwise return false.
     */
    public boolean openBuffedTrackRecord(@NonNull String path) {
        // TODO TEST
        mPath = path;
        Sink sink;
        try {
            sink = Okio.sink(new File(path), true);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }
        GzipSink gzipSink = new GzipSink(sink);
        mBufferedSink = Okio.buffer(gzipSink);

        return true;
    }

    /**
     * Write line into buffer. one line format must be: latitude + \n + longitude + \n
     * + height + \n
     *
     * @return return true if write successfully., otherwise return false.
     */
    public boolean writeTrack2Buffer(@NonNull String line) {
        try {
            mBufferedSink.writeString(line, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Log.e(TAG, "writeTrackRecord: " + line, e);
            return false;
        }

        return true;
    }

    /**
     * Write line into buffer. one line format must be: 8 bytes latitude + 8 bytes longitude
     *
     * @return return true if write successfully., otherwise return false.
     */
    public boolean write2Buffer(@NonNull ByteBuffer line) {
        try {
            mBufferedSink.write(line);
        } catch (IOException e) {
            Log.e(TAG, "writeTrackRecord: " + line, e);
            return false;
        }

        return true;
    }

    // /**
    //  * Read points of track from historical record.
    //  *
    //  * @param path file absolute path of track record
    //  * @return an NonNull PointCollection object
    //  */
    // public PointCollection readTrackRecord(String path) {
    //     PointCollection collection = new PointCollection((Iterable<Point>) null,
    //             SpatialReferences.getWebMercator());
    //     File file = new File(path);
    //     if (!file.exists()) {
    //         return collection;
    //     }
    //
    //     long index = 0;
    //     try (Source source = Okio.source(file);
    //          BufferedSource bufferedSource = Okio.buffer(new GzipSource(source))) {
    //         while (!bufferedSource.exhausted()) {
    //             String line = bufferedSource.readUtf8Line();
    //             double latitude = Double.parseDouble(line);
    //             double longitude = Double.parseDouble(line);
    //             double height = Double.parseDouble(line);
    //             collection.add(longitude, latitude, height);
    //             index++;
    //         }
    //     } catch (Exception e) {
    //         Log.e(TAG, "readTrackRecord[" + index + "]: ", e);
    //     }
    //
    //     return collection;
    // }

    /**
     * Read points of track from historical record.
     *
     * @param path file absolute path of track record
     * @return an NonNull PointCollection object
     */
    public PointCollection readRecord() {
        PointCollection collection = new PointCollection((Iterable<Point>) null,
                SpatialReferences.getWebMercator());
        File file = new File(mPath);
        if (!file.exists()) {
            return collection;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

        long index = 0;
        try (Source source = Okio.source(file);
             BufferedSource bufferedSource = Okio.buffer(new GzipSource(source))) {
            int n = 0;
            while ((n = bufferedSource.read(byteBuffer)) != -1) {
                if (n == 0) {
                    break;
                }

                int limit = n - (n % 16);
                while (byteBuffer.position() <= limit) {
                    collection.add(byteBuffer.getDouble(), byteBuffer.getDouble(), 0);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "readTrackRecord[" + index + "]: ", e);
        }

        return collection;
    }

    /**
     * flush data in buffer to underlying sink.
     */
    public void flushTrackBuffer() {
        try {
            mBufferedSink.flush();
        } catch (IOException e) {
            Log.e(TAG, "flushTrackBuffer: ", e);
        }
    }

    /**
     * close this buffer {@link BufferedSink}
     */
    public void closeTrackBuffer() {
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
    }

    /**
     * This API will open file, then close this file after finishing writing.
     *
     * @param lines consist of many lines, one line format must be:
     *              latitude + \n + longitude + \n + height + \n
     * @return true if write successfully.
     */
    public boolean writeTrackRecord(@NonNull String path, @NonNull String lines) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }

        try (Sink sink = Okio.sink(file, true);
             BufferedSink bufferedSink = Okio.buffer(new GzipSink(sink))) {
            bufferedSink.writeString(lines, StandardCharsets.UTF_8);
            bufferedSink.flush();
        } catch (Exception e) {
            Log.e(TAG, "writeTrackRecord: " + lines, e);
            return false;
        }

        return true;
    }
}