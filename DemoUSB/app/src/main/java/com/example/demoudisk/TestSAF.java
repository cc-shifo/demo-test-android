package com.example.demoudisk;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demoudisk.databinding.ActivitySafExampleBinding;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * use ACTION_CREATE_DOCUMENT / CreateDocument (SAF) or ACTION_OPEN_DOCUMENT_TREE + DocumentFile.
 * All saf APIs are provided by Android System FileExplorer.
 * <p>
 * Short answer
 * <p>
 * No — not if you have never previously been granted access. With SAF you cannot silently open an arbitrary user file
 * without user interaction. The only ways to access a SAF file "directly" (without showing the picker again) are:
 * 1.You already hold a valid persistable URI permission for that file (and you stored the Uri), or
 * 2.The file is in a location your app already has direct access to (app-specific storage, or accessible via
 * MediaStore/scoped-storage rules), or
 * Your app has special broad file access (MANAGE_EXTERNAL_STORAGE), which is restricted by Play policy.
 * Why
 * <p>
 * SAF is explicitly designed to require user consent to pick files. Persisted URI grants let you skip the picker in the
 * same install, but those grants are tied to the app instance and are cleared when the app is uninstalled. If you never
 * persisted a grant, or if the app was uninstalled/reinstalled, you must prompt the user again.
 */
public class TestSAF extends AppCompatActivity {

    private static final String TAG = "TestSAF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySafExampleBinding binding = ActivitySafExampleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // setContentView(R.layout.activity_saf_example);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_test), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.safCreate.setOnClickListener(v -> {
            // To create a new file
            createDocument.launch("my-file.txt");
        });
        binding.safOpen.setOnClickListener(v -> {
            // To pick an existing file
            openDocument.launch(new String[]{"text/plain"});
        });

    }

    private void testSafUriPath(Uri uri) {

        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                // 获取文件名（如：my_document.pdf）
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String displayName = cursor.getString(index);
                // 获取文件大小
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                String size;
                if (sizeIndex >= 0) {
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }

                Log.e(TAG, "testSafUriPath: displayName=" + displayName + ", size=" + size);

            }
        } catch (Exception exception) {
            Log.e(TAG, "testSafUriPath: ", exception);
        }

    }

    /**
     * /sdcard/Downloads或/storage/sdcard0/Download （sdcard0是sdcard的软连接）
     * uri=content://com.android.externalstorage.documents/document/primary%3ADownload%2Fmywwww-file.txt
     * <p>
     * /sdcard/Documents/
     * uri=content://com.android.externalstorage.documents/document/primary%3ADocuments%2Fmy-filaaaae.txt,
     * authority=com.android.externalstorage.documents, scheme=content,
     * path=/document/primary:Documents/my-filaaaae.txt
     * "%3A" means colon ":", %2F means file separator "/".
     *
     * /sdcard/Android/media/
     * uri=content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fmedia%2Fmy-file.txt,
     * authority=com.android.externalstorage.documents, scheme=content,
     * path=/document/primary:Android/media/my-file.txt
     * 无论选择的是Download, 还是Documents,还是其他目录，authority都是com.android.externalstorage.documents。
     * 不同的是path之中的路径，即primary后跟踪的路径。
     */
    private final ActivityResultLauncher<String>
            createDocument = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/plain"),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        writeToUri(uri, "Hello from SAF".getBytes(StandardCharsets.UTF_8));
                        Log.d(TAG, "onActivityResult: uri=" + uri + ", authority=" + uri.getAuthority()
                                + ", scheme=" + uri.getScheme() + ", path=" + uri.getPath());

                        String filePath = getPathFromSAFUri(TestSAF.this, uri);
                        Log.d(TAG, "onActivityResult: uri=filePath=" + filePath);

                        // String filePath = TestMedia.getFilePathFromUri(TestSAF.this, uri);
                        // Log.d(TAG, "onActivityResult: filePath=" + filePath);
                        //
                        // if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                        //     String docId = DocumentsContract.getDocumentId(uri);
                        //     String[] split = docId.split(":");
                        //     String type = split[0];
                        //     if ("primary".equalsIgnoreCase(type)) {
                        //         filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        //         split[1];
                        //     }
                        // }
                        // MediaScannerConnection.scanFile(TestSAF.this, new String[]{filePath}, null, null);
                    }
                }
            });


    /**
     * open System FileExplorer, wait user to pick up files. An Uri of picked-up file will be return.
     */
    private final ActivityResultLauncher<String[]> openDocument = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        readFromUri(uri);
                        testSafUriPath(uri);
                    }
                }
            });


    /**
     *
     * @param uri  provided by android FileExplorer.
     * @param data data to be write.
     */
    private void writeToUri(Uri uri, byte[] data) {
        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            if (out != null) {
                out.write(data);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //?.use { it.write(data) }
    }

    /**
     * Read file picked-up by user.
     *
     * @param uri the value is returned from android FileExplorer. This uri can only be accessed though SAF mode.
     */
    private void readFromUri(Uri uri) {
        byte[] data = new byte[30];
        try (InputStream ins = getContentResolver().openInputStream(uri)) {
            if (ins != null) {
                BufferedInputStream bf = new BufferedInputStream(ins);
                int read = bf.read(data);
                if (read > 0) {
                    Log.d(TAG, "readFromUri: " + new String(data, 0, read));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // do something with text
    }

    /**
     * get file path from SAF Uri
     *
     * @param context the context of the caller
     * @param uri     the SAF Uri to query
     * @return the file path if found, otherwise null
     */
    @Nullable
    public String getFileInfoFromSAFUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(
                uri, new String[]{
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_SIZE,
                        DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                        DocumentsContract.Document.COLUMN_MIME_TYPE
                }, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String name = cursor.getString(0);
                long size = cursor.getLong(1);
                long lastModified = cursor.getLong(2);
                String mimeType = cursor.getString(3);
                Log.d("SAFFile", "Name: " + name + ", Size: " + size + ", Type: " + mimeType + ", LastModified: "
                        + lastModified);
                // 尝试获取真实路径（仅适用于本地文件）
                // if (filePath != null) {
                //     Log.d("SAFFile", "Path: " + filePath);
                // }

            }
        }
        return null;
    }

    /**
     * 可以正常获取到DocumentProvider提供的Uri的绝对路径。
     * @param context
     * @param uri
     * @return
     */
    @Nullable
    public static String getPathFromSAFUri(Context context, Uri uri) {
        // if (DocumentsContract.isDocumentUri(context, uri)) {
        if (TestSAFUtil.isExternalStorageDocument(uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            String[] split = docId.split(":");
            String type = split[0];
            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + split[1];
            }
        }

        // Log.e(TAG, "getFilePathFromUri: isDocumentUri=" + DocumentsContract.isDocumentUri(context, uri));

        return null;
    }
}
