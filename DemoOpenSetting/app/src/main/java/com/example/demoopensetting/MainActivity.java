package com.example.demoopensetting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.demoopensetting.databinding.ActivityMainBinding;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQ_CODE = 1000;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private static final String ALL_PERMISSIONS_WIFI_33[] = {
            // INTERNET
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,

            // WiFi
            Manifest.permission.NEARBY_WIFI_DEVICES,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
    };

    private static final String ALL_PERMISSIONS_WIFI_32[] = {
            // INTERNET
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,

            // WiFi
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private ActivityMainBinding mBinding;
    private final ActivityResultLauncher<String[]> requestWiFiPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        boolean granted = false;
                        if (result != null) {
                            for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                                granted = Boolean.TRUE.equals(entry.getValue());
                                if (!granted) {
                                    break;
                                }
                            }
                        }

                        if (!granted) {
                            Toast.makeText(getApplicationContext(), R.string.
                                            please_allow_permission_toast, Toast.LENGTH_LONG);
                        } else {
                            toWiFiSettingMethod1();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.btnOpenWifiSetting1.setOnClickListener(v -> {
            checkPermissions();
        });
    }

    /**
     * 检查wifi设置权限
     */
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            toWiFiSettingMethod1();
        } /*else if (shouldShowRequestPermissionRationale(...)){
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.
            // showInContextUI(...);
        } */ else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            grantWiFiPermissions();
        }
    }

    private void grantWiFiPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestWiFiPermissionLauncher.launch(ALL_PERMISSIONS_WIFI_33);
        } else {
            requestWiFiPermissionLauncher.launch(ALL_PERMISSIONS_WIFI_32);
        }
    }

    private void toWiFiSettingMethod1() {
        // android.provider.Settings.ACTION_WIFI_SETTINGS等于Settings.ACTION_WIFI_SETTINGS
        // startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

        Intent intent = new Intent();
        intent.setClassName("com.android.settings",
                "com.android.settings.Settings$WifiSettingsActivity");
        startActivity(intent);
        // startActivity(new Intent());//com.android.settings.Settings$WifiSettingsActivity
        //com.android.settings.Settings$WifiSettingsActivity
    }
}