package com.example.demosetlanguage;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {
    private static Application mApp;
    public static Application getApp() {
        return mApp;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
}
