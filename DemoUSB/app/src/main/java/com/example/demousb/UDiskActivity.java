package com.example.demousb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demousb.databinding.ActivityUdiskBinding;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UDiskActivity extends AppCompatActivity {
    private static final String TAG = "UDiskActivity";
    private ActivityUdiskBinding mBinding;
    private UDiskReceiver mReceiver;
    private StringBuilder mStringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityUdiskBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_udisk);
        initTextLog();
        initUDiskReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private void initTextLog() {
        mStringBuilder = new StringBuilder();
    }

    private void initUDiskReceiver() {
        mReceiver = new UDiskReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(1000);
        intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);

        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);

        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);

        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addDataScheme("file");
        registerReceiver(mReceiver, intentFilter);
        testUSBPaths();
    }

    private void testUSBPaths() {
        Log.d(TAG, "\n\n\n\n\ntestUSBPaths: ");
        StorageManager srgMgr = (StorageManager) this.getSystemService(Context.STORAGE_SERVICE);
        List<String> pathList = getUsbPaths(this);
        for (String p : pathList) {
            boolean mounted = checkMounted(srgMgr, p);
            Log.d(TAG, "path: " + p);
            Log.d(TAG,  "mounted: " + mounted);
            File f = new File(p);
            Log.d(TAG,  "isEmulated: " + Environment.isExternalStorageEmulated(f));
            Log.d(TAG,  "isRemovable: " + Environment.isExternalStorageRemovable(f));
        }
    }

    // 查找存储设备的路径
    private List<String> getUsbPaths(@NonNull Context cxt) {
        List<String> usbPaths = new ArrayList<>(0);
        try {
            StorageManager srgMgr = (StorageManager) cxt.getSystemService(Context.STORAGE_SERVICE);
            Class<StorageManager> srgMgrClass = StorageManager.class;
            String[] paths = (String[]) srgMgrClass.getMethod("getVolumePaths").invoke(srgMgr);
            if (paths != null) {
                return Arrays.asList(paths);
            }

        } catch (Exception e) {
            Log.e(TAG, "getUsbPaths: ", e);
        }
        return usbPaths;
    }

    // 检查存储路径上的设备是否可用(已挂载)
    private boolean checkMounted(@NonNull StorageManager srgMgr, @Nullable String path) {
        if (path == null) {
            return false;
        }
        try {
            Method getStorageState = srgMgr.getClass().getMethod("getVolumeState", String.class);
            String mountState = (String) getStorageState.invoke(srgMgr, path);
            boolean mounted = !path.contains("emulated") && Environment.MEDIA_MOUNTED.equals(mountState);
            // Method getStorageVolume = srgMgr.getClass().getMethod("getStorageVolume", File.class);
            // StorageVolume storageVolume = (StorageVolume)getStorageVolume.invoke(srgMgr, new File(path));
            // if (storageVolume != null) {
            //     storageVolume.getState();
            // }
            return mounted;

        } catch (Exception e) {
            Log.e(TAG, "getMountedUSBPath: ", e);
        }

        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getAllVolumes(@NonNull Context context) {
        StorageManager srgMgr = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            List<StorageVolume> volumeList = srgMgr.getStorageVolumes();
            for (StorageVolume volume : volumeList) {
                if (!volume.isPrimary() && !volume.isEmulated() && volume.isRemovable()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Log.d(TAG, "Volumes Name: " + volume.getMediaStoreVolumeName());
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Log.d(TAG, "Volumes directory: " + volume.getDirectory());
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void storageManagerCallback(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            StorageManager srgMgr = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            // srgMgr.registerStorageVolumeCallback(Executor, StorageVolumeCallback)
        }
    }

    private static class UDiskReceiver extends BroadcastReceiver {
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
                    Log.d(TAG, "onReceive: ACTION_MEDIA_SCANNER_FINISHED");
                    // 获取挂载路径, 读取U盘文件
                    Uri uri = intent.getData();
                    if (uri != null) {
                        String filePath = uri.getPath();
                        File rootFile = new File(filePath);
                        for (File file : Objects.requireNonNull(rootFile.listFiles())) {
                            Log.d(TAG, "name: " + file.getName() + "Path: " + file.getAbsolutePath());
                        }
                    }
                    Log.d(TAG, "\n\n");
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


}