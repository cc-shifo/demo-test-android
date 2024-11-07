package com.example.demoudisk;

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
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.MutableLiveData;

import com.example.demoudisk.databinding.ActivityUdiskBinding;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UDiskActivity extends AppCompatActivity {
    private static final String TAG = "UDiskActivity";
    private ActivityUdiskBinding mBinding;
    private UDiskReceiver mReceiver;
    private StringBuilder mStringBuilder;
    private MutableLiveData<List<String>> mLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityUdiskBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initTextLog();
        initTestBtn();
        initUDiskReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }


    private Uri mSDUri;

    private void initTextLog() {
        mStringBuilder = new StringBuilder();

        mSDUri = getIntent().getData();
    }

    private List<String> mVolumeRootPathList = new ArrayList<>(0);

    private void initTestBtn() {
        mBinding.btnUsbDiskCreateDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVolumeRootPathList.isEmpty()) {
                    Toast.makeText(UDiskActivity.this, "no volume path", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    for (int i = 0; i < mVolumeRootPathList.size(); i++) {
                        testCreateDirection30(mVolumeRootPathList.get(i), i);
                    }
                } else {
                    for (int i = 0; i < mVolumeRootPathList.size(); i++) {
                        testCreateDirection29(mVolumeRootPathList.get(i), i);
                    }
                }
            }
        });

        mBinding.btnUsbDiskPermissionSd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // File file = new File("storage/OFF9-2904");
                // // DocumentFile documentFile = DocumentFile.fromFile(file);
                // Uri uri = Uri.parse("content://com.android.externalstorage
                // .documents/tree/0FF9-2904:");
                // DocumentFile documentFile = DocumentFile.fromTreeUri(UDiskActivity.this, uri);
                // if (documentFile != null) {
                //     Log.d(TAG, "onClick: " + documentFile.getUri());
                // }
            }
        });
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
        mVolumeRootPathList.clear();
        Log.d(TAG, "\n\n\n\n\ntestUSBPaths: ");
        StorageManager srgMgr = (StorageManager) this.getSystemService(Context.STORAGE_SERVICE);
        List<String> pathList = getUsbPaths(this);
        StorageVolume pV = srgMgr.getPrimaryStorageVolume();
        for (String p : pathList) {
            boolean mounted = checkMounted(srgMgr, p);
            Log.d(TAG, "path: " + p);
            Log.d(TAG, "mounted: " + mounted);
            File f = new File(p);

            StorageVolume volume = srgMgr.getStorageVolume(f);
            Log.d(TAG, "state: " + Environment.getExternalStorageState(f));
            Log.d(TAG, "isEmulated: " + Environment.isExternalStorageEmulated(f));
            Log.d(TAG, "isRemovable: " + Environment.isExternalStorageRemovable(f));
            if (mounted) {
                mVolumeRootPathList.add(p);
            }
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
            boolean mounted = !path.contains("emulated") && Environment.MEDIA_MOUNTED.equals(
                    mountState);
            // Method getStorageVolume = srgMgr.getClass().getMethod("getStorageVolume", File
            // .class);
            // StorageVolume storageVolume = (StorageVolume)getStorageVolume.invoke(srgMgr, new
            // File(path));
            // if (storageVolume != null) {
            //     storageVolume.getState();
            // }
            return mounted;

        } catch (Exception e) {
            Log.e(TAG, "getMountedUSBPath: ", e);
        }

        return false;
    }


    // android 11及以上
    private void testCreateDirection30(@NonNull String volumeRootPath, int id) {
        File rootUSBDisk = new File(volumeRootPath);
        Log.d(TAG, "testCreateDirection30: " + volumeRootPath
                + ", canRead=" + rootUSBDisk.canRead()
                + ", canWrite=" + rootUSBDisk.canWrite()
                + ", canExecute=" + rootUSBDisk.canExecute()
             );

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        File dir = new File(
                volumeRootPath + File.separator + "dir-" + id + "-" + format.format(new Date()));
        boolean createdDir = dir.exists() ? dir.isDirectory() : dir.mkdirs();
        if (createdDir) {
            Log.d(TAG, "testCreateDirection30: true, " + dir.getAbsolutePath());
        } else {
            Log.d(TAG, "testCreateDirection30: false, " + dir.getAbsolutePath());
        }
    }

    private void testCreateDirection29(@NonNull String volumeRootPath, int id) {
        DocumentsUtils.getExtSdCardPaths(this);
        File rootUSBDisk = new File(volumeRootPath);
        Log.d(TAG, "testCreateDirection29: " + volumeRootPath
                + ", canRead=" + rootUSBDisk.canRead()
                + ", canWrite=" + rootUSBDisk.canWrite()
                + ", canExecute=" + rootUSBDisk.canExecute()
             );
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        DocumentFile root = DocumentFile.fromTreeUri(this, mSDUri);
        DocumentFile rootDir = DocumentFile.fromTreeUri(UDiskActivity.this, mSDUri);
        if (rootDir != null && rootDir.canWrite()
                && rootDir.canRead() && rootDir.isDirectory()) {
            DocumentFile[] file = rootDir.listFiles();
            for (DocumentFile doc : file) {
                Log.d(TAG, "name: " + doc.getName());
            }
        }


        DocumentFile documentFile = null;
        if (root != null) {
            String path = "abcd" + File.separator
                    + "123" + File.separator
                    + "abcd" + File.separator
                    + format.format(new Date());
            documentFile = root.createDirectory(path);
            documentFile.createFile("application/gzip", "demo-hello");
            documentFile.createFile("*/*", "demo-hello-any-type");
        }
        // File dir = new File(volumeRootPath + File.separator + "ASF-dir-" + id + "-" +format
        // .format(new Date()));
        // DocumentFile documentFile = DocumentsUtils.getDocumentFile(dir, true,
        //         UDiskActivity.this);
        if (documentFile != null) {
            Log.d(TAG, "testCreateDirection29: DocumentFile" + volumeRootPath
                    + ", canRead=" + documentFile.canRead()
                    + ", canWrite=" + documentFile.canWrite()
                    + ", exists=" + documentFile.exists()
                    + ", getUri=" + documentFile.getUri()
                    + ", createDirectory=" + documentFile.createDirectory("123")
                 );
        }

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
            StorageManager srgMgr = (StorageManager) context.getSystemService(
                    Context.STORAGE_SERVICE);
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
                        Log.d(TAG, "onReceive: Uri=" + uri.toString()
                                + "\ngetScheme=" + uri.getScheme()
                                + "\ngetAuthority=" + uri.getAuthority()
                                + "\ngetPath=" + uri.getPath()
                                + "\ngetEncodedPath=" + uri.getEncodedPath()
                                + "\ngetFragment=" + uri.getFragment()
                                + "\ngetEncodedFragment=" + uri.getEncodedFragment()
                             );
                        // onReceive: Uri=file:///storage/FE4CC8274CC7D913
                        // getScheme=file
                        // getAuthority=getPath=/storage/FE4CC8274CC7D913
                        // getEncodedPath=/storage/FE4CC8274CC7D913
                        // getFragment=null
                        // getEncodedFragment=null
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            String filePath = uri.getPath();
                            File rootFile = new File(filePath);
                            for (File file : Objects.requireNonNull(rootFile.listFiles())) {
                                Log.d(TAG,
                                        "name: " + file.getName() + "Path: " +
                                                file.getAbsolutePath());
                            }
                        } else {
                            DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri);
                            if (documentFile != null && documentFile.canWrite()
                                    && documentFile.canRead()) {
                                DocumentFile[] file = documentFile.listFiles();
                                for (DocumentFile doc : file) {
                                    Log.d(TAG, "name: " + doc.getName() + "Path: ");
                                }
                            }
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