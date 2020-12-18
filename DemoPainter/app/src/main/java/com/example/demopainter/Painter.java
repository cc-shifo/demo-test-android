package com.example.demopainter;

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

import java.io.File;
import java.io.FileOutputStream;

@SuppressWarnings("unused")
public class Painter extends View {
    private Paint mPaint;
    private Path mPath;
    private float mStartX;
    private float mStartY;
    private int mWidth;
    private int mHeight;

    /**
     * attribute
     */
    private int mBrushColor;
    private float mBrushWidth;

    /**
     * pixels
     */
    private float mCropPadding;
    private int mWatermarkTextColor;
    private float mWatermarkTextSize;
    private String mWatermark;

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
                DisplayUtils.dp2px(context, 5.0f));
        mCropPadding = array.getDimension(R.styleable.Painter_cropPadding,
                DisplayUtils.dp2px(context, 15.0f));
        mWatermarkTextColor = array.getColor(R.styleable.Painter_watermarkTextColor,
                Color.GRAY);
        mWatermarkTextSize = array.getDimension(R.styleable.Painter_watermarkTextSize,
                DisplayUtils.sp2px(context, 15.0f));
        array.recycle();
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
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
                LogUtils.d("ACTION_UP");
                break;

            case MotionEvent.ACTION_DOWN:
                LogUtils.d("ACTION_DOWN");
                mStartX = event.getX();
                mStartY = event.getY();
                mPath.moveTo(mStartX, mStartY);
                break;

            case MotionEvent.ACTION_MOVE:
                LogUtils.d("ACTION_MOVE");
                x = event.getX();
                y = event.getY();
                if (x < 0 || x > mWidth || y < 0 || y > mHeight) {
                    return false;
                }
                mPath.quadTo(mStartX, mStartY, x, y);
                mStartX = x;
                mStartY = y;
                break;
            default:
                return false;
        }

        RectF rectF = new RectF();
        mPath.computeBounds(rectF, true);
        LogUtils.d("l: " + rectF.left + ", r: " + rectF.right
                + ", t: " + rectF.top + ", b: " + rectF.bottom);
        LogUtils.d("ex: " + event.getX() + ", " + "ey: " + event.getY());
        LogUtils.d("eRx: " + event.getRawX() + ", " + "eRy: " + event.getRawY());

        //invalidate();//move事件比较频繁，改post绘制。
        postInvalidate();
        return true;
    }

    public void cleanPaint() {
        mPath.reset();
        invalidate();
    }

    public boolean isEmptyPainting() {
        return mPath.isEmpty();
    }

    /**
     * @param watermark if it's not empty, watermark will be painted.
     */
    public void setWatermark(String watermark) {
        mWatermark = watermark;
    }

    /**
     * @param brushColor rgb see {@link Color#rgb(int, int, int)}
     */
    public void setBrushColor(int brushColor) {
        mBrushColor = brushColor;
        mPaint.setColor(mBrushColor);
    }

    /**
     * @param brushWidth with in pixels
     */
    public void setBrushWidth(float brushWidth) {
        mBrushWidth = brushWidth;
        mPaint.setStrokeWidth(mBrushWidth);
    }

    /**
     * @param cropPadding with in pixels
     */
    public void setCropPadding(float cropPadding) {
        mCropPadding = cropPadding;
    }

    /**
     * @param watermarkTextColor rgb see {@link Color#rgb(int, int, int)}
     */
    public void setWatermarkTextColor(int watermarkTextColor) {
        mWatermarkTextColor = watermarkTextColor;
    }

    /**
     * @param watermarkTextSize with in sp
     */
    public void setWatermarkTextSize(float watermarkTextSize) {
        mWatermarkTextSize = DisplayUtils.sp2px(getContext(), watermarkTextSize);
    }

    /**
     * @return null if any error happens, otherwise return bitmap for this view. Do not forget to
     * recycle this bitmap by calling {@link Bitmap#recycle()}.
     */
    public Bitmap getViewBitmapWithWaterMark() {
        return getViewBitmap(mWatermark);
    }
    /**
     * @return null if any error happens, otherwise return bitmap for this view. Do not forget to
     * recycle this bitmap by calling {@link Bitmap#recycle()}.
     */
    public Bitmap getViewBitmap() {
        return getViewBitmap(null);
    }

    public Bitmap getPathBitmapWithWatermark() {
       return getPathBitmap(mWatermark);
    }

    public Bitmap getPathBitmap() {
        return getPathBitmap(null);
    }

    /**
     * @param width  In pixel unit. Width of signature picture to be printed
     * @param height In pixel unit. Height of signature picture to be printed
     * @return A bitmap whose boundary is compose of the touch path and {@link #mCropPadding}
     * boundary. If any errors happen, return a bitmap with an alpha 1x1 pixel size bitmap.
     */
    public Bitmap getCropPaintingWithWaterMark(int width, int height) {
        return getCropPainting(width, height, mWatermark);
    }
    /**
     * @param width  In pixel unit. Width of signature picture to be printed
     * @param height In pixel unit. Height of signature picture to be printed
     * @return A bitmap whose boundary is compose of the touch path and {@link #mCropPadding}
     * boundary. If any errors happen, return a bitmap with an alpha 1x1 pixel size bitmap.
     */
    public Bitmap getCropPainting(int width, int height) {
        return getCropPainting(width, height, null);
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
        mPath.setFillType(Path.FillType.WINDING);
        mStartX = 0;
        mStartY = 0;
    }

    /**
     * @return null if any error happens, otherwise return bitmap for this view. Do not forget to
     * recycle this bitmap by calling {@link Bitmap#recycle()}.
     */
    private Bitmap getViewBitmap(String watermark) {
        int maxW = getWidth();
        int maxH = getHeight();
        if (maxW <= 0 || maxH <= 0) {
            return null;
        }

        Bitmap viewBitmap;
        try {
            viewBitmap = Bitmap.createBitmap(maxW, maxH, Bitmap.Config.RGB_565);
        } catch (Exception e) {
            LogUtils.e(e);
            return null;
        }

        Canvas viewCanvas = new Canvas(viewBitmap);
        this.draw(viewCanvas);
        if (watermark != null && !watermark.isEmpty()) {
            float cX = maxW / 2.0f;
            float cY = maxH / 2.0f;
            drawWatermark(viewCanvas, watermark, cX, cY);
        }
        return viewBitmap;
    }

    private Bitmap getPathBitmap(String watermark) {
        Bitmap bitmap = getViewBitmap(null);
        if (bitmap == null) {
            return null;
        }

        float maxW = bitmap.getWidth();
        float maxH = bitmap.getHeight();
        float start = 0;
        float top = 0;
        float end = maxW;
        float bottom = maxH;
        if (!mPath.isEmpty()) {
            //mPath.close(); //因此闭合，图片起点到终点会连线。
            RectF rect = new RectF();
            mPath.computeBounds(rect, true);
            start = rect.left;
            top = rect.top;
            end = rect.right;
            bottom = rect.bottom;
        }

        // end == start, top == bottom
        if (start == end) {
            start = start - mBrushWidth / 2;
            end = end + mBrushWidth / 2;
        }
        if (top == bottom) {
            top = top - mBrushWidth / 2;
            bottom = bottom + mBrushWidth / 2;
        }

        start = Math.max(start, 0);
        top = Math.max(top, 0);
        end = Math.min(end, maxW);
        bottom = Math.min(bottom, maxH);
        float w = end - start;
        float h = bottom - top;
        Bitmap bitmapPath;
        try {
            bitmapPath = Bitmap.createBitmap(bitmap, (int) start, (int) top, (int) w,
                    (int) h);
        } catch (Exception e) {
            LogUtils.e(e);
            bitmap.recycle();
            return null;
        }

        bitmap.recycle();
        Canvas canvas = new Canvas(bitmapPath);
        if (watermark != null && !watermark.isEmpty()) {
            float cX = bitmapPath.getWidth() / 2.0f;
            float cY = bitmapPath.getHeight() / 2.0f;
            drawWatermark(canvas, watermark, cX, cY);
        }

        return bitmapPath;
    }

    /**
     * Draw the text, with origin at (x,y), using the specified paint. The origin is interpreted
     * based on the Align setting in the paint.
     *
     * @param text The text to be drawn
     * @param x    The center x-coordinate of the origin of the text being drawn
     * @param y    The center y-coordinate of the origin of the text being drawn
     */
    private void drawWatermark(@NonNull Canvas canvas, String text, float x, float y) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(0xFF);
        paint.setDither(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(mWatermarkTextSize);
        paint.setColor(mWatermarkTextColor);
        Paint.FontMetricsInt metricsInt = paint.getFontMetricsInt();
        float deltaY = (metricsInt.bottom - metricsInt.top) / 2.0f;
        canvas.drawText(text, x, y + deltaY, paint);
    }

    /**
     * @param width  In pixel unit. Width of signature picture to be printed
     * @param height In pixel unit. Height of signature picture to be printed
     * @return A bitmap whose boundary is compose of the touch path and {@link #mCropPadding}
     * boundary. If any errors happen, return a bitmap with an alpha 1x1 pixel size bitmap.
     */
    private Bitmap getCropPainting(int width, int height, String watermark) {
        if (width <= 0 || height <= 0) {
            return null;
        }

        Bitmap pathBitmap = getPathBitmap();
        if (pathBitmap == null) {
            return null;
        }

        Matrix matrix = calculateMatrix(pathBitmap.getWidth(), pathBitmap.getHeight(), width,
                height);
        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        } catch (Exception e) {
            LogUtils.e(e);
            return null;
        }
        Canvas crop = new Canvas(bitmap);
        crop.drawColor(Color.rgb(0xFF, 0xFF, 0xFF));
        crop.drawBitmap(pathBitmap, matrix, null);
        pathBitmap.recycle();
        if (watermark != null && !watermark.isEmpty()) {
            float cX = width / 2.0f;
            float cY = height / 2.0f;
            drawWatermark(crop, watermark, cX, cY);
        }

        return bitmap;
    }

    /**
     * @param pW     width of path bitmap
     * @param pH     height of path bitmap
     * @param width  width of final target bitmap
     * @param height height of final target bitmap
     * @return matrix
     */
    private Matrix calculateMatrix(int pW, int pH, int width, int height) {
        int deltaW = width - (int) (2 * mCropPadding);
        int deltaH = height - (int) (2 * mCropPadding);
        if (deltaW <= 0) {
            deltaW = width;
        }
        if (deltaH <= 0) {
            deltaH = height;
        }

        Matrix matrix = new Matrix();
        float translateX;
        float translateY;
        if (deltaW < pW || deltaH < pH) {
            float sx = deltaW * 1.0f / pW;
            float sy = deltaH * 1.0f / pH;
            //整体缩变
            float scaledFactor = Math.min(sx, sy);
            matrix.postScale(scaledFactor, scaledFactor);
            translateX = (width - (pW * scaledFactor)) / 2.0f - 0.5f;
            translateY = (height - (pH * scaledFactor)) / 2.0f - 0.5f;
        } else {
            translateX = (width - pW) / 2.0f;
            translateY = (height - pH) / 2.0f;
        }
        matrix.postTranslate(translateX, translateY);

        // 算法有误差
        /*if (deltaW < pW || deltaH < pH) {
            float sx;
            float sy;
            if (pW > deltaW) {
                sx = (deltaW * 1.0f) / pW;
                sy = sx;
                float h = pH * sx + 0.5f;
                if (Double.compare(h, deltaH) >= 0) {
                    sy = (deltaH * pW * 1.0f) / (pH * deltaW);
                    sx = (deltaH * 1.0f) / pH;
                }
            } else {
                // pH > deltaH
                sy = (deltaH * 1.0f) / pH;
                sx = sy;
                float w = pW * sy + 0.5f;
                if (Double.compare(w, deltaW) >= 0) {
                    sx = (deltaW * pH * 1.0f) / (pW * deltaH);
                    sy  = (deltaW * 1.0f) / pW;
                }
            }
            matrix.postScale(sx, sy);
            translateX = (width - (pW * sx)) / 2.0f - 0.5f;
            translateY = (height - (pH * sy)) / 2.0f - 0.5f;
        } else {
            translateX = (width - pW) / 2.0f;
            translateY = (height - pH) / 2.0f;
        }
        matrix.postTranslate(translateX, translateY);*/

        return matrix;
    }
}
