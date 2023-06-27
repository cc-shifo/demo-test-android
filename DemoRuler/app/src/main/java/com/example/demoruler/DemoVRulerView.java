package com.example.demoruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * 尺子构成：
 * 1、垂直的线条，上下边界线；
 * 2、垂直居中方向当前刻度线；
 * 3、动态刻度值。
 */
public class DemoVRulerView extends View {
    /**
     * 尺子属性：最小刻度值精度（短刻度线表示的值），精度精确到十分位。
     */
    private static final int SCALE_PRECISION = 10;

    /**
     * 尺子属性：尺子包含的条线段个数。
     */
    private final int mRulerSize;

    /**
     * 尺子属性：一条线段由几条刻度组成。
     */
    private final int mSegmentSize;
    /**
     * 尺子属性：单一刻度表示值。
     */
    private final float mScaleSize;

    /**
     * 尺子属性：刻度线最长长度值
     */
    private final float mSLLengthMax;
    /**
     * 尺子属性：刻度线最短长度值
     */
    private final float mSLLengthMin;

    /**
     * 尺子属性：刻度尺的长度。刻度尺线段个数 x 单条线段刻度个数 x 刻度值。用于计算尺子的上下边界。
     */
    private int mRuleValue;

    /**
     * 尺子属性：单条线段的长度
     */
    private int mSegmentValue;

    /**
     * 尺子属性：一条刻度的大小
     */
    private int mScaleValue;

    /**
     * 绘制属性：绘制刻度线画笔的最大宽度
     */
    private final float mSLStrokeMax;
    /**
     * 绘制属性：绘制刻度线画笔的最小宽度
     */
    private final float mSLStrokeMin;

    /**
     * 绘制属性：刻度文字的大小
     */
    private final int mScaleTextSize;

    /**
     * 绘制属性：刻度文字颜色
     */
    private final int mScaleTextColor;

    /**
     * 绘制属性：提醒标签的文字颜色
     */
    private final int mHintLabelTextColor;

    /**
     * 绘制属性：提醒信息的文字颜色
     */
    private final int mHintTextColor;

    /**
     * 绘制属性：提醒文字粗等字体大小
     */
    private final int mHintTextBoldSize;
    /**
     * 绘制属性：提醒文字中等字体大小
     */
    private final int mHintTextMiddleSize;

    /**
     * 绘制属性：提醒文字小体字大小
     */
    private final int mHintTextSmallSize;

    /**
     * 绘制属性：尺子画笔
     */
    private Paint mScalePaint;

    /**
     * 绘制属性：尺子文本画笔
     */
    private Paint mScaleTextPaint;
    /**
     * 绘制属性：尺子文本画笔
     */
    private Paint mHintPaint;

    /**
     * 绘制属性：尺子文本加粗
     */
    private Typeface mHintBoldTypeface;

    /**
     * 绘制属性：界面的宽
     */
    private int mWidth;
    /**
     * 绘制属性：界面的高
     */
    private int mHeight;
    /**
     * 绘制属性：单一刻度占用的高度
     */
    private float mScaleHeight;

    /**
     * 绘制属性：刻度文字的高，像素。
     */
    private float mScaleTextHeight;

    /**
     * 绘制属性：提示信息边距
     */
    private static final int MARGIN_CURSOR = 4;
    /**
     * 绘制属性：提示信息宽度
     */
    private float mHintWidth;
    /**
     * 绘制属性：提示信息宽度
     */
    private float mHintHeight;
    /**
     * 绘制属性：提示信息边框top,left,right,bottom
     */
    private float mHintTop;
    private float mHintLeft;
    private float mHintRight;
    private float mHintBottom;
    /**
     * 绘制属性：提示信息SPD, m/s, 00.0的top,left坐标，相对于当前view的。
     */
    private float mHintTextX;
    private float mHintTextY;
    private float mHintLabelX;
    private float mHintLabelY;
    private float mHintUnitX;
    private float mHintUnitY;

    /**
     * 当前刻度值
     */
    private int mCurrentValue;


    public DemoVRulerView(Context context) {
        this(context, null);
    }

    public DemoVRulerView(Context context,
                          @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemoVRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DemoVRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DemoVRulerView);
        mRulerSize = array.getDimensionPixelSize(R.styleable.DemoVRulerView_rulerSize, 4);
        mSegmentSize = array.getDimensionPixelSize(R.styleable.DemoVRulerView_segmentSize, 5);
        mScaleSize = array.getFloat(R.styleable.DemoVRulerView_scaleSize, 0.2f);
        mSLLengthMax = array.getDimensionPixelSize(R.styleable.DemoVRulerView_scaleLineMax, 24);
        mSLLengthMin = array.getDimensionPixelSize(R.styleable.DemoVRulerView_scaleLineMin, 12);
        mSLStrokeMax = array.getDimensionPixelSize(R.styleable.DemoVRulerView_scaleLineStrokeMax,
                8);
        mSLStrokeMin = array.getDimensionPixelSize(R.styleable.DemoVRulerView_scaleLineStrokeMax,
                4);
        mScaleTextSize = array.getDimensionPixelSize(R.styleable.DemoVRulerView_scaleTextSize, 28);
        mScaleTextColor = array.getColor(R.styleable.DemoVRulerView_scaleTextColor, Color.GREEN);

        mHintTextBoldSize = array.getDimensionPixelSize(R.styleable
                .DemoVRulerView_hintTextBoldSize, 28);
        mHintTextMiddleSize = array.getDimensionPixelSize(R.styleable
                .DemoVRulerView_hintTextMiddleSize, 16);
        mHintTextSmallSize = array.getDimensionPixelSize(R.styleable
                .DemoVRulerView_hintTextSmallSize, 12);
        mHintLabelTextColor = array.getColor(R.styleable.DemoVRulerView_hintLabelTextColor,
                Color.WHITE);
        mHintTextColor = array.getColor(R.styleable.DemoVRulerView_hintTextColor, Color.GREEN);
        array.recycle();
        init();
    }

    private void init() {
        mScalePaint = new Paint();
        mScalePaint.setAntiAlias(true);
        mScalePaint.setDither(true);
        mScalePaint.setStyle(Paint.Style.FILL);
        mScalePaint.setStrokeWidth(mSLStrokeMax);

        mScaleTextPaint = new Paint();
        mScaleTextPaint.setAntiAlias(true);
        mScaleTextPaint.setDither(true);
        mScaleTextPaint.setTypeface(Typeface.SANS_SERIF);
        mScaleTextPaint.setTextSize(mScaleTextSize);
        mScaleTextPaint.setStyle(Paint.Style.FILL);
        Paint.FontMetrics metrics = mScaleTextPaint.getFontMetrics();
        mScaleTextHeight = metrics.bottom - metrics.top;

        mHintPaint = new Paint();
        mHintPaint.setAntiAlias(true);
        mHintPaint.setDither(true);
        mHintPaint.setStyle(Paint.Style.FILL);
        mHintPaint.setTypeface(Typeface.SANS_SERIF);

        // 提示文字粗体字
        mHintBoldTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);

        // 尺子总刻度值，一条线段刻度值，单一刻度表示的刻度值
        mScaleValue = (int) (mScaleSize * SCALE_PRECISION);
        mSegmentValue = mSegmentSize * mScaleValue;
        mRuleValue = (mRulerSize * mSegmentValue);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = calculateWidth(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        // 尺子总刻度值，一条线段刻度值，单一刻度表示的刻度值
        mScaleHeight = h * 1f / (mRulerSize * mSegmentSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initLayout();
    }

    /**
     * 提示信息
     */
    private static final String HINT_TEXT = "00.0";
    private static final String HINT_LABEL = "SPD";
    private static final String HINT_UNIT = "m/s";
    /**
     * 提示信息内部填充边距，单位像素。
     */
    private static final int HINT_PADDING = 8;

    private void calHintMeasure() {
        // 矩形边界线的宽度。
        float border = mSLStrokeMin;

        // SPD, m/s, 00.0宽度
        mHintPaint.setTypeface(mHintBoldTypeface);
        mHintPaint.setTextSize(mHintTextMiddleSize);
        Paint.FontMetrics metrics = mHintPaint.getFontMetrics();
        float w1 = mHintPaint.measureText(HINT_TEXT) + HINT_PADDING;
        float h1 = metrics.bottom - metrics.top;
        mHintPaint.setTypeface(Typeface.SANS_SERIF);
        mHintPaint.setTextSize(mHintTextMiddleSize);
        metrics = mHintPaint.getFontMetrics();
        float w2 = mHintPaint.measureText(HINT_LABEL);
        float h2 = metrics.bottom - metrics.top;
        mHintPaint.setTypeface(Typeface.SANS_SERIF);
        mHintPaint.setTextSize(mHintTextSmallSize);
        metrics = mHintPaint.getFontMetrics();
        float w3 = mHintPaint.measureText(HINT_UNIT);
        float h3 = metrics.bottom - metrics.top;
        mHintWidth = w1 + HINT_PADDING + Math.max(w2, w3) + HINT_PADDING * 2 + border;
        mHintHeight = Math.max(h1, h2 + h3) + HINT_PADDING * 2 + border;
    }

    /**
     * 初始位置信息。
     * 尺子的位置；
     * 提示矩形框的位置；
     * 提示信息在矩形框内的位置；
     */
    private void initLayout() {
        //画圆角矩形
        float strokeCenter = mSLStrokeMin / 2;
        mHintLeft = mWidth - mSLLengthMax - mHintWidth - strokeCenter;
        mHintTop = mHeight / 2f - mHintHeight / 2 - strokeCenter;
        mHintRight = mWidth - mSLLengthMax + strokeCenter;
        mHintBottom = mHeight / 2f + mHintHeight / 2 + strokeCenter;

        // SPD
        mHintPaint.setTypeface(Typeface.SANS_SERIF);
        mHintPaint.setTextSize(mHintTextMiddleSize);
        mHintPaint.setStyle(Paint.Style.FILL);
        mHintPaint.setColor(Color.WHITE);
        Rect rect = new Rect();
        mHintPaint.getTextBounds(HINT_LABEL, 0, HINT_LABEL.length(), rect);
        mHintLabelX = mHintLeft + HINT_PADDING;
        mHintLabelY = mHintTop + HINT_PADDING + rect.height() / 2f;

        // m/s
        mHintPaint.setTypeface(Typeface.SANS_SERIF);
        mHintPaint.setTextSize(mHintTextSmallSize);
        mHintPaint.setStyle(Paint.Style.FILL);
        mHintPaint.setColor(Color.WHITE);
        mHintPaint.getTextBounds(HINT_UNIT, 0, HINT_UNIT.length(), rect);
        mHintUnitX = mHintLeft + HINT_PADDING;
        mHintUnitY = mHintBottom - HINT_PADDING - rect.height() / 2f;

        // 00.0
        mHintPaint.setTypeface(mHintBoldTypeface);
        mHintPaint.setTextSize(mHintTextBoldSize);
        mHintPaint.setColor(mHintTextColor);
        String txt = String.valueOf(mCurrentValue / 10);
        mHintPaint.getTextBounds(HINT_TEXT, 0, txt.length(), rect);
        mHintTextX = mHintRight - HINT_PADDING - rect.width();
        mHintTextY = (mHintBottom - mHintTop) / 2 + mHintTop;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.RED);
        drawRuleLine(canvas);
        // drawCursor(canvas);
        drawScaleLine(canvas);
    }

    /**
     * 获取当前刻度值
     *
     * @return 当前刻度值
     */
    public float getCurrentValue() {
        return mCurrentValue;
    }

    /**
     * 设置当前刻度值。数值变化大于0.1时有效。
     *
     * @param currentValue 前刻度值，有效精度到十分位
     */
    public void setCurrentValue(float currentValue) {
        int d = (int) (currentValue * SCALE_PRECISION);
        if (Math.abs(d - mCurrentValue) >= 1) {
            mCurrentValue = d;
            postInvalidate();
        }
    }

    /**
     * 计算宽度或高度的真实大小
     *
     * @param widthMeasureSpec 测量规则
     * @return 真实的大小
     * @see ViewGroup#getChildMeasureSpec(int, int, int)
     */
    private int calculateWidth(int widthMeasureSpec) {
        final int mode = MeasureSpec.getMode(widthMeasureSpec);
        int realSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (mode) {
            // 精确模式：已经确定具体数值：layout_width为具体值，或match_parent
            case MeasureSpec.EXACTLY:
                break;
            // 最大模式：最大不能超过父控件给的widthSize：layout_width为wrap_content
            case MeasureSpec.AT_MOST:
                // 未指定尺寸模式：一般父控件是AdapterView
            case MeasureSpec.UNSPECIFIED:
            default:
                // (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mScaleTextSize *
                // 10f, getResources().getDisplayMetrics())
                calHintMeasure();
                int defaultContentSize = (int) (mHintWidth + mSLLengthMax + 0.5);
                realSize = Math.min(realSize, defaultContentSize);
                break;
        }
        return realSize;
    }

    /**
     * 刻度尺线。垂直竖线，上下两端的边界线段。
     */
    private void drawRuleLine(Canvas canvas) {
        // 垂直线
        mScalePaint.setStrokeCap(Paint.Cap.BUTT);
        mScalePaint.setColor(mScaleTextColor);
        mScalePaint.setStrokeWidth(mSLStrokeMax);
        canvas.drawLine(mWidth - mSLStrokeMax / 2, 0, mWidth - mSLStrokeMax / 2, mHeight,
                mScalePaint);// stroke以中心线进行绘制

        // 上下两端的边界线段
        float xMax = mWidth - mSLLengthMax;
        // 线包含在区域内
        canvas.drawLine(xMax, mSLStrokeMax / 2f, mWidth, mSLStrokeMax / 2f, mScalePaint);
        float bottom = mHeight - mSLStrokeMax / 2f;
        canvas.drawLine(xMax, bottom, mWidth, bottom, mScalePaint);// 线包含在区域内
    }

    /**
     * 绘制垂直方向中心点的刻度线，刻度值
     */
    private void drawScaleLine(Canvas canvas) {
        float distance;// 底部第一条刻度距离底部边界的刻度间距
        int index;// 底部第一条刻度在单条线段中的索引
        int bottomVal;// 底部第一条刻度的所在线段长刻度线的刻度值
        int n;
        // 刻度间距（像素）, mScaleHeight
        // 总刻度个数, n
        // 底部第一条刻度距离底部边界的刻度间距, distance
        // 底部第一条刻度在单条线段中的索引, start, 1~5
        // 根据刻度个数循环，叠加单一刻度值
        // 识别大刻度和小刻度：start / mSegmentValue == 0表示大
        // 计算当前刻度的y: 当前刻度占尺子刻度的百分比 * 尺子高度
        // 计算长刻度线的刻度值：bottomNum +
        int halfValue = mRuleValue >> 1;
        if (mCurrentValue >= halfValue) {
            n = mRulerSize * mSegmentSize;
            distance = mScaleValue * 1f - (mCurrentValue - halfValue) % mScaleValue;
            index = (mCurrentValue % mSegmentValue) / mScaleValue + 1;
            bottomVal = (mCurrentValue - halfValue) / mScaleValue;
            if (mCurrentValue % mSegmentValue == 0) {
                n++; // 再加上长刻度线点
                index = 5; // 长刻度线点
            }
        } else {
            // 0~mCurrentValue + mRuleHalfValue
            n = (mCurrentValue + halfValue) / mScaleValue + 1; // 再加上起始点0
            distance = halfValue * 1f - mCurrentValue;
            index = 0;
            bottomVal = 0;
        }

        float xMax = mWidth - mSLLengthMax;
        float xMin = mWidth - mSLLengthMin;
        mScalePaint.setColor(mScaleTextColor);
        mScaleTextPaint.setColor(mScaleTextColor);
        for (int i = 0; i < n; i++) {
            float y = mHeight - (i * mScaleValue + distance) / mRuleValue * mHeight;
            if (index % mSegmentSize == 0) {// 长刻度线
                mScalePaint.setStrokeWidth(mSLStrokeMax);
                y -= mSLStrokeMax / 2f;
                canvas.drawLine(xMax, y, mWidth, y, mScalePaint);
                // 指针线位置的刻度值不画，跳过
                if (y < mHintTop - mScaleTextHeight || y > mHintBottom + mScaleTextHeight) {
                    // 刻度值
                    int fNum = (i + bottomVal) / mSegmentSize;
                    String text = Integer.toString(fNum);
                    final float textWidth = mScaleTextPaint.measureText(text);
                    float tH = mScaleTextPaint.ascent() + mScaleTextPaint.descent();
                    canvas.drawText(text, xMax - textWidth - MARGIN_CURSOR, y - tH / 2,
                            mScaleTextPaint);
                }
            } else {// 短刻度线
                mScalePaint.setStrokeWidth(mSLStrokeMin);
                y -= mSLStrokeMin / 2f;
                canvas.drawLine(xMin, y, mWidth, y, mScalePaint);
            }
            index++;
        }
    }

    /**
     * 绘制当前刻度值指针线
     */
    private void drawCursor(Canvas canvas) {
        // 画指针线
        mScalePaint.setColor(mScaleTextColor);
        mScalePaint.setStrokeWidth(mSLStrokeMin);
        mScalePaint.setStrokeCap(Paint.Cap.ROUND);
        float x = mWidth - mSLLengthMax;
        float y = mHeight / 2f - mSLStrokeMin / 2;
        canvas.drawLine(x, y, mWidth, y, mScalePaint);

        // 画圆角矩形
        canvas.drawRoundRect(mHintLeft, mHintTop, mHintRight, mHintBottom, 10, 10, mScalePaint);
        // 画小三角形指针

        // 绘制SPD, m/s 00.0
        mHintPaint.setTypeface(Typeface.SANS_SERIF);
        mHintPaint.setTextSize(mHintTextMiddleSize);
        mHintPaint.setStyle(Paint.Style.FILL);
        mHintPaint.setColor(mHintLabelTextColor);
        canvas.drawText(HINT_LABEL, mHintLabelX, mHintLabelY, mHintPaint);
        mHintPaint.setTextSize(mHintTextSmallSize);
        canvas.drawText(HINT_UNIT, mHintUnitX, mHintUnitY, mHintPaint);

        mHintPaint.setTypeface(mHintBoldTypeface);
        mHintPaint.setTextSize(mHintTextBoldSize);
        mHintPaint.setColor(mHintTextColor);
        String txt = String.valueOf(mCurrentValue / 10);
        float tW = mHintPaint.measureText(txt);
        mHintTextX = mHintRight - HINT_PADDING - tW;
        canvas.drawText(txt, mHintTextX, mHintTextY, mHintPaint);
    }
}
