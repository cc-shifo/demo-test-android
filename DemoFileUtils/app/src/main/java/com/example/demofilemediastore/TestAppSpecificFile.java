package com.example.demofilemediastore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import timber.log.Timber;

/**
 * 应用专属文件：仅仅供应用本身访问的文件。
 * 从内部存储空间访问，可以使用 getFilesDir() 或 getCacheDir() 方法
 * <p>
 * 从外部存储空间访问，可以使用 getExternalFilesDir() 或 getExternalCacheDir() 方法
 * 从4.3开始应用访问应用自身创建的这个目录（/data/data/包名）不需要申请读写权限，访问其他app创建的属于
 * 它们的目录就需要申请WRITE_EXTERNAL_STORAGE和/或READ_EXTERNAL_STORAGE权限。
 */
public class TestAppSpecific {
    public void createExternalStoragePrivateFile(@NonNull Context context) {
        // 为null时，Create a path where we will place our private file on external
        // storage(/data/data/包名).
        File file = new File(context.getExternalFilesDir(null), "DemoFile.jpg");

        try {
            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            @SuppressLint("ResourceType") InputStream is =
                    context.getResources().openRawResource(R.raw.tmp_test);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Timber.e("ExternalStorage Error writing %s", e);
        }
    }

    public void deleteExternalStoragePrivateFile(@NonNull Context context) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(context.getExternalFilesDir(null), "DemoFile.jpg");
        file.delete();
    }

    public boolean hasExternalStoragePrivateFile(@NonNull Context context) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(context.getExternalFilesDir(null), "DemoFile.jpg");
        return file.exists();
    }

    public void createExternalStoragePrivatePicture(@NonNull Context context) {
        // 传入参数不为null时，在/data/data/包名/下创建子目录
        // Create a path where we will place our picture in our own private
        // pictures directory.  Note that we don't really need to place a
        // picture in DIRECTORY_PICTURES, since the media scanner will see
        // all media in these directories; this may be useful with other
        // media types such as DIRECTORY_MUSIC however to help it classify
        // your media for display to the user.
        File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DemoPicture.jpg");

        try {
            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            @SuppressLint("ResourceType") InputStream is =
                    context.getResources().openRawResource(R.raw.tmp_test);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context,
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Timber.i("ExternalStorage Scanned %s: " ,path);
                            Timber.i("ExternalStorage -> uri=%s", uri.toString());
                        }
                    });
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Timber.e(e);
        }
    }

    public void deleteExternalStoragePrivatePicture(@NonNull Context context) {
        // Create a path where we will place our picture in the user's
        // public pictures directory and delete the file.  If external
        // storage is not currently mounted this will fail.
        File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (path != null) {
            File file = new File(path, "DemoPicture.jpg");
            file.delete();
        }
    }

    public boolean hasExternalStoragePrivatePicture(@NonNull Context context) {
        // Create a path where we will place our picture in the user's
        // public pictures directory and check if the file exists.  If
        // external storage is not currently mounted this will think the
        // picture doesn't exist.
        File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (path != null) {
            File file = new File(path, "DemoPicture.jpg");
            return file.exists();
        }
        return false;
    }
}
