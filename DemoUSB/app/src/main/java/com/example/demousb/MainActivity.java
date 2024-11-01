package com.example.demousb;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.Size;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.demousb.databinding.ActivityMainBinding;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1024;


    private ActivityMainBinding mBinding;
    /**
     * Android 10及以下。Manifest中requestLegacyExternalStorage=true
     */
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            // Manifest.permission.VIBRATE, // Gimbal rotation
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, // Log files
            // Manifest.permission.BLUETOOTH, // Bluetooth connected products
            // Manifest.permission.BLUETOOTH_ADMIN, // Bluetooth connected products
            android.Manifest.permission.READ_EXTERNAL_STORAGE, // Log files
            // Manifest.permission.RECORD_AUDIO // Speaker accessory
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        checkAndRequestPermissions();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onPermissionResult(requestCode, permissions, grantResults);
    }

    private void checkAndRequestPermissions() {
        if (hasPermissions(this, REQUIRED_PERMISSION_LIST)) {
            init();
        } else {
            requestPermissions(REQUIRED_PERMISSION_LIST, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean hasPermissions(@NonNull Context context,
            @Size(min = 1) @NonNull String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");

            // DANGER ZONE!!! Changing this will break the library.
            return true;
        }

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private void onPermissionResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && permissions.length == grantResults.length) {
                int i;
                for (i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        break;
                    }
                }

                if (i == grantResults.length) {
                    init();// 都允许
                } else {
                    requestPermissions(REQUIRED_PERMISSION_LIST, PERMISSION_REQUEST_CODE);
                }
            }
        }
    }

    private void init() {
        mBinding.btnUsbDisk.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UDiskActivity.class);
            startActivity(intent);
        });
        mBinding.btnUsbUsb.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, USBActivity.class);
            startActivity(intent);
        });
    }

    /**
     * https://developer.android.google.cn/training/data-storage/manage-all-files?hl=zh-cn
     * #operations-allowed-manage-external-storage
     * 对 USB On-The-Go (OTG) 驱动器和 SD 卡的根目录的访问权限。
     * 当应用具有 MANAGE_EXTERNAL_STORAGE 权限时，它可以使用 MediaStore API
     * 或直接文件路径访问这些额外的文件和目录。但是，当您使用存储访问框架时，只有在您不具备
     * MANAGE_EXTERNAL_STORAGE 权限也能访问文件或目录的情况下才能访问文件或目录。
     * <p>
     * <p>
     * 不同Android版本应用兼容性
     * 所有版本
     * https://developer.android.google.cn/about/versions?hl=zh-cn
     * android 13
     * https://developer.android.google.cn/about/versions/13?hl=zh-cn
     * https://developer.android.google.cn/about/versions/13/migration?hl=zh-cn
     * <p>
     * 以 Android 10 或更高版本为目标平台的应用可以访问系统为每个外部存储卷分配的唯一名称。
     * 此命名系统可帮助您高效地整理内容并将内容编入索引，还可让您控制新媒体文件的存储位置。
     * https://developer.android.google.cn/training/data-storage/shared/media?hl=zh-cn#storage
     * -volume
     */
    private void getExtMng() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                startActivity(intent);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void testExtVolume() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            Set<String> volumeNames = null;
            volumeNames = MediaStore.getExternalVolumeNames(this);
            String firstVolumeName = volumeNames.iterator().next();
        }

        // Uri photoUri = Uri.withAppendedPath(
        //         MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        //         cursor.getString(idColumnIndex));
        // Add a specific media item.
        ContentResolver resolver = getApplicationContext()
                .getContentResolver();

        // Find all audio files on the primary external storage device.
        Uri audioCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            audioCollection = MediaStore.Audio.Media
                    .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            audioCollection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        // Publish a new song.
        ContentValues newSongDetails = new ContentValues();
        newSongDetails.put(MediaStore.Audio.Media.DISPLAY_NAME,
                "My Song.mp3");

        // Keep a handle to the new song's URI in case you need to modify it
        // later.
        Uri myFavoriteSongUri = resolver.insert(audioCollection, newSongDetails);
    }


    // Request code for creating a PDF document.
    private static final int CREATE_FILE = 1;

    private void createFile(Uri pickerInitialUri) {

        // 图片（包括照片和屏幕截图），存储在 DCIM/和 Pictures/目录中。系统将这些文件添加到 MediaStore.Images 表格中。
        // 视频，存储在 DCIM/、Movies / 和 Pictures / 目录中。系统将这些文件添加到 MediaStore.Video 表格中。
        // 音频文件，存储在 Alarms/、Audiobooks /、Music /、Notifications /、Podcasts / 和 Ringtones / 目录中。此外，
        // 系统还可以识别 Music/或 Movies/目录中的音频播放列表，以及 Recordings/目录中的录音。系统将这些文件添加到 MediaStore.Audio 表格中。
        // Recordings / 目录在 Android 11（API 级别 30）及更低版本中不可用。
        // 下载的文件，存储在 Download/目录中。在搭载 Android 10（API 级别 29）及更高版本的设备上，这些文件存储在 MediaStore.Downloads
        // 表格中。
        // 此表格在 Android 9（API 级别 28）及更低版本中不可用。
        // MediaStore.Downloads.EXTERNAL_CONTENT_URI
        // MediaStore.Downloads.getContentUri(volumeName)
        // MediaStore.Audio.Media.getContentUri();
        // ContentValues values = new ContentValues();
        // values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        // values.put(MediaStore.MediaColumns.RELATIVE_PATH, createRelativePath30(type, path));
        // Uri uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,   values);
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);


        // ActivityResultLauncher和registerForActivityResult
        startActivityForResult(intent, CREATE_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == CREATE_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
            }
        }
    }
}