package com.example.democrashhandlerapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d(TAG, "attachBaseContext: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                Log.e(TAG, "uncaughtException: ", e);
            }
        });
    }
}
