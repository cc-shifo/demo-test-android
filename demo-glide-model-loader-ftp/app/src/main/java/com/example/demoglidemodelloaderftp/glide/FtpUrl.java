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

import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.EmptySignature;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.bumptech.glide.util.Preconditions;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class FtpUrl implements Key {
    private final String mUrl;
    private String safeStringUrl;
    private URL safeUrl;

    public FtpUrl(String url) {
        mUrl = url;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(getCacheKey().getBytes(CHARSET));
    }

    private String getCacheKey() {
        // HttpUrl httpUrl = HttpUrl.parse(mUrl);
        URL httpUrl;
        try {
            httpUrl = new URL(mUrl);
        } catch (MalformedURLException e) {
            Timber.e(new IllegalArgumentException("invalid image https address"), "%s", mUrl);
            return mUrl;
        }

        String path = httpUrl.getFile();
        if (path.length() <= 1) {
            Timber.e(new IllegalArgumentException("invalid image https address"), "%s", mUrl);
            return mUrl;
        }
        int pathStart = path.indexOf('/');
        if (pathStart == -1) {
            return path;
        }

        return path.substring(pathStart + 1);
    }

    @Override
    public String toString() {
        return getSafeStringUrl();
        // return toStringUrl();
        /*return "HomeGlideUrl{" +
                "mUrl='" + mUrl + '\'' +
                '}';*/
    }

    private String getSafeStringUrl() {
        final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%;$";
        if (TextUtils.isEmpty(safeStringUrl)) {
            String unsafeStringUrl = mUrl;
            if (TextUtils.isEmpty(unsafeStringUrl)) {
                unsafeStringUrl = Preconditions.checkNotNull(mUrl);
            }
            safeStringUrl = Uri.encode(unsafeStringUrl, ALLOWED_URI_CHARS);
        }
        return safeStringUrl;
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>();
    }

    public URL toURL() throws MalformedURLException {
        if (safeUrl == null) {
            safeUrl = new URL(getSafeStringUrl());
        }
        return safeUrl;
    }

    // if ModelCache is used, equals and hashCode method must be overrided.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FtpUrl that = (FtpUrl) o;
        return Objects.equals(mUrl, that.mUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mUrl);
    }

    public Key getSignatureKey(boolean updateUniqueRes) {
        if (updateUniqueRes && mUrl.contains(".")) {
            String tail = mUrl.substring(mUrl.indexOf('.') + 1).toLowerCase();
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(tail);
            return new MediaStoreSignature(type, SystemClock.elapsedRealtime(), 0);
        }
        return EmptySignature.obtain();
    }
}
