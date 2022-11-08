package com.example.democustomsizedialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public abstract class BaseDialog extends AlertDialog {
    private static final String TAG = "BaseDialog";
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

    /**
     * id为0的主题。应该是沿用Activity的主题。
     */
    protected BaseDialog(@NonNull Context context, boolean cancelable,
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
        setContentView(setCustomContentView());
        initViewData();
        initView();
    }

    public abstract int setCustomContentView();

    public abstract void initViewData();

    public abstract void initView();

    /**
     * WindowManager.LayoutParams的宽或者高是ViewGroup.LayoutParams.WRAP_CONTENT，
     * 自定义View对应的宽，高必须是测量出来的确切值（如，固定值）。
     * WindowManager.LayoutParams的宽或者高是ViewGroup.LayoutParams.MATCH_PARENT，
     * 自定义View对应的宽，高支可以测量出来的确切值（如，固定值），也可以是MATCH_PARENT。
     */
    public abstract void customWindowSize();

    private void test() {
        Window window = getWindow();
        window.setGravity(Gravity.END);
        // 测试decor和window背景同时设置的效果(主题里已经对window的背景@null：window的被之后的decor调用覆盖。
        // window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        // window.getDecorView().setBackgroundColor(Color.GREEN);
        // WindowManager.LayoutParams params = window.getAttributes();
        // params.horizontalMargin = 0;//无效
        // params.verticalMargin = 0;//无效

        // 改宽高。效果等同window.setLayout
        // params.width = ViewGroup.LayoutParams.MATCH_PARENT;// 等同window.setLayout
        // params.height = ViewGroup.LayoutParams.MATCH_PARENT;// 等同window.setLayout
        // window.setAttributes(params);// 等同window.setLayout
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        // LayoutInspector检测到decor有padding值。
        // window.getDecorView().setPadding(0,0,0,0);


        // window.setDecorFitsSystemWindows(true);//无效
    }
}
