/*
 * = COPYRIGHT
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20220106 	         LiuJian                  Create
 */

package com.example.demoglidemodelloaderftp;

import androidx.annotation.NonNull;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import timber.log.Timber;

public class FTPDownloader {

    private FTPClient mClient;

    public FTPDownloader(@NonNull FTPClient client) {
        mClient = client;
    }

    // 实现下载文件功能，可实现断点下载
    public synchronized boolean downloadFile(String localPath, String serverPath) {
        final int INDEX = 8;
        // 先判断服务器文件是否存在
        FTPFile[] files;
        try {
            files = mClient.listFiles(serverPath);
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }

        if (files.length == 0) {
            Timber.w("Ftp server is empty.");
            return false;
        } else if (files.length <= INDEX + 1) {
            Timber.w("The number of files on ftp server is less than 10.");
            return false;
        }

        localPath = localPath + files[INDEX].getName();
        // 接着判断下载的文件是否能断点下载
        long serverSize = files[INDEX].getSize(); // 获取远程文件的长度
        File localFile = new File(localPath);
        long localSize = 0;
        if (localFile.exists()) {
            localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
            if (localSize == serverSize) {
                return true;
            }
        }

        mClient.enterLocalActiveMode();
        try {
            mClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }

        mClient.setRestartOffset(localSize);
        try (BufferedSink sink = Okio.buffer(Okio.sink(new File(localPath), true));
             BufferedSource source = Okio.buffer(Okio.source(mClient.retrieveFileStream(
                     serverPath)))) {
            byte[] b = new byte[1024];
            int length = 0;
            while ((length = source.read(b)) != -1) {
                sink.write(b, 0, length);
            }
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }

        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
        try {
            if (!mClient.completePendingCommand()) {
                mClient.logout();
                mClient.disconnect();
            }
        } catch (IOException e) {
            Timber.e(e);
        }

        return true;
    }

}
