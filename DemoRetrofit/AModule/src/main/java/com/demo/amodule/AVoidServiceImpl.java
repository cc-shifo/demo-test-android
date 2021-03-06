package com.demo.amodule;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.demo.basemodule.BusinessSDK;

@Route(path = "/aModule/serviceA")
public class AVoidServiceImpl implements BusinessSDK {
    private static final String TAG = "AVoidServiceImpl";

    @Override
    public void init(Context context) {
        Log.d(TAG, "init: ");
    }

    @Override
    public String printPackName(@NonNull Context context) {
        Log.d(TAG, "getPackName: " + context.getPackageName());
        Log.d(TAG, "getPackageCodePath: " + context.getPackageCodePath());

        return this.toString();
    }
}
