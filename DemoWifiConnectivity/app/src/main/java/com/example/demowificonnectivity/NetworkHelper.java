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

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;

import androidx.annotation.NonNull;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

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
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                // .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).addCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET)
                // .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .build();
        manager.registerNetworkCallback(request, callback);
    }

    public static void unregisterCellNetworkCallback(@NonNull Context context,
            @NonNull ConnectivityManager.NetworkCallback callback) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        manager.unregisterNetworkCallback(callback);
    }

    private static final NetworkReceiver mReceiver = new NetworkReceiver();

    @SuppressLint("MissingPermission")
    public static void registerWiFiNetworkCallback(@NonNull Context context,
            @NonNull ConnectivityManager.NetworkCallback callback) {
        WifiP2pManager wifiP2pManager =
                (WifiP2pManager) context.getApplicationContext().getSystemService(
                        Context.WIFI_P2P_SERVICE);
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(
                Context.WIFI_SERVICE);
        int state = wifiManager.getWifiState();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            wifiManager.getNetworkSuggestions();
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        @SuppressLint("MissingPermission") List<WifiConfiguration> configurations =
                wifiManager.getConfiguredNetworks();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        Timber.d("initNetworkCallback: wifiManager.getWifiState()= %d", state);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo != null) {
            Timber.d("initNetworkCallback: wifiManager.getDhcpInfo()= %s", dhcpInfo.toString());
        } else {
            Timber.d("initNetworkCallback: wifiManager.getDhcpInfo()= null");
        }


        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                // .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                // .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
        manager.registerNetworkCallback(request, callback);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiver, filter);
        // getApIpAddress2();
    }

    public static void unregisterWiFiNetworkCallback(@NonNull Context context,
            @NonNull ConnectivityManager.NetworkCallback callback) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        manager.unregisterNetworkCallback(callback);
        context.unregisterReceiver(mReceiver);
    }


    public static class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();

            if (networkInfo != null) {
                Timber.d("initNetworkCallback: onReceive %s", networkInfo.toString());
            }
            // Checks the user prefs and the network connection. Based on the result, decides
            // whether
            // to refresh the display or keep the current display.
            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
            // if (WIFI.equals(sPref) && networkInfo != null
            //         && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            //     // If device has its Wi-Fi connection, sets refreshDisplay
            //     // to true. This causes the display to be refreshed when the user
            //     // returns to the app.
            //     refreshDisplay = true;
            //     Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();
            //
            //     // If the setting is ANY network and there is a network connection
            //     // (which by process of elimination would be mobile), sets refreshDisplay to
            //     true.
            // } else if (ANY.equals(sPref) && networkInfo != null) {
            //     refreshDisplay = true;
            //
            //     // Otherwise, the app can't download content--either because there is no network
            //     // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
            //     // is no Wi-Fi connection.
            //     // Sets refreshDisplay to false.
            // } else {
            //     refreshDisplay = false;
            //     Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
            // }
        }
    }

    public static InetAddress getApIpAddress1() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Timber.d("initNetworkCallback if: %s", networkInterface.getDisplayName());
                Enumeration<InetAddress> ipAddresses = networkInterface.getInetAddresses();
                while (ipAddresses.hasMoreElements()) {
                    InetAddress inetAddress = ipAddresses.nextElement();
                    Timber.d("inetAddress: %s", inetAddress.toString());
                }
                List<InterfaceAddress> interfaceAddressList =
                        networkInterface.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : interfaceAddressList) {
                    Timber.d("interfaceAddress: %s %d",
                            interfaceAddress.getAddress().toString(),
                            interfaceAddress.getNetworkPrefixLength());
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;

    }



    public static String getApIpAddress2() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.getDisplayName().contains("wlan0") ||
                        networkInterface.getDisplayName().contains("eth0") ||
                        networkInterface.getDisplayName().contains("ap0")) {
                    List<InterfaceAddress> interfaceAddressList = networkInterface.getInterfaceAddresses();
                    for (InterfaceAddress interfaceAddress : interfaceAddressList) {
                        InetAddress inetAddress = interfaceAddress.getAddress();
                        if (!inetAddress.isLinkLocalAddress()) {
                            return inetAddress.getHostAddress();
                            // int prefix = interfaceAddress.getNetworkPrefixLength();
                            // return inetAddress.toString().substring(1) + "/" + prefix;
                        }
                        Timber.d("initNetworkCallback: if %s, if address=%s, HostAddress=%s, HostName=%s, CanonicalHostName=%s" +
                                        "%b, %b, %b, %b",
                                networkInterface.getDisplayName(), inetAddress.getAddress(),
                                inetAddress.getHostAddress(), inetAddress.getHostName(),
                                inetAddress.getCanonicalHostName(),
                                inetAddress.isAnyLocalAddress(),
                                inetAddress.isLinkLocalAddress(),
                                inetAddress.isLoopbackAddress(),
                                inetAddress.isSiteLocalAddress());
                    }
                }
            }

        } catch (SocketException e) {
            Timber.e(e);
        }

        return null;

    }
}
