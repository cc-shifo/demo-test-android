package com.example.demoh5jsnativecomm;

import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class CustomWebChromeClient extends WebChromeClient {

    private WebView mWebView;
    private ValueCallback<Uri[]> mCallback;
    private FileChooserParams mFileChooserParams;

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        mWebView = webView;
        mCallback = filePathCallback;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // webView.getContext().startActivityForResult(Intent.createChooser(intent, "File Chooser"),
        //         REQUEST_FILE_PICKER);
        return true;
    }
}
