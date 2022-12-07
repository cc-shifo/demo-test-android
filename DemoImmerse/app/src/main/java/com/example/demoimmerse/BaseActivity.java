package com.example.demoimmerse;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected T mBinding;

    /**
     * 将布局id传递进BaseActivity，调用{@link DataBindingUtil}工具绑定数据和界面。
     *
     * @return Activity布局id。
     */
    protected abstract int initLayoutId();

    /**
     * 运行于{@link AppCompatActivity#setContentView(int)}之后。常用于数据的初始化等准备功能。
     */
    protected abstract void initViewData();

    /**
     * 运行于{@link BaseActivity#initViewData()}之后，常用于初始化默认界面，执行业务逻辑等工作。
     */
    protected abstract void initView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (supportImmerse()) {
            useSwipeImmerse();
        }
        changeOrientation();
        initBinding();
        initViewData();
        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (supportImmerse()) {
            useSwipeImmerse();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 是否启用自定义屏幕方向功能。默认启用。
     *
     * @return true启用，false不启用。
     */
    public boolean supportFavoriteOrientation() {
        return true;
    }

    /**
     * 是否使用横屏，默认使用横屏。在启用了自定义屏幕方向后才有效。
     *
     * @return true使用横屏，否则使用竖屏。
     */
    public boolean useScreenLandscape() {
        return true;
    }

    /**
     * 是否启用沉浸模式（非黏贴）。默认启用。
     *
     * @return true使用横屏，否则使用竖屏。
     */
    public boolean supportImmerse() {
        return true;
    }

    /**
     * 绑定数据和视图
     */
    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, initLayoutId());
    }

    /**
     * 改变屏幕方向
     */
    @SuppressLint("SourceLockedOrientationActivity")
    private void changeOrientation() {
        if (supportFavoriteOrientation()) {
            if (useScreenLandscape() && isInPortrait()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (!useScreenLandscape() && !isInPortrait()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    /**
     * 判断当前是横屏还是竖屏状态。
     *
     * @return true竖屏，false横屏。
     */
    private boolean isInPortrait() {
        return getResources().getConfiguration().orientation ==
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    private void useSwipeImmerse() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController != null) {
            // Configure the behavior of the hidden system bars
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
            // Hide both the status bar and the navigation bar
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        } else {
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
}