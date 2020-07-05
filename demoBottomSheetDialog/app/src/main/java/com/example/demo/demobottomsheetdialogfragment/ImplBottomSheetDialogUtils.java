package com.example.demo.demobottomsheetdialogfragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public class ImplBottomSheetDialogUtils extends BottomSheetDialogUtils {
    public ImplBottomSheetDialogUtils(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayoutId() {
//        return R.layout.layout;
        return R.layout.layout;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void iniView() {

    }
}
