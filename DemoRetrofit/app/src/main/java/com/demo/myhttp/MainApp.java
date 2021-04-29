package com.demo.myhttp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;

public class MainApp  extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        setApp(this);
        // These two lines must be written before init, otherwise these configurations will be
        // invalid in the init process
        ARouter.openLog();     // Print log
        ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode,
        // you must turn on debug mode! Online version needs to be closed, otherwise there is a
        // security risk)
        ARouter.init(this); // As early as possible, it is recommended to initialize in
        // the Application
    }

    public static Context getApp() {
        return mContext;
    }

    public static void setApp(Context context) {
        mContext = context;
    }

}
