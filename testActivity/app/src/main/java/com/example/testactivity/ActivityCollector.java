package com.example.testactivity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity集合
 */
@SuppressWarnings("unused")
public class ActivityCollector implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "ActivityCollector";
    /**
     * Activity集合
     */
    private static final List<Activity> activities = new ArrayList<>(5);
    /**
     * 退出回调
     */
    private Callback mExitCall;

    private ActivityCollector() {
        // nothing
    }

    public static ActivityCollector getInstance() {
        return Holder.INSTANCE;
    }

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static List<Activity> getActivities() {
        return activities;
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        // nothing
        Log.d(TAG, "onActivityCreated: " + activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        activities.add(activity);
        Log.d(TAG, "onActivityStarted: " + activity + "count=" + activities.size());
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        // nothing
        Log.d(TAG, "onActivityResumed: " + activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        // nothing
        Log.d(TAG, "onActivityPaused: " + activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activities.remove(activity);
        Log.d(TAG, "onActivityStopped: " + activity + "count=" + activities.size());
        if (activities.isEmpty() && mExitCall != null) {
            mExitCall.call();
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        // nothing
        Log.d(TAG, "onActivitySaveInstanceState: " + activity);
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        // nothing
        Log.d(TAG, "onActivityDestroyed: " + activity);
    }

    /**
     * 添加Application退出回调
     * @param callBack 退出回调
     */
    public void addExitListener(@NonNull Callback callBack) {
        mExitCall = callBack;
    }

    public interface Callback {
        void call();
    }

    /**
     * 单例类持有者
     */
    private static final class Holder {
        private static final ActivityCollector INSTANCE = new ActivityCollector();
    }
}
