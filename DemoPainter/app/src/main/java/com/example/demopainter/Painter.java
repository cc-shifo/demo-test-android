/*
 *
 * = COPYRIGHT
 *          TianYu
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20200819    	         LiuJian                  Create
 */

package com.whty.smartpos.unionpay.ui.painter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.whty.smartpos.unionpay.R;
import com.whty.smartpos.unionpay.utils.DisplayUtils;
import com.whty.smartpos.unionpay.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;

public class Painter extends View {
    private Paint mPaint;
    private Path mPath;

    private float mStartX;
    private float mStartY;

    /**
     * attribute
     */
    private int mBrushColor;
    private float mBrushWidth;
    /**
     * with and height pixels
     */
    private float mBitMapMaxSize;
    /**
     * pixels
     */
    private float mCropMaxPadding;
    private int mFeatureCodeTextColor;
    private float mFeatureCodeTextSize;

    public Painter(Context context) {
        super(context);
    }

    public Painter(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Painter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Painter(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                   int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Painter);
        mBrushColor = array.getColor(R.styleable.Painter_brushColor, Color.BLUE);
        mBrushWidth = array.getDimension(R.styleable.Painter_brushWidth,
                5.0f);
        mBitMapMaxSize = 384.0f;
        mCropMaxPadding = array.getDimension(R.styleable.Painter_cropMaxPadding,
                15.0f);
        mFeatureCodeTextColor = array.getColor(R.styleable.Painter_featureCodeTextColor,
                Color.GRAY);
        mFeatureCodeTextSize = array.getDimension(R.styleable.Painter_featureCodeTextSize,
                DisplayUtils.sp2px(context, 15.0f));
        array.recycle();
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPath.isEmpty()) {
            canvas.drawColor(Color.WHITE);
        } else {
            canvas.drawPath(mPath, mPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x;
        float y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                mPath.moveTo(mStartX, mStartY);
                break;

            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                mPath.quadTo(mStartX, mStartY, x, y);
                mStartX = x;
                mStartY = y;
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    /**
     * @param bitMapMaxSize In pixel unit. Maximum width and height of signature picture to be
     *                      printed
     */
    public void setBitMapMaxSize(float bitMapMaxSize) {
        mBitMapMaxSize = bitMapMaxSize;
    }

    public void cleanPaint() {
        mPath.reset();
        invalidate();
    }

    public boolean isEmptyPainting() {
        return mPath.isEmpty();
    }

    /**
     * @param withFeatureCode if it's not empty, feature code will be painted.
     * @return A bitmap whose boundary is compose of the touch path and {@link #mCropMaxPadding}
     * boundary. If any errors happen, return a bitmap with an alpha 1x1 pixel size bitmap.
     */
    public Bitmap getCropPainting(String withFeatureCode) {
        int maxW = getWidth();
        int maxH = getHeight();
        if (maxW <= 0 || maxH <= 0) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        }

        float start = 0;
        float top = 0;
        float end = maxW;
        float bottom = maxH;
        if (!mPath.isEmpty()) {
            RectF rect = new RectF();
            mPath.computeBounds(rect, true);
            start = rect.left;
            top = rect.top;
            end = rect.right;
            bottom = rect.bottom;
        }
        start = start - mCropMaxPadding;
        top = top - mCropMaxPadding;
        end = end + mCropMaxPadding;
        bottom = bottom + mCropMaxPadding;

        start = Math.max(start, 0);
        top = Math.max(top, 0);
        end = Math.min(end, maxW);
        bottom = Math.min(bottom, maxH);

        float w = end - start;
        float h = bottom - top;
        //float sizeMax = Math.max(mBitMapMaxSize, Math.max(w, h));
        Bitmap bitmap;
        try {
            float size = Math.max(maxW, maxH);
            Bitmap viewBitmap = Bitmap.createBitmap((int) size, (int) size, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(viewBitmap);
            canvas.drawColor(Color.rgb(0xFF, 0xFF, 0xFF));
            canvas.saveLayer(0, 0, size, size, null);

            this.draw(canvas);
            if (withFeatureCode != null && !withFeatureCode.isEmpty()) {
                float cX = (end - start) / 2 + start;
                float cY = (bottom - top) / 2 + top;
                drawFeatureCode(canvas, withFeatureCode, cX, cY);
            }

            float translateX = (size - maxW) / 2;
            float translateY = 0;
            if (maxW == size) {
                translateX = 0;
                translateY = (size - maxH) / 2;
            }
            canvas.translate(translateX, translateY);
            canvas.restore();

            start += translateX;
            top += translateY;
            Matrix matrix = null;
//            if (w > mBitMapMaxSize || h > mBitMapMaxSize) {
//                matrix = new Matrix();
//                matrix.postScale(w / mBitMapMaxSize, h / mBitMapMaxSize, start + w / 2,
//                        top + h / 2);
//            }
            bitmap = Bitmap.createBitmap(viewBitmap, (int) start, (int) top, (int) w, (int) h,
                    matrix, false);
        } catch (Exception e) {
            LogUtils.e(e);
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        }

        return bitmap;
    }

    public boolean saveImg(Bitmap bitmap) {
        File dir = this.getContext().getExternalCacheDir();
        if (dir == null) {
            LogUtils.d("getExternalCacheDir error");
            return false;
        }

        Bitmap.CompressFormat format = bitmap.getConfig() == Bitmap.Config.ARGB_8888 ?
                Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
        String suffix = bitmap.getConfig() == Bitmap.Config.ARGB_8888 ?
                "123.png" : "123.jpeg";
        String path = dir.getPath() + File.separator + suffix;
        File file = new File(path);
        if (file.exists() && !file.delete()) {
            LogUtils.d("file.delete error");
            return false;
        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            bitmap.compress(format, 100, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        }

        return true;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setFlags(Paint.DITHER_FLAG);
        mPaint.setColor(mBrushColor);
        mPaint.setStrokeWidth(mBrushWidth);

        mPath = new Path();
        mStartX = 0;
        mStartY = 0;
    }

    /**
     * Draw the text, with origin at (x,y), using the specified paint. The origin is interpreted
     * based on the Align setting in the paint.
     *
     * @param text The text to be drawn
     * @param x    The center x-coordinate of the origin of the text being drawn
     * @param y    The center y-coordinate of the origin of the text being drawn
     */
    private void drawFeatureCode(@NonNull Canvas canvas, String text, float x, float y) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(0xFF);
        paint.setDither(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(mFeatureCodeTextSize);
        paint.setColor(mFeatureCodeTextColor);
        Paint.FontMetricsInt metricsInt = paint.getFontMetricsInt();
        float deltaY = (metricsInt.bottom - metricsInt.top) / 2.0f;
        canvas.drawText(text, x, y + deltaY, paint);
    }
}
