/*
 * = COPYRIGHT
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20220106 	         LiuJian                  Create
 */

package com.example.demoglidemodelloaderftp.glide;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.util.Synthetic;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.function.Function;

import timber.log.Timber;


public class FTPURLConnection {
    private final URL mURL;
    private FTPClient mClient;
    private String mUser;
    private String mPassword;
    private int mConnectTimeout;
    private int mReadTimeout;

    public FTPURLConnection(@NonNull URL url) {
        this(url, null, null);
    }

    public FTPURLConnection(@NonNull URL url, @Nullable String user, @Nullable String password) {
        mURL = url;
        mUser = user;
        mPassword = password;
        mConnectTimeout = 1000;
        mReadTimeout = 1000;

        mClient = new FTPClient();
        mClient.setControlKeepAliveTimeout(mReadTimeout);
        mClient.setControlKeepAliveReplyTimeout(mReadTimeout); // default 1000
        mClient.setControlEncoding("UTF-8");
        mClient.setListHiddenFiles(false);

        // suppress login details
        // mClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System
        // .out), true));

        // FTPClientConfig config = new FTPClientConfig();
        // config.setUnparseableEntries(false);
        // config.setDefaultDateFormatStr(defaultDateFormat);
        // config.setRecentDateFormatStr(recentDateFormat);
        // mClient.configure(config);
    }


    public void addRequestProperty(String key, String value) {

    }

    public void setConnectTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout can not be negative");
        }
        mConnectTimeout = timeout;
    }

    public void setReadTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout can not be negative");
        }
        mReadTimeout = timeout;
    }

    // 连接到ftp服务器
    public synchronized boolean connect() {
        if (mClient.isConnected()) {//判断是否已登陆
            try {
                mClient.logout();
                mClient.disconnect();
            } catch (IOException e) {
                Timber.e(e);
            }
            return true;
        }

        mClient.setConnectTimeout(mConnectTimeout);//设置连接超时时间
        mClient.setDataTimeout(mReadTimeout);//设置连接超时时间
        mClient.setControlEncoding("utf-8");
        // Timber.w(mClient.printWorkingDirectory());
        mClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out),
                true));

        String ip = mURL.getHost();
        try {
            mClient.connect(ip);
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }
        if (!FTPReply.isPositiveCompletion(mClient.getReplyCode())) {
            try {
                mClient.disconnect();
            } catch (IOException e) {
                Timber.e(e);
                return false;
            }

        }

        if (TextUtils.isEmpty(mUser)) {
            mUser = "Anonymous";
            mPassword = null;
        }
        try {
            if (!mClient.login(mUser, mPassword)) {
                mClient.logout();
                mClient.disconnect();
            }
        } catch (IOException e) {
            Timber.e(e);
            try {
                mClient.logout();
                mClient.disconnect();
            } catch (IOException ioException) {
                Timber.e(ioException);
            }
            return false;
        }
        return true;
    }

    private InputStream getInputStream(@Nullable String file) throws IOException {
        mClient.enterLocalPassiveMode();//防止本地防火墙
        mClient.enterRemotePassiveMode();//防止远程防火墙
        mClient.setFileType(FTP.BINARY_FILE_TYPE);
        // mClient.setRestartOffset(localSize);


        // InputStream inputStream = mClient.retrieveFileStream(mURL.getFile());
        return mClient.retrieveFileStream(file);
    }



    public int getResponseCode(){
        return mClient.getReplyCode();
    }

    public boolean isHttpOk(int statusCode) {
        return FTPReply.isPositiveCompletion(statusCode);
    }

    public InputStream getStreamForSuccessfulRequest(FTPURLConnection urlConnection)
            throws IOException {
        FTPFile[] files = mClient.listFiles();
        if (files.length <= 0) {
            throw new IOException("Ftp server is empty.");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Arrays.sort(files, Comparator.comparing(new Function<FTPFile, Calendar>() {
                @Override
                public Calendar apply(FTPFile ftpFile) {
                    return ftpFile.getTimestamp();
                }
            }));
        } else {
            Arrays.sort(files, new Comparator<FTPFile>() {
                @Override
                public int compare(FTPFile o1, FTPFile o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });
        }

        String name = files[files.length-1].getName();
        if (TextUtils.isEmpty(name)) {
            name = mURL.getFile();
            if (name != null) {
                name = name.replace("/", "");
            }
        }

        return getInputStream(name);
    }

    public void completePendingCommand() {
        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
        try {
            if (!mClient.completePendingCommand()) {
                mClient.logout();
                mClient.disconnect();
            }
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    public void disconnect() {
        if (mClient.isConnected()) {
            try {
                mClient.logout();
                mClient.disconnect();
            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }

    public interface FTPUrlConnectionFactory {
        FTPURLConnection build(URL url) throws IOException;
    }

    public static class DefaultFTPUrlConnectionFactory implements
            FTPUrlConnectionFactory {

        @Synthetic
        public DefaultFTPUrlConnectionFactory() {
            // nothing
        }

        @Override
        public FTPURLConnection build(URL url) throws IOException {
            return new FTPURLConnection(url, null, null);
        }
    }
}