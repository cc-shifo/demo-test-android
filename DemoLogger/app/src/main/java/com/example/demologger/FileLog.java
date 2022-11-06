/*
 * = COPYRIGHT
 *          xxxx
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                    Author                  Action
 * 20221021                LiuJian                 Create
 */

package com.example.demologger;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

/**
 * Not a real crash reporting library!
 */
@SuppressLint("LogNotTimber")
final class FileLog {
    private static final int MAX_LOG_LENGTH = 4000;

    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");
    }

    private FileLog() {
        throw new AssertionError("No instances.");
    }

    /**
     * https://www.bookstack.cn/read/mars/ad80a40ea969eaa8.md
     *
     * @param logDir    日志存储路径
     *                  //@param filePrefix 日志文件名前缀
     * @param cacheDays 日志保存天数
     */
    public static void open(@NonNull String logDir, int cacheDays) {
        /*init xlog*/
        Xlog xlog = new Xlog();
        Log.setLogImp(xlog);

        Log.setConsoleLogOpen(false);
        // if (BuildConfig.DEBUG) {
        Log.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, "", logDir, "sample-log",
                cacheDays);
        // } else {
        //     Log.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, "", logDir, "", cacheDays);
        // }
    }

    public static void close() {
        Log.appenderFlush();
        Log.appenderClose();
    }


    public static void log(int priority, String tag, @NonNull String message, Throwable t) {
        if (message.length() < MAX_LOG_LENGTH) {
            pickLog(priority, tag, message, t);
            return;
        }

        // Split by line, then ensure each line can fit into Log's maximum length.
        for (int i = 0, length = message.length(); i < length; i++) {
            int newline = message.indexOf('\n', i);
            newline = newline != -1 ? newline : length;
            do {
                int end = Math.min(newline, i + MAX_LOG_LENGTH);
                String part = message.substring(i, end);
                pickLog(priority, tag, part, t);
                i = end;
            } while (i < newline);
        }
    }

    private static void pickLog(int priority, String tag, @NonNull String message, Throwable t) {
        switch (priority) {
            case Log.LEVEL_VERBOSE:
                Log.v(tag, message);
                break;
            case Log.LEVEL_DEBUG:
                Log.d(tag, message);
                break;
            case Log.LEVEL_INFO:
                Log.i(tag, message);
                break;
            case Log.LEVEL_WARNING:
                Log.w(tag, message);
                break;
            case Log.LEVEL_FATAL:
                Log.f(tag, message);
                break;
            case Log.LEVEL_ERROR:
                Log.e(tag, message, t);
                break;
            default:
                break;
        }
    }

}
