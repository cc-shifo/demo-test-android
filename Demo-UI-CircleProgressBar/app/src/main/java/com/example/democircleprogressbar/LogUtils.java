/*
 * = COPYRIGHT
 *     TianYu
 *
 * Description:
 *
 * Date                    Author                    Action
 * 2020-06-30              LiuJian                    update
 */

package com.example.democircleprogressbar;

import android.util.Log;

public final class LogUtils {
    private static boolean enableDebug = false;

    private LogUtils() {
        //nothing
    }

    private static String getTag() {
        StackTraceElement[] traceElements = new Throwable().getStackTrace();
        if (traceElements == null || traceElements.length == 0) {
            return "LogUtils";
        }

        return traceElements[2].getClassName() + "." + traceElements[2].getMethodName()
                + "(line:" + traceElements[2].getLineNumber() + ")";
    }
    public static void v(String msg) {
        if (BuildConfig.DEBUG || enableDebug) {
            Log.v(getTag(), msg);
        }
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG || enableDebug) {
            Log.d(getTag(), msg);
        }
    }

    public static void i(String msg){
        if (BuildConfig.DEBUG || enableDebug) {
            Log.i(getTag(), msg);
        }
    }

    public static void w(String msg){
        if (BuildConfig.DEBUG || enableDebug) {
            Log.w(getTag(), msg);
        }
    }

    public static void e(String msg){
        if (BuildConfig.DEBUG || enableDebug) {
            Log.e(getTag(), msg);
        }
    }

    public static void e(Exception e){
        if (BuildConfig.DEBUG || enableDebug) {
            Log.e(getTag(), e.toString());
        }
    }

    public static void e(String msg, Throwable e) {
        if (BuildConfig.DEBUG || enableDebug) {
            Log.e(getTag(), msg, e);
        }
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG || enableDebug) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG || enableDebug) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg){
        if (BuildConfig.DEBUG || enableDebug) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg){
        if (BuildConfig.DEBUG || enableDebug) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg){
        if (BuildConfig.DEBUG || enableDebug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (BuildConfig.DEBUG || enableDebug) {
            Log.e(tag, msg, e);
        }
    }

    @SuppressWarnings("unused")
    public static void enableDebug(boolean enable) {
        enableDebug = enable;
    }

}
