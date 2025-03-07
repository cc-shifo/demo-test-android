package com.example.democropbitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PictureUtil {
    private static final String TAG = "PictureUtil";


    /**
     *
     * @param context 上下文
     * @param idBg 背景图Drawable资源id
     * @param idFg 前景图Drawable资源id
     * @return 成功，返回合成后的图片；失败，返回null
     */
    @Nullable
    public static Bitmap fillPicture(@NonNull Context context, @DrawableRes int idBg, @DrawableRes int idFg) {
        Bitmap src = decodeDrawable(context, R.drawable.ic_bg_1920x1080);
        if (src == null) {
            return null;
        }
        Bitmap bg = Bitmap.createBitmap(src, (1920 - 1080) / 2, 0, 1080, 1080);
        // Bitmap fg = readImage("/storage/emulated/0/Download/ic_pt_sel_mark.png")
        Bitmap fg = decodeDrawable(context, R.drawable.ic_fg_1080x1080);
        return mergeBitmap(bg, fg);
    }

    /**
     * 从Drawable目录读取原始图片(避免缩放)，转成Bitmap
     * @param context 上下文，用于获取资源管理器
     * @param id 被读取的{@link android.graphics.drawable.Drawable}资源
     * @return 成功，返回与被读取资源对应的Bitmap，否则返回null
     */
    @Nullable
    public static Bitmap decodeDrawable(@NonNull Context context, int id) {
        TypedValue value = new TypedValue();
        Resources res = context.getResources();
        try (InputStream inputStream = res.openRawResource(id, value)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inTargetDensity = value.density;
            opts.inScaled = false;
            return BitmapFactory.decodeResource(res, id, opts);
        } catch (Exception e) {
            Log.e(TAG, "decodeDrawable: ", e);
        }

        return null;
    }


    /**
     * 叠加两个bitmap
     * @param background 背景图
     * @param foreground 前景图。
     * @return 成功返回叠加后的bitmap，否则返回null。
     */
    @Nullable
    private static Bitmap mergeBitmap(@Nullable Bitmap background, @Nullable Bitmap foreground) {
        if (background == null || foreground == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        Rect base = new Rect(0, 0, bgWidth, bgHeight);
        Rect fRect = new Rect(0, 0, bgWidth, bgHeight);


        // 基础
        Bitmap bmp = background.copy(Bitmap.Config.ARGB_8888, true);
        Canvas cv = new Canvas(bmp);
        cv.drawBitmap(foreground, fRect, base, null);

        // Bitmap bmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        // Canvas cv = new Canvas(bmp);
        // cv.save();
        // cv.drawBitmap(background, 0, 0, null);
        // cv.save();
        // cv.drawBitmap(foreground, 0, 0, null);
        // // cv.restore();
        return bmp;
    }
}

