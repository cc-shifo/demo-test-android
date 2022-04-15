package com.example.demofilemediastore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import timber.log.Timber;

public class TestStorageAccessFramework {
    // Request code for creating a PDF document.
    public static final int CREATE_FILE = 1;

    public void createFile(@NonNull Activity context, Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        context.startActivityForResult(intent, CREATE_FILE);
    }

    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 2;

    /**
     * android 11不能用ACTION_OPEN_DOCUMENT的intent去访问以下两个目录下的文件
     * The Android/data/ directory and all subdirectories.
     * The Android/obb/ directory and all subdirectories.
     *
     * @param pickerInitialUri 要打开的文件的uri
     */
    private void openFile(@NonNull Activity activity, Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        activity.startActivityForResult(intent, PICK_PDF_FILE);
    }

    /**
     * ACTION_OPEN_DOCUMENT_TREE不能访问以下目录：
     * <p>
     * 1.内部存储卷的根目录。
     * 2.设备制造商认为可靠的各个 SD 卡卷的根目录(即/sdcard/目录)，无论该卡是模拟卡还是可移除的卡。可靠的卷是指应用在大多数情况下可以成功访问的卷。
     * 3.Download 目录。
     * 4.此外，在 Android 11（API 级别 30）及更高版本中，您不能使用 ACTION_OPEN_DOCUMENT_TREE intent
     * 操作来请求用户从以下目录中选择单独的文件：
     * Android/data/ 目录及其所有子目录。
     * Android/obb/ 目录及其所有子目录。
     */
    // Request code for selecting a PDF document.
    private static final int PICK_TREE = 3;

    public void openDirectory(@NonNull Activity activity, Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        activity.startActivityForResult(intent, PICK_TREE);
    }

    @SuppressLint("WrongConstant")
    public void grantPersistentUriPermissions(@NonNull Activity activity,
                                              @NonNull Intent intent, Uri uri) {
        final int takeFlags = intent.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // Check for the freshest data.
        activity.getContentResolver().takePersistableUriPermission(uri, takeFlags);
    }

    public void dumpImageMetaData(Uri uri, @NonNull Activity activity) {

        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
        Cursor cursor = activity.getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                @SuppressLint("Range") String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Timber.d("Display Name: %s", displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null. But because an
                // int can't be null, the behavior is implementation-specific,
                // and unpredictable. So as
                // a rule, check if it's null before assigning to an int. This will
                // happen often: The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Timber.d("Size: %s", size);
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * open a Bitmap file given its URI
     * <p>
     * You should complete this operation on a background thread, not the UI thread.
     */
    public Bitmap getBitmapFromUri(@NonNull Activity activity, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                activity.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    /**
     * 直接获取Document.COLUMN_FLAGS与FLAG_SUPPORTS_WRITE进行比较。判断是否可写。
     * The DocumentFile class's canWrite() method doesn't necessarily indicate that your app can
     * edit a document. That's because this method returns true if Document.COLUMN_FLAGS contains
     * either FLAG_SUPPORTS_DELETE or FLAG_SUPPORTS_WRITE. To determine whether your app can edit
     * a given document, query the value of FLAG_SUPPORTS_WRITE directly
     */
    private String readTextFromUri(@NonNull Activity activity, Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     activity.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 写文件
     */
    private void alterDocument(@NonNull Activity activity, Uri uri) {
        try {
            ParcelFileDescriptor pfd = activity.getContentResolver().
                    openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write(("Overwritten at " + System.currentTimeMillis() +
                    "\n").getBytes());
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对应的URI的 Document.COLUMN_FLAGS支持删除的话，通过以下操作删除文件。
     * If you have the URI for a document and the document's Document.COLUMN_FLAGS contains
     * SUPPORTS_DELETE, you can delete the document
     */
    public void delete(@NonNull Activity activity, Uri uri) {
        try {
            DocumentsContract.deleteDocument(activity.getContentResolver(), uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * MediaStore的getMediaUri()获取到的URI根Document Provider获取到的URI是等效的。
     * MediaStore的getMediaUri()访问共享资源更容易;
     * Document Provider获取到的URI需要授予权限。
     * The getMediaUri() method provides a media store URI that is equivalent to the given
     * documents provider URI. The 2 URIs refer to the same underlying item. Using the media
     * store URI, you can more easily access media files from shared storage.
     */

    /**
     * 在用户做出选择后，请使用结果数据中的 URI 来确定文件是否为虚拟文件
     */
    public boolean isVirtualFile(@NonNull Activity activity, Uri uri) {
        if (!DocumentsContract.isDocumentUri(activity, uri)) {
            return false;
        }

        Cursor cursor = activity.getContentResolver().query(
                uri,
                new String[]{DocumentsContract.Document.COLUMN_FLAGS},
                null, null, null);

        int flags = 0;
        if (cursor.moveToFirst()) {
            flags = cursor.getInt(0);
        }
        cursor.close();

        return (flags & DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT) != 0;
    }

    /**
     * uri类型转换mimeTypeFilter类型
     */
    private InputStream getInputStreamForVirtualFile(@NonNull Activity activity, Uri uri,
                                                     String mimeTypeFilter)
            throws IOException {

        ContentResolver resolver = activity.getContentResolver();

        String[] openableMimeTypes = resolver.getStreamTypes(uri, mimeTypeFilter);

        if (openableMimeTypes == null ||
                openableMimeTypes.length < 1) {
            throw new FileNotFoundException();
        }

        return resolver
                .openTypedAssetFileDescriptor(uri, openableMimeTypes[0], null)
                .createInputStream();
    }
}
