/*
 * = COPYRIGHT
 *          // description
 *     Copyright (C) 2021-06-08  XXXX rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                    Author                    Action
 * 2021-06-08             Liu Jian                   Create
 */

package com.example.demoroundcornerprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;


public class ProgressView extends View {
    private static final String TAG = "ProgressView";
    private String mText;
    private int mTextSize;
    /**
     * default white
     */
    private int mTextColor;
    /**
     * default purple_FF5045E6
     */
    private int mBorderColor;
    /**
     * default 1dp
     */
    private int mBorderWidth;
    /**
     * default white
     */
    private int mSolidColor;
    /**
     * default purple_FF5045E6
     */
    private int mReachedColor;
    /**
     * default grey_FFCCCCCC
     */
    private int mUnreachedColor;
    /**
     * default 16dp
     */
    private int mCornerRadius;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private Paint mReachedPaint;
    private RectF mBoundRect;
    private RectF mProgressRect;
    private float mCenterX;
    private float mCenterY;
    private float mWidth;
    private float mHeight;


    @IntDefDownloadStatus
    private int mState;
    private float mReached = 0.0f;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                        int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        mText = array.getString(R.styleable.ProgressView_textAttr);
        if (mText == null || mText.isEmpty()) {
            mText = context.getString(R.string
                    .activity_download_progress_view_state_init);
        }
        mTextSize = array.getDimensionPixelSize(R.styleable.ProgressView_textSizeAttr,
                context.getResources().getDimensionPixelOffset(R.dimen
                        .activity_download_rv_item_btn_txt_size));
        mTextColor = array.getColor(R.styleable.ProgressView_textColorAttr,
                context.getColor(R.color.white));
        mBorderColor = array.getColor(R.styleable.ProgressView_borderColorAttr,
                context.getColor(R.color.purple_FF5045E6));
        mBorderWidth = array.getDimensionPixelSize(R.styleable.ProgressView_borderWidth,
                context.getResources().getDimensionPixelOffset(R.dimen
                        .activity_download_rv_item_btn_border_width));
        mSolidColor = array.getColor(R.styleable.ProgressView_solidColorAttr,
                context.getColor(R.color.white));
        mReachedColor = array.getColor(R.styleable.ProgressView_reachedColor,
                context.getColor(R.color.purple_FF5045E6));
        mUnreachedColor = array.getColor(R.styleable.ProgressView_unreachedColor,
                context.getColor(R.color.grey_FFCCCCCC));
        mCornerRadius = array.getDimensionPixelSize(R.styleable.ProgressView_cornerRadius,
                context.getResources().getDimensionPixelOffset(R.dimen
                        .activity_download_rv_item_btn_download_round));
        array.recycle();
        init();
    }

    private void init() {
        mState = IntDefDownloadStatus.INIT;
        mBoundRect = new RectF();
        mProgressRect = new RectF();

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setDither(true);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTypeface(Typeface.SANS_SERIF);

        mReachedPaint = new Paint();
        mReachedPaint.setAntiAlias(true);
        mReachedPaint.setDither(true);
        mReachedPaint.setStyle(Paint.Style.FILL);
        mReachedPaint.setColor(mBorderColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBoundRect.set(mBorderWidth / 2f, mBorderWidth / 2f, w - mBorderWidth / 2f,
                h - mBorderWidth / 2f);
        mCenterX = (w + getPaddingStart() - getPaddingEnd()) / 2f;
        mCenterY = (h + getPaddingTop() - getPaddingBottom()) / 2f;

        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawForeground(canvas);
        drawProgressOrText(canvas);
        // if (mState == IntDefDownloadStatus.DOWNLOADING) {
        //     mBackgroundPaint.setStyle(Paint.Style.FILL);
        //     mBackgroundPaint.setColor(mUnreachedColor);
        //     canvas.drawRoundRect(mBoundRect, mCornerRadius, mCornerRadius, mBackgroundPaint);
        //
        //     mBackgroundPaint.setColor(mTextColor);
        //     mBackgroundPaint.setTextSize(mTextSize);
        //     mBackgroundPaint.setTypeface(Typeface.SANS_SERIF);
        //     float tW = mBackgroundPaint.measureText(mText);
        //     float tH = mBackgroundPaint.ascent() + mBackgroundPaint.descent();
        //     canvas.drawText(mText, mCenterX - tW / 2, mCenterY - tH / 2, mBackgroundPaint);
        // } else if (mState == IntDefDownloadStatus.STOPPED) {
        //     mBackgroundPaint.setStyle(Paint.Style.STROKE);
        //     mBackgroundPaint.setStrokeWidth(mBorderWidth);
        //     mBackgroundPaint.setColor(mBorderColor);
        //     canvas.drawRoundRect(mBoundRect, mCornerRadius, mCornerRadius, mBackgroundPaint);
        //
        //     mBackgroundPaint.setStyle(Paint.Style.FILL);
        //     mBackgroundPaint.setColor(mSolidColor);
        //     canvas.drawRoundRect(mBoundRect, mCornerRadius, mCornerRadius, mBackgroundPaint);
        //
        //     mBackgroundPaint.setColor(mTextColor);
        //     mBackgroundPaint.setTextSize(mTextSize);
        //     mBackgroundPaint.setTypeface(Typeface.SANS_SERIF);
        //     float tW = mBackgroundPaint.measureText(mText);
        //     float tH = mBackgroundPaint.ascent() + mBackgroundPaint.descent();
        //     canvas.drawText(mText, mCenterX - tW / 2, mCenterY - tH / 2, mBackgroundPaint);
        // } else if (mState == IntDefDownloadStatus.READY) {
        //     mBackgroundPaint.setStyle(Paint.Style.STROKE);
        //     mBackgroundPaint.setStrokeWidth(mBorderWidth);
        //     mBackgroundPaint.setColor(mBorderColor);
        //     canvas.drawRoundRect(mBoundRect, mCornerRadius, mCornerRadius, mBackgroundPaint);
        //
        //     mBackgroundPaint.setStyle(Paint.Style.FILL);
        //     mBackgroundPaint.setColor(mSolidColor);
        //     canvas.drawRoundRect(mBoundRect, mCornerRadius, mCornerRadius, mBackgroundPaint);
        //
        //     mBackgroundPaint.setColor(mBorderColor);
        //     mBackgroundPaint.setTextSize(mTextSize);
        //     mBackgroundPaint.setTypeface(Typeface.SANS_SERIF);
        //     float tW = mBackgroundPaint.measureText(mText);
        //     float tH = mBackgroundPaint.ascent() + mBackgroundPaint.descent();
        //     canvas.drawText(mText, mCenterX - tW / 2, mCenterY - tH / 2, mBackgroundPaint);
        // } else if (mState == IntDefDownloadStatus.SUCCESS) {
        //     mBackgroundPaint.setColor(mTextColor);
        //     mBackgroundPaint.setTextSize(mTextSize);
        //     mBackgroundPaint.setTypeface(Typeface.SANS_SERIF);
        //     float tW = mBackgroundPaint.measureText(mText);
        //     float tH = mBackgroundPaint.ascent() + mBackgroundPaint.descent();
        //     canvas.drawText(mText, mCenterX - tW / 2, mCenterY - tH / 2, mBackgroundPaint);
        // } else if (mState == IntDefDownloadStatus.FAILED) {
        //     mBackgroundPaint.setStyle(Paint.Style.STROKE);
        //     mBackgroundPaint.setStrokeWidth(mBorderWidth);
        //     mBackgroundPaint.setColor(mBorderColor);
        //     canvas.drawRoundRect(mBoundRect, mCornerRadius, mCornerRadius, mBackgroundPaint);
        //
        //     mBackgroundPaint.setStyle(Paint.Style.FILL);
        //     mBackgroundPaint.setColor(mSolidColor);
        //     canvas.drawRoundRect(mBoundRect, mCornerRadius, mCornerRadius, mBackgroundPaint);
        //
        //     mBackgroundPaint.setColor(mBorderColor);
        //     mBackgroundPaint.setTextSize(mTextSize);
        //     mBackgroundPaint.setTypeface(Typeface.SANS_SERIF);
        //     float tW = mBackgroundPaint.measureText(mText);
        //     float tH = mBackgroundPaint.ascent() + mBackgroundPaint.descent();
        //     mBackgroundPaint.setTextAlign(Paint.Align.CENTER);
        //     canvas.drawText(mText, mCenterX - tW / 2, mCenterY - tH / 2, mBackgroundPaint);
        // } else {
        //     // IntDefDownloadStatus.INIT
        //     mBackgroundPaint.setStyle(Paint.Style.STROKE);
        //     mBackgroundPaint.setStrokeWidth(mBorderWidth);
        //     mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        //     mBackgroundPaint.setColor(mBorderColor);
        //     canvas.drawRoundRect(mBoundRect, mCornerRadius, mCornerRadius, mBackgroundPaint);
        //
        //     mReachedPaint.setStyle(Paint.Style.FILL);
        //     mReachedPaint.setColor(mSolidColor);
        //     canvas.drawRoundRect(mBoundRect, mCornerRadius, mCornerRadius, mReachedPaint);
        // }
        //
        // if (hasDownloaded()) {
        //     mBackgroundPaint.setStyle(Paint.Style.FILL);
        //     float w = mBoundRect.width() * mReached / 100;
        //     mBackgroundPaint.setColor(mReachedColor);
        //     mProgressRect.set(0, 0, w, mBoundRect.height());
        //     canvas.drawRoundRect(mProgressRect, mCornerRadius, mCornerRadius, mBackgroundPaint);
        // }
        //
        // mTextPaint.setColor(mBorderColor);
        // float tW = mTextPaint.measureText(mText);
        // float tH = mBackgroundPaint.ascent() + mBackgroundPaint.descent();
        // canvas.drawText(mText, mCenterX - tW / 2, mCenterY - tH / 2, mTextPaint);
    }

    private void drawBackground(Canvas canvas) {
        if (mState == IntDefDownloadStatus.DOWNLOADING) {
            mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mBackgroundPaint.setColor(mUnreachedColor);
        } else if (mState == IntDefDownloadStatus.SUCCESS) {
            mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mBackgroundPaint.setColor(mReachedColor);
        }/* else if (mState == IntDefDownloadStatus.STOPPED) {
            mBackgroundPaint.setStyle(Paint.Style.STROKE);
            mBackgroundPaint.setColor(mBorderColor);
        } else if (mState == IntDefDownloadStatus.READY) {
            mBackgroundPaint.setStyle(Paint.Style.STROKE);
            mBackgroundPaint.setColor(mBorderColor);
        } else if (mState == IntDefDownloadStatus.FAILED) {
            mBackgroundPaint.setStyle(Paint.Style.STROKE);
            mBackgroundPaint.setColor(mBorderColor);
        } */ else {
            // IntDefDownloadStatus.INIT
            // IntDefDownloadStatus.STOPPED
            // IntDefDownloadStatus.READY
            // IntDefDownloadStatus.FAILED
            mBackgroundPaint.setStyle(Paint.Style.STROKE);
            mBackgroundPaint.setColor(mBorderColor);
        }

        mBackgroundPaint.setStrokeWidth(mBorderWidth);
        canvas.drawRoundRect(mBoundRect, mCornerRadius, mCornerRadius, mBackgroundPaint);
    }

    private void drawForeground(Canvas canvas) {
        if (!hasDownloaded()) {
            return;
        }

        mReachedPaint.setStyle(Paint.Style.FILL);
        mReachedPaint.setColor(mReachedColor);
        float w = mWidth * mReached / 100;
        double a = mCornerRadius; // a >= b, oval focus on X axis
        double b = mCornerRadius;
        double x = a - w / 2;
        double y = Math.sqrt(b * b - (b * b * x * x) / (a * a));
        if (Double.compare(y, b) > 0 || Double.compare(w / 2, a) == 0
                || Double.compare(w / 2, a) > 0) {
            mProgressRect.set(0, 0, w, mHeight);
            canvas.drawRoundRect(mProgressRect, mCornerRadius, mCornerRadius, mReachedPaint);
            Log.d(TAG, "drawForeground: w >= mCornerRadius, w, a, b=" + w + ", r=" + mCornerRadius
                    + ", " + a + ", " + b);
        } else {

            Log.d(TAG, "drawForeground: w, a, b=" + w + ", " + a + ", " + b);
            mProgressRect.set(0, (float) (b - y), w, (float) (b + y));
            canvas.drawRoundRect(mProgressRect, w / 2, (float) y, mReachedPaint);
            Log.d(TAG, "drawForeground: w, x, y = " + w + ", " + x + ", " + y);

        }
    }

    private void drawProgressOrText(Canvas canvas) {
        if (mText == null || mText.isEmpty()) {
            switch (mState) {
                case IntDefDownloadStatus.DOWNLOADING:
                    mText = mReached + getResources().getString(R.string
                            .activity_download_progress_view_state_downloading);
                    break;
                case IntDefDownloadStatus.SUCCESS:
                    mText = getResources().getString(R.string
                            .activity_download_progress_view_state_success);
                    break;
                case IntDefDownloadStatus.FAILED:
                    mText = getResources().getString(R.string
                            .activity_download_progress_view_state_failed);
                    break;
                case IntDefDownloadStatus.STOPPED:
                    mText = getResources().getString(R.string
                            .activity_download_progress_view_state_stopped);
                    break;
                case IntDefDownloadStatus.READY:
                    mText = getResources().getString(R.string
                            .activity_download_progress_view_state_ready);
                    break;
                default:
                    mText = getResources().getString(R.string
                            .activity_download_progress_view_state_init);
                    break;
            }
        }

        if (mState == IntDefDownloadStatus.DOWNLOADING) {
            mTextPaint.setColor(mTextColor);
        } else if (mState == IntDefDownloadStatus.SUCCESS) {
            mTextPaint.setColor(mTextColor);
        }/* else if (mState == IntDefDownloadStatus.STOPPED) {
            mBackgroundPaint.setStyle(Paint.Style.STROKE);
            mBackgroundPaint.setColor(mBorderColor);
        } else if (mState == IntDefDownloadStatus.READY) {
            mBackgroundPaint.setStyle(Paint.Style.STROKE);
            mBackgroundPaint.setColor(mBorderColor);
        } else if (mState == IntDefDownloadStatus.FAILED) {
            mBackgroundPaint.setStyle(Paint.Style.STROKE);
            mBackgroundPaint.setColor(mBorderColor);
        } */ else {
            // IntDefDownloadStatus.INIT
            // IntDefDownloadStatus.STOPPED
            // IntDefDownloadStatus.READY
            // IntDefDownloadStatus.FAILED
            mTextPaint.setColor(mBorderColor);
        }

        float tW = mTextPaint.measureText(mText);
        float tH = mTextPaint.ascent() + mTextPaint.descent();
        canvas.drawText(mText, mCenterX - tW / 2, mCenterY - tH / 2, mTextPaint);
    }

    public void setState(int state) {
        mState = state;
    }

    /**
     * @param percent the value of percent. E.g, if it reaches 45.4%, this parameter value is 45.4.
     */
    public void setProgress(float percent) {
        mReached = percent;
        if (Float.compare(percent, 100f) > 0 || Float.compare(percent, 100f) == 0) {
            mReached = 100f;
        }
        postInvalidate();
    }

    public void setText(String text) {
        mText = text;
        postInvalidate();
    }

    private boolean hasDownloaded() {
        return Float.compare(mReached, 0.0f) > 0;
    }
}
