/*
 * ============================================================================
 * PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 * This software is supplied under the terms of a license agreement or nondisclosure
 * agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 * disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2018-?  PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description:
 * Revision History:
 * Date	             Author	                Action
 * 20180903   	     ligq           	Create/Add/Modify/Delete
 * ============================================================================
 */

package com.pax.helloworld;

import android.util.Log;

/**
 * @author ligq
 */
public final class LogUtil {
    private LogUtil() {
        throw new IllegalArgumentException();
    }

    private static String getTag() {
        StackTraceElement[] trace = new Throwable().getStackTrace();
        if (trace == null || trace.length == 0) {
            return "";
        }
        return trace[2].getClassName() + "." + trace[2].getMethodName() + "(line:" + trace[2].getLineNumber() + ")";
    }

    public static void d(Object content) {
        d(getTag(), content);
    }

    public static void e(Exception e) {
        e(getTag(), e);
    }

    public static void i(Object content) {
        i(getTag(), content);
    }

    public static void e(String tag, Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, "", e);
        }
    }

    public static void d(String tag, Object content) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, content.toString());
        }
    }

    public static void i(String tag, Object content) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, content.toString());
        }
    }
}
