package com.example.demowificonnectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class CellModel {
    private final ScheduledThreadPoolExecutor mExecutor = new ScheduledThreadPoolExecutor(3);
    private ConnectivityManager.NetworkCallback mWiFiCallback;
    private final CellTCPHelper mWifiTCPHelper = new CellTCPHelper();

    // 连接成功后发送序号清零
    private long mSendSerialNum = 0;

    public void listen(@NonNull Context context) {
        mWiFiCallback = new BaseNetCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                startService(network);
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                stopService();
            }
        };
        NetworkHelper.registerCellNetworkCallback(context, mWiFiCallback);
    }

    public void cancelListen(@NonNull Context context) {
        stopService();
        if (mWiFiCallback != null) {
            NetworkHelper.unregisterCellNetworkCallback(context, mWiFiCallback);
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

    public void bindWifiNetwork(@NonNull Network wifiNetwork) {
        mWifiTCPHelper.bindWifiNetwork(wifiNetwork);
    }

    public void connectAlways(@NonNull final Network network) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mWifiTCPHelper.reset();
                mWifiTCPHelper.createSocket();
                mWifiTCPHelper.bindWifiNetwork(network);
                mWifiTCPHelper.connectAlways();
            }
        });
    }

    public void connectWifi12345() {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mWifiTCPHelper.connectCell();
            }
        });
    }

    public void disconnectWifi() {
        mWifiTCPHelper.disconnectCell();
    }

    public void sendWifi12345() {
        final byte[] bytes = new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05,
                0x06, 0x07, 0x08, 0x09, 0x0A,
                0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00
        };
        mSendSerialNum++;
        bytes[10] = (byte) ((mSendSerialNum >>> 56) & 0xFF);
        bytes[11] = (byte) ((mSendSerialNum >>> 48) & 0xFF);
        bytes[12] = (byte) ((mSendSerialNum >>> 40) & 0xFF);
        bytes[13] = (byte) ((mSendSerialNum >>> 32) & 0xFF);
        bytes[14] = (byte) ((mSendSerialNum >>> 24) & 0xFF);
        bytes[15] = (byte) ((mSendSerialNum >>> 16) & 0xFF);
        bytes[16] = (byte) ((mSendSerialNum >>> 8) & 0xFF);
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

    public void rcvCellAlways() {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mWifiTCPHelper.rcvCellAlways();
            }
        });
    }

    private void startService(@NonNull final Network network) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (mWifiTCPHelper != null) {
                    mWifiTCPHelper.disconnectCell();
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
