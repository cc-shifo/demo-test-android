package com.example.demoudisk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.Objects;

// https://www.cnblogs.com/andy-songwei/p/7219065.html
public class UDiskReceiver extends BroadcastReceiver {
    private static final String TAG = "UDiskActivity";
    public UDiskReceiver() {
        // nothing
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_MEDIA_CHECKING:
                Log.d(TAG, "onReceive: ACTION_MEDIA_CHECKING");
                break;
            case Intent.ACTION_MEDIA_SCANNER_STARTED:
                Log.d(TAG, "onReceive: ACTION_MEDIA_SCANNER_STARTED");
                break;
            case Intent.ACTION_MEDIA_SCANNER_SCAN_FILE:
                Log.d(TAG, "onReceive: ACTION_MEDIA_SCANNER_SCAN_FILE");
                break;
            case Intent.ACTION_MEDIA_SCANNER_FINISHED:
                Log.d(TAG, "onReceive: ACTION_MEDIA_SCANNER_FINISHED");
                break;
            case Intent.ACTION_MEDIA_MOUNTED:
                // 获取挂载路径, 读取U盘文件
                Uri uri = intent.getData();
                if (uri != null) {
                    Log.d(TAG, "onReceive: \n");
                    String filePath = uri.getPath();
                    File rootFile = new File(filePath);
                    for (File file : Objects.requireNonNull(rootFile.listFiles())) {
                        Log.d(TAG, "name: " + file.getName() + "Path: " + file.getAbsolutePath());
                    }
                }
                break;
            case Intent.ACTION_MEDIA_EJECT:
                Log.d(TAG, "onReceive: ACTION_MEDIA_EJECT");
                break;
            case Intent.ACTION_MEDIA_UNMOUNTED:
                Log.d(TAG, "onReceive: ACTION_MEDIA_UNMOUNTED");
                break;
            case Intent.ACTION_MEDIA_REMOVED:
                Log.d(TAG, "onReceive: ACTION_MEDIA_REMOVED");
                break;
            case Intent.ACTION_MEDIA_BAD_REMOVAL:
                Log.d(TAG, "onReceive: ACTION_MEDIA_BAD_REMOVAL");
                break;
            default:
                break;
        }
    }
}