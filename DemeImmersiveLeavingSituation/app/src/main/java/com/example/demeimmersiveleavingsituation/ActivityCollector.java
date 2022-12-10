
package com.example.demeimmersiveleavingsituation;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {

    private static final List<Activity> activities = new ArrayList<>();

    private ActivityCollector() {
        // nothing
    }

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static List<Activity> getActivities() {
        return activities;
    }
}
