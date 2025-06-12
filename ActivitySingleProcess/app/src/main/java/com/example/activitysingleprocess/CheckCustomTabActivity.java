package com.example.activitysingleprocess;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;

import java.util.List;

public class CheckCustomTabActivity extends AppCompatActivity {

    private TextView tvStatus;
    private Button btnTest;
    private boolean isCustomTabsSupported = false;
    private static final String TEST_URL = "https://developer.android.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_chrome_custom_tab);

        tvStatus = findViewById(R.id.tvStatus);
        btnTest = findViewById(R.id.btnTest);

        // 检查设备是否支持Custom Tabs
        checkCustomTabsSupport();

        btnTest.setOnClickListener(v -> {
            if (isCustomTabsSupported) {
                openUrlWithCustomTabs(TEST_URL);
            } else {
                Toast.makeText(CheckCustomTabActivity.this,
                        "Custom Tabs not supported. Using fallback.",
                        Toast.LENGTH_SHORT).show();
                openUrlWithFallback(TEST_URL);
            }
        });
        tvStatus.setOnClickListener(v -> {
            tvStatus.setText("正在检测设备兼容性...");
            // checkCustomTabsSupport();
            isCustomTabsAvailable();
        });
    }

    private void isCustomTabsAvailable() {
        PackageManager pm = getPackageManager();
        Intent serviceIntent = new Intent("android.support.customtabs.action.CustomTabsService");
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(serviceIntent, 0);
        isCustomTabsSupported = resolveInfos != null && !resolveInfos.isEmpty();
        if (!isCustomTabsSupported) {
            tvStatus.setText("❌ Custom Tabs not supported\n\nNo browser available that supports Custom Tabs");
        } else {
            tvStatus.setText("✅ Custom Tabs supported\n\nDefault Custom Tabs browser: " + resolveInfos.size());
        }
    }

    // 检查设备是否支持Custom Tabs
    private void checkCustomTabsSupport() {
        String packageName = CustomTabsClient.getPackageName(this, null);

        if (packageName == null) {
            tvStatus.setText("❌ Custom Tabs not supported\n\nNo browser available that supports Custom Tabs");
            isCustomTabsSupported = false;
        } else {
            tvStatus.setText("✅ Custom Tabs supported\n\nDefault Custom Tabs browser: " + packageName);
            isCustomTabsSupported = true;
        }

        btnTest.setEnabled(true);
    }

    // 使用Custom Tabs打开URL
    private void openUrlWithCustomTabs(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        // 自定义UI样式
        builder.setToolbarColor(Color.parseColor("#4285F4")); // Google Blue
        builder.setSecondaryToolbarColor(Color.parseColor("#3367D6")); // Darker Blue
        builder.setShowTitle(true);
        builder.setUrlBarHidingEnabled(true);

        // 添加自定义操作按钮
        builder.addDefaultShareMenuItem();

        // 添加动画效果
        // builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
        // builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right);

        // 构建并启动Custom Tabs
        CustomTabsIntent customTabsIntent = builder.build();

        // 预热浏览器以加快加载速度
        warmUpBrowser();

        // 启动Custom Tabs
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    // 预热浏览器以加快加载速度
    private void warmUpBrowser() {
        String packageName = CustomTabsClient.getPackageName(this, null);
        if (packageName == null) return;

        CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                // 预热浏览器
                client.warmup(0);

                // 创建会话
                CustomTabsSession session = client.newSession(null);
                if (session != null) {
                    session.mayLaunchUrl(Uri.parse(TEST_URL), null, null);
                }

                // 断开连接
                unbindService(this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {}
        };

        CustomTabsClient.bindCustomTabsService(this, packageName, connection);
    }

    // 备选方案：使用系统浏览器
    private void openUrlWithFallback(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "无法打开URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}