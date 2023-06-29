package com.example.demoruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 尺子构成：
 * 1、垂直的线条，上下边界线；
 * 2、垂直居中方向当前刻度线；
 * 3、动态刻度值。
 */
public class DemoCenterView extends View {
    /**
     * 尺子属性：最小刻度值精度（短刻度线表示的值），精度精确到十分位。
     */
    private static final int SCALE_PRECISION = 10;

    /**
     * 绘制属性：界面的宽
     */
    private int mWidth;
    /**
     * 绘制属性：界面的高
     */
    private int mHeight;

    /**
     * 绘制属性：圆圈中心点x坐标，相对于view的
     */
    private float mCircleX;
    /**
     * 绘制属性：圆圈中心点y坐标，相对于view的
     */
    private float mCircleY;

    /**
     * 绘制属性：圆圈的半径
     */
    private float mRadius;

    /**
     * 绘制属性：画笔
     */
    private Paint mPaint;

    /**
     * 绘制属性：线条颜色
     */
    private final int mColor;

    /**
     * 绘制属性：绘制界面线条，圆圈的整个宽度
     */
    private final int mStrokeWidth;

    /**
     * 虚线路径
     */
    private Path mDashPath;

    /**
     * 虚线路径效果
     */
    private DashPathEffect mPathEffect;

    /**
     * 绘制属性：带箭头的线条
     */
    private Path mPathArrow;

    /**
     * 绘制属性：带箭头的线条
     */
    private final float mArrowLineLength;

    /**
     * 绘制属性：带箭头的线条
     */
    private final float mArrowLength;

    /**
     * 绘制属性：左箭头的x坐标，默认箭头指向正北。
     */
    private float mArrowLX;
    /**
     * 绘制属性：左箭头的y坐标
     */
    private float mArrowLY;
    /**
     * 绘制属性：右箭头的x坐标
     */
    private float mArrowRX;
    /**
     * 绘制属性：右箭头的y坐标
     */
    private float mArrowRY;

    /**
     * 当前值
     */
    private float mCurrentV1; // 水平
    private float mCurrentV2; // 垂直
    private float mCurrentV3; // 方位

    public DemoCenterView(Context context) {
        this(context, null);
    }

    public DemoCenterView(Context context,
                          @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemoCenterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DemoCenterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DemoVRulerView);
        mStrokeWidth = array.getDimensionPixelSize(R.styleable.DemoVRulerView_scaleLineStrokeMax,
                8);
        mArrowLineLength = array.getDimensionPixelSize(R.styleable.DemoVRulerView_scaleLineMax, 40);
        mArrowLength = array.getDimensionPixelSize(R.styleable.DemoVRulerView_scaleLineMin, 8);
        mColor = array.getColor(R.styleable.DemoVRulerView_scaleTextColor, Color.GREEN);
        array.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPathArrow = new Path();
        mDashPath = new Path();
        mPathEffect = new DashPathEffect(new float[]{mStrokeWidth / 4f, mStrokeWidth * 1.5f}, 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mRadius = Math.min(w, h) / 2f;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initLayout();
    }

    /**
     * 初始位置信息。
     * 尺子的位置；
     * 提示矩形框的位置；
     * 提示信息在矩形框内的位置；
     */
    private void initLayout() {
        mCircleX = mWidth / 2f;
        mCircleY = mHeight / 2f;
        mArrowLX = (float) (mCircleX - Math.sin(45) * mArrowLength);
        mArrowLY = (float) (mCircleY - mArrowLineLength + Math.sin(45) * mArrowLength);
        mArrowRX = (float) (mCircleX + Math.sin(45) * mArrowLength);
        mArrowRY = (float) (mCircleY - mArrowLineLength + Math.sin(45) * mArrowLength);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawNavDirection(canvas);
        drawHVLine(canvas);
    }

    /**
     * 设置当前刻度值。数值变化大于0.1时有效。
     *
     * @param v1H 前刻度值，有效精度到十分位
     */
    public void setCurrentValue(float v1H, float v2V, float v3) {
        boolean c1 = Math.abs(v1H - mCurrentV1) >= 0.1f;
        boolean c2 = Math.abs(v2V - mCurrentV2) >= 0.1f;
        boolean c3 = Math.abs(v3 - mCurrentV3) >= 0.1f;
        if (c1) {
            mCurrentV1 = v1H;
        }
        if (c2) {
            mCurrentV2 = v2V;
        }
        if (c3) {
            mCurrentV3 = v3;
        }
        if (c1 || c2 || c3) {
            postInvalidate();
        }
    }

    /**
     * 绘制水平和垂直线
     */
    private void drawHVLine(Canvas canvas) {
        canvas.save();
        resetPaint();
        canvas.rotate(mCurrentV1, mCircleX, mCircleY);
        // 绘制水平线
        canvas.drawLine(mArrowLineLength + mArrowLength, mCircleY,
                mCircleX - mArrowLineLength - mArrowLength, mCircleY, mPaint);
        canvas.drawLine(mCircleX + mArrowLineLength + mArrowLength, mCircleY,
                mWidth - mArrowLineLength - mArrowLength, mCircleY, mPaint);
        // 绘制垂直线
        double d = Math.sin(mCurrentV2 / 180 * Math.PI) * mRadius;
        mPaint.setPathEffect(mPathEffect);
        mDashPath.reset();
        mDashPath.moveTo(mCircleX, mCircleY);
        mDashPath.lineTo(mCircleX, (float) (mCircleY - d));
        // mDashPath.close();
        canvas.drawPath(mDashPath, mPaint);
        // canvas.drawLine(mCircleX, mCircleY, mCircleX, (float) (mCircleY - d), mPaint);
        mDashPath.close();
        canvas.restore();
    }

    /**
     * 绘制方向
     */
    private void drawNavDirection(Canvas canvas) {
        resetPaint();
        canvas.save();
        mPathArrow.reset();
        mPathArrow.moveTo(mCircleX, mCircleY);
        mPathArrow.lineTo(mCircleX, mCircleY - mArrowLineLength);
        mPathArrow.moveTo(mArrowLX, mArrowLY);
        mPathArrow.lineTo(mCircleX, mCircleY - mArrowLineLength);
        mPathArrow.lineTo(mArrowRX, mArrowRY);
        mPathArrow.close();
        canvas.drawPath(mPathArrow, mPaint);
        //
        // if (mCurrentV3 == 0) {
        //
        // } else if (mCurrentV3 > 0 && mCurrentV3 < 90) {
        //     canvas.rotate(-mCurrentV3);
        // } else if (mCurrentV3 == 90) {
        //     canvas.rotate(-90f);
        // } else if (mCurrentV3 == 180) {
        //
        // } else if (mCurrentV3 < 360 && mCurrentV3 > 180) {
        //
        // }

        canvas.restore();
    }

    /**
     * 清除画笔到配置参数
     */
    private void resetPaint() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mStrokeWidth);
    }
}
