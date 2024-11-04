package com.example.demowarningbar;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.widget.PopupWindow;

public class TintedBitmapDrawable extends BitmapDrawable {

    Resources res;
    Bitmap bitmap;
    private int tint;
    private int tintAlpha = 0;


    public TintedBitmapDrawable(Resources res, Bitmap bitmap,int tint) {
        super(res, bitmap);
        this.tint = tint;
        tintAlpha = Color.alpha(tint);
    }

    @Override
    public void setTint(int tint) {
        this.tint = Color.alpha(tint);
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = getPaint();
        ColorFilter filter = paint.getColorFilter();
        if (filter == null) {
            paint.setColorFilter(new LightingColorFilter(tint, 0));
            paint.setAlpha(tintAlpha);
        }
        super.draw(canvas);
    }
}
