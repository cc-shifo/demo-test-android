package com.example.demo.demobottomsheetdialogfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public abstract class BottomSheetDialogUtils2 extends BottomSheetDialog {
    private static final String TAG = "BottomSheetDialogUtils2";
    private Context mContext;
    private BottomSheetDialog mSheetDialog;
    private BottomSheetBehavior<? extends View> mSheetBehavior;

    public BottomSheetDialogUtils2(@NonNull Context context) {
        super(context);
    }

    public BottomSheetDialogUtils2(@NonNull Context context, int theme) {
        super(context, theme);
    }

    public BottomSheetDialogUtils2(@NonNull Context context, boolean cancelable,
                                   OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(setLayoutId());
        //        FrameLayout frameLayout = getDelegate().findViewById(android.support.design
        //                .R.id.design_bottom_sheet);
        View view = LayoutInflater.from(getContext()).inflate(setLayoutId(), null);
        setContentView(view);
        FrameLayout frameLayout = (FrameLayout) view.getParent();
        //        assert frameLayout != null;
        mSheetBehavior = BottomSheetBehavior.from(frameLayout);
        initData();
        iniView();
    }

    /**
     * called on onCreate
     *
     * @return dialog layout
     */
    protected abstract int setLayoutId();

    /**
     * called on {@link BottomSheetDialog#onCreate(Bundle)} and after
     * {@link BottomSheetDialogUtils#setLayoutId()}
     */
    protected abstract void initData();

    /**
     * called after {@link BottomSheetDialogUtils#initData()}
     */
    protected void iniView() {
        Window window = getWindow();
        if (window != null) {
            Log.d(TAG, "BottomSheetDialogUtils: non null window");
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.0f;
            window.setAttributes(params);
        }

        mSheetBehavior.setHideable(false);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    mSheetDialog.dismiss();
                    mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    Log.d(TAG, "onStateChanged: " + view.toString() + "state: " + i);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                Log.d(TAG, "onSlide: " + view.toString() + ", offset: " + v);
            }
        });
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
    }

    protected BottomSheetDialog getView() {
        return this;
    }
}
