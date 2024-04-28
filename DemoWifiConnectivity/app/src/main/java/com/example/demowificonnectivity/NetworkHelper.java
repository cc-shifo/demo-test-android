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
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

import androidx.annotation.NonNull;

import timber.log.Timber;

/**
 * 监听网络是否可以访问互联网。
 */
public class NetworkHelper {

    private volatile boolean mConnected;
    public NetworkHelper() {
        // nothing
    }

    public static void registerCellNetworkCallback(@NonNull Context context,
            @NonNull ConnectivityManager.NetworkCallback callback) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                // .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                // .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .build();
        manager.registerNetworkCallback(request, callback);
    }

    public static void unregisterCellNetworkCallback(@NonNull Context context,
            @NonNull ConnectivityManager.NetworkCallback callback) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        manager.unregisterNetworkCallback(callback);
    }


    public static void registerWiFiNetworkCallback(@NonNull Context context,
            @NonNull ConnectivityManager.NetworkCallback callback) {
        WifiP2pManager wifiP2pManager = (WifiP2pManager)context.getApplicationContext().getSystemService(
                Context.WIFI_P2P_SERVICE);
        WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(
                Context.WIFI_SERVICE);
        int state = wifiManager.getWifiState();
        Timber.d("initNetworkCallback: wifiManager.getWifiState()= %d", state);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo != null) {
            Timber.d("initNetworkCallback: wifiManager.getDhcpInfo()= %s", dhcpInfo.toString());
        } else {
            Timber.d("initNetworkCallback: wifiManager.getDhcpInfo()= null");
        }


        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                // .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                // .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        manager.registerNetworkCallback(request, callback);
    }

    public static void unregisterWiFiNetworkCallback(@NonNull Context context,
            @NonNull ConnectivityManager.NetworkCallback callback) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        manager.unregisterNetworkCallback(callback);
    }

}
