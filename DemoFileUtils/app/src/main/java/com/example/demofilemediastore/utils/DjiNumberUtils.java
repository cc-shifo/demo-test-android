package com.zhd.hiair.djisdkmanager;


import android.text.TextUtils;

import java.util.regex.Pattern;

public class DjiNumberUtils {

    public static float parseFloat(String number, float defaultValue) {
        try {
            float value = Float.parseFloat(number);
            return value;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int parseInt(String number, int defaultValue) {
        try {
            int value = Integer.parseInt(number);
            return value;
        } catch (NumberFormatException e) {
            // ignore
        }
        return defaultValue;
    }

    public static double parseDouble(String number, double defaultValue) {
        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /*
     * 是否为浮点数？double或float类型。
     * @param str 传入的字符串。
     * @return 是浮点数返回true,否则返回false。
     */
    public static boolean isDoubleOrFloat(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }
}
