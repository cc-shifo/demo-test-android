package com.example.demowificonnectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class WiFiUDPServerModel {
    private final ScheduledThreadPoolExecutor mExecutor = new ScheduledThreadPoolExecutor(5);
    private ConnectivityManager.NetworkCallback mWiFiCallback;
    private final WiFiUDPServerHelper mWifiTCPHelper = new WiFiUDPServerHelper();
    private final APHelper mAPHelper = new APHelper();

    // 连接成功后发送序号清零
    private long mSendSerialNum = 0;

    public void listen(@NonNull Context context) {
        // mAPHelper.register(context);
        // mAPHelper.getAllIPs();
        // ConnectivityManager manager = (ConnectivityManager) context
        //                     .getSystemService(Context.CONNECTIVITY_SERVICE);
        // Network network = manager.getActiveNetwork();
        //
        //
        // mWiFiCallback = new BaseNetCallback() {
        //     @Override
        //     public void onAvailable(@NonNull Network network) {
        //         super.onAvailable(network);
        //         String address = "";
        //         String ipAddress = "";
        //         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        //             ConnectivityManager manager = (ConnectivityManager) context
        //                     .getSystemService(Context.CONNECTIVITY_SERVICE);
        //             LinkProperties linkProperties = manager.getLinkProperties(network);
        //             address = linkProperties.getDhcpServerAddress().getHostAddress();
        //             // linkProperties.getLinkAddresses(); // 本机地址
        //         } else {
        //             WifiManager wifiManager = (WifiManager) MyAPP.getMyAPP()
        //                     .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //             // 31 Build.VERSION_CODES.S deprecated
        //             DhcpInfo info = wifiManager.getDhcpInfo();
        //             if (info != null) {
        //                 ipAddress = WifiTCPHelper.ipv4Int2Str(info.ipAddress);
        //                 address = WifiTCPHelper.ipv4Int2Str(info.serverAddress);
        //             }
        //         }
        //         // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        //         //     capabilities.getNetworkSpecifier();
        //         // }
        //         // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        //         //     capabilities.getTransportInfo(); // 连接到热点时，获取的是本机当前地址
        //         // }
        //         Timber.d("initNetworkCallback: server address %s, local ip %s", address, ipAddress);
        //         if (!TextUtils.isEmpty(address)) {
        //             // startService(network, address, 12345);
        //         }
        //     }
        //
        //     @Override
        //     public void onCapabilitiesChanged(@NonNull Network network,
        //             @NonNull NetworkCapabilities capabilities) {
        //         super.onCapabilitiesChanged(network, capabilities);
        //     }
        //
        //     @Override
        //     public void onLost(@NonNull Network network) {
        //         super.onLost(network);
        //         stopService();
        //     }
        // };
        // NetworkHelper.registerWiFiNetworkCallback(context, mWiFiCallback);
        startService("", 12345);
    }

    public void cancelListen(@NonNull Context context) {
        // mAPHelper.unregister(context);
        // mAPHelper.stop();
        stopService();
        if (mWiFiCallback != null) {
            NetworkHelper.unregisterWiFiNetworkCallback(context, mWiFiCallback);
            mWiFiCallback = null;
        }
    }

    public LiveData<String> getMsg() {
        return mWifiTCPHelper.mMsgTextView;
    }

    public LiveData<String> getSentData() {
        return mWifiTCPHelper.mSendTextView;
    }

    public LiveData<String> getRcvData() {
        return mWifiTCPHelper.mRcvTextView;
    }

    public void createSocket() {
        mWifiTCPHelper.reset();
        mWifiTCPHelper.createSocket();

        // if (mWifiNetwork != null) {
        //     if (mWifiSocket != null && !mWifiSocket.isClosed()) {
        //         try {
        //             mWifiSocket.close();
        //         } catch (IOException e) {
        //             Timber.e(e);
        //             textMessage("connectWifi: close Exception!!! " + e);
        //         }
        //     }
        //
        //
        //     SocketFactory wifiSocket = mWifiNetwork.getSocketFactory();
        //     try {
        //         mWifiSocket = wifiSocket.createSocket();
        //     } catch (IOException e) {
        //         Timber.e(e);
        //         textMessage("connectWifi: connect Exception!!! " + e);
        //         mWifiSocket = null;
        //     }
        // }
    }

    public void bindWifiNetwork(@NonNull Network wifiNetwork) {
        mWifiTCPHelper.bindWifiNetwork(wifiNetwork);
    }

    public void connectWifi12345() {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mWifiTCPHelper.connectWifi();
            }
        });
    }

    public void disconnectWifi() {
        mWifiTCPHelper.disconnectWifi();
    }

    public void sendWifi12345() {
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
            mWifiTCPHelper.sendWifi(bytes, 0, bytes.length);
            // byte[] rcv = new byte[8192];
            // int i = rcvWifi(rcv);
            // if (i > 0) {
            //     textRcv(rcv, i);
            // }
        });
    }

    public void rcvWifi12345(@NonNull final byte[] rcv) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mWifiTCPHelper.rcvWifi(rcv);
            }
        });
    }

    private void startService(/*@NonNull final Network network,*/
            @NonNull final String address, int port) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (mWifiTCPHelper != null) {
                    mWifiTCPHelper.disconnectWifi();
                    mWifiTCPHelper.mainEnter(/*network, */address, port);
                }
            }
        });
    }

    private void stopService() {
        if (mWifiTCPHelper != null) {
            mWifiTCPHelper.destroy();
        }
    }
}
