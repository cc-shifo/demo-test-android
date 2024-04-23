package com.example.demowificonnectivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demowificonnectivity.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.net.SocketFactory;

import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private ScheduledThreadPoolExecutor mExecutor = new ScheduledThreadPoolExecutor(3);
    private ScheduledFuture<?> mScheduledFuture = null;
    private Future<?> mFuture = null;
    private boolean mStopped = false;
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
    protected void onDestroy() {
        super.onDestroy();
        disconnectWifi();
        if (mExecutor != null) {
            mExecutor.shutdown();
        }
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
        }
    }

    private static final int RC_ALL_PERMISSION = 1004;

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
        openSetting();
        connectWifi12345();
        sendWifi12345();
        rcvWifi12345();
    }

    private Network mWifiNetwork;
    private Socket mWifiSocket;
    private Network mCellNetwork;

    private void getNetWork() {
        mWifiNetwork = null;
        mCellNetwork = null;
        // https://www.jianshu.com/p/d261e5b7ea38
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private void connectWifi12345() {
        mBinding.btnConnect12345.setOnClickListener(v -> connectWifi());
        mBinding.btnDisconnect12345.setOnClickListener(v -> disconnectWifi());
    }

    // 连接成功后发送序号清零
    private long mSendSerialNum = 0;

    private static final int TEXT_BUFFER_SIZE = 4096;
    private StringBuilder mSendText = new StringBuilder(TEXT_BUFFER_SIZE);
    private StringBuilder mRcvText = new StringBuilder(TEXT_BUFFER_SIZE);
    private void sendWifi12345() {
        mBinding.btnSend12345.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final byte[] bytes = new byte[]{
                        0x01, 0x02, 0x03, 0x04, 0x05,
                        0x06, 0x07, 0x08, 0x09, 0x0A,
                        0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00
                };
                mSendSerialNum++;
                bytes[10] = (byte) ((mSendSerialNum >> 56) & 0xFF);
                bytes[11] = (byte) ((mSendSerialNum >> 48) & 0xFF);
                bytes[12] = (byte) ((mSendSerialNum >> 40) & 0xFF);
                bytes[13] = (byte) ((mSendSerialNum >> 32) & 0xFF);
                bytes[14] = (byte) ((mSendSerialNum >> 24) & 0xFF);
                bytes[15] = (byte) ((mSendSerialNum >> 16) & 0xFF);
                bytes[16] = (byte) ((mSendSerialNum >> 8) & 0xFF);
                bytes[17] = (byte) (mSendSerialNum & 0xFF);
                mExecutor.submit(() -> {
                    sendWifi(bytes, bytes.length);
                    // byte[] rcv = new byte[8192];
                    // int i = rcvWifi(rcv);
                    // if (i > 0) {
                    //     textRcv(rcv, i);
                    // }
                });
            }
        });
    }

    private void rcvWifi12345() {
        mBinding.btnRcv12345.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        byte[] rcv = new byte[8192];
                        while (!mStopped && mWifiSocket != null && !mWifiSocket.isClosed()) {
                            int i = rcvWifi(rcv);
                            if (i > 0) {
                                textRcv(rcv, i);
                            }
                        }

                    }
                });
            }
        });
    }


    private void connectWifi() {
        getNetWork();
        if (mFuture != null && (!mFuture.isDone() || !mFuture.isCancelled())) {
            mFuture.cancel(true);
        }
        mFuture = mExecutor.submit(() -> {
            if (mWifiNetwork != null) {
                if (mWifiSocket != null && !mWifiSocket.isClosed()) {
                    try {
                        mWifiSocket.close();
                    } catch (IOException e) {
                        Timber.e(e);
                        textMessage("connectWifi: close Exception!!! " + e);
                    }
                }
                SocketFactory wifiSocket = mWifiNetwork.getSocketFactory();
                try {
                    mWifiSocket = wifiSocket.createSocket();
                    InetSocketAddress a = new InetSocketAddress("192.168.137.1", 12345);
                    mWifiSocket.connect(a, 15000);
                    mSendSerialNum = 0;
                    textMessage("connectWifi: true");
                } catch (IOException e) {
                    Timber.e(e);
                    textMessage("connectWifi: connect Exception!!! " + e);
                    mWifiSocket = null;
                }
            }
            return null;
        });

    }


    private int sendWifi(byte[] bytes, int len) {
        OutputStream out = null;
        if (mWifiSocket == null || mWifiSocket.isClosed()) {
            textMessage("sendWifi: mWifiSocket null or closed");
            return -1;
        }
        try {
            out = mWifiSocket.getOutputStream();
            out.write(bytes, 0, len);
            textSend(bytes, len);
            return len;
        } catch (IOException e) {
            Timber.e(e);
            if (!mWifiSocket.isClosed()) {
                try {
                    mWifiSocket.close();
                } catch (IOException ex) {
                    Timber.e(ex);
                    textMessage("sendWifi: " + ex);
                }
            }
        } finally {
            // if (out != null) {
            //     try {
            //         mWifiSocket.shutdownOutput();
            //     } catch (IOException e) {
            //         Timber.e(e);
            //     }
            // }
            // if (out != null) {
            //     try {
            //         out.close(); // 整个socket都关闭了
            //     } catch (IOException e) {
            //         Timber.e(e);
            //     }
            // }
        }

        return -1;
    }

    private int rcvWifi(byte[] bytes) {
        InputStream in = null;
        if (mWifiSocket == null || mWifiSocket.isClosed()) {
            textMessage("rcvWifi: mWifiSocket null or closed");
            return -1;
        }
        try {
            // StandardSocketOptions, 读写之前设置才有效。
            mWifiSocket.setSoTimeout(10000); // SocketOptions.SO_TIMEOUT
            in = mWifiSocket.getInputStream();
            return in.read(bytes, 0, bytes.length);
        } catch (IOException e) {
            if (!(e instanceof SocketTimeoutException)) {
                Timber.e(e);
                if (!mWifiSocket.isClosed()) {
                    try {
                        mWifiSocket.close();
                    } catch (IOException ex) {
                        Timber.e(ex);
                        textMessage("rcvWifi: " + ex);
                    }
                }
            }
        } finally {
            // if (in != null) {
            //     try {
            //         in.close(); // 不能单独关闭，否则整个socket的双向都被关了。
            //     } catch (IOException e) {
            //         Timber.e(e);
            //     }
            // }
        }

        return -1;
    }

    private void disconnectWifi() {
        mStopped = true;
        if (mWifiSocket != null && !mWifiSocket.isClosed()) {
            try {
                mWifiSocket.close();
            } catch (IOException e) {
                Timber.e(e);
            }
            mWifiSocket = null;
            textMessage("disconnectWifi: true");
        }
    }


    public void textSend(byte[] buff, int len) {
        if (mSendText.length() + len * 3 >= TEXT_BUFFER_SIZE) {
            mSendText.setLength(0);
        }
        String s = String.format("[socket-send]>>: %s\n", HexUtil.byte2Hex(buff, len));
        mSendText.append(s);
        mBinding.tvSend.post(new Runnable() {
            @Override
            public void run() {
                mBinding.tvSend.setText(mSendText.toString());
            }
        });
    }

    public void textRcv(byte[] buff, int len) {
        if (mRcvText.length() + len * 3 >= TEXT_BUFFER_SIZE) {
            mRcvText.setLength(0);
        }
        String s = String.format("[socket-rcv]<<: %s\n", HexUtil.byte2Hex(buff, len));
        mRcvText.append(s);
        mBinding.tvRcv.post(new Runnable() {
            @Override
            public void run() {
                mBinding.tvRcv.setText(mRcvText.toString());
            }
        });
    }

    public void textMessage(@NonNull String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mBinding.tvMessage.setText(message);
        } else {
            mBinding.tvMessage.post(new Runnable() {
                @Override
                public void run() {
                    mBinding.tvMessage.setText(message);
                }
            });
        }
    }
}