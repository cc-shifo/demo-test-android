package com.example.demoscreenorientation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.demoscreenorientation.databinding.ActivityMainBinding;

/**
 * 观察主动设置的回调。
 *
 *
 * 结论：
 * 1.xml没有配置android:configChanges的情况下，进入APP时屏幕的方向与APP的设置方向不一样，设置后会重启Activity。
 * 进入APP时屏幕的方向与设置的方向一样（及不用设置），进入APP后，即便是旋转屏幕（模拟器测试），Activity也不会重启。
 *
 * 2.xml配置了android:configChanges的情况下，进入APP时屏幕的方向与APP的设置方向不一样，设置后不会重启Activity。
 * 进入APP时屏幕的方向与设置的方向一样（及不用设置），进入APP后，即便是旋转屏幕（模拟器测试），Activity同样不会重启。
 *
 * 3.横屏可以在super.onCreate()之前就设置。
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate1: " + getOriString());
        changeOrientation();
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Log.d(TAG, "onCreate2: ");
        mBinding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, XmlConfigMainActivity.class);
                startActivity(intent);
            }
        });
        mBinding.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DefaultChangeMainActivity.class);
                startActivity(intent);
            }
        });
        mBinding.btnLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LandMainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged1: " + getOriString());
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged2: " + getOriString());
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart1: " + getOriString());
        super.onStart();
        Log.d(TAG, "onStart1: " + getOriString());
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume1: " + getOriString());
        super.onResume();
        Log.d(TAG, "onResume2: " + getOriString());
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop1: " + getOriString());
        super.onStop();
        Log.d(TAG, "onStop2: " + getOriString());
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy1: " + getOriString());
        super.onDestroy();
        Log.d(TAG, "onDestroy2: " + getOriString());
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

    private String getOriString() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ?
                "[landscape]" : "[portrait]";
    }
    /**
     * 改变屏幕方向
     */
    @SuppressLint("SourceLockedOrientationActivity")
    private void changeOrientation() {
        Log.d(TAG, "changeOrientation: " + getOriString());
        if (supportFavoriteOrientation()) {
            if (useScreenLandscape() && isInPortrait()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (!useScreenLandscape() && !isInPortrait()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    private Configuration keepOrientation(@NonNull Configuration newConfig) {
        if (supportFavoriteOrientation()) {
            if (useScreenLandscape() && isInPortrait()) {
                newConfig.orientation = Configuration.ORIENTATION_LANDSCAPE;
                // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (!useScreenLandscape() && !isInPortrait()) {
                newConfig.orientation = Configuration.ORIENTATION_PORTRAIT;
                // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        return newConfig;
    }

    /**
     * 判断当前是横屏还是竖屏状态。
     *
     * @return true竖屏，false横屏。
     */
    private boolean isInPortrait() {
        // return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }
}