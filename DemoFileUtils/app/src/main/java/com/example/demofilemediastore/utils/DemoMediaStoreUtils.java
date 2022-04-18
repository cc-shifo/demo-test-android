package com.example.demofilemediastore.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class DemoMediaStoreUtils {

    private DemoMediaStoreUtils() {
        // nothing
    }

    /**
     * save the {@link InputStream} in a path which consists of the parameters of {@code type}
     * {@code path} and {@code name}. E.g if the type is {@link MediaType#DOCUMENTS}, the path
     * is "hello", and the file name is "demo.txt", the full path of the {@link InputStream} that
     * will be persisted is "/sdcard/Documents/hello/demo".
     *
     * @param type     the parent path.
     * @param path     the subdirectory.
     * @param fileName the file name.
     * @return true if write successfully
     */
    public static boolean write(@NonNull ContentResolver contentResolver, @NonNull InputStream in,
                                @MediaType int type, @Nullable String path,
                                @NonNull String fileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, createRelativePath30(type, path));
            return writeIO30(contentResolver, values, in);
        } else {
            return writeIO(in, type, path, fileName);
        }
    }

    /**
     * Get the string value of path according to the inputted parameters.
     *
     * @param type to indicate an parent directory.
     * @param path the subdirectory and the one represented by {@code type}.
     * @return the relative path to the path of "/sdcard/"
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private static String createRelativePath30(@MediaType int type, String path) {
        String relativePath = path == null ? "" : "/" + path;
        switch (type) {
            case MediaType.IMAGE:
                relativePath = Environment.DIRECTORY_PICTURES + relativePath;
                break;
            case MediaType.VIDEO:
                relativePath = Environment.DIRECTORY_MOVIES + relativePath;
                break;
            case MediaType.RECORD:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                    relativePath = Environment.DIRECTORY_RECORDINGS + relativePath;
                } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    // DCIM
                    relativePath = Environment.DIRECTORY_DCIM + relativePath;
                } else {
                    relativePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                            + File.pathSeparator + "Sounds" + File.pathSeparator + relativePath;
                }
                break;
            case MediaType.DOWNLOAD:
                relativePath = Environment.DIRECTORY_DOWNLOADS + relativePath;
                break;
            case MediaType.DOCUMENTS:
                relativePath = Environment.DIRECTORY_DOCUMENTS + relativePath;
                break;
            default:
                throw new IllegalArgumentException("invalid type");
        }

        return relativePath;
    }

    /**
     * @param contentResolver the content resolver of current application
     * @param values          content values used by the content resolver of current application
     * @param inputStream     the data source which to be write.
     * @return true if write successfully, otherwise return false.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private static boolean writeIO30(@NonNull ContentResolver contentResolver,
                                     @NonNull ContentValues values,
                                     @NonNull InputStream inputStream) {
        boolean result = false;
        Uri uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try (BufferedInputStream bis = new BufferedInputStream(inputStream);
                 OutputStream outputStream = contentResolver.openOutputStream(uri)) {
                if (outputStream != null) {
                    BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                    byte[] byteBuffer = new byte[1024];
                    int bytes = bis.read(byteBuffer);
                    while (bytes >= 0) {
                        bos.write(byteBuffer, 0, bytes);
                        bos.flush();
                        bytes = bis.read(byteBuffer);
                    }
                }
            } catch (IOException ioException) {
                Timber.e(ioException);
                return false;
            }

            result = true;
        }

        return result;
    }

    /**
     * Create every directory if it isn't existed.
     *
     * @param type to indicate an parent directory.
     * @param path the subdirectory and the one represented by {@code type}.
     * @return the full directory if it is existed or created successfully.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static String createPath(@MediaType int type, String path) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        switch (type) {
            case MediaType.IMAGE:
                fullPath = fullPath + File.pathSeparator + Environment.DIRECTORY_PICTURES;
                break;
            case MediaType.VIDEO:
                fullPath = fullPath + File.pathSeparator + Environment.DIRECTORY_MOVIES;
                break;
            case MediaType.RECORD:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    // DCIM
                    fullPath = fullPath + File.pathSeparator + Environment.DIRECTORY_DCIM;
                } else {
                    fullPath = fullPath + File.pathSeparator + "Sounds";
                }
                break;
            case MediaType.DOWNLOAD:
                fullPath = fullPath + File.pathSeparator + Environment.DIRECTORY_DOWNLOADS;
                break;
            case MediaType.DOCUMENTS:
                fullPath = fullPath + File.pathSeparator + Environment.DIRECTORY_DOCUMENTS;
                break;
            default:
                throw new IllegalArgumentException("invalid type");
        }

        fullPath = fullPath + (path == null ? "" : File.pathSeparator + path);
        FileUtils.createOrExistsDir(fullPath);

        return fullPath;
    }

    /**
     * save the {@link InputStream} in a path which consists of the parameters of {@code type}
     * {@code path} and {@code name}. E.g if the type is {@link MediaType#DOCUMENTS}, the path
     * is "hello", and the file name is "demo.txt", the full path of the {@link InputStream} that
     * will be persisted is "/sdcard/Documents/hello/demo".
     *
     * @param type     the parent path.
     * @param path     the subdirectory under the path indicated by parameter of type.
     * @param fileName the file name.
     * @return true if write successfully
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean writeIO(@NonNull InputStream inputStream, @MediaType int type,
                                   @Nullable String path, @NonNull String fileName) {
        String directory = createPath(type, path);
        File file = new File(directory + File.pathSeparator + fileName);
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             OutputStream outputStream = new FileOutputStream(file)) {
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            byte[] byteBuffer = new byte[1024];
            int bytes = bis.read(byteBuffer);
            while (bytes >= 0) {
                bos.write(byteBuffer, 0, bytes);
                bos.flush();
                bytes = bis.read(byteBuffer);
            }
        } catch (IOException ioException) {
            Timber.e(ioException);
            return false;
        }

        return true;
    }

    public static List<Uri> loadImages(@NonNull ContentResolver contentResolver) {
        return loadMedias(contentResolver, MediaType.IMAGE);
    }

    public static List<Uri> loadVideos(@NonNull ContentResolver contentResolver) {
        return loadMedias(contentResolver, MediaType.VIDEO);
    }

    public static List<Uri> loadAudios(@NonNull ContentResolver contentResolver) {
        return loadMedias(contentResolver, MediaType.RECORD);
    }

    @SuppressLint("SwitchIntDef")
    private static List<Uri> loadMedias(@NonNull ContentResolver contentResolver,
                                        @MediaType int type) {
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        switch (type) {
            case MediaType.IMAGE:
                collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                break;
            case MediaType.VIDEO:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                } else {
                    collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
                break;
            case MediaType.RECORD:
                collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                break;
            default:
                break;
        }

        List<Uri> list = new ArrayList<>(0);
        Cursor cursor = contentResolver.query(collection, null,
                null, null, MediaStore.MediaColumns.DATE_ADDED + "DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        , id);
                list.add(uri);
            }
            cursor.close();
        }
        return list;
    }

    // open download directory
    // open document directory
}
