package com.example.demoopensetting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.Nullable;

public class WifiScanReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiScanReceiver";
    /**
     * 监听到wifi连接断开事件的回调
     */
    private DisconnectedCB mDisconnectedCB;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String act = intent.getAction();
        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(act))  {
            handleSupplicantState(intent);
        }
    }

    public void setDisconnectedCB(@Nullable DisconnectedCB disconnectedCB) {
        mDisconnectedCB = disconnectedCB;
    }

    private void handleSupplicantState(Intent intent) {
        // 获取连接状态
        SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);

        switch (supplicantState) {
            // 成功
            case COMPLETED:
                // wifiManager.connectionInfo?.ssid?.replace("\"", "")?.let {
                // connectListener.invoke(it, false);
                // }
                Log.i(TAG, "获取连接状态：成功");
                break;
                
            // 不活跃的
            case INACTIVE:
                Log.i(TAG, "获取连接状态：不活跃的");
                break;
            // 接口禁用
            case INTERFACE_DISABLED: {
                Log.i(TAG, "获取连接状态：接口禁用");
                break;
            }
            case DISCONNECTED: {
                Log.i(TAG, "获取连接状态：断开连接");
                if (mDisconnectedCB != null) {
                    mDisconnectedCB.disconnected();
                }
                break;
            }
            case SCANNING: {
                Log.i(TAG, "获取连接状态：正在扫描");
                break;
            }
            case AUTHENTICATING: {
                Log.i(TAG, "获取连接状态：正在验证");
                break;
            }
            case ASSOCIATING: {
                Log.i(TAG, "获取连接状态：正在关联");
                break;
            }
            case ASSOCIATED: {
                Log.i(TAG, "获取连接状态：已经关联");
                break;
            }
            case FOUR_WAY_HANDSHAKE: {
                Log.i(TAG, "获取连接状态：四次握手");
                break;
            }
            case GROUP_HANDSHAKE: {
                Log.i(TAG, "获取连接状态：组握手");
                break;
            }
            case DORMANT:
                Log.i(TAG, "获取连接状态：休眠");
                break;
            case UNINITIALIZED:
                Log.i(TAG, "获取连接状态：未初始化");
                break;
            case INVALID:
                Log.i(TAG, "获取连接状态：无效的");
                break;
            default:
                Log.i(TAG, "wifi连接结果通知");
                break;
        }
    }

    /**
     * 简单处理WIFI断开事件的回调。
     */
    public interface DisconnectedCB {
        void disconnected();
    }
}
