/*
 * = COPYRIGHT
 *     TianYu
 *
 * Description:
 *
 * Date                    Author                    Action
 * 2020-06-29              LiuJian                    Create
 */

package com.example.democircleprogressbar;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DisplayUtils {
    /**
     * sp to px
     *
     * @param sp size in sp
     * @return size in pixels.
     */
    public static float sp2px(Context context, float sp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    /**
     * sp to px
     *
     * @param sp size in sp
     * @return size in pixels.
     */
    public static float sp2pxAnother(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    /**
     * dp to px
     *
     * @param dp size in dp
     * @return size in pixels.
     */
    public static float dp2px(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    /**
     * dp to px
     *
     * @param dp size in dp
     * @return font size in pixels.
     */
    public static float dp2pxAnother(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    /**
     * px to sp
     *
     * @param px in pixel
     * @return sp
     */
    public static float px2sp(Context context, float px) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return px / fontScale + 0.5f;
    }

    /**
     * px to dp
     *
     * @param px in pixel
     * @return dp
     */
    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
