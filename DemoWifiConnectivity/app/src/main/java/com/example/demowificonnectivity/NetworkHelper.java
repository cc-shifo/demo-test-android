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
public class NetworkHelper extends ConnectivityManager.NetworkCallback {
    private volatile boolean mConnected;
    public NetworkHelper() {
        // nothing
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        Timber.i("initNetworkCallback onAvailable: %s", network.toString());
    }

    @Override
    public void onLosing(@NonNull Network network, int maxMsToLive) {
        Timber.i("initNetworkCallback onLosing: %s, maxMsToLive=%d",
                network.toString(), maxMsToLive);
    }

    @Override
    public void onLost(@NonNull Network network) {
        Timber.i("initNetworkCallback onLost: %s", network.toString());
    }

    @Override
    public void onUnavailable() {
        Timber.i("initNetworkCallback onUnavailable");
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network,
            @NonNull NetworkCapabilities capabilities) {
        mConnected = capabilities.hasCapability(NetworkCapabilities
                .NET_CAPABILITY_INTERNET) && capabilities.hasCapability(NetworkCapabilities
                .NET_CAPABILITY_VALIDATED);
        Timber.i("initNetworkCallback onCapabilitiesChanged: %s, connected=%s",
                network.toString(), String.valueOf(mConnected));
    }

    @Override
    public void onLinkPropertiesChanged(@NonNull Network network,
            @NonNull LinkProperties linkProperties) {
        Timber.i("initNetworkCallback onLinkPropertiesChanged: %s, properties=%s",
                network.toString(), linkProperties.toString());
    }

    @Override
    public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
        Timber.i("initNetworkCallback onBlockedStatusChanged: %s, blocked=%s",
                network.toString(), String.valueOf(blocked));
    }

    public void registerNetworkCallback(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
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
}
