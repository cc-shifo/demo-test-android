package com.example.demoudisk;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 通用 Documents 文件创建工具类
 * 兼容 Android 7 ~ Android 14
 */
public class DocumentFileHelper {

    public static final int REQUEST_CODE_CREATE_DOCUMENT = 2001;

    /**
     * 创建 Documents 下的文件
     * @param activity 当前 Activity
     * @param fileName 文件名（如 example.txt）
     * @param mimeType MIME 类型（如 text/plain）
     */
    public static void createDocumentFile(@NonNull Activity activity,
            @NonNull String fileName,
            @NonNull String mimeType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ 必须用 SAF
            launchSAFCreateDocument(activity, fileName, mimeType);
        } else {
            // Android 9 及以下可直接写入
            createFileLegacy(activity, fileName, mimeType);
        }
    }

    /**
     * Android 10+ 使用 SAF 创建文件
     */
    private static void launchSAFCreateDocument(Activity activity, String fileName, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        activity.startActivityForResult(intent, REQUEST_CODE_CREATE_DOCUMENT);
    }

    /**
     * Android 9 及以下直接写入 /Documents
     */
    private static void createFileLegacy(Activity activity, String fileName, String mimeType) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!documentsDir.exists() && !documentsDir.mkdirs()) {
            Toast.makeText(activity, "无法创建 Documents 目录", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(documentsDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("Hello from Legacy!".getBytes());
            fos.flush();
            Toast.makeText(activity, "文件已创建: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "写入失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 在 onActivityResult 中调用，处理 SAF 返回的 URI
     */
    public static void handleActivityResult(@NonNull Activity activity,
            int requestCode,
            int resultCode,
            @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_CREATE_DOCUMENT && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    writeContentToUri(activity.getContentResolver(), uri, "Hello from SAF!");
                    Toast.makeText(activity, "文件已创建: " + uri, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * 向指定 URI 写入内容
     */
    private static void writeContentToUri(ContentResolver resolver, Uri uri, String content) {
        try (OutputStream os = resolver.openOutputStream(uri)) {
            if (os != null) {
                os.write(content.getBytes());
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}