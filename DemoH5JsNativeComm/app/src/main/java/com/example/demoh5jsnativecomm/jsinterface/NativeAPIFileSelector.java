package com.example.demoh5jsnativecomm.jsinterface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NativeAPIFileSelector {
    private static final String API_NAME = "NativeAPIFileSelector";

    @SuppressLint("StaticFieldLeak")
    private static NativeAPIFileSelector mNativeAPIFileSelector;
    private Context mContext;

    public static synchronized NativeAPIFileSelector getInstance() {
        if (mNativeAPIFileSelector == null) {
            mNativeAPIFileSelector = new NativeAPIFileSelector();
        }

        return mNativeAPIFileSelector;
    }

    public void init(@NonNull Context context) {
        mContext = context;
    }


    private NativeAPIFileSelector() {
        // nothing
    }

    public void registerAllJsAPI(@NonNull WebView webView) {
        webView.addJavascriptInterface(this, API_NAME);
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
