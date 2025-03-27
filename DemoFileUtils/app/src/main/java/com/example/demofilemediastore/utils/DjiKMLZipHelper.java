package com.zhd.hiair.djisdkmanager;

import android.content.Context;
import android.util.Log;

import com.dji.wpmzsdk.common.utils.DocumentsUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class DjiKMLZipHelper {

    private final static String TAG = ("KMLZipHelper");

    private DjiKMLZipHelper() {
    }

    public static String getSelectFilePath(Context context, String srcFile, String selectFileName) {
        try (ZipInputStream inZip = new ZipInputStream(DocumentsUtils.getInputStream(context, new File(srcFile)))) {
            ZipEntry zipEntry;
            while ((zipEntry = inZip.getNextEntry()) != null) {
                String name = zipEntry.getName();

                if (!zipEntry.isDirectory() && name.endsWith(selectFileName)) {
                    return name;
                }
            }
        } catch (Exception e) {
           Log.e(TAG , e.getMessage());
        }
        return null;
    }

    /**
     * 解压指定文件
     * @param context
     * @param srcFile
     * @param selectFileName
     */
    public static File unZipFile(Context context, String srcFile, String destFile, String selectFileName) {
        OutputStream out = null;
        try (ZipInputStream inZip = new ZipInputStream(DocumentsUtils.getInputStream(context, new File(srcFile)))) {
            ZipEntry zipEntry;
            while ((zipEntry = inZip.getNextEntry()) != null) {
                String name = zipEntry.getName();

                if (!zipEntry.isDirectory() && name.endsWith(selectFileName)) {
                    File file = new File(destFile);
                    // get the output stream of the file
                    out = DocumentsUtils.getOutputStream(context, file);
                    int len;
                    byte[] buffer = new byte[1024];
                    // read (len) bytes into buffer
                    while ((len = inZip.read(buffer)) != -1) {
                        // write (len) byte from buffer at the position 0
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                    return file;
                }
            }
        } catch (Exception e) {
            Log.e(TAG , e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG,e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * DeCompress the ZIP to the path
     *
     * @param zipFileString name of ZIP
     * @param outPathString path to be unZIP
     */
    public static void unZipFolder(Context context, String zipFileString, String outPathString, boolean flatFolder) {
        try (ZipInputStream inZip = new ZipInputStream(DocumentsUtils.getInputStream(context, new File(zipFileString)))) {
            ZipEntry zipEntry;
            while ((zipEntry = inZip.getNextEntry()) != null) {
                String name = zipEntry.getName();

                if (zipEntry.isDirectory()) {
                    // get the folder name of the widget
                    File folder = new File(outPathString, name);
                    DocumentsUtils.mkdirs(folder);
                } else {
                    if (flatFolder && name.contains("/")) {
                        name = name.substring(name.lastIndexOf('/'));
                    }

                    unZipFileEntry(context, inZip, outPathString, name);
                }
            }
        } catch (Exception e) {
            Log.e(TAG , e.getMessage());
        }
    }

    private static void unZipFileEntry(Context context, ZipInputStream inZip, String outPathString, String name) {
        File file = new File(outPathString, name);
        if (file.getParentFile() != null) {
            DocumentsUtils.mkdirs(file.getParentFile());
        }

        try (OutputStream out = DocumentsUtils.getOutputStream(context, file)) {
            int len;
            byte[] buffer = new byte[1024];
            while ((len = inZip.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                out.flush();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    public static void zipFiles(Context context, List<String> files, String zipFile) throws Exception {
        //创建ZIP
        try (FileOutputStream outputStream = new FileOutputStream(zipFile);
             ZipOutputStream outZip = new ZipOutputStream(outputStream)) {
            for (String filePath : files) {
                //创建文件
                File file = new File(filePath);
                //压缩
                doZipFiles(context,file.getParent() + File.separator, file.getName(), outZip);
            }
            //完成和关闭
            outZip.finish();
            outputStream.getFD().sync();
        }
    }

    private static void doZipFiles(Context context, String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString, fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            InputStream inputStream = DocumentsUtils.getInputStream(context, file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            inputStream.close();
            zipOutputSteam.closeEntry();
        } else {
            //文件夹
            String[] fileList = file.list();
            //没有子文件和压缩
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            //子文件和递归
            for (String aFileList : fileList) {
                doZipFiles(context, folderString, fileString + File.separator + aFileList, zipOutputSteam);
            }
        }
    }
}
