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
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import timber.log.Timber;

/**
 * 监听网络是否可以访问互联网。
 */
public class ImplNetworkCallback extends ConnectivityManager.NetworkCallback {
    private volatile boolean mConnected;
    public ImplNetworkCallback() {
        // nothing
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        // ConnectivityManager manager = (ConnectivityManager)
        //         MyAPP.getMyAPP().getSystemService(Context.CONNECTIVITY_SERVICE);
        // NetworkCapabilities c = manager.getNetworkCapabilities(network);
        //
        // c.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);

        Timber.tag(MainActivity.TAG).i("initNetworkCallback onAvailable: %s", network.toString());
    }

    @Override
    public void onLosing(@NonNull Network network, int maxMsToLive) {
        Timber.tag(MainActivity.TAG).i("initNetworkCallback onLosing: %s, maxMsToLive=%d",
                network.toString(), maxMsToLive);
    }

    @Override
    public void onLost(@NonNull Network network) {
        Timber.tag(MainActivity.TAG).i("initNetworkCallback onLost: %s", network.toString());
    }

    @Override
    public void onUnavailable() {
        Timber.tag(MainActivity.TAG).i("initNetworkCallback onUnavailable");
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network,
            @NonNull NetworkCapabilities capabilities) {
        mConnected = capabilities.hasCapability(NetworkCapabilities
                .NET_CAPABILITY_INTERNET) && capabilities.hasCapability(NetworkCapabilities
                .NET_CAPABILITY_VALIDATED);

        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);

        Timber.tag(MainActivity.TAG).i("initNetworkCallback onCapabilitiesChanged: %s, connected=%s",
                network.toString(), String.valueOf(mConnected));
    }

    @Override
    public void onLinkPropertiesChanged(@NonNull Network network,
            @NonNull LinkProperties linkProperties) {
        Timber.tag(MainActivity.TAG).i("initNetworkCallback onLinkPropertiesChanged: %s, properties=%s",
                network.toString(), linkProperties.toString());
    }

    @Override
    public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
        Timber.tag(MainActivity.TAG).i("initNetworkCallback onBlockedStatusChanged: %s, blocked=%s",
                network.toString(), String.valueOf(blocked));
    }

    public void registerNetworkCallback(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                // .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .build();
        manager.registerNetworkCallback(request, this);
    }

    public void unregisterNetworkCallback(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        manager.unregisterNetworkCallback(this);
    }

    public boolean isConnected() {
        return mConnected;
    }

    public void registerWiFiNetworkCallback(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        manager.registerNetworkCallback(request, this);
    }

    public void unregisterWiFiNetworkCallback(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        manager.unregisterNetworkCallback(this);
    }

}
