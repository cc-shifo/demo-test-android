package com.example.demo.demobottomsheetdialogfragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public abstract class BottomSheetDialogUtils {
    private static final String TAG = "BottomSheetDialogUtils";
    BottomSheetDialog mSheetDialog;
    BottomSheetBehavior mSheetBehavior;

    public BottomSheetDialogUtils(@NonNull Context context) {
        mSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(setLayoutId(), null);
        mSheetDialog.setContentView(view);

//        FrameLayout frameLayout = mSheetDialog.getDelegate().findViewById(android.support.design
//         .R.id.design_bottom_sheet);
        FrameLayout frameLayout = (FrameLayout) view.getParent();
        mSheetBehavior = BottomSheetBehavior.from(frameLayout);
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
        mSheetDialog.setCancelable(true);
        mSheetDialog.setCanceledOnTouchOutside(true);
    }

    public void show() {
        Window window = mSheetDialog.getWindow();
        if (window != null) {
            Log.d(TAG, "BottomSheetDialogUtils: non null window");
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.0f;
            window.setAttributes(params);
        }
        mSheetDialog.show();
    }

    public boolean isShowing() {
        return mSheetDialog.isShowing();
    }

    public void dismiss() {
        mSheetDialog.dismiss();
    }

    protected abstract int setLayoutId();

    protected abstract void initData();

    protected abstract void iniView();

}
