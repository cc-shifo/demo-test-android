package com.example.demoudisk;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.Size;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import com.example.demoudisk.databinding.ActivityMainBinding;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1024;
    private static final int PERMISSION_MANAGE_ALL_REQUEST_CODE = 1025;

    private ActivityResultLauncher<Intent> mLauncherManagerAppAllFiles;
    private ActivityMainBinding mBinding;

    private Uri mUri;
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

    /**
     * Android 11, 12
     */
    private static final String[] REQUIRED_PERMISSION_LIST_30 = new String[]{
            // Manifest.permission.VIBRATE, // Gimbal rotation
            // Manifest.permission.BLUETOOTH, // Bluetooth connected products
            // Manifest.permission.BLUETOOTH_ADMIN, // Bluetooth connected products
            android.Manifest.permission.READ_EXTERNAL_STORAGE, // Log files
            // Manifest.permission.RECORD_AUDIO // Speaker accessory
    };

    /**
     * Android 13
     * target 33即以上申请READ_EXTERNAL_STORAGE立即返回false，在target 33之下是true。
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private static final String[] REQUIRED_PERMISSION_LIST_33 = new String[]{
            // Manifest.permission.VIBRATE, // Gimbal rotation
            Manifest.permission.READ_MEDIA_IMAGES, // Log files
            Manifest.permission.READ_MEDIA_VIDEO, // Log files
            Manifest.permission.READ_MEDIA_AUDIO, // Log files
            // Manifest.permission.BLUETOOTH, // Bluetooth connected products
            // Manifest.permission.BLUETOOTH_ADMIN, // Bluetooth connected products
            // Manifest.permission.RECORD_AUDIO // Speaker accessory
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        checkAndRequestPermissions();
        registerLauncherForManagerAppAllFile();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onPermissionResult(requestCode, permissions, grantResults);
    }


    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestPermissions33();// 13
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            checkAndRequestPermissions30();// 11
        } else {
            checkAndRequestPermissions29();// 10~7
        }
    }

    @RequiresApi(api = 33)
    private void checkAndRequestPermissions33() {
        if (hasPermissions(this, REQUIRED_PERMISSION_LIST_33)) {
            initBusiness();// 都允许
        } else {
            requestPermissions(REQUIRED_PERMISSION_LIST_33, PERMISSION_REQUEST_CODE);
        }
    }


    @RequiresApi(api = 30)
    private void checkAndRequestPermissions30() {
        if (hasPermissions(this, REQUIRED_PERMISSION_LIST_30)) {
            initBusiness();// 都允许
        } else {
            requestPermissions(REQUIRED_PERMISSION_LIST_30, PERMISSION_REQUEST_CODE);
        }
    }

    private void checkAndRequestPermissions29() {
        if (hasPermissions(this, REQUIRED_PERMISSION_LIST)) {
            initBusiness();// 都允许
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
                    initBusiness();// 都允许
                }/* else { // 避免循环请求
                    requestPermissions(REQUIRED_PERMISSION_LIST, PERMISSION_REQUEST_CODE);
                }*/
            }
        }
    }

    private void initBusiness() {
        mBinding.btnUsbDisk.setOnClickListener(v -> {
            initCompactUSBDisk();
        });
        mBinding.btnUsbUsb.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, USBActivity.class);
            startActivity(intent);
        });

        mBinding.btnTestSaf.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, TestSAF.class);
            startActivity(intent);
        });
        mBinding.btnTestMedia.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, TestMedia.class);
            startActivity(intent);
        });

        mBinding.btnTestDoc.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, TestDocument.class);
            startActivity(intent);
        });

        mBinding.btnTestKotlin.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, TestKotlinActivity.class);
            startActivity(intent);
        });
    }

    /**
     *      int uid = context.getApplicationInfo().uid;
     *
     *
     *      return PackageManager.PERMISSION_GRANTED
     *                         == context.checkPermission(
     *                                 Manifest.permission.MANAGE_EXTERNAL_STORAGE, Process.myPid
     *                                 (), uid);
     *
     * {@link Environment#getStorageDirectory()}
     */
    private void initCompactUSBDisk() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 11 Use File API Access SD
            if (Environment.isExternalStorageManager()) {
                Intent intent = new Intent(MainActivity.this, UDiskActivity.class);
                startActivity(intent);
            } else {
                requestManageAppAllFiles(); // 11 File Access
            }
        } else/* if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) */{
            // 7~10, SAF Document API
            if (mUri != null) {
                DocumentFile root = DocumentFile.fromTreeUri(this, mUri);
                if (root != null && root.canWrite() && root.canRead()) {
                    Intent intent = new Intent(MainActivity.this, UDiskActivity.class);
                    intent.setData(mUri);// SD卡的root Uri传递出去
                    startActivity(intent);
                    return;
                }
            }
            StorageManager srgMgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
            Intent intent = null;
            List<StorageVolume> volumeList = srgMgr.getStorageVolumes();
            for (StorageVolume volume : volumeList) {
                if (!volume.isPrimary() && !volume.isEmulated() && volume.isRemovable()) {
                    // intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    // final String rootId = isEmulated()
                    //         ? DocumentsContract.EXTERNAL_STORAGE_PRIMARY_EMULATED_ROOT_ID
                    //         : mFsUuid;
                    // final Uri rootUri = DocumentsContract.buildRootUri(
                    //         DocumentsContract.EXTERNAL_STORAGE_PROVIDER_AUTHORITY, rootId);
                    // final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    //         .putExtra(DocumentsContract.EXTRA_INITIAL_URI, rootUri)
                    //         .putExtra(DocumentsContract.EXTRA_SHOW_ADVANCED, true);

                    // new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                    //         .authority(authority).appendPath(PATH_ROOT).appendPath(rootId)
                    //         .build();

                    // String volumeName = MediaStore.getVolumeName(uri);
                    // Objects.equals(volumeName, MediaStore.VOLUME_EXTERNAL)

                    // content://com.android.externalstorage.documents/tree/4B71-8328%3A
                    // Uri uri =new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                    //         .authority("com.android.externalstorage.documents").appendPath
                    //         ("tree").appendPath("4B71-8328:").build();
                    // String volumeName = MediaStore.getVolumeName(uri);
                    // Log.d(TAG, "initCompactUSBDisk: volumeName=" + volumeName);
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                        // 10 Storage Access Framework
                        intent = volume.createOpenDocumentTreeIntent();// 用户手动选, 选择后返回Uri
                    } else {// 9~7  Storage Access Framework
                        intent = volume.createAccessIntent(null);
                    }
                    Log.d(TAG, "initCompactUSBDisk: Uuid=" + volume.getUuid());
                    break;
                }
            }

            if (intent != null) {
                mLauncherManagerAppAllFiles.launch(intent, null);
            }
        }/* else { // 9~7  Storage Access Framework
            StorageManager srgMgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
            Intent intent = null;
            List<StorageVolume> volumeList = srgMgr.getStorageVolumes();
            for (StorageVolume volume : volumeList) {
                if (!volume.isPrimary() && !volume.isEmulated() && volume.isRemovable()) {
                    intent = volume.createAccessIntent(null);
                    break;
                }
            }

            mLauncherManagerAppAllFiles.launch(intent, null);
            // Intent intent = new Intent(MainActivity.this, UDiskActivity.class);
            // startActivity(intent);
        }*/
    }

    public void openDirectory(Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);
        //
        // startActivityForResult(intent, your-request-code
        //
        //                       );
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
    @SuppressLint("WrongConstant")
    private void registerLauncherForManagerAppAllFile() {
        mLauncherManagerAppAllFiles = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Android 11 在请求完ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION后，直接返回CANCEL和null
                    // Android 12 在请求完ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION后，直接返回CANCEL和null
                    // Android 13 在请求完ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION后，直接返回CANCEL和null
                    Log.d(TAG, "registerLauncherForManagerAppAllFile: " + result.getResultCode()
                            + ", getData=" + result.getData()
                            + ", uri=" + (result.getData() != null && result.getData()
                            .getData() != null ? result.getData().getData().toString() : "null"));
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {// Android 7~10 SAF Uri
                            Intent data = result.getData();
                            Uri uri = data.getData();
                            int takeFlags = data.getFlags();
                            if ((takeFlags & Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    != Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    || (takeFlags & Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                                    != Intent.FLAG_GRANT_WRITE_URI_PERMISSION) {
                                takeFlags = takeFlags & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                // Check for the freshest data.
                                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                            }
                            Log.d(TAG, "registerLauncherForManagerAppAllFile: Uri=" + uri.toString()
                                    + "\ngetScheme=" + uri.getScheme()
                                    + "\ngetAuthority=" + uri.getAuthority()
                                    + "\ngetPath=" + uri.getPath()
                                    + "\ngetEncodedPath=" + uri.getEncodedPath()
                                    + "\ngetFragment=" + uri.getFragment()
                                    + "\ngetEncodedFragment=" + uri.getEncodedFragment()
                                    + "\ngetDocumentId=" + (DocumentsContract
                                    .isDocumentUri(MainActivity.this, uri) ?
                                    DocumentsContract.getDocumentId(uri): "not document uri")
                                    + "\nisTreeUri=" + DocumentsContract.isTreeUri(uri)
                                    // + "\nisTreeUri=" + DocumentsContract.EXTERNAL_STORAGE_PROVIDER_AUTHORITY
                                    // + "\nisTreeUri=" + Providers.AUTHORITY_STORAGE // DocumentUI

                            );
                            // DocumentsContract.isDocumentUri(context, uri)
                            // DocumentsContract.getDocumentId(uri)
                            // registerLauncherForManagerAppAllFile: Uri=content://com.android.externalstorage.documents/tree/4B71-8328%3A
                            // getScheme=content
                            // getAuthority=com.android.externalstorage.documents
                            // getPath=/tree/4B71-8328:
                            // getEncodedPath=/tree/4B71-8328%3A
                            // getFragment=null
                            // getEncodedFragment=null
                            // getDocumentId=not document uri
                            // isTreeUri=true
                            // intent.setData(uri);
                            mUri = uri;// SD卡的root Uri传递出去
                        }

                        // Intent intent = new Intent(MainActivity.this, UDiskActivity.class);
                        // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        //     startActivity(intent);
                        // }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestManageAppAllFiles() {
        // 方式一跳转到总设置界面，然后在总设置界面上选择当前APP，进入到授权界面。
        // Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        // mLauncherManagerAppAllFiles.launch(intent, null);

        // 方式二，直接进入到指定APP的授权界面进行设置。
        // 必须在intent DATA里设置包名.
        // For example "package:com.my.app".
        // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        Intent intent2 = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent2.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
        // intent2.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
        mLauncherManagerAppAllFiles.launch(intent2, null);
        // // ActivityResultLauncher和registerForActivityResult

        // 方式三，deprecated
        // Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        // // intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
        // startActivityForResult(intent, PERMISSION_MANAGE_ALL_REQUEST_CODE);
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
        // StorageManager#getStorageVolume("");
        // MediaStore#VOLUME_EXTERNAL
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

    // @Override
    // public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
    //     super.onActivityResult(requestCode, resultCode, resultData);
    //     if (requestCode == CREATE_FILE
    //             && resultCode == Activity.RESULT_OK) {
    //         // The result data contains a URI for the document or directory that
    //         // the user selected.
    //         Uri uri = null;
    //         if (resultData != null) {
    //             uri = resultData.getData();
    //             // Perform operations on the document using its URI.
    //         }
    //     } else if (requestCode == PERMISSION_MANAGE_ALL_REQUEST_CODE) {
    //
    //     }
    // }

    private void testMedia() {
        // https://developer.android.google.cn/training/data-storage/shared/media?hl=zh-cn#direct-file-paths
        // List<Uri> urisToModify = /* A collection of content URIs to modify. */
        //         PendingIntent editPendingIntent = MediaStore.createWriteRequest(contentResolver,
        //         urisToModify);
        //
        // // Launch a system prompt requesting user permission for the operation.
        // startIntentSenderForResult(editPendingIntent.getIntentSender(),
        //         EDIT_REQUEST_CODE, null, 0, 0, 0);


        // 创建Uri
        // Uri contentUri = ContentUris.withAppendedId(
        //         MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        //         cursor.getLong(Integer.parseInt(BaseColumns._ID)));
        // String fileOpenMode = "r";
        // ParcelFileDescriptor parcelFd =
        //         resolver.openFileDescriptor(contentUri, fileOpenMode);
        // if (parcelFd != null) {
        //     int fd = parcelFd.detachFd();
        //     // Pass the integer value "fd" into your native code. Remember to call
        //     // close(2) on the file descriptor when you're done using it.
        // }


        // 创建Uri
        // Updates an existing media item.
        // long mediaId = // MediaStore.Audio.Media._ID of item to update.
        //         ContentResolver resolver = getApplicationContext()
        //         .getContentResolver();
        //
        // // When performing a single item update, prefer using the ID.
        // String selection = MediaStore.Audio.Media._ID + " = ?";
        //
        // // By using selection + args you protect against improper escaping of
        // // values. Here, "song" is an in-memory object that caches the song's
        // // information.
        // String[] selectionArgs = new String[] { getId().toString() };
        //
        // // Update an existing song.
        // ContentValues updatedSongDetails = new ContentValues();
        // updatedSongDetails.put(MediaStore.Audio.Media.DISPLAY_NAME,
        //         "My Favorite Song.mp3");
        //
        // // Use the individual song's URI to represent the collection that's
        // // updated.
        // int numSongsUpdated = resolver.update(
        //         myFavoriteSongUri,
        //         updatedSongDetails,
        //         selection,
        //         selectionArgs);
    }
}