package com.example.demowarningbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

@SuppressLint("ViewConstructor")
public class PopoverView extends FrameLayout {
    private int popoverBackgroundColor;
    private int arrowColor;
    private float borderRadius;
    private boolean showArrow;
    private float arrowOffset;
    private ArrowPosition arrowPosition;

    // 箭头旋转的角度
    private float arrowAngle = 270F;

    private ImageView arrowImageView;
    private CardView contentLayout;

    // 阴影
    private Paint shadowPaint;
    private float shadowDx;
    private float shadowDy;
    private RectF shadowRectF;
    private boolean enableDropShadow;

    public PopoverView(@NonNull Context context) {
        this(context, Color.parseColor("#FFCC00"), Color.parseColor("#FFCC00"),
                0, true, 0.5f, ArrowPosition.TOP);
    }

    public PopoverView(@NonNull Context context, int backgroundColor, int arrowColor,
            float borderRadius, boolean showArrow, float arrowOffset, ArrowPosition arrowPosition) {
        super(context);
        this.popoverBackgroundColor = backgroundColor;
        this.arrowColor = arrowColor;
        this.borderRadius = borderRadius;
        this.showArrow = showArrow;
        this.arrowOffset = arrowOffset;
        this.arrowPosition = arrowPosition;
        initParam();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // post(this::updateArrow);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!enableDropShadow) {
            return;
        }
        shadowRectF.set(contentLayout.getLeft() + shadowDx,
                contentLayout.getTop() + shadowDy,
                contentLayout.getRight() + shadowDx,
                contentLayout.getBottom() + shadowDy);
        canvas.save();
        float r = contentLayout.getRadius();
        canvas.drawRoundRect(shadowRectF, r, r, shadowPaint);
        canvas.restore();
    }

    public PopoverView setContentView(@NonNull View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
        this.contentLayout.addView(view);
        return this;
    }

    public PopoverView setContentView(@NonNull View view,
            @NonNull ViewGroup.LayoutParams layoutParams) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
        this.contentLayout.addView(view, layoutParams);
        // this.contentLayout.addView(view);
        return this;
    }

    public PopoverView setArrowOffset(float arrowOffset) {
        this.arrowOffset = arrowOffset;
        updateArrow();
        return this;
    }

    public void setDropShadow(float blurRadius, float dx, float dy, int color) {
        // Android 10以下系统不能用硬件加速，否则BlurMaskFilter不生效
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        enableDropShadow = true;
        shadowDx = dx;
        shadowDy = dy;
        BlurMaskFilter blurMaskFilter = new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL);

        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(color);
        shadowPaint.setMaskFilter(blurMaskFilter);


        int padding = (int) (blurRadius * 2);
        setPadding(padding, padding, padding, padding);
        setWillNotDraw(false);
        postInvalidate();
    }

    // 箭头位置
    enum ArrowPosition {
        TOP,
        RIGHT,
        BOTTOM,
        LEFT
    }

    private void initParam() {
        shadowPaint = new Paint();
        shadowRectF = new RectF();

        if (arrowPosition == ArrowPosition.TOP) {
            arrowAngle = 270f;
        } else if (arrowPosition == ArrowPosition.RIGHT) {
            arrowAngle = 0f;
        } else if (arrowPosition == ArrowPosition.BOTTOM) {
            arrowAngle = 90f;
        } else {
            arrowAngle = 180f;
        }

        initViews();
    }

    private void initViews() {
        contentLayout = new CardView(getContext());
        contentLayout.setRadius(borderRadius);
        contentLayout.setCardElevation(0);
        contentLayout.setClickable(true);
        contentLayout.setId(View.generateViewId());
        contentLayout.setCardBackgroundColor(popoverBackgroundColor);

        FrameLayout.LayoutParams contentLayoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        arrowImageView = new ImageView(getContext());
        arrowImageView.setId(View.generateViewId());
        arrowImageView.setImageDrawable(getArrowDrawable());

        FrameLayout.LayoutParams arrowLayoutParams = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        arrowImageView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        if (showArrow) {
            if (arrowPosition == ArrowPosition.TOP) {
                arrowLayoutParams.gravity = Gravity.TOP;
                contentLayoutParams.topMargin = arrowImageView.getMeasuredHeight();
            } else if (arrowPosition == ArrowPosition.RIGHT) {
                arrowLayoutParams.gravity = Gravity.END;
                contentLayoutParams.rightMargin = arrowImageView.getMeasuredWidth();
            } else if (arrowPosition == ArrowPosition.BOTTOM) {
                arrowLayoutParams.gravity = Gravity.BOTTOM;
                contentLayoutParams.bottomMargin = arrowImageView.getMeasuredHeight();
            } else {
                arrowLayoutParams.gravity = Gravity.START;
                contentLayoutParams.leftMargin = arrowImageView.getMeasuredWidth();
            }
        }

        addView(arrowImageView, arrowLayoutParams);
        addView(contentLayout, contentLayoutParams);

        post(this::updateArrow);
    }

    private Drawable getArrowDrawable() {
        int arrowRes = R.drawable.uxsdk_ic_themedark_popover_arrow_left;
        Bitmap source = BitmapFactory.decodeResource(this.getResources(), arrowRes);

        Bitmap rotateBitmap = rotateBitmap(source, arrowAngle);
        return new TintedBitmapDrawable(getResources(), rotateBitmap, arrowColor);
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    private void updateArrow() {
        if (!showArrow) {
            arrowImageView.setVisibility(GONE);
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)
                arrowImageView.getLayoutParams();
        if (arrowPosition == ArrowPosition.TOP || arrowPosition == ArrowPosition.BOTTOM) {
            float x = contentLayout.getWidth() * arrowOffset - arrowImageView.getWidth() / 2f;
            layoutParams.leftMargin = (int) x;
        } else {
            float y = contentLayout.getHeight() * arrowOffset - arrowImageView.getHeight() / 2f;
            layoutParams.topMargin = (int) y;
        }
        arrowImageView.setLayoutParams(layoutParams);
    }

}
