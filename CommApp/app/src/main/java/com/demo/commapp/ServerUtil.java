package com.demo.commapp;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerUtil {
    private static final String TAG = "ServerSkUtil";
    private static ServerUtil mInstance;
    private ServerSocket mServerSocket;
    public static final int PORT = 10000;
    private static final int MAX_BUF = 2048;
    private String mIP;
    private String mPort;

    private ServerUtil() {
        //nothing
    }

    public static synchronized ServerUtil getInstance() {
        if (mInstance == null) {
            mInstance = new ServerUtil();
        }

        return mInstance;
    }

    public boolean init() {
        try {
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
        } catch (IOException e) {
            Log.e(TAG, "init: ", e);
            return false;
        }

        return true;
    }

    public boolean listen(int port) {
        try {
            final int MAX_QUEUE = 1;
            // if (port <= 0) {
            //     port = PORT;
            // }
            mServerSocket.bind(new InetSocketAddress((InetAddress) null, PORT), MAX_QUEUE);
        } catch (IOException e) {
            Log.e(TAG, "listen: ", e);
            return false;
        }

        return true;
    }

    public Socket accept() {
        Socket socket;
        // mServerSocket.setSoTimeout();
        if (BuildConfig.DEBUG || CommService.isEnableDebug()) {
            // String addr = new String(mServerSocket.getInetAddress().getAddress());
            String host = mServerSocket.getInetAddress().getHostAddress();
            String port = String.valueOf(mServerSocket.getLocalPort());
            // Log.d(TAG, "accept: mServerSocket addr=" + addr);
            Log.d(TAG, "accept: mServerSocket host=" + host);
            Log.d(TAG, "accept: mServerSocket port=" + port);
        }

        try {
            socket = mServerSocket.accept();
        } catch (IOException e) {
            Log.e(TAG, "accept: ", e);
            return null;
        }

        if (BuildConfig.DEBUG || CommService.isEnableDebug()) {
            // String addr = new String(socket.getInetAddress().getAddress());
            String host = socket.getInetAddress().getHostAddress();
            String port = String.valueOf(socket.getLocalPort());
            // Log.d(TAG, "accept: mClient client=" + addr);
            Log.d(TAG, "accept: mClient host=" + host);
            Log.d(TAG, "accept: mClient port=" + port);
        }
        return socket;
    }

    public int write(@NonNull Socket socket, @NonNull byte[] data) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data, 0, data.length);
            return data.length;
        } catch (IOException e) {
            Log.e(TAG, "write: ", e);
        }
        return -1;
    }

    public int read(@NonNull Socket socket, @NonNull byte[] buf) {
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
            return inputStream.read(buf, 0, buf.length);
        } catch (IOException e) {
            Log.e(TAG, "read: ", e);
        }

        return -1;
    }

    public void cancelAccept() {
        if (mServerSocket == null || !mServerSocket.isBound() || mServerSocket.isClosed()) {
            return;
        }

        try {
            mServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "cancelAccept: ", e);
        }
    }

    public void close(Socket socket) {
        if (socket == null || socket.isClosed()) {
            return;
        }

        try {
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "close: ", e);
        }
    }
}
