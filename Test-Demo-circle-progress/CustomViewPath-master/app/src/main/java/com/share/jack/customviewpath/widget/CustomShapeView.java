package com.share.jack.customviewpath.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 */

public class CustomShapeView extends View {

    private Paint mPaint;
    private Path path;
    private RectF mRectF;

    public CustomShapeView(Context context) {
        this(context, null);
    }

    public CustomShapeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomShapeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();       // 创建画笔
        path = new Path();
        mRectF = new RectF();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.BLACK);  // 画笔颜色 - 黑色
        mPaint.setStyle(Paint.Style.STROKE);  // 填充模式 - 描边
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);


        /**
         * 矩形
         */
        //        RectF rectF = new RectF(5, 5, 400, 400);
        //        //第二个参数表示是顺时针还是逆时针,CW表示顺时针,CCW表示逆时针
        //        path.addRect(rectF, Path.Direction.CW);
        //        path.addRect(0, 0, 400, 400, Path.Direction.CCW);
        //        path.setLastPoint(300, 0);

        //        /**
        //         * 圆形
        //         */
        //        path.addCircle(200, 200, 100, Path.Direction.CW); //创建一个圆心坐标为(200,200)，半径为100的圆

        /**
         * 圆角矩形
         */
        //        RectF rectF = new RectF(100, 100, 800, 600);
        //        /**
        //         * addRoundRect(RectF rect, float rx, float ry, Direction dir)
        //         * 四个角的圆并不是正圆,而是椭圆,所以rx,ry为椭圆的两个半径
        //         */
        //        path.addRoundRect(rectF, 150, 150, Path.Direction.CW);    //画一个圆角矩形

        path.reset();
        path.moveTo(0, 0);
        path.lineTo(100, 100);
        canvas.drawColor(0x6F3F51B5);
        canvas.drawPath(path, mPaint);

        canvas.save();
        canvas.drawColor(0x6F03a44e);
        mPaint.setColor(0xFFFFFFFF);  // 画笔颜色 - 黑色
        //canvas.rotate(100, 50, 50);
        canvas.rotate(30, 50, 50);
        //        canvas.drawArc(new RectF(0, 0, 50 * 2, 50 * 2), 90, 90, false, mPaint);
        path.reset();
        path.moveTo(0, 0);
        path.lineTo(100, 100);
        canvas.drawPath(path, mPaint);
        mRectF.top = 0;
        mRectF.left = 0;
        mRectF.right = getWidth();
        mRectF.bottom = getHeight();
        canvas.clipRect(mRectF);
        canvas.restore();
        /**
         * 椭圆
         */
        //        RectF rect = new RectF(100, 100, 800, 500);
        //        path.addOval(rect, Path.Direction.CW);

    }
}