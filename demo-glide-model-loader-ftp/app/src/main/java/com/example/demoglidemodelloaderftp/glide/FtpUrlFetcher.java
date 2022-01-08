package com.example.demoglidemodelloaderftp.glide;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.HttpException;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.util.LogTime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * A DataFetcher that retrieves an {@link InputStream} for a Url.
 */
public class FtpUrlFetcher implements DataFetcher<InputStream> {
    private static final String TAG = "HomeHttpUrlFetcher";
    private static final int MAXIMUM_REDIRECTS = 5;
    @VisibleForTesting
    static final String REDIRECT_HEADER_FIELD = "Location";

    @VisibleForTesting
    static final FTPURLConnection.FTPUrlConnectionFactory DEFAULT_CONNECTION_FACTORY =
            new FTPURLConnection.DefaultFTPUrlConnectionFactory();
    /**
     * Returned when a connection error prevented us from receiving an http error.
     */
    @VisibleForTesting
    static final int INVALID_STATUS_CODE = -1;

    private final FtpUrl mGlideUrl;
    private final int mTimeout;
    private final FTPURLConnection.FTPUrlConnectionFactory mConnectionFactory;

    private FTPURLConnection mUrlConnection;
    private InputStream mStream;
    private volatile boolean mIsCancelled;

    public FtpUrlFetcher(FtpUrl glideUrl, int timeout) {
        this(glideUrl, timeout, DEFAULT_CONNECTION_FACTORY);
    }

    @VisibleForTesting
    FtpUrlFetcher(FtpUrl glideUrl, int timeout,
                  FTPURLConnection.FTPUrlConnectionFactory connectionFactory) {
        this.mGlideUrl = glideUrl;
        this.mTimeout = timeout;
        this.mConnectionFactory = connectionFactory;
    }

    @Override
    public void loadData(
            @NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        long startTime = LogTime.getLogTime();
        try {
            InputStream result =
                    loadDataWithRedirects(mGlideUrl.toURL(), 0, null, mGlideUrl.getHeaders());
            callback.onDataReady(result);
        } catch (IOException e) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Failed to load data for url", e);
            }
            callback.onLoadFailed(e);
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Finished http url fetcher fetch in " + LogTime
                        .getElapsedMillis(startTime));
            }
        }
    }

    private InputStream loadDataWithRedirects(
            URL url, int redirects, URL lastUrl, Map<String, String> headers) throws HttpException {
        if (redirects >= MAXIMUM_REDIRECTS) {
            throw new HttpException(
                    "Too many (> " + MAXIMUM_REDIRECTS + ") redirects!", INVALID_STATUS_CODE);
        } else {
            // Comparing the URLs using .equals performs additional network I/O and is generally
            // broken.
            // See http://michaelscharf.blogspot.com/2006/11/javaneturlequals-and-hashcode-make
            // .html.
            try {
                if (lastUrl != null && url.toURI().equals(lastUrl.toURI())) {
                    throw new HttpException("In re-direct loop", INVALID_STATUS_CODE);
                }
            } catch (URISyntaxException e) {
                // Do nothing, this is best effort.
            }
        }

        mUrlConnection = buildAndConfigureConnection(url, headers);

        // try {
            // Connect explicitly to avoid errors in decoders if connection fails.
            mUrlConnection.connect();
            // Set the stream so that it's closed in cleanup to avoid resource leaks. See #2352.
            // mStream = mUrlConnection.getInputStream();
        // } catch (IOException e) {
        //     throw new HttpException("Failed to connect or obtain data", getFTPStatusCodeOrInvalid(
        //             mUrlConnection), e);
        // }

        if (mIsCancelled) {
            return null;
        }

        final int statusCode = getFTPStatusCodeOrInvalid(mUrlConnection);
        // Referencing constants is less clear than a simple static method.
        if (mUrlConnection.isHttpOk(statusCode)) {
            return getStreamForSuccessfulRequest(mUrlConnection);
        } else {
            throw new HttpException("Failed to get a response message", statusCode);
        }
    }

    private static int getFTPStatusCodeOrInvalid(FTPURLConnection urlConnection) {
        return urlConnection.getResponseCode();
        // return INVALID_STATUS_CODE;
    }

    private FTPURLConnection buildAndConfigureConnection(URL url, Map<String, String> headers)
            throws HttpException {
        FTPURLConnection urlConnection;
        try {
            urlConnection = mConnectionFactory.build(url);
        } catch (IOException e) {
            throw new HttpException("URL.openConnection threw", /*statusCode=*/ 0, e);
        }
        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            urlConnection.addRequestProperty(headerEntry.getKey(), headerEntry.getValue());
        }
        urlConnection.setConnectTimeout(mTimeout);
        urlConnection.setReadTimeout(mTimeout);
        return urlConnection;
    }

    private InputStream getStreamForSuccessfulRequest(FTPURLConnection urlConnection)
            throws HttpException {
        try {
            mStream = mUrlConnection.getStreamForSuccessfulRequest(urlConnection);
        } catch (IOException e) {
            throw new HttpException(e.getMessage(), HttpException.UNKNOWN);
        }
        return mStream;
    }

    @Override
    public void cleanup() {
        if (mStream != null) {
            try {
                mStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        if (mUrlConnection != null) {
            mUrlConnection.completePendingCommand();
            mUrlConnection.disconnect();
        }
        mUrlConnection = null;
    }

    @Override
    public void cancel() {
        // TODO: we should consider disconnecting the url connection here, but we can't do so
        // directly because cancel is often called on the main thread.
        mIsCancelled = true;
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }


}
