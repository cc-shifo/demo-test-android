package com.example.demogetapplication;

import android.app.Application;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AppUtil {
    private static final String TAG = "AppUtil";
    private static Application myApp;

    public static Application getApplication1() {
        if (myApp == null) {
            try {
                Method currentApplication = Class.forName("android.app.ActivityThread")
                        .getDeclaredMethod("currentApplication");
                myApp = (Application) currentApplication.invoke(null);
            } catch (Exception e) {
                Log.e(TAG, "getApplication1: ", e);
            }
        }

        return myApp;
    }

    /**
     * 反射获取Application
     */
    public static Application getApplication2() {
        if (myApp == null) {
            try {
                Class localClass1 = Class.forName("com.android.internal.os.RuntimeInit");
                Field localField1 = localClass1.getDeclaredField("mApplicationObject");
                localField1.setAccessible(true);
                Object localObject1 = localField1.get(localClass1);

                Class localClass2 = Class.forName("android.app.ActivityThread$ApplicationThread");
                Field localField2 = localClass2.getDeclaredField("this$0");
                localField2.setAccessible(true);
                Object localObject2 = localField2.get(localObject1);

                Class localClass3 = Class.forName("android.app.ActivityThread");
                Method localMethod = localClass3.getMethod("getApplication", new Class[0]);
                localMethod.setAccessible(true);
                Application localApplication =
                        (Application) localMethod.invoke(localObject2, new Object[0]);
                if (localApplication != null){
                    myApp = localApplication;
                }
            } catch (Exception localException) {
                Log.e(TAG, "getApplication2: ", localException);
            }
        }

        return myApp;
    }
}
