package com.example.demowificonnectivity;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

public class MyAPP extends Application {
    private static Context mContext;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = base;

        Timber.plant(new Timber.DebugTree());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Context getMyAPP() {
        return mContext;
    }
}
