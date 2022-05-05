package com.amap.map3d.demo.util;

import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;

public class DisplayUtil {
    /**
     * 屏幕宽度，横向像素点个数
     */
    private final int mScreenWidth;
    /**
     * 屏幕高度，纵向像素点个数
     */
    private final int mScreenHeight;

    /**
     * 屏幕分辨率
     */
    private final Point mScreenResolution;

    /**
     * 屏幕密度,dots-per-inch
     */
    private final int mDensityDpi;
    /**
     * 缩放系数
     */
    private final float mScaleFactor;
    private final float mFontScaleFactor;
    private final float mXdpi;
    private final float mYdpi;
    private static DisplayUtil mDisplayUtil;

    private DisplayUtil() {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        mScreenResolution = new Point(dm.widthPixels, dm.heightPixels);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mDensityDpi = dm.densityDpi;
        mXdpi = dm.xdpi;
        mYdpi = dm.ydpi;
        mScaleFactor = dm.density;
        mFontScaleFactor = dm.scaledDensity;
    }

    private static void getInstance() {
        if (mDisplayUtil == null) {
            synchronized (DisplayUtil.class) {
                if (mDisplayUtil == null) {
                    mDisplayUtil = new DisplayUtil();
                }
            }
        }
    }

    public static int dp2px(float dpValue) {
        if (mDisplayUtil == null) {
            getInstance();
        }
        return (int) (mDisplayUtil.mScaleFactor * dpValue + 0.5f);
    }
}
