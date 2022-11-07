package com.example.democustomsizedialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public abstract class BaseDialog extends AlertDialog {
    /**
     * 构造函数处理了以下功能:
     * 1、获取mWindowManger。
     * 2、创建Window mWindow=new PhoneWindow() 。Window里设置了约束Window的WindowManager.LayoutParams，
     * 同时初始了Window的位置。
     */
    protected BaseDialog(@NonNull Context context) {
        super(context);
    }

    protected BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable,
                         @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * setContentView(@LayoutRes int layoutResID)流程构造了mDector，也执行generateLayout()修改了
     * window的模糊图层。同时将View添加mDector。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initViewData();
        initView();
    }

    public abstract int getLayoutId();

    public abstract void initViewData();

    public abstract void initView();
}
