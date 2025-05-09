/*
 * = COPYRIGHT
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20211125 	         LiuJian                  Create
 */

package com.example.democlipoutrect;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ClipOutRectProgressView extends View {
    private String mText;
    private int mTextSize;
    /**
     * default 0xFFFFFFFF
     */
    private int mTextColor;
    /**
     * default 0x8000FF00
     */
    private int mBorderColor;
    /**
     * default 1dp
     */
    private int mBorderWidth;
    /**
     * default 0xFF00FF00
     */
    private int mReachedColor;
    /**
     * default 0x8000FF00
     */
    private int mUnreachedColor;
    /**
     * default 1dp
     * 暂时支持圆角边框，圆角进度暂不支持。
     */
    private int mCornerRadius;

    private Paint mTextPaint;
    private Paint mReachedPaint;
    private Paint mUnreachedPaint;
    private Paint mBorderPaint;

    private RectF mBorderRect;
    private RectF mUnreachedRect;
    private RectF mReachedRect;
    private float mCenterX;
    private float mCenterY;
    private float mWidth;
    private float mHeight;


    private float mProgress = 0.0f;

    /*public ClipOutRectProgressView(Context context) {
        this(context, null);
    }*/

    public ClipOutRectProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ClipOutRectProgressView);
        mText = array.getString(R.styleable.ClipOutRectProgressView_text);
        if (mText == null) {
            mText = "";
        }
        mTextSize = array.getDimensionPixelSize(R.styleable.ClipOutRectProgressView_textSize, context
                .getResources().getDimensionPixelOffset(R.dimen.clip_out_rect_progress_view_text_size));
        // @RequiresApi(api = Build.VERSION_CODES.M)
        // mTextColor = array.getColor(R.styleable.ClipOutRectProgressView_textColor,
        //         context.getColor(R.color.job_progress_view_text_color));
        mTextColor = array.getColor(R.styleable.ClipOutRectProgressView_textColor,
                0xFFFFFFFF);
        mBorderColor = array.getColor(R.styleable.ClipOutRectProgressView_borderColor,
                0x8000FF00);
        mBorderWidth = array.getDimensionPixelSize(R.styleable.ClipOutRectProgressView_borderWidth,
                context.getResources().getDimensionPixelOffset(R.dimen
                        .clip_out_rect_progress_view_border_width));
        mReachedColor = array.getColor(R.styleable.ClipOutRectProgressView_reachedColor, 0xFF00FF00);
        mUnreachedColor = array.getColor(R.styleable.ClipOutRectProgressView_unreachedColor,
                0x8000FF00);
        mCornerRadius = array.getDimensionPixelSize(R.styleable.ClipOutRectProgressView_cornerRadius,
                context.getResources().getDimensionPixelOffset(R.dimen
                        .clip_out_rect_progress_view_corner_radius));
        array.recycle();
        init();
    }

    // public ClipOutRectProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    //     super(context, attrs, defStyleAttr, 0);
    //
    // }

    private void init() {
        mBorderRect = new RectF();
        mUnreachedRect = new RectF();
        mReachedRect = new RectF();


        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTypeface(Typeface.SANS_SERIF);

        mReachedPaint = new Paint();
        mReachedPaint.setAntiAlias(true);
        mReachedPaint.setDither(true);
        mReachedPaint.setStyle(Paint.Style.FILL);
        mReachedPaint.setColor(mReachedColor);

        mUnreachedPaint = new Paint();
        mUnreachedPaint.setAntiAlias(true);
        mUnreachedPaint.setDither(true);
        mUnreachedPaint.setStyle(Paint.Style.FILL);
        mUnreachedPaint.setColor(mUnreachedColor);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setDither(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mBorderRect.set(mBorderWidth / 2f, mBorderWidth / 2f, w - mBorderWidth / 2f,
                h - mBorderWidth / 2f);
        mUnreachedRect.set(mBorderWidth, mBorderWidth, w - mBorderWidth,
                h - mBorderWidth);
        mCenterX = (w + getPaddingStart() - getPaddingEnd()) / 2f;
        mCenterY = (h + getPaddingTop() - getPaddingBottom()) / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorder(canvas);
        drawReached(canvas);
        drawUnreached(canvas);
        drawMsg(canvas);
    }

    @SuppressWarnings("SingleStatementInBlock")
    private void drawUnreached(Canvas canvas) {
        canvas.save();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 画布裁剪外的区域有效。即当前画布是canvas，剪切mReachedRect区域后，mReachedRect区域之外的区域
            // 有效（绘制的内容可以显示出来）
            canvas.clipOutRect(mReachedRect);
        } else {
            canvas.clipRect(mReachedRect, Region.Op.DIFFERENCE);
        }
        canvas.drawRoundRect(mUnreachedRect, mCornerRadius, mCornerRadius, mUnreachedPaint);
        canvas.restore();

        // float w = mWidth * mReached / 100;
        // double a = mCornerRadius; // a >= b, oval focus on X axis
        // double b = mCornerRadius;
        // double x = a - w / 2;
        // double y = Math.sqrt(b * b - (b * b * x * x) / (a * a));
        // if (Double.compare(y, b) > 0 || Double.compare(w / 2, a) == 0
        //         || Double.compare(w / 2, a) > 0) {
        //     mProgressRect.set(0, 0, w, mHeight);
        //     canvas.drawRoundRect(mProgressRect, mCornerRadius, mCornerRadius, mReachedPaint);
        // } else {
        //     mProgressRect.set(0, (float) (b - y), w, (float) (b + y));
        //     canvas.drawRoundRect(mProgressRect, w / 2, (float) y, mReachedPaint);
        // }
    }

    private void drawMsg(Canvas canvas) {
        float tW = mTextPaint.measureText(mText);
        float tH = mTextPaint.ascent() + mTextPaint.descent();
        canvas.drawText(mText, mCenterX - tW / 2, mCenterY - tH / 2, mTextPaint);
    }

    private void drawReached(Canvas canvas) {
        // canvas.save();
        float w = mWidth * mProgress / 100.0f;
        mReachedRect.set(0, 0, w, mHeight);
        canvas.drawRoundRect(mReachedRect, mCornerRadius, mCornerRadius, mReachedPaint);
        // float tW = mTextPaint.measureText(mText);
        // float tH = mTextPaint.ascent() + mTextPaint.descent();
        // canvas.drawText(mText, mCenterX - tW / 2, mCenterY - tH / 2, mTextPaint);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(mUnreachedRect);
        } else {
            canvas.clipRect(mUnreachedRect, Region.Op.DIFFERENCE);
        }*/
        // canvas.restore();
    }

    private void drawBorder(Canvas canvas) {
        canvas.drawRoundRect(mBorderRect, mCornerRadius, mCornerRadius, mBorderPaint);
    }

    /**
     * @param percent the value of percent. E.g, if it reaches 45.4%, this parameter value is 45.4.
     */
    public void setProgress(float percent) {
        mProgress = percent;
        mText = mProgress + "%";
        postInvalidate();
    }

}
