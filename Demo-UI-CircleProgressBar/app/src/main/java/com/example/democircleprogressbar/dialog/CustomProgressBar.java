
package com.example.democircleprogressbar.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.example.democircleprogressbar.DisplayUtils;
import com.example.democircleprogressbar.R;


public class CustomProgressBar extends ProgressBar {
    /**
     * text content in circle
     */
    private int mTextColor;
    private int mTextSize;
    /**
     * line width
     */
    private int mStrokeWidth;
    /**
     * color for drawArc
     */
    private int progressColor;
    /**
     * color of the stroked circle
     */
    private int progressRemainedColor;
    private int mRadius;
    /**
     * circle center coordinate
     */
    private int mCenterX;
    private int mCenterY;
    /**
     * draw drawArc
     */
    private RectF mRectF;
    /**
     * painter attribute
     */
    private Paint mPaint;

    public CustomProgressBar(Context context) {
        this(context, null);
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar);
        mTextColor = array.getColor(R.styleable.CustomProgressBar_progressTextColor, 0xFFFFFFFF);
        mTextSize = (int) array.getDimension(R.styleable.CustomProgressBar_progressTextSize,
                DisplayUtils.sp2px(context, 20.0f));
        mStrokeWidth = (int) array.getDimension(R.styleable.CustomProgressBar_progressStrokeWidth,
                DisplayUtils.dp2px(context, 25.0f));
        progressColor = array.getColor(R.styleable.CustomProgressBar_progressColor, 0xFFFFFFFF);
        progressRemainedColor = array.getColor(R.styleable.CustomProgressBar_progressRemainedColor,
                0xFFFFFFFF);
        mRadius = (int) array.getDimension(R.styleable.CustomProgressBar_progressRadius, 0.0f);
        array.recycle();
        mPaint = new Paint();
        mRectF = new RectF();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int rectW = width - getPaddingStart() - getPaddingEnd();
        int rectH = height - getPaddingTop() - getPaddingBottom();
        int radiusTmp = (Math.min(rectW, rectH) - mStrokeWidth) / 2;
        if (mRadius > radiusTmp || mRadius == 0)
            mRadius = radiusTmp;
        mCenterX = getPaddingStart() + rectW / 2;
        mCenterY = getPaddingTop() + rectH / 2;

        float left = mCenterX *1.0f - mRadius/* - mStrokeWidth / 2.0f*/;
        float top = mCenterY *1.0f - mRadius/* - mStrokeWidth / 2.0f*/;
        float right = mCenterX *1.0f + mRadius/* + mStrokeWidth / 2.0f*/;
        float bottom = mCenterY *1.0f + mRadius/* + mStrokeWidth / 2.0f*/;
        mRectF.set(left, top, right, bottom);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);

        // draw text in center
        int progress = getProgress();
        String txt = progress + "s";
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        float tW = mPaint.measureText(txt);
        float tH = mPaint.descent() + mPaint.ascent();
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(txt, mCenterX - tW / 2, mCenterY - tH / 2, mPaint);

        // draw remained progress
        mPaint.setColor(progressRemainedColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);

        // draw progress
        mPaint.setColor(progressColor);
        float sweepAngle = progress * 1.0f / getMax() * 360;
        canvas.drawArc(mRectF, -90, -sweepAngle, false, mPaint);
        canvas.restore();
    }

}
