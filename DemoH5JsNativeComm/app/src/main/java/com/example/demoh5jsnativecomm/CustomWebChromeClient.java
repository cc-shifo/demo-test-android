package com.example.demoh5jsnativecomm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.demoh5jsnativecomm.fileexplorer.FileExplorerActivity;

public class CustomWebChromeClient extends WebChromeClient {

    private Activity mActivity;
    private WebView mWebView;
    private ValueCallback<Uri[]> mCallback;
    private FileChooserParams mFileChooserParams;

    public CustomWebChromeClient(Activity activity, WebView webView) {
        mActivity = activity;
        // mWebView = webView;
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        // mWebView = webView;
        mCallback = filePathCallback;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // webView.getContext().startActivityForResult(Intent.createChooser(intent, "File Chooser"),
        //         REQUEST_FILE_PICKER);
        FileExplorerActivity.openFileExplorerForResult(mActivity, webView);
        return true;
    }

    public ValueCallback<Uri[]> getCallback() {
        return mCallback;
    }

    public void setCallback(ValueCallback<Uri[]> callback) {
        mCallback = callback;
    }

    public void destroy() {
        mActivity = null;
        mCallback = null;
        mWebView = null;
    }
}
