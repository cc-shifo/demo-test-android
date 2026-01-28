package com.example.demoudisk;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopySafIntoMediaUtil {
    private static final String TAG = "CopySafIntoMediaUtil";

    /**
     * Get display name of a document Uri provided by android DocumentProvider.
     *
     * @param context Application context.
     * @param uri     Document Uri provided by android DocumentProvider.
     * @return
     */
    public static String getDisplayName(Context context, Uri uri) {
        String name = null;
        try (Cursor c = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null,
                null)) {
            if (c != null) {
                if (c.moveToFirst()) {
                    name = c.getString(c.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getDisplayName: ", e);
        }

        return name;
    }

    /**
     * Copy SAF Uri content into an indexed MediaStore Downloads entry (API 29+).
     * Returns the new content Uri (MediaStore) or null on error.
     *
     * @param context Application context.
     * @param srcUri  SAF Uri(provided by android DocumentProvider) to copy.
     * @return New content Uri (MediaStore) or null on error.
     */
    public static Uri copySafUriToMediaStoreDownloads(Context context, Uri srcUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String displayName = getDisplayName(context, srcUri) != null ? getDisplayName(context, srcUri)
                    : "file_from_saf_${System.currentTimeMillis()}";
            String mime = context.getContentResolver().getType(srcUri) != null ? context.getContentResolver()
                    .getType(srcUri) : "application/octet-stream";
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, mime);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            ContentResolver resolver = context.getContentResolver();
            Uri dstUri = resolver.insert(collection, values);
            if (dstUri != null) {
                try (InputStream inputStream = resolver.openInputStream(srcUri);
                     OutputStream outputStream = resolver.openOutputStream(dstUri)) {
                    if (inputStream == null || outputStream == null) {
                        Log.e(TAG, "copySafUriToMediaStoreDownloads: inputStream or outputStream is null");
                        return null;
                    }
                    FileUtils.copy(inputStream, outputStream);
                } catch (IOException e) {
                    Log.e(TAG, "copySafUriToMediaStoreDownloads: ", e);
                }
            }
            return dstUri;
        } else {
            // Pre-Q: fall back to file copy + scan
            return copySafUriToDownloadsAndScan(context, srcUri);
        }
    }

    /**
     * Copy SAF Uri content to a physical file under /sdcard/Download and call MediaScannerConnection.scanFile.
     * Returns the File path string or null on error.
     * <p>
     * NOTE: On API < 29 you need WRITE_EXTERNAL_STORAGE and possibly READ_EXTERNAL_STORAGE permission.
     * On API >= 29 writing directly to Environment.getExternalStoragePublicDirectory(...) will usually fail due to
     * scoped storage.
     */
    public static Uri copySafUriToDownloadsAndScan(Context context, Uri srcUri) {
        try {
            String displayName = getDisplayName(context, srcUri) != null ? getDisplayName(context, srcUri)
                    : "file_from_saf_${System.currentTimeMillis()}";
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            File outFile = new File(downloadsDir, displayName);
            try (InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
                 FileOutputStream outputStream = new FileOutputStream(outFile)) {
                if (inputStream == null) {
                    Log.e(TAG, "copySafUriToDownloadsAndScan: inputStream or outputStream is null");
                    return null;
                }
                // FileUtils.copy(inputStream, outputStream);
            } catch (IOException e) {
                Log.e(TAG, "copySafUriToDownloadsAndScan: ", e);
            }

            // // Ask media scanner to index the physical file we just wrote
            // // callback: indexed. You can log or use 'uri' returned by scanner.
            // MediaScannerConnection.scanFile(context, new String[]{outFile.getAbsolutePath()}, null,
            //         new MediaScannerConnection.MediaScannerConnectionClient() {
            //             @Override
            //             public void onMediaScannerConnected() {
            //
            //             }
            //
            //             @Override
            //             public void onScanCompleted(String path, Uri uri) {
            //                 Log.d(TAG, "onScanCompleted: path =" + path + ", uri = " + uri);
            //             }
            //         });

            return Uri.fromFile(outFile);
        } catch (Exception e) {
            Log.e(TAG, "copySafUriToDownloadsAndScan: ", e);
        }

        return null;
    }
}
