package com.example.demosplashscreen;

import android.app.Application;
import android.util.Log;

public class MainApp extends Application {
    private static final String TAG = "MainApp";
    @Override
    public void onCreate() {
        Log.d(TAG, "MainApp onCreate: ");
        super.onCreate();
        // testSplashScreen();
    }

    // 不设置mSplashScreen.setKeepOnScreenCondition，单凭Application的耗时，观察splash screen是否一直显示
    // 结论是，splash screen会一直等等Application的耗时任务完成，然后才会关闭。
    // 通过观察Application#onCreate的log时间和mSplashScreen.setOnExitAnimationListener的log时间，发现正好相差6000ms左右。
    private void testSplashScreen() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
