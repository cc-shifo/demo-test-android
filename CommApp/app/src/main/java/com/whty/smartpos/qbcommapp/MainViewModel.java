package com.whty.smartpos.qbcommapp;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";

    private final MutableLiveData<String> mIPv4;
    private final MutableLiveData<String> mIPv6;
    private final MutableLiveData<String> mPort;
    private ConnectivityManager mCM;
    private final ConnectivityManager.NetworkCallback mCallback =
            new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    Log.d(TAG, "onAvailable: " + Thread.currentThread().getId());
                    LinkProperties properties = mCM.getLinkProperties(network);
                    String ifName = properties.getInterfaceName();
                    Log.d(TAG, "onAvailable: InterfaceName=" + ifName);
                    // NetworkInterface.getByName(ifName) to get NetworkInterface
                    // Enumeration<NetworkInterface> interfaceEnumeration =
                    //         NetworkInterface.getNetworkInterfaces();
                    List<LinkAddress> list = properties.getLinkAddresses();
                    InetAddress inetAddress = null;
                    for (LinkAddress linkAddress : list) {
                        inetAddress = linkAddress.getAddress();
                        Log.d(TAG, "onAvailable: ip=" + inetAddress.getHostAddress());
                        if (inetAddress instanceof Inet4Address) {
                            mIPv4.postValue(inetAddress.getHostAddress());
                        } else if (inetAddress instanceof Inet6Address) {
                            mIPv6.postValue(inetAddress.getHostAddress());
                        }
                    }
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    mIPv4.postValue("");
                }
            };

    public MainViewModel(@NonNull Application application) {
        super(application);
        mIPv4 = new MutableLiveData<>();
        mIPv6 = new MutableLiveData<>();
        mPort = new MutableLiveData<>();
    }

    public void getLocalIP() {
        mCM = (ConnectivityManager) getApplication().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request =
                new NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build();

        mCM.requestNetwork(request, mCallback);
    }

    public MutableLiveData<String> getIPv4() {
        return mIPv4;
    }

    public MutableLiveData<String> getIPv6() {
        return mIPv6;
    }

    public MutableLiveData<String> getPort() {
        return mPort;
    }

    public void destroy() {
        mCM.unregisterNetworkCallback(mCallback);
    }
}
