package com.example.demowarningbar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class Popover {

    private Builder mBuilder;


    private int mWidth;
    private int mHeight;

    private PopupWindow mPopupWindow;
    private PopoverView mPopoverView;
    private PopupWindow.OnDismissListener mDismissListener;

    // 是否是文本内容显示
    private boolean mIsTextContent;
    private Runnable mRequestLayoutRunnable;

    public Popover(@NonNull Popover.Builder builder) {
        this.mBuilder = builder;
        mPopupWindow = new PopupWindow();
        initParam();
    }

    public void showTest1(@NonNull ViewGroup parent) {
        mPopoverView = createPopoverView();
        // configShadow(mPopoverView);
        parent.removeView(mPopoverView);
        parent.addView(mPopoverView);
    }

    public void showTest2(@NonNull ViewGroup parent) {

        int[] xy = createPopupWindow(mBuilder.customView);
        // mPopupWindow.showAsDropDown(parent);
        mPopupWindow.showAtLocation(parent,
                Gravity.NO_GRAVITY, xy[0], xy[1]);
    }

    public void show() {
        int[] popupWindowLocation = initPopover();
        mPopupWindow.showAtLocation(mBuilder.anchor,
                Gravity.NO_GRAVITY,
                popupWindowLocation[0] + mBuilder.xOffset,
                popupWindowLocation[1] + mBuilder.yOffset);
    }

    public void show(int gravity, int x, int y) {
        initPopover();
        mPopupWindow.showAtLocation(mBuilder.anchor, gravity, x, y);
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    @Nullable
    public View getContentView() {
        return mPopupWindow.getContentView();
    }

    public void setOnDismissListener(@Nullable PopupWindow.OnDismissListener dismissListener) {
        mDismissListener = dismissListener;
    }

    public void requestLayout() {
        if (mPopoverView != null) {
            mPopoverView.post(mRequestLayoutRunnable);
        }
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    private void initParam() {
        mRequestLayoutRunnable = () -> {
            // 只有处理自定义view的时候才进行下面的重新
            if (!mIsTextContent) {
                int[] newLocation = configPopupWindow(mPopoverView);
                int h;
                View root = mPopupWindow.getContentView().getRootView();
                if (root.getHeight() > mPopoverView.getMeasuredHeight()) {
                    h = mPopoverView.getMeasuredHeight();
                } else {
                    h = mPopupWindow.getHeight();
                }

                int w;
                if (root.getLayoutParams().width > mPopoverView.getMeasuredWidth()) {
                    w = mPopoverView.getMeasuredWidth();
                } else {
                    w = mPopupWindow.getWidth();
                }
                mPopupWindow.update(newLocation[0] + mBuilder.xOffset,
                        newLocation[1] + mBuilder.yOffset, w, h);
            }
        };
        mPopupWindow.setOnDismissListener(() -> {
            if (mPopoverView != null) {
                mPopoverView.removeCallbacks(mRequestLayoutRunnable);
            }
            if (mDismissListener != null) {
                mDismissListener.onDismiss();
            }
        });
    }

    private int[] initPopover() {
        mPopoverView = createPopoverView();
        configShadow(mPopoverView);
        int[] popupWindowLocation = configPopupWindow(mPopoverView);
        mWidth = mPopoverView.getMeasuredWidth();
        mHeight = mPopoverView.getMeasuredHeight();

        mPopoverView.addOnLayoutChangeListener(
                (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    int newWidth = right - left;
                    int newHeight = bottom - top;
                    if (newWidth != mWidth || newHeight != mHeight) {
                        mWidth = newWidth;
                        mHeight = newHeight;
                        requestLayout();
                    }
                });



        if (mPopupWindow.isFocusable()) {
            View cV = mPopupWindow.getContentView();
            if (cV != null) {
                cV.setFocusable(true);
                cV.setFocusableInTouchMode(true);
                cV.requestFocus();
                cV.setOnKeyListener(new DispatchViewKeyEventToActivityListener());
            }
        }

        return popupWindowLocation;
    }

    private int[] createPopupWindow(@NonNull View contentView) {
        mPopupWindow.setContentView(contentView);
        mPopupWindow.setWidth(200);
        mPopupWindow.setHeight(200);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 允许popup window超过屏幕
        mPopupWindow.setClippingEnabled(false);


        int anchorWidth = mBuilder.anchor.getWidth();
        int anchorHeight = mBuilder.anchor.getHeight();
        int screenWidth = DisplayUtil.getLandScreenSize(mBuilder.anchor.getContext()).getWidth();
        int screenHeight = DisplayUtil.getLandScreenSize(mBuilder.anchor.getContext()).getHeight();

        int[] anchorViewLocationInScreen = new int[2];
        mBuilder.anchor.getLocationOnScreen(anchorViewLocationInScreen);

        int[] location = new int[2];
        location[0] = anchorViewLocationInScreen[0];
        location[1] = anchorViewLocationInScreen[1];
        return location;
    }

    int[] calculateShowAtLocation(int[] anchorViewLocationInScreen, int popoverWidth,
            int popoverHeight, int anchorWidth, int anchorHeight, PopoverView view) {
        int[] location = new int[2];
        switch (mBuilder.position) {
            case LEFT: {
                location[0] = anchorViewLocationInScreen[0] - popoverWidth +
                        view.getPaddingStart();
                switch (mBuilder.align) {
                    case TOP:
                        location[1] = anchorViewLocationInScreen[1] - view.getPaddingTop();
                        break;
                    case BOTTOM:
                        location[1] = anchorViewLocationInScreen[1] + anchorHeight - popoverHeight
                                + view.getPaddingTop();
                        break;
                    default:
                        location[1] = anchorViewLocationInScreen[1] + anchorHeight / 2
                                - popoverHeight / 2;
                        break;
                }
            }
            break;
            case RIGHT:
                location[0] = anchorViewLocationInScreen[0] + anchorWidth -
                        view.getPaddingStart();
                switch (mBuilder.align) {
                    case TOP:
                        location[1] = anchorViewLocationInScreen[1] - view.getPaddingTop();
                        break;
                    case BOTTOM:
                        location[1] = anchorViewLocationInScreen[1] + anchorHeight - popoverHeight
                                + view.getPaddingTop();
                        break;
                    default:
                        location[1] = anchorViewLocationInScreen[1] + anchorHeight / 2
                                - popoverHeight / 2;
                        break;
                }
                break;

            case TOP:
                location[1] = anchorViewLocationInScreen[1] - popoverHeight + view
                        .getPaddingTop();
                switch (mBuilder.align) {
                    case LEFT:
                        location[0] = anchorViewLocationInScreen[0] - view.getPaddingStart();
                        break;
                    case RIGHT:
                        location[0] = anchorViewLocationInScreen[0] + anchorWidth - popoverWidth
                                + view.getPaddingStart();
                        break;
                    default:
                        location[0] = anchorViewLocationInScreen[0] + anchorWidth / 2
                                - popoverWidth / 2;
                        break;
                }
                break;
            case BOTTOM:
                location[1] = anchorViewLocationInScreen[1] + anchorHeight - view
                        .getPaddingTop();
                switch (mBuilder.align) {
                    case LEFT:
                        location[0] = anchorViewLocationInScreen[0] - view.getPaddingStart();
                        break;
                    case RIGHT:
                        location[0] = anchorViewLocationInScreen[0] + anchorWidth - popoverWidth
                                + view.getPaddingStart();
                        break;
                    default:
                        location[0] = anchorViewLocationInScreen[0] + anchorWidth / 2
                                - popoverWidth / 2;
                        break;
                }
                break;
            default:
                break;
        }

        return location;
    }

    /**
     * 配置PopupWindow
     */
    @NonNull
    private int[] configPopupWindow(PopoverView popoverView) {
        mPopupWindow.setContentView(popoverView);
        mPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(mBuilder.focusable);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 允许popup window超过屏幕
        mPopupWindow.setClippingEnabled(false);
        mDismissListener = mBuilder.dismissListener;

        popoverView.setClickable(true);
        popoverView.setOnClickListener(v -> dismiss());
        int[] anchorViewLocationInScreen = new int[2];
        mBuilder.anchor.getLocationOnScreen(anchorViewLocationInScreen);

        mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED);
        int popoverWidth = mPopupWindow.getContentView().getMeasuredWidth();
        int popoverHeight = mPopupWindow.getContentView().getMeasuredHeight();
        if (mIsTextContent) {
            mPopupWindow.setWidth(popoverWidth);
            mPopupWindow.setHeight(popoverHeight);
        }
        int anchorWidth = mBuilder.anchor.getWidth();
        int anchorHeight = mBuilder.anchor.getHeight();
        int screenWidth = DisplayUtil.getLandScreenSize(mBuilder.anchor.getContext()).getWidth();
        int screenHeight = DisplayUtil.getLandScreenSize(mBuilder.anchor.getContext()).getHeight();

        int[] popupWindowLocation = getShowLocation(anchorViewLocationInScreen, popoverWidth,
                popoverHeight, anchorWidth, anchorHeight, popoverView);
        adjustPopupWindowLayoutParams(popupWindowLocation, popoverWidth, popoverHeight,
                screenWidth, screenHeight, popoverView);

        float arrowPosition = (mBuilder.position == Position.BOTTOM ||
                mBuilder.position == Position.TOP) ?
                (anchorViewLocationInScreen[0] + anchorWidth / 2f - popupWindowLocation[0]
                        - popoverView.getPaddingStart()) /
                        (popoverWidth - popoverView.getPaddingStart() * 2)
                : (anchorViewLocationInScreen[1] + anchorHeight / 2f - popupWindowLocation[1]
                - popoverView.getPaddingTop()) / (popoverHeight - popoverView.getPaddingTop() * 2);

        popoverView.setArrowOffset(arrowPosition);

        return popupWindowLocation;
    }

    /**
     * 获取popupwindow显示坐标
     */
    private int[] getShowLocation(int[] anchorViewLocationInScreen, int popoverWidth,
            int popoverHeight, int anchorWidth, int anchorHeight, PopoverView view) {
        int[] location = new int[2];
        switch (mBuilder.position) {
            case LEFT: {
                location[0] = anchorViewLocationInScreen[0] - popoverWidth +
                        view.getPaddingStart();
                switch (mBuilder.align) {
                    case TOP:
                        location[1] = anchorViewLocationInScreen[1] - view.getPaddingTop();
                        break;
                    case BOTTOM:
                        location[1] = anchorViewLocationInScreen[1] + anchorHeight - popoverHeight
                                + view.getPaddingTop();
                        break;
                    default:
                        location[1] = anchorViewLocationInScreen[1] + anchorHeight / 2
                                - popoverHeight / 2;
                        break;
                }
            }
            break;
            case RIGHT:
                location[0] = anchorViewLocationInScreen[0] + anchorWidth -
                        view.getPaddingStart();
                switch (mBuilder.align) {
                    case TOP:
                        location[1] = anchorViewLocationInScreen[1] - view.getPaddingTop();
                        break;
                    case BOTTOM:
                        location[1] = anchorViewLocationInScreen[1] + anchorHeight - popoverHeight
                                + view.getPaddingTop();
                        break;
                    default:
                        location[1] = anchorViewLocationInScreen[1] + anchorHeight / 2
                                - popoverHeight / 2;
                        break;
                }
                break;

            case TOP:
                location[1] = anchorViewLocationInScreen[1] - popoverHeight + view
                        .getPaddingTop();
                switch (mBuilder.align) {
                    case LEFT:
                        location[0] = anchorViewLocationInScreen[0] - view.getPaddingStart();
                        break;
                    case RIGHT:
                        location[0] = anchorViewLocationInScreen[0] + anchorWidth - popoverWidth
                                + view.getPaddingStart();
                        break;
                    default:
                        location[0] = anchorViewLocationInScreen[0] + anchorWidth / 2
                                - popoverWidth / 2;
                        break;
                }
                break;
            case BOTTOM:
                location[1] = anchorViewLocationInScreen[1] + anchorHeight - view
                        .getPaddingTop();
                switch (mBuilder.align) {
                    case LEFT:
                        location[0] = anchorViewLocationInScreen[0] - view.getPaddingStart();
                        break;
                    case RIGHT:
                        location[0] = anchorViewLocationInScreen[0] + anchorWidth - popoverWidth
                                + view.getPaddingStart();
                        break;
                    default:
                        location[0] = anchorViewLocationInScreen[0] + anchorWidth / 2
                                - popoverWidth / 2;
                        break;
                }
                break;
            default:
                break;
        }

        return location;
    }


    /**
     * 防止popup window显示超出屏幕
     */
    private void adjustPopupWindowLayoutParams(int[] location, int popoverWidth, int popoverHeight,
            int screenWidth, int screenHeight, PopoverView popoverView) {
        if (location[0] + popoverWidth - popoverView.getPaddingStart() >
                screenWidth - mBuilder.rightScreenMargin) {
            location[0] = screenWidth - popoverWidth + popoverView.getPaddingStart()
                    - mBuilder.rightScreenMargin;
        }

        if (location[0] < mBuilder.leftScreenMargin) {
            location[0] = -popoverView.getPaddingStart() + mBuilder.leftScreenMargin;
        }


        if (mBuilder.position != Position.TOP && mBuilder.position != Position.BOTTOM) {
            if (location[1] + popoverHeight > screenHeight - mBuilder.bottomScreenMargin) {
                location[1] = screenHeight - popoverHeight + popoverView.getPaddingBottom()
                        - mBuilder.bottomScreenMargin;
            }

            if (location[1] < mBuilder.topScreenMargin) {
                location[1] = -popoverView.getPaddingTop() + mBuilder.topScreenMargin;
            }
        } else {
            if (mBuilder.position == Position.BOTTOM &&
                    location[1] + popoverHeight > screenHeight - mBuilder.bottomScreenMargin) {
                // 向下弹出时，计算最大高度，防止超过距离下方的边距的设置
                mPopupWindow.setHeight(screenHeight - location[1] - mBuilder.bottomScreenMargin);
            } else if (mBuilder.position == Position.TOP && location[1] < mBuilder.topScreenMargin) {
                // 向上弹出时，计算最大高度，防止超过距离上方边距的设置
                location[1] = -popoverView.getPaddingTop() + mBuilder.topScreenMargin;
                mPopupWindow.setHeight((int) (mBuilder.anchor.getY() - mBuilder.topScreenMargin));
            }
        }
    }

    /**
     * 配置阴影
     */
    private void configShadow(PopoverView view) {
        if (mBuilder.enableShadow) {
            view.setDropShadow(mBuilder.dropShadow.blurRadius, mBuilder.dropShadow.dx,
                    mBuilder.dropShadow.dy, mBuilder.dropShadow.color);
        }
    }

    /**
     * 创建popupwindow的内容
     */
    private PopoverView createPopoverView()  {
        mIsTextContent = false;

        PopoverView.ArrowPosition arrowPosition;

        if (mBuilder.position == Position.LEFT) {
            arrowPosition = PopoverView.ArrowPosition.RIGHT;
        } else if (mBuilder.position == Position.RIGHT) {
            arrowPosition = PopoverView.ArrowPosition.LEFT;
        } else if (mBuilder.position == Position.TOP) {
            arrowPosition = PopoverView.ArrowPosition.BOTTOM;
        } else {
            arrowPosition = PopoverView.ArrowPosition.TOP;
        }

        PopoverView popoverView = new PopoverView(mBuilder.anchor.getContext(),
                mBuilder.backgroundColor, mBuilder.arrowColor,
                mBuilder.anchor.getResources().getDimension(R.dimen.dp_4),
                mBuilder.showArrow, 0.5f, arrowPosition);


        if (mBuilder.customView != null) {
            popoverView.setContentView(mBuilder.customView, mBuilder.layoutParams);
            // popoverView.setContentView(mBuilder.customView, mBuilder.layoutParams);
        } else if (mBuilder.customLayoutRes != 0) {
            View view = LayoutInflater.from(mBuilder.anchor.getContext()).inflate(
                    mBuilder.customLayoutRes, popoverView, false);
            popoverView.setContentView(view, view.getLayoutParams());
        } else if (mBuilder.content != null) {
            // 纯文本提示的Popover
            mIsTextContent = true;
            TextView textView = new TextView(mBuilder.anchor.getContext());
            textView.setText(mBuilder.content);
            textView.setTextColor(mBuilder.anchor.getResources().getColor(mBuilder.textColor, null));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12F);
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            int padding = (int) mBuilder.anchor.getResources().getDimension(R.dimen.dp_8);
            textView.setPadding(padding, padding, padding, padding);
            popoverView.setContentView(textView, mBuilder.layoutParams);
        }

        return popoverView;
    }

    /**
     * Popover相对Anchor View弹出的位置
     */
    public enum Position {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    /**
     * 对齐Anchor View的方式
     */
    public enum Align {
        LEFT,
        RIGHT,
        CENTER,
        TOP,
        BOTTOM
    }

    /**
     * 阴影配置
     */
    public static class DropShadow {
        int color;
        float dx;
        float dy;
        float blurRadius;

        public DropShadow(int color, float dx, float dy, float blurRadius) {
            this.color = color;
            this.dx = dx;
            this.dy = dy;
            this.blurRadius = blurRadius;
        }
    }


    public static class Builder {
        private View anchor;
        private CharSequence content = null;
        private View customView = null;
        private int customLayoutRes;
        private ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        private Align align = Align.CENTER;
        private Position position = Position.BOTTOM;
        private boolean enableShadow = true;
        private boolean showArrow = true;
        private boolean focusable = true;
        private int backgroundColor = Color.parseColor("#FFCC00");
        private DropShadow dropShadow;
        private PopupWindow.OnDismissListener dismissListener;
        private int xOffset;
        private int yOffset;
        private int leftScreenMargin;
        private int rightScreenMargin;
        private int topScreenMargin;
        private int bottomScreenMargin;
        private int arrowColor = backgroundColor;
        @ColorRes
        private int textColor = R.color.black;


        public Builder(View anchor) {
            this.anchor = anchor;
            initParam();
        }

        private void initParam() {
            dropShadow = new DropShadow(
                    anchor.getResources().getColor(R.color.waring_bar_black_20_percent, null),
                    0,
                    anchor.getResources().getDimension(R.dimen.dp_4),
                    anchor.getResources().getDimension(R.dimen.dp_16));
        }

        /**
         * 显示的文本
         */
        public Builder content(@Nullable CharSequence content) {
            this.content = content;
            return this;
        }

        public Builder content(@StringRes int contentRes) {
            this.content = anchor.getContext().getString(contentRes);
            return this;
        }

        /**
         * 设置自定义的view
         */
        public Builder customView(@NonNull View view) {
            this.customView = view;
            return this;
        }

        public Builder customView(@LayoutRes int layoutRes) {
            this.customLayoutRes = layoutRes;
            return this;
        }

        /**
         * 自定义view添加到popover里的layout参数
         * 默认是wrap_content
         */
        public Builder layoutParams(@NonNull ViewGroup.LayoutParams layoutParams) {
            this.layoutParams = layoutParams;
            return this;
        }

        /**
         * 自定义view的尺寸
         */
        public Builder size(int width, int height) {
            this.layoutParams = new ViewGroup.LayoutParams(width, height);
            return this;
        }

        /**
         * 对齐anchor的方式
         */
        public Builder align(Align align) {
            this.align = align;
            return this;
        }

        /**
         * 显示在anchor的哪个位置上
         */
        public Builder position(Position position) {
            this.position = position;
            return this;
        }

        /**
         * 是否使用阴影
         */
        public Builder enableShadow(boolean enableShadow) {
            this.enableShadow = enableShadow;
            return this;
        }

        /**
         * 是否显示小箭头
         */
        public Builder showArrow(boolean showArrow) {
            this.showArrow = showArrow;
            return this;
        }

        /**
         * focusable设置为true时，popupwindow会处理返回键等事件，设置为false时，点击事件可以穿透popupwindow传递给下层
         * 默认是true
         */
        public Builder focusable(boolean focusable) {
            this.focusable = focusable;
            return this;
        }

        /**
         * 背景颜色
         */
        public Builder backgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * 阴影参数
         * color: 阴影颜色
         * dx: x轴偏移
         * dy: y轴偏移
         * blurRadius: 模糊范围，数值越大模糊范围越大
         */
        public Builder dropShadow(@NonNull DropShadow dropShadow) {
            this.dropShadow = dropShadow;
            return this;
        }

        /**
         * popover消失时的回调
         */
        public Builder onDismiss(@Nullable PopupWindow.OnDismissListener dismissListener) {
            this.dismissListener = dismissListener;
            return this;
        }

        /**
         * 显示坐标x轴偏移
         * 默认0
         */
        public Builder xOffset(int xOffset) {
            this.xOffset = xOffset;
            return this;
        }

        /**
         * 显示坐标y轴偏移
         * 默认是0
         */
        public Builder yOffset(int yOffset) {
            this.yOffset = yOffset;
            return this;
        }

        /**
         * 距离屏幕边缘的边距
         */
        public Builder allScreenMargin(int margin) {
            this.topScreenMargin = margin;
            this.rightScreenMargin = margin;
            this.bottomScreenMargin = margin;
            this.leftScreenMargin = margin;
            return this;
        }

        /**
         * 距离右边屏幕边距。像素单位
         */
        public Builder rightScreenMargin(int margin) {
            this.rightScreenMargin = margin;
            return this;
        }

        /**
         * 距离下边屏幕的边距
         */
        public Builder bottomScreenMargin(int margin) {
            this.bottomScreenMargin = margin;
            return this;
        }

        /**
         * 距离左边屏幕的边距
         */
        public Builder leftScreenMargin(int margin) {
            this.leftScreenMargin = margin;
            return this;
        }

        /**
         * 距离上边屏幕的边距
         */
        public Builder topScreenMargin(int margin) {
            this.topScreenMargin = margin;
            return this;
        }

        /**
         * 指定箭头颜色
         */
        public Builder arrowColor(int color) {
            this.arrowColor = color;
            return this;
        }


        public Popover build() {
            return new Popover(Popover.Builder.this);
        }

        public Builder textColor(int color) {
            this.textColor = color;
            return this;
        }
    }
}
