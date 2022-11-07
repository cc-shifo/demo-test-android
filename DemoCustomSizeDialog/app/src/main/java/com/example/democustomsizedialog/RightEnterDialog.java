package com.example.democustomsizedialog;

import android.content.Context;

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
        return 0;
    }

    @Override
    public void initViewData() {

    }

    @Override
    public void initView() {

    }
}
