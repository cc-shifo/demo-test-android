package com.example.demobluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    private BluetoothBroadcastReceiver mBTReceiver;
    private ActivityResultLauncher<Intent> mResultLauncher;

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


        // Manifest.permission.BLUETOOTH_CONNECT permission which can be gained with
        // Activity.requestPermissions(String[], int)
        //
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {// Android 11
            // Manifest.permission.BLUETOOTH_ADMIN permission which can be gained with a simple <uses-permission> manifest tag
            // Manifest.permission.BLUETOOTH_ADMIN
            // Manifest.permission.BLUETOOTH
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN)
                    != PackageManager.PERMISSION_GRANTED) {
                // ActivityCompat.requestPermissions(MainActivity.this,
                //         new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                //         PackageManager.PERMISSION_GRANTED);
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

        mBinding.enableBluetooth.setOnClickListener(view -> enableBluetooth());
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // result.getResultCode() == RESULT_OK 或 RESULT_CANCELED完全不准。改成广播监听。
                    Intent data = result.getData();
                    Log.d(TAG, "ActivityResultCallback: " + result.getResultCode() + ", data=" + data);
                    if (result.getResultCode() == RESULT_OK) {
                        // 蓝牙已启用
                        Log.d(TAG, "ActivityResultCallback: ");
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        // 用户拒绝启用蓝牙
                        Log.d(TAG, "ActivityResultCallback: 用户拒绝启用蓝牙");
                    }
                });


        mBinding.disableBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableBluetooth();
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
    protected void onStart() {
        super.onStart();
        // 注册广播接收器
        mBTReceiver = new BluetoothBroadcastReceiver();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBTReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 注销广播接收器
        unregisterReceiver(mBTReceiver);
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

    public static class BluetoothBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    if (state == BluetoothAdapter.STATE_ON) {
                        // 蓝牙已启用
                        Log.d(TAG, "BluetoothBroadcastReceiver: 蓝牙已启用");
                    } else if (state == BluetoothAdapter.STATE_OFF) {
                        // 蓝牙已禁用
                        Log.d(TAG, "BluetoothBroadcastReceiver: 蓝牙已禁用");
                    }
                }
            }
        }
    }

    private void enableBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        PackageManager.PERMISSION_GRANTED);
                return;
            }
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            // Manifest.permission.BLUETOOTH_ADMIN只需要在Manifest中声明即可，不需要动态申请权限
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN)
                    != PackageManager.PERMISSION_GRANTED) {
                // Manifest.permission.BLUETOOTH_ADMIN permission which can be gained with a simple <uses-permission>
                // manifest tag
                Log.e(TAG, "enableBluetooth: " + Manifest.permission.BLUETOOTH_ADMIN
                     + " permission which can be gained with a simple <uses-permission> manifest tag");
                Toast.makeText(this, "请先授予 " + Manifest.permission.BLUETOOTH_ADMIN + " 权限", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // send action to enable or disable bluetooth
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, 8888);
            return;
        }

        // Build.VERSION.SDK_INT <= Build.VERSION_CODES.R Manifest.permission.BLUETOOTH_ADMIN
        BluetoothManager wifiManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = wifiManager.getAdapter();
        boolean isDisabled = bluetoothAdapter.disable();
        if (isDisabled) {
            Log.d(TAG, "onClick: 蓝牙已禁用");
        } else {
            Log.d(TAG, "onClick: 蓝牙禁用失败");
        }
    }

    private void disableBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        PackageManager.PERMISSION_GRANTED);
                return;
            }
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            // Manifest.permission.BLUETOOTH_ADMIN
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN)
                    != PackageManager.PERMISSION_GRANTED) {
                // Manifest.permission.BLUETOOTH_ADMIN permission which can be gained with a simple <uses-permission>
                // manifest tag
                Log.e(TAG, "enableBluetooth: " + Manifest.permission.BLUETOOTH_ADMIN
                        + " permission which can be gained with a simple <uses-permission> manifest tag");
                Toast.makeText(this, "请先授予 " + Manifest.permission.BLUETOOTH_ADMIN + " 权限", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // send action to enable or disable bluetooth
            Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_DISABLE");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivityForResult(intent, 8888);
            // 或者
            mResultLauncher.launch(intent);
        } else {
            // Build.VERSION.SDK_INT <= Build.VERSION_CODES.R Manifest.permission.BLUETOOTH_ADMIN
            BluetoothManager wifiManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = wifiManager.getAdapter();
            boolean isDisabled = bluetoothAdapter.disable();
            if (isDisabled) {
                Log.d(TAG, "onClick: 蓝牙已禁用");
            } else {
                Log.d(TAG, "onClick: 蓝牙禁用失败");
            }
        }
    }
}
