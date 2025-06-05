package com.example.activitysingleprocess;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.util.List;

public class MyApp extends Application {
    private static final String TAG = "MyApp";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName(this);
        Log.e(TAG, "activitysingleprocess onCreate: " + processName);
        // 在所有进程中初始化TheRouter
        // 主进程额外初始化逻辑
        if (getPackageName().equals(processName)) {
            Log.e(TAG, "activitysingleprocess onCreate: main");
        } else {
            Log.e(TAG, "activitysingleprocess onCreate: sub");
        }
        Log.e(TAG, "activitysingleprocess onCreate: pid=" + Process.myPid());

    }


    // 获取当前进程名的方法
    private String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        if (processes == null) {
            return null;
        }
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processes) {
            if (info.pid == myPid) {
                return info.processName;
            }
        }
        return null;
    }
}
