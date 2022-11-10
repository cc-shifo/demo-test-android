package com.example.democustomsizedialog;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RightEnterDialog extends BaseDialog {
    private static final String TAG = "RightEnterDialog";

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
        // window.setGravity(Gravity.END);

        WindowManager.LayoutParams params = window.getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        params.width = (int) (dm.widthPixels * 0.5f);
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.END;
        // window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
        // .MATCH_PARENT);
        window.getDecorView().setBackgroundColor(Color.GREEN);
        View d = window.getDecorView();
        d.setPadding(0, 0, 0, 0);
        Log.d(TAG, "[DemoDialogFragment] customWindowSize");
    }

    @Override
    public int getCustomContentView() {
        return R.layout.layout_dialog_right;
    }

    @Override
    public void initViewData() {

    }

    @Override
    public void initView() {

    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "[DemoDialogFragment] AnchorEndDialog onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "[DemoDialogFragment] AnchorEndDialog onStop");
    }
}
