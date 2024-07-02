package com.example.demowificonnectivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import timber.log.Timber;

public class WiFiManualConTest {
    private final ScheduledThreadPoolExecutor mExecutor = new ScheduledThreadPoolExecutor(3);
    private ConnectivityManager.NetworkCallback mWiFiCallback;
    private final WiFiUDPClientHelper mWifiTCPHelper = new WiFiUDPClientHelper();
    private static final NetworkHelper.NetworkReceiver
            mReceiver = new NetworkHelper.NetworkReceiver();

    // 连接成功后发送序号清零
    private long mSendSerialNum = 0;

    public void listen(@NonNull Context context) {
        mWiFiCallback = new BaseNetCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                String address = "";
                String ipAddress = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ConnectivityManager manager = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    LinkProperties linkProperties = manager.getLinkProperties(network);
                    address = linkProperties.getDhcpServerAddress().getHostAddress();
                    // linkProperties.getLinkAddresses(); // 本机地址
                } else {
                    WifiManager wifiManager = (WifiManager) MyAPP.getMyAPP()
                            .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    // 31 Build.VERSION_CODES.S deprecated
                    DhcpInfo info = wifiManager.getDhcpInfo();
                    if (info != null) {
                        ipAddress = WifiTCPHelper.ipv4Int2Str(info.ipAddress);
                        address = WifiTCPHelper.ipv4Int2Str(info.serverAddress);
                    }
                }
                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //     capabilities.getNetworkSpecifier();
                // }
                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //     capabilities.getTransportInfo(); // 连接到热点时，获取的是本机当前地址
                // }
                Timber.d("initNetworkCallback: server address %s, local ip %s", address, ipAddress);
                if (!TextUtils.isEmpty(address)) {
                    startService(network, address, 12345);
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                stopService();
            }
        };
        registerWiFiNetworkCallback(context, mWiFiCallback);
    }

    public void cancelListen(@NonNull Context context) {
        stopService();
        if (mWiFiCallback != null) {
            unregisterWiFiNetworkCallback(context, mWiFiCallback);
            mWiFiCallback = null;
        }
    }

    public LiveData<String> getMsg() {
        return mWifiTCPHelper.mMsgTextView;
    }

    public LiveData<String> getSentData() {
        return mWifiTCPHelper.mSendTextView;
    }

    public LiveData<String> getRcvData() {
        return mWifiTCPHelper.mRcvTextView;
    }

    public void createSocket() {
        mWifiTCPHelper.reset();
        mWifiTCPHelper.createSocket();

        // if (mWifiNetwork != null) {
        //     if (mWifiSocket != null && !mWifiSocket.isClosed()) {
        //         try {
        //             mWifiSocket.close();
        //         } catch (IOException e) {
        //             Timber.e(e);
        //             textMessage("connectWifi: close Exception!!! " + e);
        //         }
        //     }
        //
        //
        //     SocketFactory wifiSocket = mWifiNetwork.getSocketFactory();
        //     try {
        //         mWifiSocket = wifiSocket.createSocket();
        //     } catch (IOException e) {
        //         Timber.e(e);
        //         textMessage("connectWifi: connect Exception!!! " + e);
        //         mWifiSocket = null;
        //     }
        // }
    }

    public void bindWifiNetwork(@NonNull Network wifiNetwork) {
        mWifiTCPHelper.bindWifiNetwork(wifiNetwork);
    }

    public void connectWifi12345() {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mWifiTCPHelper.connectWifi();
            }
        });
    }

    public void disconnectWifi() {
        mWifiTCPHelper.disconnectWifi();
    }

    public void sendWifi12345() {
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
            mWifiTCPHelper.sendWifi(bytes, 0, bytes.length);
            // byte[] rcv = new byte[8192];
            // int i = rcvWifi(rcv);
            // if (i > 0) {
            //     textRcv(rcv, i);
            // }
        });
    }

    public void rcvWifi12345(@NonNull final byte[] rcv) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mWifiTCPHelper.rcvWifi(rcv);
            }
        });
    }

    private void startService(@NonNull final Network network,
            @NonNull final String address, int port) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (mWifiTCPHelper != null) {
                    mWifiTCPHelper.disconnectWifi();
                    mWifiTCPHelper.mainEnter(network, address, port);
                }
            }
        });
    }

    private void stopService() {
        if (mWifiTCPHelper != null) {
            mWifiTCPHelper.destroy();
        }
    }


    @SuppressLint("MissingPermission")
    public  void registerWiFiNetworkCallback(@NonNull Context context,
            @NonNull ConnectivityManager.NetworkCallback callback) {
        // WifiP2pManager wifiP2pManager =
        //         (WifiP2pManager) context.getApplicationContext().getSystemService(
        //                 Context.WIFI_P2P_SERVICE);
        // WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(
        //         Context.WIFI_SERVICE);
        // int state = wifiManager.getWifiState();
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        //     wifiManager.getNetworkSuggestions();
        // }
        // WifiInfo info = wifiManager.getConnectionInfo();
        // @SuppressLint("MissingPermission") List<WifiConfiguration> configurations =
        //         wifiManager.getConfiguredNetworks();
        // List<ScanResult> scanResults = wifiManager.getScanResults();
        // Timber.d("initNetworkCallback: wifiManager.getWifiState()= %d", state);
        // DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        // if (dhcpInfo != null) {
        //     Timber.d("initNetworkCallback: wifiManager.getDhcpInfo()= %s", dhcpInfo.toString());
        // } else {
        //     Timber.d("initNetworkCallback: wifiManager.getDhcpInfo()= null");
        // }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            final NetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                    .setSsid("helloworld")
                    // .setSsidPattern(new PatternMatcher("helloworld", PatternMatcher.PATTERN_LITERAL))
                    .setWpa2Passphrase("12345678")
                            // .setSsidPattern(new PatternMatcher("test", PatternMatcher.PATTERN_PREFIX))
                            // .setBssidPattern(MacAddress.fromString("10:03:23:00:00:00"), MacAddress.fromString("ff:ff:ff:00:00:00"))
                            .build();

            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkRequest request = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    // .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                    // .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .setNetworkSpecifier(specifier)
                    .build();

            manager.requestNetwork(request, callback);
            // IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            // context.registerReceiver(mReceiver, filter);
            // getApIpAddress2();
        } else {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(
                    Context.WIFI_SERVICE);
            @SuppressLint("MissingPermission")
            List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
            List<ScanResult> scanResults = wifiManager.getScanResults(); // 到时候监听广播
            if (scanResults != null) {
                for (ScanResult result : scanResults) {
                    result.capabilities.contains("WEP"); // 加密方式
                }
            }
            if (configurations != null) {
                for (WifiConfiguration c : configurations) {
                    c.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK);
                    // c.SSID //
                }
            }

            testSuggestion();

            // int networkId = wifiManager.addNetwork(accessPoint.wifiConfiguration);
            // wifiManager.enableNetwork(networkId, true);


            WifiConfiguration wifiCong = new WifiConfiguration();
            wifiCong.SSID = "helloworld";
            wifiCong.preSharedKey = "123456";

        }

    }

    public  void unregisterWiFiNetworkCallback(@NonNull Context context,
            @NonNull ConnectivityManager.NetworkCallback callback) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        manager.unregisterNetworkCallback(callback);
        context.unregisterReceiver(mReceiver);
    }

    public void testSuggestion() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            return;
        }
        final WifiNetworkSuggestion suggestion1 =
                new WifiNetworkSuggestion.Builder()
                        .setSsid("test111111")
                        .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                        .build();

        final WifiNetworkSuggestion suggestion2 =
                new WifiNetworkSuggestion.Builder()
                        .setSsid("test222222")
                        .setWpa2Passphrase("test123456")
                        .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                        .build();

        final WifiNetworkSuggestion suggestion3 =
                new WifiNetworkSuggestion.Builder()
                        .setSsid("test333333")
                        .setWpa3Passphrase("test6789")
                        .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                        .build();

        final PasspointConfiguration passpointConfig = new PasspointConfiguration();
        // configure passpointConfig to include a valid Passpoint configuration

        final WifiNetworkSuggestion suggestion4 =
                new WifiNetworkSuggestion.Builder()
                        .setPasspointConfig(passpointConfig)
                        .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                        .build();

        final List<WifiNetworkSuggestion> suggestionsList =
                new ArrayList<WifiNetworkSuggestion>() {
                    {
                        add(suggestion1);
                        add(suggestion2);
                        add(suggestion3);
                        add(suggestion4);
                    }
                };

        final WifiManager wifiManager = (WifiManager) MyAPP.getMyAPP().getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);

        final int status = wifiManager.addNetworkSuggestions(suggestionsList);
        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            // do error handling here…
        }

        // Optional (Wait for post connection broadcast to one of your suggestions)
        final IntentFilter intentFilter =
                new IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);

        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals(
                        WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                    return;
                }
                // do post connect processing here...
            }
        };
        // context.registerReceiver(broadcastReceiver, intentFilter);

    }
}
