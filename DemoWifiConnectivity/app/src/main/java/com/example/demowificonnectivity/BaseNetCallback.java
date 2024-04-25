/*
 * = COPYRIGHT
 *          // xx
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20240301 	         LiuJian                  Create
 *
 */

package com.example.demowificonnectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import java.util.List;

import timber.log.Timber;

/**
 * 监听网络是否可以访问互联网。
 */
public class BaseNetCallback extends ConnectivityManager.NetworkCallback {
    public static final String TAG = "BaseNet";
    private volatile boolean mConnected;

    public BaseNetCallback() {
        // nothing
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        // ConnectivityManager manager = (ConnectivityManager)
        //         MyAPP.getMyAPP().getSystemService(Context.CONNECTIVITY_SERVICE);
        // NetworkCapabilities c = manager.getNetworkCapabilities(network);
        //
        // c.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        Timber.tag(MainActivity.TAG).i("initNetworkCallback onAvailable: %s, %s",
                network.toString(), this.toString());
        getNetWork();
    }

    @Override
    public void onLosing(@NonNull Network network, int maxMsToLive) {
        Timber.tag(MainActivity.TAG).i("initNetworkCallback onLosing: %s, maxMsToLive=%d, %s",
                network.toString(), maxMsToLive, this.toString());
    }

    @Override
    public void onLost(@NonNull Network network) {
        Timber.tag(MainActivity.TAG).i("initNetworkCallback onLost: %s, %s", network.toString()
                , this.toString());
    }

    @Override
    public void onUnavailable() {
        Timber.tag(MainActivity.TAG).i("initNetworkCallback onUnavailable, %s", this.toString());
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network,
            @NonNull NetworkCapabilities capabilities) {
        mConnected = capabilities.hasCapability(NetworkCapabilities
                .NET_CAPABILITY_INTERNET) && capabilities.hasCapability(NetworkCapabilities
                .NET_CAPABILITY_VALIDATED);

        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);

        Timber.tag(MainActivity.TAG).i(
                "initNetworkCallback onCapabilitiesChanged: %s, connected=%s, %s",
                network.toString(), String.valueOf(mConnected), this.toString());
    }

    @Override
    public void onLinkPropertiesChanged(@NonNull Network network,
            @NonNull LinkProperties linkProperties) {
        Timber.tag(MainActivity.TAG).i(
                "initNetworkCallback onLinkPropertiesChanged: %s, properties=%s, %s",
                network.toString(), linkProperties.toString(), this.toString());
        List<LinkAddress> list = linkProperties.getLinkAddresses();
        for (LinkAddress a : list) {
            Timber.tag(MainActivity.TAG).i(
                    "initNetworkCallback onLinkPropertiesChanged: %s", a.getAddress().toString());
        }

    }

    @Override
    public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
        Timber.tag(MainActivity.TAG).i(
                "initNetworkCallback onBlockedStatusChanged: %s, blocked=%s, %s",
                network.toString(), String.valueOf(blocked), this.toString());
    }
    //
    // public void registerNetworkCallback(@NonNull Context context) {
    //     ConnectivityManager manager = (ConnectivityManager) context
    //             .getSystemService(Context.CONNECTIVITY_SERVICE);
    //     NetworkRequest request = new NetworkRequest.Builder()
    //             // .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    //             .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    //             .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    //             .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    //             .build();
    //     manager.registerNetworkCallback(request, this);
    // }
    //
    // public void unregisterNetworkCallback(@NonNull Context context) {
    //     ConnectivityManager manager = (ConnectivityManager) context
    //             .getSystemService(Context.CONNECTIVITY_SERVICE);
    //     manager.unregisterNetworkCallback(this);
    // }
    //
    // public boolean isConnected() {
    //     return mConnected;
    // }
    //
    // public void registerWiFiNetworkCallback(@NonNull Context context) {
    //     ConnectivityManager manager = (ConnectivityManager) context
    //             .getSystemService(Context.CONNECTIVITY_SERVICE);
    //     NetworkRequest request = new NetworkRequest.Builder()
    //             .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    //             .build();
    //     manager.registerNetworkCallback(request, this);
    // }
    //
    // public void unregisterWiFiNetworkCallback(@NonNull Context context) {
    //     ConnectivityManager manager = (ConnectivityManager) context
    //             .getSystemService(Context.CONNECTIVITY_SERVICE);
    //     manager.unregisterNetworkCallback(this);
    // }

    private Network mWifiNetwork;// 打印
    private Network mCellNetwork; // 打印

    private void getNetWork() {
        mWifiNetwork = null;
        mCellNetwork = null;
        // https://www.jianshu.com/p/d261e5b7ea38
        ConnectivityManager manager = (ConnectivityManager)
                MyAPP.getMyAPP().getSystemService(Context.CONNECTIVITY_SERVICE);
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

        if (mWifiNetwork == null) {
            Timber.tag(TAG).d("initNetworkCallback mWifiNetwork null");
        } else {
            Timber.tag(TAG).d("initNetworkCallback mWifiNetwork %s", mWifiNetwork.toString());
        }

        if (mCellNetwork == null) {
            Timber.tag(TAG).d("initNetworkCallback mCellNetwork null");
        } else {
            Timber.tag(TAG).d("initNetworkCallback mCellNetwork %s", mCellNetwork.toString());
        }
    }

}
