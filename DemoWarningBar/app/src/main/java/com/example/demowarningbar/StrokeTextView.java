package com.example.demowarningbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;

/**
 * 支持描边的TextView
 */
@SuppressLint("AppCompatCustomView")
public class StrokeTextView extends TextView {


    private float strokeWidth;
    private int strokeColor;

    private boolean onDrawingFlag;

    public StrokeTextView(Context context) {
        super(context);
    }

    public StrokeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StrokeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.Widget_StrokeTextView);
    }

    public StrokeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    @Override
    public void invalidate() {
        if (!onDrawingFlag) {
            super.invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            super.onDraw(canvas);
            return;
        }

        onDrawingFlag = true;
        ColorStateList lastColor = getTextColors();
        setTextColor(strokeColor);
        getPaint().setStyle(Paint.Style.STROKE);
        getPaint().setStrokeWidth(strokeWidth);
        super.onDraw(canvas);

        setTextColor(lastColor);
        getPaint().setStyle(Paint.Style.FILL);
        super.onDraw(canvas);
        onDrawingFlag = false;
    }

    private void initView(@Nullable AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.StrokeTextView);
        strokeWidth = ta.getDimension(R.styleable.StrokeTextView_textStrokeWidth, 0F);
        strokeColor = ta.getColor(R.styleable.StrokeTextView_textStrokeColor, 0);
        ta.recycle();

        try {
            Field field = TextView.class.getDeclaredField("mShadowRadius");
            field.setAccessible(true);
            field.setFloat(this, strokeWidth);
            field.setAccessible(false);
        } catch (Exception e) {
            // 如果上面的方法失败了，使用下面这个备用方式
            setShadowLayer(strokeWidth, 0F, 0F, Color.TRANSPARENT);
        }
    }
}
