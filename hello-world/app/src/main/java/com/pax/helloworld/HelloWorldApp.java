package com.pax.helloworld;

import android.app.Application;

public class HelloWorldApp extends Application {
    private static Application mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static Application getApplication() {
        return mApplication;
    }
}
