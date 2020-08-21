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
                break;

            case MotionEvent.ACTION_DOWN:

                mStartX = event.getX();
                mStartY = event.getY();
                mPath.moveTo(mStartX, mStartY);
                break;

            case MotionEvent.ACTION_MOVE:
                LogUtils.d("ACTION_MOVE");
                x = event.getX();
                y = event.getY();
                x = Math.min(mWidth, Math.max(x, 0));
                y = Math.min(mHeight, Math.max(y, 0));
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
        invalidate();
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
     * @return null if any error happens, otherwise return bitmap for this view. Do not forget to
     * recycle this bitmap by calling {@link Bitmap#recycle()}.
     */
    public Bitmap getViewBitmap() {
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
        if (mWatermark != null && !mWatermark.isEmpty()) {
            float cX = maxW / 2.0f;
            float cY = maxH / 2.0f;
            drawWatermark(viewCanvas, mWatermark, cX, cY);
        }
        return viewBitmap;
    }

    public Bitmap getPathBitmap() {
        Bitmap bitmap = getViewBitmap();
        if (bitmap == null) {
            return null;
        }

        float maxW = bitmap.getWidth();
        float maxH = bitmap.getHeight();
        float start = 0;
        float top = 0;
        float end = maxW;
        float bottom = maxH;
        mPath.close();
        if (!mPath.isEmpty()) {
            RectF rect = new RectF();
            mPath.computeBounds(rect, true);
            start = rect.left;
            top = rect.top;
            end = rect.right;
            bottom = rect.bottom;
        }

        // end == start, top == bottom
        float w = end - start;
        float h = bottom - top;
//        if (Double.compare(w, 1.0f) < 0) {
//            w = mBrushWidth;
//        }
//        if (Double.compare(h, 1.0f) < 0) {
//            h = mBrushWidth;
//        }

        Bitmap bitmapPath;
        try {
            bitmapPath = Bitmap.createBitmap(bitmap, (int) start, (int) top, (int)w,
                    (int)h);
        } catch (Exception e) {
            LogUtils.e(e);
            return null;
        }

        bitmap.recycle();
        return bitmapPath;
    }

    /**
     * @param width  In pixel unit. Width of signature picture to be printed
     * @param height In pixel unit. Height of signature picture to be printed
     * @return A bitmap whose boundary is compose of the touch path and {@link #mCropPadding}
     * boundary. If any errors happen, return a bitmap with an alpha 1x1 pixel size bitmap.
     */
    public Bitmap getCropPainting(int width, int height) {
        Bitmap pathBitmap = getPathBitmap();
        if (pathBitmap == null) {
            return null;
        }
        return pathBitmap;

        /*float start = 0;
        float top = 0;
        float end = pathBitmap.getWidth();
        float bottom = pathBitmap.getHeight();
        start = start - mCropPadding;
        top = top - mCropPadding;
        end = end + mCropPadding;
        bottom = bottom + mCropPadding;

        start = Math.max(start, 0);
        top = Math.max(top, 0);
        end = Math.min(end, maxW);
        bottom = Math.min(bottom, maxH);*/

        //        int pW = pathBitmap.getWidth() - (int) (2 * mCropPadding);
        //        int pH = pathBitmap.getHeight() - (int) (2 * mCropPadding);
        //        Matrix matrix = new Matrix();
        //        float sx = 1.0f;
        //        float sy = 1.0f;
        //        if (pW > width) {
        //            sx = (width * 1.0f) / pW;
        //            sy = sx;
        //            float h = pH * sx + 0.5f;
        //            if (Double.compare(h, height) >= 0) {
        //                sy = height / h;
        //                sy *= sx;
        //            }
        //        } else if (pH > height) {
        //            sy = (height * 1.0f) / pH;
        //            sx = sy;
        //            float w = pW * sy + 0.5f;
        //            if (Double.compare(w, width) >= 0) {
        //                sx = width / w;
        //                sx *= sy;
        //            }
        //        }
        //
        //        float srcCx = pathBitmap.getWidth() / 2.0f;
        //        float srcCY = pathBitmap.getHeight() / 2.0f;
        //        matrix.postScale(sx, sy, srcCx, srcCY);
        //        matrix.postTranslate(mCropPadding, mCropPadding);
        //        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        //        Canvas crop = new Canvas(bitmap);
        //        crop.drawColor(Color.rgb(0xFF, 0xFF, 0xFF));
        //        crop.drawBitmap(bitmap, matrix, null);
        //
        //        return bitmap;
    }

    /**
     * @return A bitmap whose boundary is compose of the touch path and {@link #mCropPadding}
     * boundary. If any errors happen, return a bitmap with an alpha 1x1 pixel size bitmap.
     */
    public Bitmap getCropPainting() {
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
        start = start - mCropPadding;
        top = top - mCropPadding;
        end = end + mCropPadding;
        bottom = bottom + mCropPadding;

        start = Math.max(start, 0);
        top = Math.max(top, 0);
        end = Math.min(end, maxW);
        bottom = Math.min(bottom, maxH);

        float w = end - start;
        float h = bottom - top;
        Bitmap bitmap;
        try {
            Bitmap viewBitmap = Bitmap.createBitmap(maxW, maxH, Bitmap.Config.RGB_565);
            Canvas viewCanvas = new Canvas(viewBitmap);
            this.draw(viewCanvas);
            if (mWatermark != null && !mWatermark.isEmpty()) {
                float cX = (end - start) / 2 + start;
                float cY = (bottom - top) / 2 + top;
                drawWatermark(viewCanvas, mWatermark, cX, cY);
            }

            float size = Math.max(maxW, maxH);
            float translateX = (size - maxW) / 2;
            float translateY = 0;
            if (maxW == size) {
                translateX = 0;
                translateY = (size - maxH) / 2;
            }
            bitmap = Bitmap.createBitmap((int) size, (int) size, viewBitmap.getConfig());
            Canvas canvas = new Canvas(bitmap);
            Matrix matrix = new Matrix();
            matrix.postTranslate(translateX, translateY);
            canvas.drawColor(Color.rgb(0xFF, 0xFF, 0xFF));
            canvas.drawBitmap(viewBitmap, matrix, null);
            //            canvas.translate(200, translateY);
            //canvas.restore();

            //Matrix matrix = new Matrix();
            //matrix.postTranslate(200, 0);
            //bitmap = Bitmap.createBitmap(viewBitmap, 0, 0, viewBitmap.getWidth(),
            //        viewBitmap.getHeight(), matrix, false);;
            //start += translateX;
            //top += translateY;
            //Matrix matrix = null;
            //            if (w > mBitMapMaxSize || h > mBitMapMaxSize) {
            //                matrix = new Matrix();
            //                matrix.postScale(w / mBitMapMaxSize, h / mBitMapMaxSize, start + w
            //                / 2,
            //                        top + h / 2);
            //            }
            //bitmap = Bitmap.createBitmap(viewBitmap, (int) start, (int) top, (int) w, (int) h,
            //matrix, false);
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
        mPath.setFillType(Path.FillType.WINDING);
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
}
