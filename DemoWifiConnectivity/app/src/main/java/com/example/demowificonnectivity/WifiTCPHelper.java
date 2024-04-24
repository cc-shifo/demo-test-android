package com.example.demowificonnectivity;

import android.net.Network;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import timber.log.Timber;

public class WifiTCPHelper  extends BaseTCPHelper {
    public static final String TAG = "MainActivity-Net-Wifi";

    private ScheduledFuture<?> mScheduledFuture = null;
    private Future<?> mFuture = null;
    private boolean mStopped = false;
    private Network mWifiNetwork;// 打印
    private Socket mWifiSocket;

    public final MutableLiveData<String> mMsgTextView = new MutableLiveData<>();
    public final MutableLiveData<String> mSendTextView = new MutableLiveData<>();
    public final MutableLiveData<String> mRcvTextView = new MutableLiveData<>();



    private static final int TEXT_BUFFER_SIZE = 4096;
    private StringBuilder mSendText = new StringBuilder(TEXT_BUFFER_SIZE);
    private StringBuilder mRcvText = new StringBuilder(TEXT_BUFFER_SIZE);


    public void createSocket() {
        if (mWifiSocket != null && !mWifiSocket.isClosed()) {
            try {
                mWifiSocket.close();
            } catch (IOException e) {
                Timber.e(e);
                textMessage("connectWifi: close Exception!!! " + e);
            }
        }
        mWifiSocket = new Socket();

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

    public void mainEnter(@NonNull Network network) {
        createSocket();
        bindWifiNetwork(network);
        connectAlways();
        rcvWifiAlways();
    }

    public void connectAlways() {
        mStopped = false;
        while (!mStopped && (mWifiSocket == null || mWifiSocket.isClosed())) {
            connectWifi();
            if (mWifiSocket != null) {
                break;
            }
        }
    }

    public void connectWifi() {
        try {
            InetSocketAddress a = new InetSocketAddress("192.168.137.1", 12345);
            mWifiSocket.connect(a, 2000);
            textMessage("connectWifi: true");
        } catch (IOException e) {
            Timber.e(e);
            textMessage("connectWifi: connect Exception!!! " + e);
            mWifiSocket = null;
        }
    }

    public int sendWifi(byte[] bytes, int len) {
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

    public int rcvWifi(byte[] bytes) {
        InputStream in = null;
        if (mWifiSocket == null || mWifiSocket.isClosed()) {
            textMessage("rcvWifi: mWifiSocket null or closed");
            return -1;
        }
        try {
            // StandardSocketOptions, 读写之前设置才有效。
            mWifiSocket.setSoTimeout(5000); // SocketOptions.SO_TIMEOUT
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

    public void rcvWifiAlways() {
        byte[] rcv = new byte[8192];
        while (!mStopped && mWifiSocket != null && !mWifiSocket.isClosed()) {
            int i = rcvWifi(rcv);
            if (i > 0) {
                textRcv(rcv, i);
            }
        }
    }

    public void disconnectWifi() {
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

    public void destroy() {
        disconnectWifi();
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
        }
    }

    private void textSend(byte[] buff, int len) {
        if (mSendText.length() + len * 3 >= TEXT_BUFFER_SIZE) {
            mSendText.setLength(0);
        }
        String s = String.format("[socket-send]>>: %s\n", HexUtil.byte2Hex(buff, len));
        mSendText.append(s);
        mSendTextView.postValue(mSendText.toString());
    }

    private void textRcv(byte[] buff, int len) {
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
