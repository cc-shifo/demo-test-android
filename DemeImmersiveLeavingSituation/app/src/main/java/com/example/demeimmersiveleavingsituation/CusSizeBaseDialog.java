/*
 * = COPYRIGHT
 *          xxxx
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                    Author                  Action
 * 2022-11-08              LiuJian                 Create
 */

package com.example.demeimmersiveleavingsituation;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * 可定制大小的对话框基类。
 * 通过重写{@link #customWindowSize()}改变对话框大小。
 * 通过{@link #setCustomContentView()}返回自定义view。
 */
public abstract class CusSizeBaseDialog<T extends ViewDataBinding> extends AlertDialog {
    protected T mBinding;

    /**
     * 构造函数处理了以下功能:
     * 1、获取mWindowManger。
     * 2、创建Window mWindow=new PhoneWindow() 。Window里设置了约束Window的WindowManager.LayoutParams，
     * 同时初始了Window的位置。
     */
    protected CusSizeBaseDialog(@NonNull Context context) {
        super(context);
    }

    protected CusSizeBaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    /**
     * id为0的主题。应该是沿用Activity的主题。
     */
    protected CusSizeBaseDialog(@NonNull Context context, boolean cancelable,
                                @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * setContentView(@LayoutRes int layoutResID)流程构造了mDecor，也执行generateLayout()修改了
     * window的模糊图层。同时将View添加mDecor。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customWindowSize();// 可以在setContentView()之前或之后。之前先设置好参数，然后执行。放之后要重绘制。
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                                           setCustomContentView(), null, false);
        setContentView(mBinding.getRoot());
        initViewData();
        initView();
    }

    /**
     * 默认没有tittle，没有window background，decor没有frame padding.
     * 居中，宽和高占据APP可设置区域(除系统状态栏，导航栏外的区域）最大值。当前函数先于
     * {@link #setCustomContentView()}执行。
     * <p>
     * WindowManager.LayoutParams的宽或者高是ViewGroup.LayoutParams.WRAP_CONTENT，
     * 自定义View对应的宽，高必须是测量出来的确切值（如，固定值）。
     * WindowManager.LayoutParams的宽或者高是ViewGroup.LayoutParams.MATCH_PARENT，
     * 自定义View对应的宽，高支可以测量出来的确切值（如，固定值），也可以是MATCH_PARENT。
     */
    protected void customWindowSize() {
        Window window = getWindow();
        window.clearFlags(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawable(null);
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        View d = window.getDecorView();
        d.setPadding(0, 0, 0, 0);
    }

    /**
     * 自定义view
     *
     * @return 自定义view的布局id。
     */
    public abstract int setCustomContentView();

    /**
     * 自定义View构建后，初始化数据。该函数在{@link #setCustomContentView()}之后执行。
     */
    public abstract void initViewData();

    /**
     * 执行业务相关的view的初始化。该函数在{@link #initViewData()}之后执行。
     */
    public abstract void initView();

}
