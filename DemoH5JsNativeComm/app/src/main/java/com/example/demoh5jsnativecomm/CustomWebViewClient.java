package com.example.demoh5jsnativecomm;

import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

public class CustomWebViewClient extends WebViewClient {

    private ViewGroup mParent;
    private WebView mWebView;

    public CustomWebViewClient(@NonNull ViewGroup parent, @NonNull WebView webView) {
        mParent = parent;
        mWebView = webView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        mWebView.loadUrl(request.getUrl().toString());
        return true;
    }

    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        mParent.removeView(mWebView);
        mWebView.destroy();
        mWebView = null;
        mParent = null;
        return true;
    }
}
