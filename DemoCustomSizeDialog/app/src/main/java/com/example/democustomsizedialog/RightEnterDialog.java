package com.example.democustomsizedialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RightEnterDialog extends BaseDialog {
    protected RightEnterDialog(@NonNull Context context) {
        super(context);
    }

    protected RightEnterDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RightEnterDialog(@NonNull Context context, boolean cancelable,
                               @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_dialog_right;
    }

    @Override
    public void initViewData() {

    }

    @Override
    public void initView() {
        Window window = getWindow();
        window.setGravity(Gravity.END);
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        //     window.getInsetsController();
        // }

        // 测试decor和window背景同时设置的效果(主题里已经对window的背景@null：window的被之后的decor调用覆盖。
        // window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        window.getDecorView().setBackgroundColor(Color.GREEN);
        WindowManager.LayoutParams params = window.getAttributes();
        // params.horizontalMargin = 0;//无效
        // params.verticalMargin = 0;//无效

        // 改宽高。效果等同window.setLayout
        // params.width = ViewGroup.LayoutParams.MATCH_PARENT;// 等同window.setLayout
        // params.height = ViewGroup.LayoutParams.MATCH_PARENT;// 等同window.setLayout
        // window.setAttributes(params);// 等同window.setLayout
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        // LayoutInspector检测到decor有padding值。
        // window.getDecorView().setPadding(0,0,0,0);


        // window.setDecorFitsSystemWindows(true);//无效
    }
}
