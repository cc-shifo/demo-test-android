package com.example.demowificonnectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import timber.log.Timber;

public class APHelper {
    private ScheduledThreadPoolExecutor mExecutor = new ScheduledThreadPoolExecutor(3);

    private void register(@NonNull Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intentFilter.addAction("android.net.conn.TETHER_STATE_CHANGED");
        context.getApplicationContext().registerReceiver(mReceiver, intentFilter);
    }

    private void unregister(@NonNull Context context) {
        context.getApplicationContext().unregisterReceiver(mReceiver);
    }

    public static final int WIFI_AP_STATE_ENABLED = 13;
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            getApIP();
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Timber.d("initNetworkCallback: APHelper Receiver action:" + action);
            if ("android.net.conn.TETHER_STATE_CHANGED".equals(action)) {
                mExecutor.submit(mRunnable);
            }
        }
    };

    public String getApIP() {
        try {
            Enumeration<NetworkInterface>
                    networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                if (ni.isUp() && !ni.isPointToPoint() && !ni.isLoopback() && ("ap0".equals(
                        ni.getName()) || "softap0".equals(ni.getName()))) {
                    List<InterfaceAddress> interfaceAddresses = ni.getInterfaceAddresses();
                    for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                        if (interfaceAddress.getAddress() != null) {
                            Timber.d("initNetworkCallback address:" +
                                    interfaceAddress.getAddress().toString());
                            if (interfaceAddress.getAddress().toString().contains("/192.168")) {
                                String softApIP =
                                        interfaceAddress.getAddress().toString().substring(1);
                                Timber.d("initNetworkCallback getApIP:" + softApIP);
                                return softApIP;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e, "initNetworkCallback");
        }
        return null;
    }

    private Future<?> mFuture;
    private boolean mStop;

    public void getAllIPs() {
        mFuture = mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mStop = false;
                try {
                    Enumeration<NetworkInterface>
                            networkInterfaces = NetworkInterface.getNetworkInterfaces();
                    while (!mStop && networkInterfaces.hasMoreElements()) {
                        NetworkInterface ni = networkInterfaces.nextElement();
                        Timber.d("initNetworkCallback address: if=%s---->\n\n\n", ni.toString());
                        List<InterfaceAddress> interfaceAddresses = ni.getInterfaceAddresses();
                        for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                            if (interfaceAddress.getAddress() != null) {
                                Timber.d("initNetworkCallback address: if name=%s, %s, %s",
                                        ni.getDisplayName(),
                                        ni.getName(),
                                        interfaceAddress.getAddress().toString());
                                if (interfaceAddress.getAddress().toString().contains("/192.168")) {
                                    String softApIP =
                                            interfaceAddress.getAddress().toString().substring(1);
                                    Timber.d("initNetworkCallback getApIP:" + softApIP);
                                }
                            }
                        }
                        Timber.d("initNetworkCallback address: if=%s<----\n\n\n", ni.toString());
                    }
                } catch (Exception e) {
                    Timber.e(e, "initNetworkCallback");
                }
            }
        });

    }

    public void stop() {
        mStop = true;
        if (mFuture != null && (!mFuture.isCancelled() || !mFuture.isDone())) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }
}
