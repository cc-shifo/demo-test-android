package com.pax.helloworld;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Tool {
    private static final String TAG = "Tool";
    private Tool() {
    }

    private static Resources res;
    static {
        res = HelloWorldApp.getApplication().getResources();
    }
    public static int dp2px(float dpValue){
        return (int) (dpValue*res.getDisplayMetrics().density+0.5f);
    }
    public static int px2dp(float pxValue){
        return (int) (pxValue/res.getDisplayMetrics().density+0.5f);
    }
    public static int getSmallestScreenWidth(){
        return res.getConfiguration().smallestScreenWidthDp;
    }
    public static int getScreenWidth(){
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.widthPixels;
    }
    public static int getColor(int color){
        return res.getColor(color);
    }
    public static String getString(int string){
        return res.getString(string);
    }

    public static Drawable getDrawable(int drawableId) {
        return res.getDrawable(drawableId);
    }
    public static int getScreenHeight(){
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int x = Integer.parseInt(field.get(object).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            Log.w("CustomKeyboardEditText", "", e);
        }
        return statusBarHeight;
    }

    public static int getVirtualBarHeight(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - display.getHeight();
        } catch (Exception e) {
            LogUtil.d(TAG,e.getMessage());
        }
        return vh;
    }

    public static void startApp(String appPackageName){
        try{
            Intent intent = HelloWorldApp.getApplication().getPackageManager().getLaunchIntentForPackage(appPackageName);
            HelloWorldApp.getApplication().startActivity(intent);
        }catch(Exception e){
            LogUtil.d(TAG,e.getMessage());
        }
    }

    public static boolean isPackageInstall(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException var4) {
            return false;
        }

        return packageInfo != null;
    }

    public static boolean isEdcInstall() {
        return isPackageInstall(HelloWorldApp.getApplication(),"com.pax.edc");
    }
    public static boolean isPaxPayInstall() {
        return isPackageInstall(HelloWorldApp.getApplication(),"com.pax.paxpay");
    }
}
