package com.example.demowificonnectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import androidx.annotation.NonNull;

import java.net.Socket;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class WiFiModel {
    private ScheduledThreadPoolExecutor mExecutor = new ScheduledThreadPoolExecutor(3);
    private ConnectivityManager.NetworkCallback mWiFiCallback;
    private WifiTCPHelper mWifiTCPHelper = new WifiTCPHelper();

    // 连接成功后发送序号清零
    private long mSendSerialNum = 0;

    public void listen(@NonNull Context context) {
        mWiFiCallback = new BaseNetCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                // startService(network);
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                // stopService();
            }
        };
        NetworkHelper.registerWiFiNetworkCallback(context, mWiFiCallback);
    }

    public void cancelListen(@NonNull Context context) {
        stopService();
        if (mWiFiCallback != null) {
            NetworkHelper.unregisterWiFiNetworkCallback(context, mWiFiCallback);
            mWiFiCallback = null;
        }
    }

    public void createSocket() {
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
            mWifiTCPHelper.sendWifi(bytes, bytes.length);
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

    private void startService(@NonNull final Network network) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (mWifiTCPHelper != null) {
                    mWifiTCPHelper.destroy();
                    mWifiTCPHelper.mainEnter(network);
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
