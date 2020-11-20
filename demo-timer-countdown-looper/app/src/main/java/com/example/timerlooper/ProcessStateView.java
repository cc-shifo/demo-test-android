/*
 *
 * = COPYRIGHT
 *          TianYu
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20200924    	         LiuJian                  Create
 */

package com.example.timerlooper;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;

import androidx.annotation.Nullable;


@SuppressWarnings("unused")
public class ProcessStateView extends View {
    /**
     * line width
     */
    private int mStrokeWidth;


    /**
     * circle center coordinate
     */
    private int mCenterX;
    private int mCenterY;

    /**
     * text content in circle
     */
    private int mTextColor;
    private int mTextSize;
    private String mText;

    /**
     * dynamic ring paint
     */
    private int mRingColor;
    private Matrix mSweepGradientMatrix;
    private SweepGradient mSweepGradient;
    private RectF mRectF;
    private float mStartArc;

    /**
     * result paint, static ring
     */
    private Paint mPaint;
    private Paint mDynamicRingPaint;


    private Path mSuccessPath;
    private Path mFailurePath;
    private Path mResultImagePath;
    private PathMeasure mPathMeasure;
    private float mTickProgress;
    private float mRightProgress;
    private float mLeftProgress;


    private ValueAnimator mSweepAnimator;
    private ValueAnimator mSucAnimator;
    private ValueAnimator mFailureAnimRight;
    private ValueAnimator mFailureAnimLeft;
    private ProcessStateView.State mState;
    private StateListener mStateListener;

    public interface StateListener {
        /**
         * Called when success animation is end.
         */
        void onEnd();
    }

    public ProcessStateView(Context context) {
        this(context, null);
    }

    public ProcessStateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ProcessStateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ProcessStateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProcessStateView);
        mTextColor = array.getColor(R.styleable.ProcessStateView_messageTextColor, 0xFF000000);
        mTextSize = (int) array.getDimension(R.styleable.ProcessStateView_messageTextSize,
                DisplayUtils.sp2px(context, 20.0f));
        mText = array.getString(R.styleable.ProcessStateView_message);
        mStrokeWidth = (int) array.getDimension(R.styleable.ProcessStateView_ringStrokeWidth,
                DisplayUtils.dp2px(context, 10.0f));
        mRingColor = array.getColor(R.styleable.ProcessStateView_ringColor, 0xFF458DE6);
        array.recycle();
        init();
    }

    private void init() {
        mDynamicRingPaint = new Paint();
        mRectF = new RectF();
        mSweepGradientMatrix = new Matrix();
        mState = State.STATE_PROCESSING;
        mDynamicRingPaint.setAntiAlias(true);
        mDynamicRingPaint.setDither(true);
        mDynamicRingPaint.setStyle(Paint.Style.STROKE);
        mDynamicRingPaint.setStrokeCap(Paint.Cap.ROUND);
        mDynamicRingPaint.setStrokeWidth(mStrokeWidth);

        // result
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mTextColor);

        mSuccessPath = new Path();
        mFailurePath = new Path();
        mResultImagePath = new Path();
        mPathMeasure = new PathMeasure();
        initProcessingAnimation();
        initSucAnimation();
        initFailureAnimation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int rectW = w - getPaddingStart() - getPaddingEnd();
        int rectH = h - getPaddingTop() - getPaddingBottom();
        int radiusTmp = (Math.min(rectW, rectH) - mStrokeWidth) / 2;
        mCenterX = getPaddingStart() + rectW / 2;
        mCenterY = getPaddingTop() + rectH / 2;

        float left = mCenterX * 1.0f - radiusTmp/* - mStrokeWidth / 2.0f*/;
        float top = mCenterY * 1.0f - radiusTmp/* - mStrokeWidth / 2.0f*/;
        float right = mCenterX * 1.0f + radiusTmp/* + mStrokeWidth / 2.0f*/;
        float bottom = mCenterY * 1.0f + radiusTmp/* + mStrokeWidth / 2.0f*/;
        mRectF.set(left, top, right, bottom);

        // 3 o'clock direction means 0 angle. If rotate around clockwise, the angle value is
        // positive, otherwise the value is negative.
        mSweepGradient = new SweepGradient(mCenterX, mCenterY,
                new int[]{0xFF458de6, /*Color.RED, Color.GREEN, */Color.TRANSPARENT}, null);
        mStartArc = 90;
        mSweepGradientMatrix.setRotate(-mStartArc, mCenterX, mCenterY);
        mSweepGradient.setLocalMatrix(mSweepGradientMatrix);
        mDynamicRingPaint.setShader(mSweepGradient);

        float x1 = (right - left) / 2 - radiusTmp * 3 / 5.0f;
        float y1 = radiusTmp + radiusTmp / 12.0f;
        float x2 = ((right - left) / 2) - 1.0f - radiusTmp / 6.0f;
        float y2 = radiusTmp + radiusTmp / 2.0f + 1 + radiusTmp / 12.0f;
        float x3 = (right - left) / 2 + radiusTmp * 3.0f / 5.0f + radiusTmp / 6.0f;
        float y3 = radiusTmp * 3 / 5.0f + radiusTmp / 12.0f;
        mSuccessPath.reset();
        mSuccessPath.moveTo(x1, y1);
        mSuccessPath.lineTo(x2, y2);
        mSuccessPath.lineTo(x3, y3);
        mPathMeasure.setPath(mSuccessPath, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mState == State.STATE_PROCESSING) {
            if (mText != null && !mText.isEmpty()) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mTextColor);
                mPaint.setTextSize(mTextSize);
                float tW = mPaint.measureText(mText);
                float tH = mPaint.descent() + mPaint.ascent();
                canvas.drawText(mText, mCenterX - tW / 2, mCenterY - tH / 2, mPaint);
            }
            canvas.save();
            canvas.rotate(-mStartArc, mCenterX, mCenterY);
            canvas.drawArc(mRectF, -90, -360, false, mDynamicRingPaint);
            canvas.restore();
        } else if (mState == State.STATE_SUCCESS) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mRingColor);
            canvas.drawArc(mRectF, -90, -360, false, mPaint);

            mResultImagePath.reset();
            LogUtils.d("mTickProgress: " + mTickProgress);
            LogUtils.d("mTickProgress Length: " + mPathMeasure.getLength());
            mPathMeasure.setPath(mSuccessPath, false);
            mPathMeasure.getSegment(0, mTickProgress * mPathMeasure.getLength(),
                    mResultImagePath, true);
            canvas.drawPath(mResultImagePath, mPaint);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(0xFFFF6A4C);
            canvas.drawArc(mRectF, -90, -360, false, mPaint);

            mResultImagePath.reset();
            mFailurePath.reset();
            mFailurePath.moveTo((mRectF.right - mRectF.left) / 3.0f * 2
                            + (mRectF.right - mRectF.left) / 12.0f,
                    (mRectF.right - mRectF.left) / 3.0f);
            mFailurePath.lineTo((mRectF.right - mRectF.left) / 3.0f,
                    (mRectF.right - mRectF.left) / 3.0f * 2
                            + (mRectF.right - mRectF.left) / 12.0f);
            mPathMeasure.setPath(mFailurePath, false);
            mPathMeasure.getSegment(0, mRightProgress * mPathMeasure.getLength(),
                    mResultImagePath, true);
            canvas.drawPath(mResultImagePath, mPaint);

            mResultImagePath.reset();
            mFailurePath.reset();
            mFailurePath.moveTo((mRectF.right - mRectF.left) / 3.0f,
                    (mRectF.right - mRectF.left) / 3.0f);
            mFailurePath.lineTo((mRectF.right - mRectF.left) / 3.0f * 2
                            + (mRectF.right - mRectF.left) / 12.0f,
                    (mRectF.right - mRectF.left) / 3.0f * 2
                            + (mRectF.right - mRectF.left) / 12.0f);
            mPathMeasure.nextContour();
            mPathMeasure.setPath(mFailurePath, false);
            mPathMeasure.getSegment(0, mLeftProgress * mPathMeasure.getLength(),
                    mResultImagePath, true);
            canvas.drawPath(mResultImagePath, mPaint);
        }
    }

    private void initProcessingAnimation() {
        mSweepAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mSweepAnimator.setRepeatMode(ValueAnimator.RESTART);
        mSweepAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mSweepAnimator.setDuration(2400);
        mSweepAnimator.setInterpolator(new LinearInterpolator());
        mSweepAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartArc = (float) animation.getAnimatedValue() * 360.0f + 90;
                mStartArc %= 360.0f;
                postInvalidate();
            }
        });
        mSweepAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mStartArc = 90;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //nothing
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //nothing
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                ValueAnimator valueAnimator = (ValueAnimator) animation;
                float v = (float) valueAnimator.getAnimatedValue();
                mStartArc = v * 360.0f + 90;
            }
        });
    }

    private void initSucAnimation() {
        mSucAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mSucAnimator.setRepeatMode(ValueAnimator.RESTART);
        mSucAnimator.setRepeatCount(0);
        mSucAnimator.setDuration(500);
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.lineTo(2 / 5f, 3 / 5f);
        path.lineTo(1f, 1f);
        mSucAnimator.setInterpolator(new PathInterpolator(path));
        mSucAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTickProgress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        mSucAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //noting
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mStateListener != null) {
                    mStateListener.onEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //noting
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //noting
            }
        });
    }

    private void initFailureAnimation() {
        mFailureAnimRight = ValueAnimator.ofFloat(0.0f, 1.0f);
        mFailureAnimRight.setRepeatMode(ValueAnimator.RESTART);
        mFailureAnimRight.setRepeatCount(0);
        mFailureAnimRight.setDuration(250);
        Path path = new Path();
        path.moveTo(0f, 0f);
        path.lineTo(1 / 3f, 1 / 3f);
        path.lineTo(2 / 3f, 2 / 3f);
        path.lineTo(1f, 1f);
        mFailureAnimRight.setInterpolator(new PathInterpolator(path));
        mFailureAnimRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLeftProgress = 0;
                mRightProgress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        mFailureAnimRight.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //noting
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFailureAnimLeft.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //noting
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //noting
            }
        });

        mFailureAnimLeft = ValueAnimator.ofFloat(0.0f, 1.0f);
        mFailureAnimLeft.setRepeatMode(ValueAnimator.RESTART);
        mFailureAnimLeft.setRepeatCount(0);
        mFailureAnimLeft.setDuration(250);
        mFailureAnimLeft.setInterpolator(new PathInterpolator(path));
        mFailureAnimLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLeftProgress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }


    public void startStateAnia() {
        if (mState == State.STATE_PROCESSING) {
            if (mSucAnimator.isRunning()) {
                mSucAnimator.cancel();
            }

            if (mFailureAnimLeft.isRunning()) {
                mFailureAnimLeft.cancel();
            }

            if (mFailureAnimRight.isRunning()) {
                mFailureAnimRight.cancel();
            }

            if (!mSweepAnimator.isRunning()) {
                mSweepAnimator.start();
            }
        } else if (mState == State.STATE_SUCCESS) {
            if (mSweepAnimator.isRunning()) {
                mSweepAnimator.cancel();
            }

            if (mFailureAnimLeft.isRunning()) {
                mFailureAnimLeft.cancel();
            }

            if (mFailureAnimRight.isRunning()) {
                mFailureAnimRight.cancel();
            }

            if (!mSucAnimator.isRunning()) {
                mSucAnimator.start();
            }
        } else {
            if (mSweepAnimator.isRunning()) {
                mSweepAnimator.cancel();
            }

            if (mSucAnimator.isRunning()) {
                mSucAnimator.cancel();
            }

            if (!mFailureAnimRight.isRunning()) {
                mFailureAnimRight.start();
            }
        }
    }

    public void stopStateAnia() {
        stopProcessStateAnim();
        stopSucStateAnim();
        stopFailedStateAnim();
    }

    public void stopProcessStateAnim() {
        if (mSweepAnimator.isRunning())
            mSweepAnimator.cancel();
    }

    public void stopSucStateAnim() {
        if (mSucAnimator.isRunning())
            mSucAnimator.cancel();
    }

    public void stopFailedStateAnim() {
        if (mFailureAnimRight.isRunning())
            mFailureAnimRight.cancel();
        if (mFailureAnimLeft.isRunning())
            mFailureAnimLeft.cancel();
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = strokeWidth;
    }

    public void setRingColorColor(int ringColor) {
        mRingColor = ringColor;
    }

    public void setState(State state) {
        mState = state;
    }

    public void setStateListener(StateListener stateListener) {
        mStateListener = stateListener;
    }

    public enum State {
        STATE_FAILED,
        STATE_SUCCESS,//处理中…
        STATE_PROCESSING;

        State() {
            //nothing
        }
    }
}
