package com.example.demo.demobottomsheetdialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseBottomDialogFragment2 extends BottomSheetDialogFragment {
    private Dialog mDialog;
    private View mMenu;
    private BottomSheetBehavior mBehavior;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = super.onCreateDialog(savedInstanceState);
        return mDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mMenu == null) {
            mMenu = inflater.inflate(setLayoutId(), container, false);
        }
        mBehavior = BottomSheetBehavior.from(container);
        mBehavior.setSkipCollapsed(true);
        mBehavior.setHideable(true);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //禁止拖拽，
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    //设置为收缩状态
                    mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        return mMenu;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) mMenu.getParent()).removeView(mMenu);
    }

    protected abstract int setLayoutId();

    protected abstract void initData();

    protected abstract void iniView();

}
