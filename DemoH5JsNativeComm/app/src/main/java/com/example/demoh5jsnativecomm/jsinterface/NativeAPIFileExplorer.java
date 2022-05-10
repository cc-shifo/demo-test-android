package com.example.demoh5jsnativecomm.jsinterface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.example.demoh5jsnativecomm.fileexplorer.FileExplorerActivity;

public class NativeAPIFileExplorer {
    private static final String API_NAME = "NativeAPIFileExplorer";

    @SuppressLint("StaticFieldLeak")
    private static NativeAPIFileExplorer mNativeAPIFileExplorer;
    private Context mContext;
    private WebView mWebView;

    private NativeAPIFileExplorer() {
        // nothing
    }

    public static synchronized NativeAPIFileExplorer getInstance() {
        if (mNativeAPIFileExplorer == null) {
            mNativeAPIFileExplorer = new NativeAPIFileExplorer();
        }

        return mNativeAPIFileExplorer;
    }

    public void init(@NonNull Context context) {
        mContext = context;
    }

    public void registerAllJsAPI(@NonNull WebView webView) {
        mWebView = webView;
        webView.addJavascriptInterface(this, API_NAME);
    }

    /**
     * get the absolute path of selected file。
     */
    @JavascriptInterface
    public void openFileExplorer() {
        FileExplorerActivity.openFileExplorer(mContext, mWebView);
    }

    public void destroy() {
        mContext = null;
    }
}
