package com.example.demowificonnectivity;

import android.net.Network;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import timber.log.Timber;

public class WiFiUDPServerHelper {
    public static final String TAG = "MainActivity-Net-Wifi";

    private final ScheduledFuture<?> mScheduledFuture = null;
    private final Future<?> mFuture = null;
    private boolean mStopped = false;
    private Network mWifiNetwork;// 打印
    private DatagramSocket mWifiSocket;
    private final DatagramPacket mRcvPacket;
    DatagramPacket mSendPacket;

    private boolean mConnected;

    private String mIP;
    private int mPort;

    public final MutableLiveData<String> mMsgTextView = new MutableLiveData<>();
    public final MutableLiveData<String> mSendTextView = new MutableLiveData<>();
    public final MutableLiveData<String> mRcvTextView = new MutableLiveData<>();


    private static final int TEXT_BUFFER_SIZE = 4096;
    private final StringBuilder mSendText = new StringBuilder(TEXT_BUFFER_SIZE);
    private final StringBuilder mRcvText = new StringBuilder(TEXT_BUFFER_SIZE);

    public WiFiUDPServerHelper() {
        mRcvPacket = new DatagramPacket(new byte[8192], 8192);
        mSendPacket = new DatagramPacket(new byte[8192], 8192);
    }

    public void mainEnter(/*@NonNull Network network, */@NonNull String ip, int port) {
        mStopped = false;
        mIP = ip;
        // "192.168.137.1", 12345
        mPort = port;
        do {
            createSocket();
            // bindWifiNetwork(network);
            // mRcvPacket.setData(bytes, 0, bytes.length);
            try {
                mWifiSocket.receive(mRcvPacket);
                mSendPacket.setAddress(mRcvPacket.getAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendWifiAlways();
            if (!mStopped) {
                ThreadUtil.safeThreadSleep5000MS();
            }
        } while (!mStopped);
    }

    public void reset() {
        mStopped = false;
    }

    public void createSocket() {
        if (mWifiSocket != null && !mWifiSocket.isClosed()) {
            mWifiSocket.close();
        }
        try {
            mWifiSocket = new DatagramSocket(12345);
            mWifiSocket.setReuseAddress(true);
        } catch (IOException e) {
            Timber.e(e);
            textMessage("createSocket: create Exception!!! " + e);
        }

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
        mWifiNetwork = wifiNetwork;
        try {
            if (mWifiSocket != null) {
                wifiNetwork.bindSocket(mWifiSocket);
            }
        } catch (IOException e) {
            Timber.tag(TAG).e(e);
        }
    }
    public void connectAlways() {
        while (!mStopped && (mWifiSocket == null || !mWifiSocket.isClosed())) {
            connectWifi();
            if (mWifiSocket != null) {
                break;
            }
        }
    }

    public void connectWifi() {
        try {
            InetAddress address = InetAddress.getByName(mIP);
            mWifiSocket.setSoTimeout(2000);
            mWifiSocket.connect(address, mPort);
            textMessage("connectWifi: true");
            mConnected = true;
        } catch (IOException e) {
            Timber.e(e);
            textMessage("connectWifi: connect Exception!!! " + e);
            mConnected = false;
            mWifiSocket = null;
        }
    }

    public void sendWifiAlways() {
        byte[] send = new byte[8192];
        while (!mStopped && mWifiSocket != null && !mWifiSocket.isClosed()) {
            int i = sendWifi(send, 0, send.length);
            if (i > 0) {
                textDebugSend(send, i);
            }

            SocketAddress address = mWifiSocket.getRemoteSocketAddress();
            if (address == null) {
                ThreadUtil.safeThreadSleep5000MS();
            } else {
                mSendPacket.setAddress(((InetSocketAddress)address).getAddress());
            }
        }
    }

    public int sendWifi(byte[] bytes, int off, int len) {
        OutputStream out = null;
        if (mWifiSocket == null || mWifiSocket.isClosed()) {
            textMessage("sendWifi: mWifiSocket null or closed");
            return -1;
        }
        try {
            mSendPacket.setData(bytes, off, len);
            mWifiSocket.send(mSendPacket);
            textDebugSend(bytes, len);
            return len;
        } catch (IOException e) {
            textMessage("sendWifi: " + e);
            Timber.e(e);
            if (!mWifiSocket.isClosed()) {
                mWifiSocket.close();
            }
        } /*finally {
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
        }*/

        return -1;
    }

    public void rcvWifiAlways() {
        byte[] rcv = new byte[8192];
        while (!mStopped && mWifiSocket != null && !mWifiSocket.isClosed()) {
            int i = rcvWifi(rcv);
            if (i > 0) {
                textDebugRcv(rcv, i);
            }
        }
    }

    public int rcvWifi(byte[] bytes) {
        InputStream in = null;
        if (mWifiSocket == null || mWifiSocket.isClosed()) {
            textMessage("rcvWifi: mWifiSocket null or closed");
            return -1;
        }
        try {
            // StandardSocketOptions, 读写之前设置才有效。
            mWifiSocket.setSoTimeout(5000); // SocketOptions.SO_TIMEOUT
            mRcvPacket.setData(bytes, 0, bytes.length);
            mWifiSocket.receive(mRcvPacket);
            textDebugRcv(mRcvPacket.getData(), mRcvPacket.getLength());
            return mRcvPacket.getLength();
        } catch (IOException e) {
            if (!(e instanceof SocketTimeoutException)) {
                textMessage("rcvWifi: " + e);
                Timber.e(e);
                if (!mWifiSocket.isClosed()) {
                    mWifiSocket.close();
                }
            }
        } /*finally {
            // if (in != null) {
            //     try {
            //         in.close(); // 不能单独关闭，否则整个socket的双向都被关了。
            //     } catch (IOException e) {
            //         Timber.e(e);
            //     }
            // }
        }*/

        return -1;
    }

    public void disconnectWifi() {
        mStopped = true;
        if (mWifiSocket != null && !mWifiSocket.isClosed()) {
            mWifiSocket.close();
            mWifiSocket = null;
            textMessage("disconnectWifi: true");
        }
    }

    public void destroy() {
        disconnectWifi();
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
        }
        textMessage("connected: network lost");
    }

    private void textDebugSend(byte[] buff, int len) {
        if (mSendText.length() + len * 3 >= TEXT_BUFFER_SIZE) {
            mSendText.setLength(0);
        }
        String s = String.format("[socket-send]>>: %s\n", HexUtil.byte2Hex(buff, len));
        mSendText.append(s);
        mSendTextView.postValue(mSendText.toString());
    }

    private void textDebugRcv(byte[] buff, int len) {
        if (mRcvText.length() + len * 3 >= TEXT_BUFFER_SIZE) {
            mRcvText.setLength(0);
        }
        String s = String.format("[socket-rcv]<<: %s\n", HexUtil.byte2Hex(buff, len));
        mRcvText.append(s);
        mRcvTextView.postValue(mRcvText.toString());
    }

    private void textMessage(@NonNull String message) {
        mMsgTextView.postValue(message);
    }
}
