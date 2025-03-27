package com.zhd.hiair.djisdkmanager;

/*
 * Copyright (c) 2014, DJI All Rights Reserved.
 */

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import androidx.annotation.Nullable;


import com.dji.wpmzsdk.BuildConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;



/**
 * FileUtils
 * Created by luca on 2016/11/29.
 */

public class DjiFileUtils {
    /**
     * TAG for log messages.
     */
    static final String TAG = "FileUtils";
    public static final String LOG_PATH = "/sdcard/DJI/DJIPilot/Logs/";

    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";

    public static final String FILE_BASE = "file://";
    public static final String CONTENT_BASE = "content:";


    public static final String HIDDEN_PREFIX = ".";
    public static final char DOT = '.';
    public static final int MXA_REMAINING_CAPACITY = 50*1024*1024;

    /**
     * DJI项目数据存放的根目录
     */
    private static String dirName;

    private DjiFileUtils() {
    }
    public static String toReadableSize(long size) {
        if (size <= 0) {
            return "0B";
        }
        if (size < 1024) {
            return size + "B";
        }
        String[] sizeUnits = new String[] { "B", "KiB", "MiB", "GiB", "TiB", "PiB" };
        int unitIndex = 0;
        long newSize = size;
        for (; unitIndex < sizeUnits.length - 1; unitIndex++) {
            size = size / 1024;
            if (size < 1000) {
                unitIndex++;
                break;
            } else {
                newSize = size;
            }
        }
        float result = newSize / 1024f;
        DecimalFormat df = new DecimalFormat("####.##", new DecimalFormatSymbols(Locale.US));
        return df.format(result) + sizeUnits[unitIndex];
    }

    /**
     * Description : 判断文件是否存在
     *
     * @param path
     *     文件路径
     *
     * @return true/false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:38:04
     */
    public static boolean exist(final String path) {
        boolean ret = false;
        if (null != path) {
            final File file = new File(path);
            ret = file.exists();
        }
        return ret;
    }

    /**
     * Description : 判断文件是否存在并且是文件类型
     *
     * @param path
     *     文件路径
     *
     * @return true/false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:38:21
     */
    public static boolean isFile(final String path) {
        boolean ret = false;
        if (null != path) {
            final File file = new File(path);
            ret = (file.exists() && file.isFile());
        }
        return ret;
    }

    /**
     * Description : 判断文件是否存在并且是文件类型
     *
     * @param file
     *     文件
     *
     * @return true/false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:38:58
     */
    public static boolean isFile(final File file) {
        boolean ret = false;
        if (file != null && file.exists() && file.isFile()) {
            ret = true;
        }
        return ret;
    }

    /**
     * Description : 判断文件是否存在并且是目录类型
     *
     * @param path
     *     文件路径
     *
     * @return true/false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:39:16
     */
    public static boolean isDir(final String path) {
        boolean ret = false;
        if (null != path) {
            final File file = new File(path);
            ret = (file.exists() && file.isDirectory());
        }
        return ret;
    }

    /**
     * Description : 判断文件是否存在并且是目录类型
     *
     * @return true/false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:39:47
     */
    public static boolean isDir(final File file) {
        boolean ret = false;
        if (null != file && file.exists() && file.isDirectory()) {
            ret = true;
        }
        return ret;
    }

    /**
     * Description : 获取指定目录的子文件个数
     *
     * @author : gashion.fang
     * @date : 2014-12-18 下午7:28:22
     */
    public static int getSubFileCount(final String path, final FilenameFilter filter) {
        int count = 0;
        if (null != path) {
            final File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                File[] subs = file.listFiles(filter);
                if (subs != null) {
                    count = subs.length;
                }
            }
        }
        return count;
    }

    /**
     * Description : 创建文件，如果父目录不存在，则会自动创建
     *
     * @param path
     *     文件路径
     *
     * @return true/false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:40:54
     */
    public static boolean createFile(final String path) {
        boolean ret = false;
        if (null != path) {
            final File file = new File(path);
            ret = createFile(file);
        }
        return ret;
    }

    /**
     * Description : 创建文件，如果父目录不存在，则会自动创建
     *
     * @param file
     *     文件
     *
     * @return true/false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:41:37
     */
    public static boolean createFile(final File file) {
        boolean ret = false;
        if (null != file) {
            if (!file.exists() || !file.isFile()) {
                final File parentFile = file.getParentFile();
                if (mkdirs(parentFile)) {
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        ret = (file.exists() && file.isFile());
                    }
                }
            } else {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Description : 删除文件
     *
     * @param path
     *     文件路径
     *
     * @return true/false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:41:59
     */
    public static boolean delFile(final String path) {
        boolean ret = false;
        if (null != path) {
            final File file = new File(path);
            delFile(file);
        }
        return ret;
    }

    /**
     * Description : 删除文件
     *
     * @param file
     *     文件
     *
     * @return true/false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:42:19
     */
    public static boolean delFile(final File file) {
        boolean ret = true;
        if (null != file && file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                final File[] files = file.listFiles();
                if (null != files && files.length > 0) {
                    for (File tmp : files) {
                        delFile(tmp);
                    }
                }
                file.delete();
            }
        }
        return ret;
    }

    /**
     * Description : 创建目录
     *
     * @param path
     *     文件路径
     *
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:42:34
     */
    public static boolean mkdirs(final String path) {
        boolean ret = false;
        if (null != path) {
            final File file = new File(path);
            ret = mkdirs(file);
        }
        return ret;
    }

    /**
     * Description : 创建目录
     *
     * @param file
     *     文件
     *
     * @return true/false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:42:51
     */
    public static boolean mkdirs(final File file) {
        boolean ret = false;
        if (null != file) {
            if (!file.exists() || !file.isDirectory()) {
                try {
                    file.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ret = (file.exists() && file.isDirectory());
                    if (!ret) {
                        try {
                            file.mkdirs();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ret = (file.exists() && file.isDirectory());
                }
            } else {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Description : 读取文件内容并返回字符串
     *
     * @param path
     *     文件路径
     *
     * @return 文件内容
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:43:10
     */
    public static String readFile(final String path) {
        StringBuilder sb = null;
        if (null != path) {
            final File file = new File(path);
            if (isFile(file)) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    sb = new StringBuilder();
                    String line = null;
                    while (null != (line = br.readLine())) {
                        sb.append(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ((sb == null) ? "" : (sb.toString()));
    }

    /**
     * Description : 补充文件内容
     *
     * @param path
     *     文件路径
     * @param content
     *     补充内容
     *
     * @return true/false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:43:42
     */
    public static boolean writeFile(final String path, final String content, final boolean append) {
        boolean ret = false;
        if (null != path && createFile(path) && content != null && content.length() > 0) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, append), 1024)) {
                bw.write(content);
                bw.flush();
                ret = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Description : 以覆盖形式写文件
     *
     * @param path
     *     文件路径
     * @param content
     *     写入内容
     * @param charsetName
     *     文件读入字符编码格式
     *
     * @return true /false
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:44:38
     */
    public static boolean writeFileByMap(final String path, final String content, final String charsetName) {
        boolean ret = false;
        if (null != path && isFile(path) && content != null && content.length() > 0) {
            try (RandomAccessFile fos = new RandomAccessFile(path, "rw");
                 FileChannel fc = fos.getChannel()) {
                MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, content.length() + 4);
                mbb.put(content.getBytes(charsetName));
                mbb.force();
                ret = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
    /**
     * Move file to another destination.
     *
     * @param file
     * @param dir
     * @throws IOException
     */
    public static void moveFile(File file, File dir) {
        try (FileChannel outputChannel = new FileOutputStream(dir).getChannel();
             FileChannel inputChannel = new FileInputStream(file).getChannel()) {
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean copyStreamByStream(final InputStream is, final String dstPath) {
        boolean ret = false;
        if (null != is && null != dstPath) {
            try (BufferedInputStream br = new BufferedInputStream(is);
                 BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(dstPath))) {
                final byte[] buffer = new byte[2048];
                int count = 0;
                while ((count = br.read(buffer, 0, 2048)) > 0) {
                    bw.write(buffer, 0, count);
                }
                bw.flush();
                ret = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * Description : 利用buffer复制流的内容到目的文件
     *
     * @author : gashion.fang
     * @date : 2014年7月31日 下午8:37:11
     */
    public static boolean copyStreamByBuffer(final InputStream is, final String dstPath) {
        boolean ret = false;
        if (null != is && null != dstPath) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is), 1024);
                 BufferedWriter bw = new BufferedWriter(new FileWriter(dstPath), 1024)) {
                final char[] buffer = new char[1024];
                int count = 0;
                while ((count = br.read(buffer, 0, 1024)) > 0) {
                    bw.write(buffer, 0, count);
                }
                bw.flush();
                ret = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }
    /**
     * copy assets file
     * @param context
     * @param fileName
     * @param newPath
     * @return
     */
    public static boolean copyAssetsFile(Context context, String fileName, String newPath) {
        boolean retVal = false;
        try (InputStream is = context.getAssets().open(fileName);
             FileOutputStream fos = new FileOutputStream(newPath)) {
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();// 刷新缓冲区
            retVal = true;
        } catch (Exception e) {
            e.printStackTrace();
            retVal = false;
        }
        return retVal;
    }

    /**
     * 复制文件
     */
    public static boolean copyFile(final String srcPath, final String dstPath) {
        boolean ret = false;
        if (null != srcPath && null != dstPath) {
            final File src = new File(srcPath);
            final File dst = new File(dstPath);
            if(dst.exists()){
                delFile(dst);
            }
            if (src.exists() && src.isFile()) {

                try {
                    try (FileInputStream fileInputStream = new FileInputStream(srcPath)) {
                        try (FileOutputStream fileOutputStream = new FileOutputStream(dstPath)) {
                            byte[] buffer = new byte[1024];
                            int byteRead;
                            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                                fileOutputStream.write(buffer, 0, byteRead);
                            }
                            fileOutputStream.flush();
                        }
                    }

                    ret = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * Writes a string to a file
     *
     * @param filePath           the path of the desired file
     * @param messageToBeWritten string to be written to file
     * @param appending          true for appending new string to the end of file, false for overwriting file content
     * @throws IOException
     */
    public static void writeStringToFile(String filePath, String messageToBeWritten, boolean appending)
            throws IOException {
        if (!TextUtils.isEmpty(messageToBeWritten)) {
            if (appending) {
                try (Writer  writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), StandardCharsets.UTF_8))) {
                    writer.write(messageToBeWritten);
                    writer.flush();
                }
            } else {
                try (FileWriter writer = new FileWriter(filePath, false)) {
                    writer.write(messageToBeWritten);
                    writer.flush();
                }
            }
        }
    }

    /**
     * Description : 利用buffer复制文件内容到目的文件
     *
     * @param srcPath
     *     源文件路径
     * @param dstPath
     *     目标文件路径
     *
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:46:11
     */
    public static boolean copyFileByBuffer(final String srcPath, final String dstPath) {
        boolean ret = false;
        if (null != srcPath && null != dstPath) {
            final File src = new File(srcPath);
            if (src.exists() && src.isFile()) {
                try (BufferedReader br = new BufferedReader(new FileReader(srcPath), 1024);
                     BufferedWriter bw = new BufferedWriter(new FileWriter(dstPath), 1024)) {
                    String line = null;
                    while (null != (line = br.readLine())) {
                        bw.write(line);
                    }
                    bw.flush();
                    ret = true;
                } catch (Exception e) {
                    Log.e(TAG, "copyFileByBuffer: " + e);
                }
            }
        }
        return ret;
    }

    /**
     * Description : 利用channel复制文件
     *
     * @param srcPath
     *     源文件路径
     * @param dstPath
     *     目标文件路径
     *
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:47:00
     */
    public static boolean copyFileByChannel(final String srcPath, final String dstPath) {
        boolean ret = false;
        if (null != srcPath && null != dstPath) {
            final File src = new File(srcPath);
            if (src.exists() && src.isFile()) {
                ByteBuffer buffer = null;
                try (FileInputStream fis = new FileInputStream(src);
                     FileOutputStream fos = new FileOutputStream(dstPath);
                     FileChannel srcFileChn = fis.getChannel();
                     FileChannel dstFileChn = fos.getChannel()){
                    buffer = ByteBuffer.allocate(1024);

                    buffer.clear();
                    while (-1 != srcFileChn.read(buffer)) {
                        buffer.flip();
                        dstFileChn.write(buffer);
                        buffer.clear();
                    }
                    dstFileChn.force(true);
                    ret = true;
                } catch (Exception ignore) {
                    Log.e(TAG, "copyFileByChannel: " + ignore);
                }
            }
        }

        return ret;
    }

    /**
     * Description : 复制整个目录
     *
     * @param srcPath
     *     源目录路径
     * @param dstPath
     *     目标目录路径
     *
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:47:50
     */
    public static boolean copyDir(final String srcPath, final String dstPath) {
        boolean ret = false;
        if (null != srcPath && dstPath != null) {
            final File file = new File(srcPath);
            final File dstFile = new File(dstPath);
            ret = copyDir(file, dstFile);
        }
        return ret;
    }

    /**
     * Description : 复制整个目录
     *
     * @param srcFile
     *     源目录路径
     * @param dstFile
     *     目标目录路径
     *
     * @author : gashion.fang
     * @date : 2014年7月30日 下午4:48:16
     */
    public static boolean copyDir(final File srcFile, final File dstFile) {
        boolean ret = false;
        if (null != srcFile && null != dstFile && isDir(srcFile) && mkdirs(dstFile)) {
            ret = true;
            final File[] files = srcFile.listFiles();
            if (null != files && files.length > 0) {
                for (File tmp : files) {
                    if (tmp.isFile()) {
                        copyFileByChannel(tmp.getAbsolutePath(), dstFile.getAbsolutePath() + "/" + tmp.getName());
                    } else if (tmp.isDirectory()) {
                        copyDir(tmp, new File(dstFile.getAbsolutePath() + File.separator + tmp.getName()));
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context
     *     The context.
     * @param uri
     *     The Uri to query.
     *
     * @author paulburke
     * @see #getFile(Context, Uri)
     */
    public static String getPath(final Context context, final Uri uri) {

        Log.d(TAG + " File -",
                "Authority: "
                        + uri.getAuthority()
                        + ", Fragment: "
                        + uri.getFragment()
                        + ", Port: "
                        + uri.getPort()
                        + ", Query: "
                        + uri.getQuery()
                        + ", Scheme: "
                        + uri.getScheme()
                        + ", Host: "
                        + uri.getHost()
                        + ", Segments: "
                        + uri.getPathSegments().toString());

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                // ExternalStorageProvider

                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {
                // DownloadsProvider

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // MediaStore (and general)

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // File
            return uri.getPath();
        }

        return null;
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     * Uri is unsupported or pointed to a remote resource.
     * @author paulburke
     * @see #getPath(Context, Uri)
     */
    public static File getFile(Context context, Uri uri) {
        if (uri != null) {
            String path = getPath(context, uri);
            if (path != null && !URLUtil.isNetworkUrl(path)) {
                return new File(path);
            }
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *     The context.
     * @param uri
     *     The Uri to query.
     * @param selection
     *     (Optional) Filter used in the query.
     * @param selectionArgs
     *     (Optional) Selection arguments used in the query.
     *
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                if (BuildConfig.DEBUG) {
                    DatabaseUtils.dumpCursor(cursor);
                }
                return cursor.getString(cursor.getColumnIndexOrThrow(column));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri
     *     The Uri to check.
     *
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *     The Uri to check.
     *
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *     The Uri to check.
     *
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *     The Uri to check.
     *
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * 获取App存放文件的目录
     * @return
     */
    public static File getAppDir(Context context) {
        if (dirName == null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dirName = Environment.getExternalStorageDirectory() + "/DJI/" + context.getPackageName();
        }
        if (dirName != null) {
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        } else {
            return context.getFilesDir();
        }
    }

    /**
     * 获取pin点导出目录
     * @return
     */
    public static File getPinExportDir() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/DJI/Pin");
        if(!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取飞行记录
     * @param context
     * @return
     */
    public static File getFlightRecordDir(Context context) {
        File dir = new File(getAppDir(context), "FlightRecord");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取音频文件存放目录
     * @return
     */
    public static File getVoiceSaveDir(Context context) {
        File cacheDir = context.getCacheDir();
        File voiceDir = new File(cacheDir, "voices");
        if (!voiceDir.exists()) {
            voiceDir.mkdirs();
        }
        return voiceDir;
    }

    /**
     * 获取UTMISS数据存放目录
     *
     * @return
     */
    public static File getUtmissSaveDir(Context context) {
        File appDir = getAppDir(context);
        File utmissDir = new File(appDir, "utmiss");
        if (!utmissDir.exists()) {
            utmissDir.mkdirs();
        }
        return utmissDir;
    }

    /**
     * 空间是否充足
     * @param need
     * @return
     */
    public static boolean isSpaceEnough(Context context, long need){
        File file = new File(getAppDir(context).getPath());
        //file.getFreeSpace()是获取root用户可以使用的空间
        //file.getUsableSpace()是获取非root用户可以使用的空间，应该使用这个
        return file.getUsableSpace() - need >= MXA_REMAINING_CAPACITY;//剩余50mb空间作为预留空间
    }

    /**
     * 重命名文件
     *
     * @author bryan.jia
     */
    public static boolean renameFile(String srcPath, String dstPath) {
        return new File(srcPath).renameTo(new File(dstPath));
    }

    /**
     * 获取单个文件的MD5值
     * @param fileName
     * @return
     */
    @Nullable
    public static String getFileMD5(String fileName) {
        return getFileMD5(new File(fileName));
    }

    @Nullable
    public static String getFileMD5(File file) {
        if (!file.isFile() || !file.exists()) {
            return null;
        }
        byte[] buffer = new byte[1024];
        try (FileInputStream inputStream = new FileInputStream(file)) {
            int len;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            while ((len = inputStream.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            return bytesToHexString(digest.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }

        return stringBuilder.toString();
    }

    /**
     * @Title: getFileSize
     * Description : 递归获取文件夹的大小
     * @param f    实例
     * @return long     返回类型
     * @throws FileNotFoundException
     */
    public static long getFileSize(File f) throws FileNotFoundException {
        long size = 0;
        File flist[] = f.listFiles();
        if (flist == null) {
            return size;
        }
        for (File file : flist) {
            if (file.isDirectory()) {
                size = size + getFileSize(file);
            } else {
                size = size + file.length();
            }
        }
        return size;
    }


    /**
     * Description: 将Assets 下的文件或者文件夹拷贝到手机存储
     */
    public static void copyAssetsDirToSDCard(Context context, String assetsDirName, String sdCardPath) {
        Log.d(TAG, "copyAssetsDirToSDCard() called with: context = [" + context + "], assetsDirName = [" + assetsDirName + "], sdCardPath = [" + sdCardPath + "]");
        try {
            String[] list = context.getAssets().list(assetsDirName);
            if (list.length == 0) {
                try (InputStream inputStream = context.getAssets().open(assetsDirName)) {
                    byte[] mByte = new byte[1024];
                    int bt = 0;
                    File file = new File(sdCardPath + File.separator
                            + assetsDirName.substring(assetsDirName.lastIndexOf('/')));
                    if (!file.exists()) {
                        file.createNewFile();
                    } else {
                        return;
                    }
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        while ((bt = inputStream.read(mByte)) != -1) {
                            fos.write(mByte, 0, bt);
                        }
                        fos.flush();
                    }
                }
            } else {
                String subDirName = assetsDirName;
                if (assetsDirName.contains("/")) {
                    subDirName = assetsDirName.substring(assetsDirName.lastIndexOf('/') + 1);
                }
                sdCardPath = sdCardPath + File.separator + subDirName;
                File file = new File(sdCardPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (String s : list) {
                    copyAssetsDirToSDCard(context, assetsDirName + File.separator + s, sdCardPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取程序根目录
     */
    public static String getDJIDirPath(Context context) {
        return Environment.getExternalStorageDirectory() + "/DJI/" + context.getPackageName();
    }
}
