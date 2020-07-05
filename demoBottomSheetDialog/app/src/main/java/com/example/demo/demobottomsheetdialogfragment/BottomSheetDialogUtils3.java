package com.example.demo.demobottomsheetdialogfragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public abstract class BottomSheetDialogUtils3 {
    private static final String TAG = "BottomSheetDialogUtils3";
    private BottomSheetDialog mSheetDialog;
    private BottomSheetBehavior<? extends View> mSheetBehavior;
    private Context mContext;

    public BottomSheetDialogUtils3(@NonNull Context context) {
        //mSheetDialog = new BottomSheetDialog(context);
        //View view = LayoutInflater.from(context).inflate(setLayoutId(), null);
        //mSheetDialog.setContentView(setLayoutId());
        mContext = context;
        mSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(setLayoutId(), null);
        mSheetDialog.setContentView(view);
//        FrameLayout frameLayout = mSheetDialog.getDelegate().findViewById(android.support.design
//                .R.id.design_bottom_sheet);
        FrameLayout frameLayout = (FrameLayout) view.getParent();
        /*CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) frameLayout.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        frameLayout.setLayoutParams(params);*/
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
     * called after {@link BottomSheetDialogUtils3#setLayoutId()}
     */
    protected abstract void initData();

    /**
     * called after {@link BottomSheetDialogUtils3#initData()}
     */
    protected void iniView() {
        Window window = mSheetDialog.getWindow();
        if (window != null) {
            Log.d(TAG, "BottomSheetDialogUtils: non null window");
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.0f;
            window.setAttributes(params);
        }

        mSheetBehavior.setHideable(false);
        mSheetBehavior.setFitToContents(true);
//        mSheetBehavior.setPeekHeight(mContext.getResources().getDimensionPixelOffset(R.dimen.home_menu_dialog_height));
        mSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_DRAGGING) {
                    mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (i == BottomSheetBehavior.STATE_HIDDEN) {
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
        mSheetDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.d(TAG, "onKey: " + keyCode);
                    mSheetDialog.dismiss();
                }
                return false;
            }
        });
    }

    protected Context getContext() {
        return mContext;
    }

    protected BottomSheetDialog getView() {
        return mSheetDialog;
    }

    protected void show() {
        Window window = mSheetDialog.getWindow();
        if (window != null) {
            Log.d(TAG, "BottomSheetDialogUtils: non null window");
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.0f;
            window.setAttributes(params);
        }
        mSheetDialog.show();
    }

    protected boolean isShowing() {
        return mSheetDialog.isShowing();
    }

    protected void dismiss() {
        mSheetDialog.dismiss();
    }


}
