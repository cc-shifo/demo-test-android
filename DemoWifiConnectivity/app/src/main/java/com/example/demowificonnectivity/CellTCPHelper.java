package com.example.demowificonnectivity;

import android.net.Network;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import timber.log.Timber;

public class CellTCPHelper extends BaseTCPHelper {
    private static final String IP = "8.135.10.183";
    private static final int PORT = 37155;
    private static final String TAG = "CellTCPHelper";
    private final ScheduledThreadPoolExecutor mExecutor = new ScheduledThreadPoolExecutor(3);
    private final Future<?> mFuture = null;
    private boolean mStopped = false;

    // 连接成功后发送序号清零
    private long mSendSerialNum = 0;

    private static final int TEXT_BUFFER_SIZE = 4096;
    private final StringBuilder mSendText = new StringBuilder(TEXT_BUFFER_SIZE);
    private final StringBuilder mRcvText = new StringBuilder(TEXT_BUFFER_SIZE);

    public final MutableLiveData<String> mMsgTextView = new MutableLiveData<>();
    public final MutableLiveData<String> mSendTextView = new MutableLiveData<>();
    public final MutableLiveData<String> mRcvTextView = new MutableLiveData<>();

    private Network mWifiNetwork;
    private Socket mWifiSocket;
    public void setCellNetwork(Network cellNetwork) {
        mWifiNetwork = cellNetwork; //mCellNetwork
    }

    public LiveData<String> getMsgTextView() {
        return mMsgTextView;
    }

    public LiveData<String> getSendTextView() {
        return mSendTextView;
    }

    public LiveData<String> getRcvTextView() {
        return mRcvTextView;
    }

    public void reset() {
        mStopped = false;
    }

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
        // if (mWifiSocket != null && !mWifiSocket.isClosed()) {
        //     try {
        //         mWifiSocket.close();
        //     } catch (IOException e) {
        //         Timber.e(e);
        //         textMessage("connectWifi: close Exception!!! " + e);
        //     }
        // }
        //
        // SocketFactory wifiSocket = mWifiNetwork.getSocketFactory();
        // try {
        //     mWifiSocket = wifiSocket.createSocket();
        // } catch (IOException e) {
        //     Timber.e(e);
        //     textMessage("connectWifi: connect Exception!!! " + e);
        //     mWifiSocket = null;
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
        mStopped = false;
        while (!mStopped) {
            createSocket();
            bindWifiNetwork(network);
            connectAlways();
            byte[] rcv = new byte[8192];
            while (!mStopped && mWifiSocket != null && !mWifiSocket.isClosed()) {
                int i = rcvWifi(rcv);
                if (i > 0) {
                    textRcv(rcv, i);
                }
            }
            if (!mStopped) {
                safeThreadSleep5000MS();
            }
        }
    }

    public void connectAlways() {
        while (!mStopped && (mWifiSocket == null || !mWifiSocket.isClosed())) {
            connectCell();
            if (mWifiSocket != null) {
                break;
            }
        }
    }


    public void sendCell12345() {
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
            sendWifi(bytes, bytes.length);
            // byte[] rcv = new byte[8192];
            // int i = rcvWifi(rcv);
            // if (i > 0) {
            //     textRcv(rcv, i);
            // }
        });
    }

    public void rcvCell12345() {
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

    public void disconnectCell() {
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
        disconnectCell();
        if (mExecutor != null) {
            mExecutor.shutdown();
        }
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
        }
        textMessage("connected: network lost");
    }

    public void connectCell() {
        try {
            InetSocketAddress a = new InetSocketAddress(IP, PORT);
            mWifiSocket.connect(a, 15000);
            mSendSerialNum = 0;
            textMessage("connectWifi: true");
        } catch (IOException e) {
            Timber.e(e);
            textMessage("connectWifi: connect Exception!!! " + e);
            mWifiSocket = null;
        }

        // if (mFuture != null && (!mFuture.isDone() || !mFuture.isCancelled())) {
        //     mFuture.cancel(true);
        // }
        // mFuture = mExecutor.submit(() -> {
        //     if (mWifiNetwork != null) {
        //         if (mWifiSocket != null && !mWifiSocket.isClosed()) {
        //             try {
        //                 mWifiSocket.close();
        //             } catch (IOException e) {
        //                 Timber.e(e);
        //                 textMessage("connectWifi: close Exception!!! " + e);
        //             }
        //         }
        //         SocketFactory wifiSocket = mWifiNetwork.getSocketFactory();
        //         try {
        //             mWifiSocket = wifiSocket.createSocket();
        //             InetSocketAddress a = new InetSocketAddress(IP, PORT);
        //             mWifiSocket.connect(a, 15000);
        //             mSendSerialNum = 0;
        //             textMessage("connectWifi: true");
        //         } catch (IOException e) {
        //             Timber.e(e);
        //             textMessage("connectWifi: connect Exception!!! " + e);
        //             mWifiSocket = null;
        //         }
        //     }
        //     return null;
        // });

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


    public void rcvCellAlways() {
        byte[] bytes = new byte[8192];
        while (!mStopped && mWifiSocket != null && !mWifiSocket.isClosed()) {
            int i = rcvWifi(bytes);
            if (i > 0) {
                textRcv(bytes, i);
            }
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

    private void safeThreadSleep5000MS() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            if (Thread.interrupted())  {
                // Clears interrupted status!
                Thread.currentThread().interrupt();
            }
        }
    }
}
