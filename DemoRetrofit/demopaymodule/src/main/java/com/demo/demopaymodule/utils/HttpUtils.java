package com.demo.demopaymodule.utils;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class HttpUtils {
    private static HttpUtils mInstance;
    private OkHttpClient mOkHttpClient;

    private HttpUtils() {
        //nothing
        mOkHttpClient = new OkHttpClient();
    }

    public static synchronized  HttpUtils getInstance() {
        if (mInstance == null) {
            mInstance = new HttpUtils();
        }

        return mInstance;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public Retrofit.Builder getRetrofit(@NonNull String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl);
    }
}
