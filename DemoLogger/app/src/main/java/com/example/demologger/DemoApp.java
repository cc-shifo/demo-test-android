package com.example.demologger;

import android.app.Application;
import android.os.Environment;

public class DemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.plant(BuildConfig.DEBUG);
    }
}
