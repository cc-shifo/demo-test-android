package com.example.demobluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demobluetooth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {// Android 11
            // Manifest.permission.BLUETOOTH_ADMIN
            // Manifest.permission.BLUETOOTH
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                        PackageManager.PERMISSION_GRANTED);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {// Android 12
            // Manifest.permission.BLUETOOTH_CONNECT
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        PackageManager.PERMISSION_GRANTED);
            }
        }


        mBinding.enableBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT)
                            == PackageManager.PERMISSION_GRANTED) {
                        // send action to enable or disable bluetooth
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, 8888);
                    }
                }
            }
        });
        final ActivityResultLauncher<Intent> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK) {
                                // 蓝牙已启用
                                Log.d(TAG, "onActivityResult: ");
                            } else if (result.getResultCode() == RESULT_CANCELED) {
                                // 用户拒绝启用蓝牙
                                Log.d(TAG, "onActivityResult: 用户拒绝启用蓝牙");
                            }
                        });
        mBinding.disableBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT)
                            == PackageManager.PERMISSION_GRANTED) {
                        // send action to enable or disable bluetooth
                        Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_DISABLE");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // startActivityForResult(intent, 8888);
                        // 或者
                        activityResultLauncher.launch(intent);
                    }
                }
            }
        });

        mBinding.settingBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intentOpenBluetoothSettings);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            if (resultCode == RESULT_OK) {
                // 蓝牙已启用
                Log.d(TAG, "onActivityResult: ");
            } else if (resultCode == RESULT_CANCELED) {
                // 用户拒绝启用蓝牙
                Log.d(TAG, "onActivityResult: 用户拒绝启用蓝牙");
            }
        }
    }
}