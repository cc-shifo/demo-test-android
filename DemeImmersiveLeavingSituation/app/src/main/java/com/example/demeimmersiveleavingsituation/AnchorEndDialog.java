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
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.demeimmersiveleavingsituation.databinding.LayoutMoreSettingDialogBinding;

/**
 * 更多设置对话框
 */
public class AnchorEndDialog extends CusSizeBaseDialog<LayoutMoreSettingDialogBinding> {

    public AnchorEndDialog(@NonNull Context context) {
        super(context, R.style.Theme_EndAnchorDialog);
    }

    @Override
    protected void customWindowSize() {
        super.customWindowSize();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.END;
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        params.width = (int) (dm.widthPixels * 0.5f);
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        useSwipeImmerse();
    }

    @Override
    public int setCustomContentView() {
        return R.layout.layout_more_setting_dialog;
    }

    @Override
    public void initViewData() {
        // nothing
    }

    @Override
    public void initView() {
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Timber.d("[MoreSettingFragment] AnchorEndDialog onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Timber.d("[MoreSettingFragment] AnchorEndDialog onStop");
    }

    @Nullable
    public LayoutMoreSettingDialogBinding getBinding() {
        return mBinding;
    }

    private void initVP() {
        // mBinding.moreSettingDialogRv
    }

    private void useSwipeImmerse() {
        // 6.0及以上才可设置状态栏采用亮色背景和字体暗色
        // <item name="android:windowLightStatusBar"> true </item>
        // 4.4及以前状态栏透明化。
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);// 5.2>= 状态栏透明化。
        // 对有toolbar场景才有效。避免toolbar 伸展到状态栏里。
        // getWindow().getDecorView().setFitsSystemWindows(true);
        // 修改成亮色背景和字体暗色
        // getWindow().getDecorView().setSystemUiVisibility(View
        // .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(
                // View.SYSTEM_UI_FLAG_LOW_PROFILE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
