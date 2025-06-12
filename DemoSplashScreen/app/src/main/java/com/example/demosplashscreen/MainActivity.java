package com.example.demosplashscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.splashscreen.SplashScreenViewProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MainViewModel mMainViewModel;
    private SplashScreen mSplashScreen;
    private boolean mOldCondition = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel = new ViewModelProvider.NewInstanceFactory().create(MainViewModel.class);
        mSplashScreen = SplashScreen.installSplashScreen(this);

        // 值根据条件决定是否保持splash screen。当不保持时，会自动调用onExitAnimationListener
        // 保持太长，如果启动界面的中心图标只是静态drawable，而不是动画。会导致splash screen一直显示，影响用户体验。
        // 如果是旧SplashActivity的耗时任务耗时时间太长，可考方式：
        // 1、快速关闭保持，然后在onExitAnimationListener中显示loading view。根据任务耗时来决定loading view显示与关闭。
        // 2、界面的中心图标使用动画，然后在onExitAnimationListener重复播放动画
        mSplashScreen.setKeepOnScreenCondition(() -> {
            boolean value = Boolean.TRUE.equals(mMainViewModel.getInitializationCompleted().getValue());
            if (value != mOldCondition) {
                mOldCondition = value;
                Log.d(TAG, "onKeepOnScreen: " + value);
            }
            return Boolean.FALSE.equals(mMainViewModel.getInitializationCompleted().getValue());
        });
        // 如果是Application的耗时任务耗时时间太长，只能考虑优化Application的耗时任务的实现方式和调用方式。
        // 经过测试，Application的耗时任务耗时时间太长，会导致splash screen一直显示，影响用户体验。
        // 例如在MainApp之中Thread.sleep(6000)，splash screen一直显示，并不会因为windowSplashScreenAnimationDuration
        // 设置的时间是200而主动关闭splash screen。
        // 因此，只能考虑将Application的耗时任务放到MainActivity中。在MainActivity中 异步处理耗时任务，同时，结合
        // setKeepOnScreenCondition条件，splash screen中的动画，setOnExitAnimationListener中动画，
        // LoadingView的显示与关闭，来控制改善用户体验。

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 设置退出动画监听器
        mSplashScreen.setOnExitAnimationListener(splashScreenViewProvider -> {
            Log.d(TAG, "onSplashScreenExit: getIconAnimationDurationMillis="
                    + splashScreenViewProvider.getIconAnimationDurationMillis());
            Log.d(TAG, "onSplashScreenExit: getIconAnimationStartMillis="
                    + splashScreenViewProvider.getIconAnimationStartMillis());
            splashScreenViewProvider.getView().animate()
                    .scaleX(0.5f)
                    .scaleY(0.5f)
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction(() -> {
                        splashScreenViewProvider.remove();
                        if (Boolean.FALSE.equals(mMainViewModel.getInitializationCompleted().getValue())) {
                            showLoadingView();
                        }
                    });
        });

        // 监听任务状态变化
        mMainViewModel.getInitializationCompleted().observe(this, isCompleted -> {
            Log.d(TAG, "onChanged: isCompleted=" + isCompleted);
            if (Boolean.TRUE.equals(isCompleted)) {
                showMainContent();
            }
        });

        // 启动初始化任务
        mMainViewModel.startInitialization();
    }

    private void showLoadingView() {
        findViewById(R.id.loadingView).setVisibility(View.VISIBLE);
    }

    private void hideLoadingView() {
        findViewById(R.id.loadingView).setVisibility(View.GONE);
    }

    private void showMainContent() {
        hideLoadingView();
        findViewById(R.id.mainContent).setVisibility(View.VISIBLE);
    }
}