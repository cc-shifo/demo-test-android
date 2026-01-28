package com.example.demoudisk;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demoudisk.databinding.ActivityMediaExampleBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TestMedia extends AppCompatActivity {

    private static final String TAG = "TestMedia";
    private long mWriteCnt;
    private long mReadCnt;
    private long mOtherReadCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMediaExampleBinding binding = ActivityMediaExampleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // setContentView(R.layout.activity_saf_example);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_test), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.textVal.setMovementMethod(new ScrollingMovementMethod());
        binding.mediaClearTxt.setOnClickListener(v -> {
            binding.textVal.setText("");
        });


        mWriteCnt = 0;
        mReadCnt = 0;
        mOtherReadCnt = 0;
        final String fileName = "test.txt";
        binding.mediaWrite.setOnClickListener(v -> {
            // To create a new file and write data to it
            Uri uri = writeBytesToDownloads(this, fileName, "text/plain", ("TestMedia" + mWriteCnt).getBytes());
            if (uri != null) {
                Log.d(TAG, "mediaWrite: uri=" + uri + ", authority=" + uri.getAuthority()
                        + ", scheme=" + uri.getScheme() + ", path=" + uri.getPath());
            }
            binding.textVal.append("mediaWrite: + " + mWriteCnt + ", uri=" + uri + "\n");
            mWriteCnt++;
            // try to get file path from SAF Uri
            // String filePath = getFilePathFromUri(this, uri);
            String filePath = getPathFromMediaStoreUri(uri, getContentResolver());
            Log.d(TAG, "mediaWrite: filePath=" + filePath);
        });
        binding.mediaRead.setOnClickListener(v -> {
            // To read the created file
            byte[] bytes = new byte[4096];
            // just read once, without guarantee to read all bytes
            int read = readBytesFromDownloads(fileName, bytes, 0, bytes.length);
            Log.e(TAG, "mediaRead: readBytesFromDownloads, read file=" + fileName + ", size=" + read);
            if (read > 0) {
                Log.d(TAG, "mediaRead: " + new String(bytes, 0, read));
            }
            binding.textVal.append("mediaRead: + " + mReadCnt + ", " + new String(bytes, 0, read) + "\n");
            mReadCnt++;
        });

        binding.mediaReadOther.setOnClickListener(v -> {
            // To read other existing file
            byte[] bytes = new byte[4096];
            int read = readBytesFromDownloads("my-file.txt", bytes, 0, bytes.length);
            Log.e(TAG, "mediaRead: readBytesFromDownloads, read file=my-file.txt, size=" + read);
            if (read > 0) {
                Log.d(TAG, "mediaRead: " + new String(bytes, 0, read));
            }
            binding.textVal.append("mediaReadOther: + " + mOtherReadCnt + ", " + new String(bytes, 0, read) + "\n");
            mOtherReadCnt++;
        });


        binding.mediaListFile.setOnClickListener(v -> {
            // To read other existing file
            List<String> list = listDownloads(this, 50);
            binding.textVal.append("mediaListFile: size=" + list.size() + ", " + list + "\n");
        });

    }

    /**
     * media includes Download, Image, video, video
     * MediaStore.VOLUME_EXTERNAL_PRIMARY:
     * 1. Download
     * Environment.DIRECTORY_DOWNLOADS -> content://media/external_primary/downloads
     * mediaWrite: uri=content://media/external_primary/downloads/1000000156, authority=media, scheme=content,
     * path=/external_primary/downloads/1000000156
     * mediaDownloadExt: uri=content://media/external/downloads, authority=media, scheme=content,
     * path=/external/downloads
     * mediaDownloadInt: uri=content://media/internal/downloads, authority=media, scheme=content,
     * path=/internal/downloads
     * 2. Image
     * mediaImageMediaExt: uri=content://media/external/images/media, authority=media, scheme=content,
     * path=/external/images/media
     * mediaVideoMediaExt: uri=content://media/external/video/media, authority=media, scheme=content,
     * path=/external/video/media
     * 3. video
     * mediaVideoMediaInt: uri=content://media/internal/video/media, authority=media, scheme=content,
     * path=/internal/video/media
     * mediaAudioMediaExt: uri=content://media/external/audio/media, authority=media, scheme=content,
     * path=/external/audio/media
     * 4.audio
     * mediaAudioMediaInt: uri=content://media/internal/audio/media, authority=media, scheme=content,
     * path=/internal/audio/media
     * mediaWrite: uri=content://media/external_primary/downloads/1000000157, authority=media, scheme=content,
     * path=/external_primary/downloads/1000000157
     */
    @Nullable
    private Uri writeBytesToDownloads(Context context, String displayName, String mimeType, byte[] data) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri mediaDownloadExt = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            Uri mediaDownloadInt = MediaStore.Downloads.INTERNAL_CONTENT_URI;
            Log.d(TAG, "mediaDownloadExt: uri=" + mediaDownloadExt +
                    ", authority=" + mediaDownloadExt.getAuthority()
                    + ", scheme=" + mediaDownloadExt.getScheme()
                    + ", path=" + mediaDownloadExt.getPath());
            Log.d(TAG, "mediaDownloadInt: uri=" + mediaDownloadInt +
                    ", authority=" + mediaDownloadInt.getAuthority()
                    + ", scheme=" + mediaDownloadInt.getScheme()
                    + ", path=" + mediaDownloadInt.getPath());
            // 照片媒体
            Uri mediaImageMediaExt = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri mediaImageMediaInt = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            Log.d(TAG, "mediaImageMediaExt: uri=" + mediaImageMediaExt +
                    ", authority=" + mediaImageMediaExt.getAuthority()
                    + ", scheme=" + mediaImageMediaExt.getScheme()
                    + ", path=" + mediaImageMediaExt.getPath());
            Log.d(TAG, "mediaImageMediaInt: uri=" + mediaImageMediaInt +
                    ", authority=" + mediaImageMediaInt.getAuthority()
                    + ", scheme=" + mediaImageMediaInt.getScheme()
                    + ", path=" + mediaImageMediaInt.getPath());

            // 视频媒体
            Uri mediaVideoMediaExt = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            Uri mediaVideoMediaInt = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
            Log.d(TAG, "mediaVideoMediaExt: uri=" + mediaVideoMediaExt +
                    ", authority=" + mediaVideoMediaExt.getAuthority()
                    + ", scheme=" + mediaVideoMediaExt.getScheme()
                    + ", path=" + mediaVideoMediaExt.getPath());
            Log.d(TAG, "mediaVideoMediaInt: uri=" + mediaVideoMediaInt +
                    ", authority=" + mediaVideoMediaInt.getAuthority()
                    + ", scheme=" + mediaVideoMediaInt.getScheme()
                    + ", path=" + mediaVideoMediaInt.getPath());

            // 音频媒体
            Uri mediaAudioMediaExt = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Uri mediaAudioMediaInt = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
            Log.d(TAG, "mediaAudioMediaExt: uri=" + mediaAudioMediaExt +
                    ", authority=" + mediaAudioMediaExt.getAuthority()
                    + ", scheme=" + mediaAudioMediaExt.getScheme()
                    + ", path=" + mediaAudioMediaExt.getPath());
            Log.d(TAG, "mediaAudioMediaInt: uri=" + mediaAudioMediaInt +
                    ", authority=" + mediaAudioMediaInt.getAuthority()
                    + ", scheme=" + mediaAudioMediaInt.getScheme()
                    + ", path=" + mediaAudioMediaInt.getPath());

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
            // RELATIVE_PATH tells MediaStore to put it in Downloads/
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            // content://media/external_primary/downloads
            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            // Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL);
            // Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_INTERNAL);
            ContentResolver resolver = getContentResolver();
            uri = resolver.insert(collection, values);
            if (uri != null) {
                try (OutputStream os = resolver.openOutputStream(uri)) {
                    if (os != null) {
                        os.write(data);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "writeBytes: ", e);
                    resolver.delete(uri, null, null);
                }
            }

            return uri;
        } else {
            // Pre-Q: write directly to Downloads directory (requires WRITE_EXTERNAL_STORAGE)
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            File outFile = new File(downloadsDir.getAbsolutePath() + File.separator + displayName);
            try (FileOutputStream os = new FileOutputStream(outFile)) {
                os.write(data);
            } catch (Exception e) {
                Log.e(TAG, "writeBytes: ", e);
                return null;
            }

            // 通知 MediaScanner
            MediaScannerConnection.scanFile(context,
                    new String[]{outFile.getAbsolutePath()},
                    null,
                    null);
            return Uri.fromFile(outFile);
        }
    }

    /**
     * Read bytes for a file named displayName from Downloads.
     * Returns the length of bytes read. If &lt; 0, then error.
     *
     * @apiNote just read once, without guarantee to read all bytes
     */
    private int readBytesFromDownloads(String displayName, byte[] buffer, int offset, int size) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String[] projection = new String[]{MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME};
            String selection = MediaStore.MediaColumns.DISPLAY_NAME + " = ?";
            String[] selectionArgs = new String[]{displayName};
            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            ContentResolver resolver = getContentResolver();
            try (Cursor cursor = resolver.query(collection, projection, selection, selectionArgs, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                    Uri uri = Uri.withAppendedPath(collection, Long.toString(id));
                    try (InputStream in = resolver.openInputStream(uri)) {
                        if (in != null) {
                            return in.read(buffer, offset, size);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "readBytesFromDownloads: ", e);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "readBytesFromDownloads: ", e);
            }
        } else {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, displayName);
            if (file.exists()) {
                try (FileInputStream in = new FileInputStream(file)) {
                    // just read once, without guarantee to read all bytes
                    return in.read(buffer, offset, size);
                } catch (IOException e) {
                    Log.e(TAG, "readBytesFromDownloads: ", e);
                }
            } else {
                Log.i(TAG, "readBytesFromDownloads: file not exists");
            }
        }

        return -1;
    }

    /**
     * List display names of files in Downloads.
     */
    private List<String> listDownloads(Context context, int limit) {
        List<String> result = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String[] projection = new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns._ID};
            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            ContentResolver resolver = context.getContentResolver();
            try (Cursor cursor = resolver.query(collection, projection, null, null, null)) {
                if (cursor != null) {
                    while (cursor.moveToNext() && result.size() < limit) {
                        String name = cursor.getString(
                                cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                        result.add(name);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "listDownloads: ", e);
            }
        } else {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File[] files = downloadsDir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length && result.size() < limit; i++) {
                    File file = files[i];
                    if (file.isFile()) {
                        result.add(file.getName());
                    }
                }
            }
        }

        return result;
    }

    /**
     * Find a content Uri for a given displayName (API 29+) or a file Uri for older devices.
     * Returns null if not found.
     */
    @Nullable
    public static Uri getUriForDownload(Context context, String displayName) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String[] projection = new String[]{MediaStore.MediaColumns._ID};
            String selection = MediaStore.MediaColumns.DISPLAY_NAME + " = ?";
            String[] selectionArgs = new String[]{displayName};
            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            ContentResolver resolver = context.getContentResolver();
            try (Cursor cursor = resolver.query(collection, projection, selection, selectionArgs, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                    uri = Uri.withAppendedPath(collection, Long.toString(id));
                }
            } catch (Exception e) {
                Log.e(TAG, "getUriForDownload: ", e);
            }
        } else {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    displayName);
            uri = file.exists() ? Uri.fromFile(file) : null;
        }

        return uri;
    }

    /**
     * 可以正常的从MediaStore Uri中获取文件路径
     * Gets the corresponding path to a file from the given content:// URI
     *
     * @param selectedVideoUri The content:// URI to find the file path from
     * @param contentResolver  The content resolver to use to perform the query.
     * @return the file path as a string
     */
    public static String getPathFromMediaStoreUri(Uri selectedVideoUri, ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);

        // 也可用下面的方法拿到cursor
        // Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }

    /**
     * DocumentProvider.
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isExternalPrimoryDownload(Uri uri) {
        // content://media/external_primary/downloads
        return "com.android.external_primary.documents".equals(uri.getAuthority()) &&
                "downloads".equals(DocumentsContract.getDocumentId(uri));
    }

    /**
     * DownloadProvider
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * MediaProvider
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * from {@link DocumentsContract}
     *
     * @param uri uri to check
     * @return true is content, otherwise false.
     */
    public static boolean isContentUri(@Nullable Uri uri) {
        return uri != null && ContentResolver.SCHEME_CONTENT.equals(uri.getScheme());
    }
}
