package com.example.demoh5jsnativecomm.jsinterface;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.annotation.NonNull;

import com.example.demoh5jsnativecomm.LocationUtils;
import com.example.demoh5jsnativecomm.R;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

public class NativeAPILocation {
    private static final String TAG = "NativeAPILocation";
    public static final String API_NAME = "NativeAPILocation";
    public static final int LOC_PERMISSIONS = 100;
    public static final int LOC_PERMISSIONS_FROM_JS = LOC_PERMISSIONS + 1;
    private final String[] LocPermissionString = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private Context mContext;
    private boolean mIsGetting;
    private String mLocationCache;


    public NativeAPILocation(@NonNull Context context) {
        mContext = context;
    }

    /**
     * 获取经纬度。
     */
    @SuppressLint("MissingPermission")
    @NonNull
    @JavascriptInterface
    public String getLocation() {
        synchronized (NativeAPILocation.this) {
            if (mIsGetting) {
                return mLocationCache != null ? mLocationCache : new GsonBuilder()
                        .serializeSpecialFloatingPointValues().create().toJson(
                                new NativeAPILocationResult(
                                        NativeAPIResultCode.LOCATION_NOT_PREPARED,
                                        new NativeAPILocationResult.Location(Double.NaN,
                                                Double.NaN, Double.NaN)));
            }
            mIsGetting = true;
            try {
                if (EasyPermissions.hasPermissions(mContext, LocPermissionString)) {
                    mLocationCache = LocationUtils.getInstance().getLocation();
                } else {
                    // Request one permission
                    EasyPermissions.requestPermissions((Activity) mContext,
                            mContext.getString(R.string.request_location_rationale),
                            LOC_PERMISSIONS_FROM_JS, LocPermissionString);
                    mLocationCache = new GsonBuilder().serializeSpecialFloatingPointValues()
                            .create().toJson(new NativeAPILocationResult(
                                    NativeAPIResultCode.REQUEST_PERMISSION,
                                    new NativeAPILocationResult
                                            .Location(Double.NaN, Double.NaN, Double.NaN
                                    )));
                }
            } catch (Exception e) {
                Log.e(TAG, "getLocation: ", e);
            } finally {
                mIsGetting = false;
            }

            return "location: " + mLocationCache + ", time:"
                    + new SimpleDateFormat("yyMMddHHmmss", Locale.CHINA).format(new Date());
        }
    }

    public void destroy() {
        mContext = null;
    }
}