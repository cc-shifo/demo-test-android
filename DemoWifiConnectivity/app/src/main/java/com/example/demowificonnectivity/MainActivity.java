package com.example.demowificonnectivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.demowificonnectivity.databinding.ActivityMainBinding;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    public static final String TAG = "MainActivity-Net";
    /**
     * Android 11及以上。
     */
    private static final String[] API30_REQUIRED_PERMISSION_LIST = new String[]{
            // Manifest.permission.VIBRATE, // Gimbal rotation
            android.Manifest.permission.INTERNET, // API requests
            android.Manifest.permission.ACCESS_WIFI_STATE, // WIFI connected products
            android.Manifest.permission.ACCESS_COARSE_LOCATION, // Maps
            android.Manifest.permission.ACCESS_NETWORK_STATE, // WIFI connected products
            android.Manifest.permission.ACCESS_FINE_LOCATION, // Maps
            android.Manifest.permission.CHANGE_WIFI_STATE,
            // Changing between WIFI and USB connection
            // Manifest.permission.WRITE_EXTERNAL_STORAGE, // Log files
            // Manifest.permission.BLUETOOTH, // Bluetooth connected products
            // Manifest.permission.BLUETOOTH_ADMIN, // Bluetooth connected products
            android.Manifest.permission.READ_EXTERNAL_STORAGE, // Log files
            android.Manifest.permission.READ_PHONE_STATE, // Device UUID accessed upon registration

            // DJI 13
            // Manifest.permission.RECORD_AUDIO // Speaker accessory
            // Manifest.permission.READ_MEDIA_IMAGES,
            // Manifest.permission.READ_MEDIA_VIDEO,
            // Manifest.permission.READ_MEDIA_AUDIO,
    };

    /**
     * Android 10及以下。Manifest中requestLegacyExternalStorage=true
     */
    private static final String[] API29_REQUIRED_PERMISSION_LIST = new String[]{
            // Manifest.permission.VIBRATE, // Gimbal rotation
            android.Manifest.permission.INTERNET, // API requests
            android.Manifest.permission.ACCESS_WIFI_STATE, // WIFI connected products
            android.Manifest.permission.ACCESS_COARSE_LOCATION, // Maps
            android.Manifest.permission.ACCESS_NETWORK_STATE, // WIFI connected products
            android.Manifest.permission.ACCESS_FINE_LOCATION, // Maps
            android.Manifest.permission.CHANGE_WIFI_STATE,
            // Changing between WIFI and USB connection
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, // Log files
            // Manifest.permission.BLUETOOTH, // Bluetooth connected products
            // Manifest.permission.BLUETOOTH_ADMIN, // Bluetooth connected products
            android.Manifest.permission.READ_EXTERNAL_STORAGE, // Log files
            Manifest.permission.READ_PHONE_STATE, // Device UUID accessed upon registration
            // Manifest.permission.RECORD_AUDIO // Speaker accessory
    };

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(mBinding.getRoot());
        requestPermissions();
        // setContentView(R.layout.activity_main);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && EasyPermissions.hasPermissions(this, API30_REQUIRED_PERMISSION_LIST))
                || (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
                EasyPermissions.hasPermissions(this,
                        API29_REQUIRED_PERMISSION_LIST))) {
            init();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        requestPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            Timber.d("isFinishing: true");
        } else {
            Timber.d("isFinishing: isFinishing false");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isDestroyed()) {
            Timber.d("isFinishing: isDestroyed true");
        } else {
            Timber.d("isFinishing: isDestroyed false");
        }
        if (mWiFiStateHelper != null) {
            mWiFiStateHelper.unregisterBroadcast(this);
        }
        if (mWifiTCPModel != null) {
            mWifiTCPModel.cancelListen(this);
        }

        if (mCellModel != null) {
            mCellModel.cancelListen(this);
        }

        if (mWiFiUDPServerModel != null) {
            mWiFiUDPServerModel.cancelListen(this);
        }

        if (mWiFiUDPClientModel != null) {
            mWiFiUDPClientModel.cancelListen(this);
        }
    }

    private static final int RC_ALL_PERMISSION = 1004;
    // private NetworkHelper mNetworkHelper;
    private WiFiStateHelper mWiFiStateHelper;

    /**
     * TODO 分类型请求。分系统版本请求。
     */
    private void requestPermissions() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && EasyPermissions.hasPermissions(this, API30_REQUIRED_PERMISSION_LIST))
                || (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
                EasyPermissions.hasPermissions(this,
                        API29_REQUIRED_PERMISSION_LIST))) {
            init();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && !EasyPermissions.hasPermissions(this, API30_REQUIRED_PERMISSION_LIST)) {
            EasyPermissions.requestPermissions(this,
                    getString(
                            R.string.request_permissions),
                    RC_ALL_PERMISSION, API30_REQUIRED_PERMISSION_LIST);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
                !EasyPermissions.hasPermissions(this,
                        API29_REQUIRED_PERMISSION_LIST)) {
            EasyPermissions.requestPermissions(this,
                    getString(
                            R.string.request_permissions),
                    RC_ALL_PERMISSION, API29_REQUIRED_PERMISSION_LIST);
        }
    }


    /**
     * initialization.
     */
    private void init() {
        mBinding.tvSend.setMovementMethod(ScrollingMovementMethod.getInstance());
        mBinding.tvRcv.setMovementMethod(ScrollingMovementMethod.getInstance());

        mWiFiStateHelper = new WiFiStateHelper();
        mWiFiStateHelper.registerBroadcast(this);


        mWifiTCPModel = new WiFiModel();
        mWifiTCPModel.getMsg().observe(this, s -> mBinding.tvMessage.setText(s));
        mWifiTCPModel.getSentData().observe(this, s -> mBinding.tvSend.setText(s));
        mWifiTCPModel.getRcvData().observe(this, s -> mBinding.tvRcv.setText(s));
        mWifiTCPModel.listen(this);
        openSetting();
        connectWifi12345();
        sendWifi12345();
        rcvWifi12345();

        mCellModel = new CellModel();
        // mCellModel.getMsg().observe(this, s -> mBinding.tvCellMessage.setText(s));
        // mWifiTCPModel.getSentData().observe(this, s -> mBinding.tvCellSend.setText(s));
        // mWifiTCPModel.getRcvData().observe(this, s -> mBinding.tvCellRcv.setText(s));
        // mCellModel.listen(MainActivity.this);
        // // connectCell12345();
        // // sendCell12345();
        // // rcvCell12345();
        // // connectCellAlways();
        // // rcvCellAlways();
        // sendCellManually();



        mWiFiUDPServerModel = new WiFiUDPServerModel();
        mWiFiUDPClientModel = new WiFiUDPClientModel();

        mBinding.rbServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWiFiUDPClientModel.cancelListen(MainActivity.this);
                    clearTextObservers();

                    mWiFiUDPServerModel.getMsg().observe(MainActivity.this, mCellMessageObserver);
                    mWiFiUDPServerModel.getSentData().observe(MainActivity.this, mCellSendObserver);
                    mWiFiUDPServerModel.getRcvData().observe(MainActivity.this, mCellRcvObserver);
                    // mWiFiUDPServerModel.listen(MainActivity.this);
                }
            }
        });
        mBinding.rbClient.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mWiFiUDPServerModel.cancelListen(MainActivity.this);
                clearTextObservers();

                mWiFiUDPClientModel.getMsg().observe(MainActivity.this, mCellMessageObserver);
                mWiFiUDPClientModel.getSentData().observe(MainActivity.this, mCellSendObserver);
                mWiFiUDPClientModel.getRcvData().observe(MainActivity.this, mCellRcvObserver);
                // mWiFiUDPClientModel.listen(MainActivity.this);
            }
        });

        mBeingInListening = false;
        mBinding.btnCellStartManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRightBtnsText();
                if (mBinding.rbServer.isChecked()) {
                    mWiFiUDPClientModel.cancelListen(MainActivity.this);
                    mWiFiUDPServerModel.listen(MainActivity.this);
                    mBeingInListening = true;
                } else if (mBinding.rbClient.isChecked()) {
                    mWiFiUDPServerModel.cancelListen(MainActivity.this);
                    mWiFiUDPClientModel.listen(MainActivity.this);
                    mBeingInListening = true;
                }
            }
        });
        mBinding.btnCellStopManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWiFiUDPServerModel.cancelListen(MainActivity.this);
                mWiFiUDPClientModel.cancelListen(MainActivity.this);
                mBeingInListening = false;
            }
        });

        // connectCell12345();
        // sendCell12345();
        // rcvCell12345();
        // connectCellAlways();
        // rcvCellAlways();
        sendCellManually();
    }

    private Observer<String> mMsgObserver;
    private Observer<String> mSentDataObserver;
    private Observer<String> mRcvDataObserver;
    private final Observer<String> mCellMessageObserver = new Observer<String>() {
        public void onChanged(String msg) {
            mBinding.tvCellMessage.setText(msg);
        }
    };
    private final Observer<String> mCellSendObserver = new Observer<String>() {
        @Override
        public void onChanged(String sendData) {
            mBinding.tvCellSend.setText(sendData);
        }
    } ;
    private final Observer<String> mCellRcvObserver = new Observer<String>() {
        @Override
        public void onChanged(String rcvData) {
            mBinding.tvCellRcv.setText(rcvData);
        }
    };
    private void clearTextObservers() {
        // mWiFiUDPServerModel.getMsg().
        mWiFiUDPServerModel.getMsg().removeObserver(mCellMessageObserver);
        mWiFiUDPServerModel.getSentData().removeObserver(mCellSendObserver);
        mWiFiUDPServerModel.getRcvData().removeObserver(mCellRcvObserver);
        mWiFiUDPClientModel.getMsg().removeObserver(mCellMessageObserver);
        mWiFiUDPClientModel.getSentData().removeObserver(mCellSendObserver);
        mWiFiUDPClientModel.getRcvData().removeObserver(mCellRcvObserver);
    }

    private void clearRightBtnsText() {
        mBinding.tvCellMessage.setText(R.string.hello_world);
        mBinding.tvCellSend.setText(R.string.hello_world);
        mBinding.tvCellRcv.setText(R.string.hello_world);
    }

    private void clearTextManuallyLeftBtns() {
        mBinding.tvMessage.setText(R.string.hello_world);
        mBinding.tvSend.setText(R.string.hello_world);
        mBinding.tvRcv.setText(R.string.hello_world);
    }


    private Network mWifiNetwork;// 打印
    private Network mCellNetwork; // 打印
    private void getNetWork() {
        mWifiNetwork = null;
        mCellNetwork = null;
        // https://www.jianshu.com/p/d261e5b7ea38
        ConnectivityManager manager = (ConnectivityManager)
                MyAPP.getMyAPP().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = manager.getAllNetworks();
        for (Network n : networks) {
            NetworkCapabilities c = manager.getNetworkCapabilities(n);
            manager.getNetworkInfo(n);
            if (c.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                mWifiNetwork = n;
                c.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else if (c.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                mCellNetwork = n;
            }
        }

        if (mWifiNetwork == null) {
            Timber.tag(TAG).d("mWifiNetwork null");
        } else {
            Timber.tag(TAG).d("mWifiNetwork %s", mWifiNetwork.toString());
        }

        if (mCellNetwork == null) {
            Timber.tag(TAG).d("mCellNetwork null");
        } else {
            Timber.tag(TAG).d("mCellNetwork %s", mCellNetwork.toString());
        }
    }

    private void openSetting() {
        mBinding.btnOpenSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });
    }

    private WiFiModel mWifiTCPModel;
    private void connectWifi12345() {
        mBinding.btnConnect12345.setOnClickListener(v -> {
            getNetWork();
            if (mWifiNetwork != null) {
                mWifiTCPModel.createSocket();
                mWifiTCPModel.bindWifiNetwork(mWifiNetwork);
            }
            mWifiTCPModel.connectWifi12345();
        });
        mBinding.btnDisconnect12345.setOnClickListener(v -> mWifiTCPModel.disconnectWifi());
    }

    private void sendWifi12345() {
        mBinding.btnSend12345.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiTCPModel.sendWifi12345();
            }
        });
    }

    private void rcvWifi12345() {
        mBinding.btnRcv12345.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = new byte[8192];
                mWifiTCPModel.rcvWifi12345(bytes);
            }
        });
    }


    // cellular
    private CellModel mCellModel;
    // private void connectCell12345() {
    //     mBinding.btnConnectCell12345.setOnClickListener(v -> {
    //         mCellModel.connectWifi12345();
    //     });
    //     mBinding.btnDisconnectCell12345.setOnClickListener(v -> {
    //         mCellModel.disconnectWifi();
    //     });
    // }
    //
    // // cellular
    // private void sendCell12345() {
    //     mBinding.btnSendCell12345.setOnClickListener(v -> {
    //         mCellModel.sendWifi12345();
    //     });
    // }
    //
    // // cellular
    // private void rcvCell12345() {
    //     mBinding.btnRcvCell12345.setOnClickListener(v -> {
    //         byte[] bytes = new byte[8192];
    //         mCellModel.rcvWifi12345(bytes);
    //     });
    // }
    //
    // public void connectCellAlways() {
    //     mBinding.btnCellConAlways.setOnClickListener(new View.OnClickListener() {
    //         @Override
    //         public void onClick(View v) {
    //             mCellModel.connectAlways();
    //         }
    //     });
    // }
    //
    // public void rcvCellAlways() {
    //     mBinding.btnCellConAlways.setOnClickListener(new View.OnClickListener() {
    //         @Override
    //         public void onClick(View v) {
    //             mCellModel.connectAlways();
    //         }
    //     });
    // }

    // cellular
    private void sendCellManually() {
        mBinding.btnCellSendManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCellModel.sendWifi12345();
            }
        });
    }

    private WiFiUDPServerModel mWiFiUDPServerModel;
    private WiFiUDPClientModel mWiFiUDPClientModel;
    private boolean mBeingInListening;

}