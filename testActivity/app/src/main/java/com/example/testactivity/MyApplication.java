package com.example.testactivity;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(ActivityCollector.getInstance());
    }
}
