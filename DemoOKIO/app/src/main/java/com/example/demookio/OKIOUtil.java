/*
 * = COPYRIGHT
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20220113 	         LiuJian                  Create
 */

package com.example.demookio;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public class OKIOUtil {
    private static final String TAG = "OKIOUtil";
    private static final int ERROR = -1;
    private static final int EXCEPTION = -2;
    private static final int ERROR_DATA = -3;

    private static OKIOUtil mInstance;
    private BufferedSink mBufferedSink;
    private BufferedSource mBufferedSource;

    private OKIOUtil() {
        // nothing
    }

    public static synchronized OKIOUtil getInstance() {
        if (mInstance == null) {
            mInstance = new OKIOUtil();
        }

        return mInstance;
    }

    /**
     * Create an {@link BufferedSink}, remember to call {@link #close()}} to
     * close this buffer when it is useless.
     *
     * @param path   file absolute path of file
     * @param r      read if true, otherwise write.
     * @param append append mode when opening for writing if true.
     * @return return true if create BufferedSink successfully, otherwise return false.
     */
    public synchronized boolean open(@NonNull Context context, @Nullable String path, boolean r,
                                     boolean append) {
        path = generatePath(context, path);
        if (path == null) {
            Log.e(TAG, "open: path created failed");
            return false;
        }

        if (r) {
            Source source;
            try {
                File file = new File(path);
                source = Okio.source(file);
            } catch (Exception e) {
                Log.e(TAG, "open: path isn't exists");
                return false;
            }
            mBufferedSource = Okio.buffer(source);
            return true;
        }

        Sink sink;
        try {
            sink = Okio.sink(new File(path), append);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "open: path isn't exists");
            return false;
        }
        mBufferedSink = Okio.buffer(sink);
        return true;
    }

    /**
     * Write src into file.
     *
     * @return return true if write successfully., otherwise return false.
     */
    @SuppressWarnings("UnusedReturnValue")
    public synchronized boolean write(@NonNull byte[] src, int off, int size) {
        try {
            mBufferedSink.write(src, off, size);
            mBufferedSink.flush();
        } catch (IOException e) {
            Log.e(TAG, "writeToBuf: ", e);
            return false;
        }
        return true;
    }

    /**
     * Read data of file.
     * <p>
     * Double.longBitsToDouble(l)
     */
    public synchronized int read(@NonNull byte[] dst) {
        final int unit = 27;
        if (dst.length < unit) {
            throw new NullPointerException("dst is empty");
        }

        int length = dst.length - dst.length % unit;
        int off = 0;
        try {
            while (!mBufferedSource.exhausted()) {
                int n = mBufferedSource.read(dst, off, length - off);
                if (n > 0) {
                    off += n;
                    if ((off % unit) == 0 && off == length) {
                        break;
                    }
                    Log.d(TAG, "read off=" + off + ", n=" + n);
                }
            }
        } catch (IOException e) {
            length = EXCEPTION;
            Log.e(TAG, "read: ", e);
        }

        if (off == 0) {
            length = ERROR;
        } else if (off != length) {
            try {
                if (!mBufferedSource.exhausted() || (off % unit) != 0) {
                    length = ERROR_DATA;
                }
            } catch (IOException e) {
                Log.e(TAG, "read: ", e);
                length = EXCEPTION;
            }
            Log.e(TAG, "!!!Error read off=" + off);
        } else {
            length = 0;
        }

        return length;
    }


    /**
     * close the internal buffer {@link BufferedSink} or {@link BufferedSource}
     */
    public synchronized void close() {
        if (mBufferedSink == null || !mBufferedSink.isOpen()) {
            return;
        }

        try {
            mBufferedSink.close();
        } catch (IOException e) {
            Log.e(TAG, "close: ", e);
        }
        mBufferedSink = null;

        if (mBufferedSource == null || !mBufferedSource.isOpen()) {
            return;
        }

        try {
            mBufferedSource.close();
        } catch (IOException e) {
            Log.e(TAG, "close: ", e);
        }

    }

    /**
     * yyyy-MM-dd-HH-mm-ss-SSS-Country
     *
     * @param context application context
     * @return the path of file.
     */
    private String generatePath(@NonNull Context context, @Nullable String path) {
        if (path == null || path.isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS",
                    Locale.getDefault());
            Date date = new Date(System.currentTimeMillis());
            String dir = context.getExternalFilesDir(null).getAbsolutePath() + File.separator +
                    "demodata";
            path = dir + File.separator + dateFormat.format(date) + "-" + Locale.getDefault()
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

}