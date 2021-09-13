package com.demo.inter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.demo.model.RequestParams;

public interface ITYSmartPosSdkManager {
    // step1
    public void setTYPayManagerListener(ITYPayManagerListener tyPayManagerListener);
    // step2
    public void init(@NonNull Context ctx);
    // step3
    public void doTrans (RequestParams params);
    // step4
    public void release();
}
