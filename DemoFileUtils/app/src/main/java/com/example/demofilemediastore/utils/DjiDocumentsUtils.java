package com.zhd.hiair.djisdkmanager;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.documentfile.provider.DocumentFile;

import com.dji.wpmzsdk.common.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description :
 * 外置 SD 卡工具类，from https://busy.im/post/android-sdcard-write/
 * @author : devin.xu
 * @filename : DocumentsUtils
 * @time : 2020-02-29
 * <p>
 * Copyright (c) 2016, DJI All Rights Reserved.
 **/
public class DjiDocumentsUtils {

    private static final String TAG = com.dji.wpmzsdk.common.utils.DocumentsUtils.class.getSimpleName();

    public static final int OPEN_DOCUMENT_TREE_CODE_IMPORT = 8000;
    public static final int OPEN_DOCUMENT_TREE_CODE_EXPORT = 8001;

    private DjiDocumentsUtils() {

    }

    /**
     * Get a list of external SD card paths. (Kitkat or higher.)
     *
     * @return A list of external SD card paths.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String[] getExtSdCardPaths(Context context) {
        List<String> extSdCardPaths = new ArrayList<>();
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null && !file.equals(context.getExternalFilesDir("external"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w(TAG, "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    extSdCardPaths.add(path);
                }
            }
        }
        if (extSdCardPaths.isEmpty()) extSdCardPaths.add("/storage/sdcard1");
        return extSdCardPaths.toArray(new String[0]);
    }

    /**
     * Determine the main folder of the external SD card containing the given file.
     *
     * @param file the file.
     * @return The main folder of the external SD card containing this file, if the file is on an SD
     * card. Otherwise,
     * null is returned.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getExtSdCardFolder(final File file, Context context) {
        String[] extSdPaths = getExtSdCardPaths(context);
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Determine if a file is on external sd card. (Kitkat or higher.)
     *
     * @param file The file.
     * @return true if on external sd card.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isOnExtSdCard(final File file, Context c) {
        return getExtSdCardFolder(file, c) != null;
    }

    /**
     * Get a DocumentFile corresponding to the given file (for writing on ExtSdCard on Android 5).
     * If the file is not
     * existing, it is created.
     *
     * @param file        The file.
     * @param isDirectory flag indicating if the file should be a directory.
     * @return The DocumentFile
     */
    public static DocumentFile getDocumentFile(final File file, final boolean isDirectory,
            Context context) {

        String baseFolder = getExtSdCardFolder(file, context);
        boolean originalDirectory = false;
        if (baseFolder == null) {
            return null;
        }

        String relativePath = "";
        try {
            String fullPath = file.getCanonicalPath();
            if (!baseFolder.equals(fullPath)) {
                relativePath = fullPath.substring(baseFolder.length() + 1);
            } else {
                originalDirectory = true;
            }
        } catch (IOException e) {
            return null;
        } catch (Exception f) {
            originalDirectory = true;
            //continue
        }

        Pair<Uri, String> resolvedRelativePath = resolvePermissionPath(context, baseFolder, relativePath);
        if (resolvedRelativePath == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(context, resolvedRelativePath.first);
        if (originalDirectory) return document;
        return getDocumentFile(resolvedRelativePath.second, isDirectory, document);
    }

    private static Pair<Uri, String> resolvePermissionPath(Context context, String baseFolder, String relativePath) {
        List<UriPermission> persistedUriPermissions = context.getContentResolver().getPersistedUriPermissions();


        for (UriPermission item : persistedUriPermissions) {
            String permissionPath = item.getUri().getLastPathSegment();
            if (permissionPath != null) {
                String[] permissionRelativePaths = permissionPath.split(":");
                String permissionRelativePath = "";
                if (permissionRelativePaths.length > 1) {
                    permissionRelativePath = permissionRelativePaths[1];
                }

                if (baseFolder.endsWith(permissionRelativePaths[0]) && relativePath.startsWith(permissionRelativePath)) {
                    Uri treeUri = item.getUri();
                    String resolvedRelativePath = relativePath.substring(permissionRelativePath.length());
                    return new Pair<>(treeUri, resolvedRelativePath);
                }
            }
        }

        return null;
    }

    private static DocumentFile getDocumentFile(String relativePath, boolean isDirectory, DocumentFile document) {
        if (null == document) {
            return null;
        }
        String[] parts = relativePath.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("")) {
                continue;
            }
            if (null == document) {
                return null;
            }
            DocumentFile nextDocument = document.findFile(parts[i]);
            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    nextDocument = document.createDirectory(parts[i]);
                } else {
                    nextDocument = document.createFile("", parts[i]);
                }
            }
            document = nextDocument;
        }

        return document;
    }

    public static boolean mkdirs( File dir) {
        return  FileUtils.mkdirs(dir);
    }

    public static boolean delete(File file) {
        return FileUtils.delFile(file);
    }



    public static InputStream getInputStream(Context context, File destFile) {
        InputStream in = null;
        try {
            if ( isOnExtSdCard(destFile, context)) {
                DocumentFile file = com.dji.wpmzsdk.common.utils.DocumentsUtils.getDocumentFile(destFile, false, context);
                if (file != null && file.canWrite()) {
                    in = context.getContentResolver().openInputStream(file.getUri());
                } else {
                    Log.e(TAG, "getInputStream file or file.cannot Write");
                }
            } else {
                String canonicalDestinationPath = destFile.getCanonicalPath();
                if (!canonicalDestinationPath.contains("s")) {
                    throw new IOException("Entry is outside of the target directory");
                }
                in = new FileInputStream(destFile);

            }
        } catch (IOException e) {
            Log.e(TAG , e.getMessage() );
        }

        return in;
    }

    public static OutputStream getOutputStream(Context context, File destFile) {
        OutputStream out = null;
        try {
            if ( isOnExtSdCard(destFile, context)) {
                DocumentFile file = com.dji.wpmzsdk.common.utils.DocumentsUtils.getDocumentFile(destFile, false, context);
                if (file != null && file.canWrite()) {
                    out = context.getContentResolver().openOutputStream(file.getUri());
                }
            } else {
                String canonicalDestinationPath = destFile.getCanonicalPath();
                if (!canonicalDestinationPath.contains("s")) {
                    throw new IOException("Entry is outside of the target directory");
                }
                out = new FileOutputStream(destFile);

            }
        } catch (IOException e) {
            Log.e(TAG , e.getMessage() );
        }
        return out;
    }

    /**
     * 检查文件是否可写
     * @param context
     * @param rootPath
     * @return
     */
    public static boolean checkWritableRootPath(Context context, String rootPath) {
        File root = new File(rootPath);
        if (!root.canWrite()) {
            if (com.dji.wpmzsdk.common.utils.DocumentsUtils.isOnExtSdCard(root, context)) {
                DocumentFile documentFile = com.dji.wpmzsdk.common.utils.DocumentsUtils.getDocumentFile(root, true, context);
                return documentFile != null && documentFile.canWrite();
            }
            return false;
        } else {
            return true;
        }
    }

}
