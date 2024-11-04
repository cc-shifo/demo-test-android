package com.example.demowarningbar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;


public class WarningBarWidget extends ConstraintLayout {
    private static final String TAG = "WarningBarWidget";
    private ViewGroup warningMessageCountWrapper;
    private TextView cardViewWarningWrapper;
    private TextView tvPhotoMessage;
    /**
     * 错误，严重类型
     */
    private TextView tvLevel3Count;
    /**
     * 提醒类型
     */
    private TextView tvLevel2Count;
    /**
     * 正常情况
     */
    private TextView tvNoMessage;

    private Popover mPopover;

    private View mPopupView;

    public WarningBarWidget(@NonNull Context context) {
        this(context, null);
    }

    public WarningBarWidget(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WarningBarWidget(@NonNull Context context,
            @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WarningBarWidget(@NonNull Context context,
            @Nullable AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        String s = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width");
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: " + "w=" + w + ", h="+ h + ", oldw="+ oldw + ", oldh="+ oldh);
    }

    private void initView() {
        inflate(getContext(), R.layout.layout_warning_bar_widget, this);
        setBackgroundColor(getContext().getColor(R.color.waring_bar_black_95_percent));
        warningMessageCountWrapper = findViewById(R.id.warning_message_count_wrapper);
        warningMessageCountWrapper.setClipToOutline(true);
        warningMessageCountWrapper.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                float radius = getResources().getDimension(R.dimen.dp_2);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });

        tvPhotoMessage = findViewById(R.id.tv_photo_count);
        tvLevel3Count = findViewById(R.id.tv_level3_count);
        tvLevel2Count = findViewById(R.id.tv_level2_count);
        cardViewWarningWrapper = findViewById(R.id.tv_warning_message);
        cardViewWarningWrapper.setClipToOutline(true);
        cardViewWarningWrapper.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                float radius = getResources().getDimension(R.dimen.dp_2);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });

        // mPopupView = new FrameLayout(getContext());
        // mPopupView.setLayoutParams(new FrameLayout.LayoutParams(80, 80));
        // mPopupView.setBackgroundColor(Color.GREEN);

        mPopupView = LayoutInflater.from(getContext()).inflate(R.layout.test_pop, this, false);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopover != null && mPopover.isShowing()) {
                    return;
                }
                if (mPopover == null) {

                    mPopover = new Popover.Builder(WarningBarWidget.this)
                            .arrowColor(getResources().getColor(
                                    R.color.waring_bar_popup_title_background_color, null))
                            .yOffset((int) getResources().getDimension(R.dimen.dp_2))
                            .backgroundColor(Color.TRANSPARENT)
                            .allScreenMargin((int) getResources().getDimension(R.dimen.dp_32))
                            .enableShadow(false)
                            .yOffset((int) getResources().getDimension(R.dimen.dp_2))
                            .customView(mPopupView)
                            .bottomScreenMargin((int) getResources().getDimension(R.dimen.dp_96))
                            .leftScreenMargin((int) getResources().getDimension(R.dimen.dp_40))
                            .align(Popover.Align.CENTER)
                            .arrowColor(getResources().getColor(R.color
                                    .waring_bar_popup_content_color_black, null))
                            .build();
                }

                // mPopover.show();// crash
                // mPopover.showTest1(WarningBarWidget.this);
                mPopover.showTest2(WarningBarWidget.this);
            }
        });
    }
}
