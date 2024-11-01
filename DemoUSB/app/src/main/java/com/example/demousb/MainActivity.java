package com.example.demousb;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

import com.example.demousb.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1024;


    private ActivityMainBinding mBinding;
    /**
     * Android 10及以下。Manifest中requestLegacyExternalStorage=true
     */
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            // Manifest.permission.VIBRATE, // Gimbal rotation
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, // Log files
            // Manifest.permission.BLUETOOTH, // Bluetooth connected products
            // Manifest.permission.BLUETOOTH_ADMIN, // Bluetooth connected products
            android.Manifest.permission.READ_EXTERNAL_STORAGE, // Log files
            // Manifest.permission.RECORD_AUDIO // Speaker accessory
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        checkAndRequestPermissions();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onPermissionResult(requestCode, permissions, grantResults);
    }

    private void checkAndRequestPermissions() {
        if (hasPermissions(this, REQUIRED_PERMISSION_LIST)) {
            init();
        } else {
            requestPermissions(REQUIRED_PERMISSION_LIST, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean hasPermissions(@NonNull Context context,
            @Size(min = 1) @NonNull String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");

            // DANGER ZONE!!! Changing this will break the library.
            return true;
        }

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private void onPermissionResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && permissions.length == grantResults.length) {
                int i;
                for (i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        break;
                    }
                }

                if (i == grantResults.length) {
                    init();// 都允许
                } else {
                    requestPermissions(REQUIRED_PERMISSION_LIST, PERMISSION_REQUEST_CODE);
                }
            }
        }
    }

    private void init() {
        mBinding.btnUsbDisk.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UDiskActivity.class);
            startActivity(intent);
        });
        mBinding.btnUsbUsb.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, USBActivity.class);
            startActivity(intent);
        });
    }

}