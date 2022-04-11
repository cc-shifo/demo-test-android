package com.example.democipherroom;

import android.app.Application;

import timber.log.Timber;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
