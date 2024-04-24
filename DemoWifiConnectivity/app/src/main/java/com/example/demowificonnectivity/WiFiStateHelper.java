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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Objects;

import timber.log.Timber;

/**
 * 监听网络是否可以访问互联网。
 */
public class WiFiStateHelper extends BroadcastReceiver {
    private volatile boolean mConnected;

    public WiFiStateHelper() {
        // nothing
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN)) {
                case WifiManager.WIFI_STATE_DISABLED: {
                    Timber.tag(MainActivity.TAG).d("WiFiStateHelper WiFi disabled");
                    break;
                }
                case WifiManager.WIFI_STATE_DISABLING: {
                    Timber.tag(MainActivity.TAG).d("WiFiStateHelper WiFi disabling");
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLED: {
                    Timber.tag(MainActivity.TAG).d("WiFiStateHelper WiFi enabled");
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLING: {
                    Timber.tag(MainActivity.TAG).d("WiFiStateHelper WiFi enabling");
                    break;
                }
                case WifiManager.WIFI_STATE_UNKNOWN: {
                    Timber.tag(MainActivity.TAG).d("WiFiStateHelper WiFi state unknown");
                    break;
                }
                default:
                    break;
            }
        }
    }

    // 注册接收器
    public void registerBroadcast(@NonNull Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.registerReceiver(this, filter);
    }

    public void unregisterBroadcast(@NonNull Context context) {
        context.unregisterReceiver(this);
    }

    public boolean isConnected() {
        return mConnected;
    }
}
