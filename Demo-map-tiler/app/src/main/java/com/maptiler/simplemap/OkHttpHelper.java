package com.maptiler.simplemap;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpHelper {

    private OkHttpHelper() {
        // nothing
    }

    // public static OkHttpHelper getInstance() {
    //     return Holder.INSTANCE;
    // }
    //
    // public static class Holder {
    //     private static final  OkHttpHelper INSTANCE = new OkHttpHelper();
    //
    //     private Holder() {
    //         // nothing
    //     }
    // }

    public static OkHttpClient createClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            if (BuildConfig.DEBUG) Log.d("Http----test", message+"");
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)//设置日志打印
                .retryOnConnectionFailure(true)//失败重连
                .connectTimeout(10, TimeUnit.SECONDS)//网络请求超时时间单位为秒
                .build();

        // okHttpClient.newBuilder().build();
        return okHttpClient;
    }
}
