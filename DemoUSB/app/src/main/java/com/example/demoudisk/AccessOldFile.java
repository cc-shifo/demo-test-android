package com.example.demoudisk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.util.Formatter;
import java.util.List;

public class AccessOldFile {
    private static final String TAG = "AccessOldFile";
    private static ActivityResultLauncher<Intent> mOldAccessLauncher1;
    private static ActivityResultLauncher<Uri> mOldAccessLauncher2;

    // frameworks/base/core/java/android/provider/Downloads.java
    /**
     * URI segment to access a publicly accessible downloaded file
     */
    public static final String PUBLICLY_ACCESSIBLE_DOWNLOADS_URI_SEGMENT = "public_downloads";
    /**
     * The content URI for accessing publicly accessible downloads (i.e., it requires no
     * permissions to access this downloaded file)
     */
    public static final Uri PUBLICLY_ACCESSIBLE_DOWNLOADS_URI =
            Uri.parse("content://downloads/" + PUBLICLY_ACCESSIBLE_DOWNLOADS_URI_SEGMENT);

    public static Uri mOldFileUrl;


    private static Formatter formatter = new Formatter();

    private static String formatTest(String format, Object... args) {
        return formatter.format(format, args).toString();
    }


    public static Intent createAccessIntent(@NonNull Activity activity, @NonNull String directoryName) {
        // @hide !Environment.isStandardDirectory(directoryName)
        final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Uri spcUri = Uri.withAppendedPath(PUBLICLY_ACCESSIBLE_DOWNLOADS_URI, directoryName);
            Uri spcUri = PUBLICLY_ACCESSIBLE_DOWNLOADS_URI;
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
            intent.addFlags(takeFlags);

            // 试验3 Download路径
            String path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator;
            DocumentFile documentFile = DocumentFile.fromFile(new File(path));
            logD(formatTest("documentFile: exists=%b, canWrite=%b, canRead=%b",
                    documentFile.exists(), documentFile.canWrite(), documentFile.canRead()));
            if (documentFile.exists()) {
                spcUri = documentFile.getUri();
                logD("documentFile: uri=%s", spcUri.toString());
            }
            // 试验2 Download下的路径：包名
            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator
                    + activity.getPackageName();
            documentFile = DocumentFile.fromFile(new File(path));
            logD("documentFile: exists=%b, canWrite=%b, canRead=%b",
                    documentFile.exists(), documentFile.canWrite(), documentFile.canRead());
            if (documentFile.exists()) {
                spcUri = documentFile.getUri();
                logD("documentFile: uri=%s", spcUri.toString());
            }

            // 试验3 Download下的路径：包名+param
            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                    + File.separator + activity.getPackageName()
                    + File.separator + "param";
            documentFile = DocumentFile.fromFile(new File(path));
            // documentFile: exists=true, canWrite=true, canRead=true, length=3452(line:84)
            logD("documentFile: exists=%b, canWrite=%b, canRead=%b, length=%d",
                    documentFile.exists(), documentFile.canWrite(), documentFile.canRead(),
                    documentFile.length());
            if (documentFile.exists()) {
                spcUri = documentFile.getUri();
                // documentFile: uri=file:///storage/emulated/0/Download/com.zhdgps.hdars/param
                logD("documentFile: uri=%s", spcUri.toString());
                logD("documentFile: delete=%b", documentFile.delete());// false
            }

            // ContentResolver 不一样
            logD("app=%s, activity=%s", activity.getApplicationContext().getContentResolver(),
                    activity.getContentResolver());

            // StorageVolume
            StorageManager srgMgr = (StorageManager) activity.getSystemService(
                    Context.STORAGE_SERVICE);
            StorageVolume volume = srgMgr.getPrimaryStorageVolume();
            // final String rootId = isEmulated()// primary
            //         ? DocumentsContract.EXTERNAL_STORAGE_PRIMARY_EMULATED_ROOT_ID
            //         : mFsUuid;
            String mFsUuid = volume.getUuid();// output null
            logD("mFsUuid=%s", mFsUuid);// false
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                logD("getStorageUuid=%s", volume.getStorageUuid().toString());// false
            }
            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                    + File.separator + activity.getPackageName()
                    + File.separator + "param";
            // StorageVolume#createOpenDocumentTreeIntent
            // DocumentsContract.EXTERNAL_STORAGE_PRIMARY_EMULATED_ROOT_ID // primary
            // final Uri rootUri = DocumentsContract.buildRootUri(
            //         DocumentsContract.EXTERNAL_STORAGE_PROVIDER_AUTHORITY, rootId);
            final Uri rootUri = DocumentsContract.buildRootUri(
                    "com.android.externalstorage.documents", "primary");
            //             Uri.withAppendedPath(rootUri, path);
            Uri.Builder builder = rootUri.buildUpon();
            builder = builder.appendEncodedPath(Environment.DIRECTORY_DOWNLOADS)
                    .appendEncodedPath(activity.getPackageName());
            spcUri = builder.build();
            // final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            //         .putExtra(DocumentsContract.EXTRA_INITIAL_URI, rootUri)
            //         .putExtra(DocumentsContract.EXTRA_SHOW_ADVANCED, true);
            logD("buildRootUri=%s", spcUri.toString());// false


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent intent1 = volume.createOpenDocumentTreeIntent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Uri uri = intent1.getParcelableExtra(DocumentsContract.EXTRA_INITIAL_URI,
                            Uri.class);
                    path = uri.toString();
                } else {
                    Uri uri = (Uri) intent1.getParcelableExtra(DocumentsContract.EXTRA_INITIAL_URI);
                    path = uri.toString();
                }
                // content://com.android.externalstorage.documents/root/primary
                logD("createOpenDocumentTreeIntent=%s", path);
            }
            String original = new String(path);


            // 打开时使用content://com.android.externalstorage.documents/document/
            String tmp = new String(path);
            tmp = tmp.replace("root", "document");
            tmp += "%3A";
            tmp += (Environment.DIRECTORY_DOWNLOADS);
            tmp += ("%2F" + activity.getPackageName());
            spcUri = Uri.parse(tmp);
            // spcUri=content://com.android.externalstorage
            // .documents/document/primary%3A%2FDownload%2Fcom.zhdgps.hdars
            logD("spcUri=%s", spcUri);

            tmp = new String(path);
            tmp = tmp.replace("root", "document");
            tmp += "%3A";
            spcUri = Uri.parse(tmp);
            spcUri = spcUri.buildUpon().appendEncodedPath(Environment.DIRECTORY_DOWNLOADS)
                    .appendEncodedPath(activity.getPackageName())
                    .build();
            // 结果：不能用， appendEncodedPath内部携带了没有编码的文件分隔符。
            // spcUri=content://com.android.externalstorage
            // .documents/document/primary%3A/Download/com.zhdgps.hdars
            logD("spcUri=%s", spcUri.toString());


            // 测试File.separator能否被编码%2F
            String path2 = Environment.DIRECTORY_DOWNLOADS +
                    File.separator + activity.getPackageName();
            String encodeStr = Uri.encode(path2);
            // encodeStr=%2FDownload%2Fcom.zhdgps.hdars
            logD("encodeStr=%s", encodeStr);

            // 测试能否直接使用路径。结果：可以用，但是没有被编译成%2F
            spcUri = Uri.parse(tmp);
            spcUri = spcUri.buildUpon().appendEncodedPath(path2)
                    .build();
            // content://com.android.externalstorage.documents/document/primary%3A/Download/com
            // .zhdgps.hdars
            logD("appendEncodedPath path2=%s", spcUri.toString());
            spcUri = Uri.parse(tmp);

            // 测试Uri.encode后的路径能否直接使用。结果：可以用，但是在%3A之后的文件分隔符还是没有转成%2F
            spcUri = spcUri.buildUpon().appendEncodedPath(Uri.encode(path2))
                    .build();
            // path3=content://com.android.externalstorage
            // .documents/document/primary%3A/Download%2Fcom.zhdgps.hdars
            logD("appendEncodedPath path3=%s", spcUri.toString());

            // 测试字符串中有被编码的%3A和没有被编码的文件分隔符，能不能统一再次编码。
            spcUri = Uri.parse(tmp);
            spcUri = spcUri.buildUpon().appendEncodedPath(path2)
                    .build();
            spcUri = Uri.parse(Uri.encode(spcUri.toString()));
            // 结果：再次编码将会把之前的%3A格式化错误。
            logD("Uri.parse(Uri.encode=%s", spcUri.toString());


            // 打开时可以不用添加flag，如final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            //                     | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            //                     | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
            //             intent.addFlags(takeFlags);
            // 打开时使用content://com.android.externalstorage.documents/document/
            // spcUri=content://com.android.externalstorage
            // .documents/document/primary%3A%2FDownload%2Fcom.zhdgps.hdars
            // 结果：正常的需要编码。
            String okTmp = original.replace("root", "document");
            okTmp += "%3A";
            okTmp += (/*"%2F" + */Environment.DIRECTORY_DOWNLOADS);
            okTmp += ("%2F" + activity.getPackageName());
            spcUri = Uri.parse(okTmp);
            // spcUri=content://com.android.externalstorage
            // .documents/document/primary%3A%2FDownload%2Fcom.zhdgps.hdars
            logD("spcUri=%s", spcUri);

            boolean granted = activity.getApplicationContext().checkCallingUriPermission(
                    spcUri,
                    /*Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | */Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    == PackageManager.PERMISSION_GRANTED;
            logD("GRANT_READ_and GRANT_WRITE_URI  granted=%b", granted);
            if (granted) {
                return null;
            }

            // 授权时，持久的使用take, 单次的使用grant
            // 授权时，使用回调里输出的tree uri
            // 授权时，使用content://com.android.externalstorage.documents/tree/
            final String testUir =
                    "content://com.android.externalstorage" +
                            ".documents/tree/primary%3ADownload%2Fcom.zhdgps.hdars";
            granted = activity.getApplicationContext().checkCallingUriPermission(
                    Uri.parse(testUir),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    == PackageManager.PERMISSION_GRANTED;
            // checkCallingUriPermission  granted=false
            logD("checkCallingUriPermission  granted=%b", granted);


            // 检查时，使用回调里输出的tree uri
            // 检查时，使用content://com.android.externalstorage.documents/tree/
            // AccessOldFile: permUri: content://com.android.externalstorage
            // .documents/tree/primary%3ADownload%2Fcom.zhdgps.hdars,
            // getPath=/tree/primary:Download/com.zhdgps.hdars, getFragment=null,
            // getPathSegments=[tree, primary:Download/com.zhdgps.hdars],
            logD("testUir=%s\n\n\n", testUir);
            List<UriPermission> uriPermissions =
                    activity.getApplicationContext().getContentResolver()
                            .getPersistedUriPermissions();
            for (UriPermission uriPermission : uriPermissions) {
                Uri permUri = uriPermission.getUri();
                String permUriStr = permUri.toString();
                logD("permUri: %s, getPath=%s, getFragment=%s, getPathSegments=%s, ",
                        permUriStr, permUri.getPath(),
                        permUri.getFragment(), permUri.getPathSegments().toString());
                boolean permWrite = uriPermission.isWritePermission();
                boolean permRead = uriPermission.isReadPermission();
                boolean permUriGranted = permUriStr.equals(testUir) && permWrite && permRead;
                logD("UriPermission: %s, permWrite=%b, permRead=%b,  uriGranted=%b",
                        permUriStr, permWrite, permRead, permUriGranted);
                if (permUriGranted) {
                    return null;
                }
            }


            // Check for the freshest data.
            // getContentResolver().takePersistableUriPermission(uri, takeFlags);
            // intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, spcUri);
        }
        return intent;
    }

    public static void logD(@NonNls String message, Object... args) {
        formatter.format(message, args);
        logD(formatter.toString());
    }

    public static void initOldAccess1Permission(@NonNull final ComponentActivity activity) {
        mOldAccessLauncher1 = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        Intent data = result.getData();
                        if (result.getResultCode() == Activity.RESULT_OK && data != null) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                                return;
                            }
                            // Android 7~10 SAF Uri
                            mOldFileUrl = data.getData();// U disk的tree Uri
                            if (mOldFileUrl != null) {
                                logD("mOldAccessLauncher: Downloads EXTERNAL_CONTENT_URI=%s",
                                        MediaStore.Downloads.EXTERNAL_CONTENT_URI);
                                logD("mOldAccessLauncher: Downloads INTERNAL_CONTENT_URI=%s",
                                        MediaStore.Downloads.INTERNAL_CONTENT_URI);
                                logD("mOldAccessLauncher: mOldFileUrl=%s",
                                        mOldFileUrl.toString());
                                logD("mOldAccessLauncher: isTreeUri=%b",
                                        DocumentsContract.isTreeUri(mOldFileUrl));
                                logD("mOldAccessLauncher: isDocumentUri=%b",
                                        DocumentsContract.isDocumentUri(activity, mOldFileUrl));
                                final List<String> paths = mOldFileUrl.getPathSegments();
                                for (int i = 0; i < paths.size(); i++) {
                                    logD("mOldAccessLauncher: getPathSegments[%d]=%s", i,
                                            paths.get(i));
                                }


                                boolean granted = activity
                                        .checkCallingUriPermission(mOldFileUrl,
                                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                                        == PackageManager.PERMISSION_GRANTED;
                                logD("GRANT_READ_and GRANT_WRITE_URI  granted=%b", granted);

                                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                //     List<Uri> collections = new ArrayList<>();
                                //     collections.add(mOldFileUrl);
                                //     PendingIntent pendingIntent = MediaStore.createDeleteRequest(
                                //             activity.getContentResolver(),
                                //             collections);
                                //     try {
                                //         activity.startIntentSenderForResult(pendingIntent
                                //         .getIntentSender(), 10001, null, 0, 0, 0);
                                //     } catch (IntentSender.SendIntentException e) {
                                //         Timber.e(e);
                                //     }
                                //     return;
                                // }


                                // 授权时，持久的使用take, 单次的使用grant
                                // 授权时，使用回调里输出的tree uri
                                activity.grantUriPermission(activity.getPackageName(), mOldFileUrl,
                                        Intent
                                                .FLAG_GRANT_READ_URI_PERMISSION | Intent
                                                .FLAG_GRANT_WRITE_URI_PERMISSION);
                                activity.getContentResolver().takePersistableUriPermission(
                                        mOldFileUrl, Intent
                                                .FLAG_GRANT_READ_URI_PERMISSION | Intent
                                                .FLAG_GRANT_WRITE_URI_PERMISSION);

                                DocumentFile file = DocumentFile.fromTreeUri(activity, mOldFileUrl);
                                if (file != null && file.isDirectory()) {
                                    DocumentFile[] fileList = file.listFiles();
                                    for (DocumentFile f : fileList) {
                                        // exists=true, canRead=true, canWrite=true, length=3452
                                        logD(
                                                "fileList: name=%s exists=%b, canRead=%b, " +
                                                        "canWrite=%b, length=%d",
                                                f.getName(), f.exists(), f.canRead(), f.canWrite(),
                                                f.length());
                                        if ("param".equals(f.getName())) {
                                            // AccessOldFile: param delete=true
                                            logD("param delete=%b", f.delete());
                                        }
                                    }
                                }


                                List<UriPermission> uriPermissions =
                                        activity.getContentResolver()
                                                .getPersistedUriPermissions();
                                for (UriPermission uriPermission : uriPermissions) {
                                    String permUriStr = uriPermission.getUri().toString();
                                    boolean permWrite = uriPermission.isWritePermission();
                                    boolean permRead = uriPermission.isReadPermission();
                                    boolean permUriGranted = permUriStr.equals(
                                            mOldFileUrl.toString()) && permWrite && permRead;
                                    logD(
                                            "uriPermissions getContentResolver: %s, permWrite=%b," +
                                                    " permRead=%b,  uriGranted=%b",
                                            permUriStr, permWrite, permRead, permUriGranted);
                                }


                                // document 模式打开的uri只能用DocumentFile来访问，不能用Mediastore#query
                                // String fileId = null;
                                // String[] columns = new String[]{
                                //         MediaStore.MediaColumns._ID,
                                //         MediaStore.MediaColumns.DATA,/*绝对路径*/
                                //         MediaStore.MediaColumns.RELATIVE_PATH,
                                //         MediaStore.MediaColumns.DISPLAY_NAME};
                                //
                                // String c1 = MediaStore.MediaColumns.DISPLAY_NAME + "=?";
                                // // https://www.cnblogs.com/webabcd/p/android_storage_Android11Demo2.html
                                // // 特别注意相对路径要添加结束时的文件分隔符，不加的话查询不到。
                                // String[] cv1 = new String[]{
                                //         /*Environment.DIRECTORY_DOWNLOADS + File.separator,*/
                                //         activity.getPackageName()};
                                // ContentResolver crl1 = activity.getContentResolver();
                                // try (Cursor cursor = crl1.query(MediaStore.Downloads
                                // .EXTERNAL_CONTENT_URI,
                                //         columns, null, null, null)) {
                                //     if (cursor != null) {
                                //         // 绝对路径
                                //         int idFilePath = cursor.getColumnIndexOrThrow(
                                //                 MediaStore.MediaColumns.DATA);
                                //         int id = cursor.getColumnIndexOrThrow(
                                //                 MediaStore.MediaColumns._ID);
                                //         while (cursor.moveToNext()) {
                                //             if (id >= 0) {
                                //                 fileId = cursor.getString(id);
                                //                 logD("RELATIVE_PATH: %s, %s", fileId,
                                //                         cursor.getString(idFilePath));
                                //                 break;
                                //             }
                                //         }
                                //     }
                                // } catch (Exception e) {
                                //     Timber.e(e);
                                // }


                                // int takeFlags = data.getFlags();
                                // takeFlags = takeFlags & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                //         | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                // // Check for the freshest data.
                                // contentResolver.takePersistableUriPermission(mOldFileUrl,
                                // takeFlags);
                                // Uri spcUri = Uri.withAppendedPath(mOldFileUrl, "param");
                                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                //     logD("mOldAccessLauncher: spcUri=%s", spcUri
                                //     .toString());
                                //     contentResolver.delete(mOldFileUrl, null);
                                // }
                                // if (paths.size() >= 4 && PATH_TREE.equals(paths.get(0))
                                //         && PATH_DOCUMENT.equals(paths.get(2))) {
                                //     return paths.get(3);
                                // }
                                // String id = DocumentsContract.getDocumentId(mOldFileUrl);
                                // logD("mOldAccessLauncher: %s, %s", mOldFileUrl.toString(),
                                // id);
                            }
                        }
                    }
                });
    }

    public static void launcher1(@NonNull Intent intent) {
        mOldAccessLauncher1.launch(intent, null);
    }

    public void initOldAccess2Permission(@NonNull ComponentActivity activity) {
        mOldAccessLauncher2 = activity.registerForActivityResult(new ActivityResultContracts
                .OpenDocumentTree(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                mOldFileUrl = result;
                if (mOldFileUrl != null) {
                    logD("mOldAccessLauncher2: " + mOldFileUrl.toString());
                }
            }
        });
    }

    public static void launcher2(@NonNull Uri uri) {
        mOldAccessLauncher2.launch(uri, null);
    }

    /**
     * {@link ActivityResultContracts.OpenDocumentTree}
     */
    // OpenDocumentTree
}
