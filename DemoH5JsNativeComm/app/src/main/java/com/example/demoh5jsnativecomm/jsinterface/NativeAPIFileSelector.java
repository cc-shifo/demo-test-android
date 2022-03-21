package com.example.demoh5jsnativecomm.jsinterface;

import android.content.Context;
import android.webkit.JavascriptInterface;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NativeAPIFileSelector {
    public static final String API_NAME = "NativeAPIFileSelector";

    private Context mContext;
    public NativeAPIFileSelector(@NonNull Context context) {
        mContext = context;
    }

    /**
     * get the absolute path of selected file。
     * @return return the absolute path。
     */
    @JavascriptInterface
    public String getFileAbsolutePath() {
        return "path: " + new SimpleDateFormat("yyMMddHHmmss", Locale.CHINA).format(new Date());
    }

    public void destroy() {
        mContext = null;
    }
}
