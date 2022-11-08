package com.example.democustomsizedialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

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
    public void customWindowSize() {
        Window window = getWindow();
        window.setBackgroundDrawable(null);
        window.clearFlags(Window.FEATURE_NO_TITLE);
        window.setGravity(Gravity.END);

        window.setBackgroundDrawable(null);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // window.getDecorView().setBackgroundColor(Color.GREEN);
        View d = window.getDecorView();
        d.setPadding(0, 0, 0, 0);
    }

    @Override
    public int setCustomContentView() {
        return R.layout.layout_dialog_right;
    }

    @Override
    public void initViewData() {

    }

    @Override
    public void initView() {

    }
}
