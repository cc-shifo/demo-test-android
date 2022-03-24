package com.example.demoh5jsnativecomm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.demoh5jsnativecomm.jsinterface.NativeAPILocationResult;
import com.example.demoh5jsnativecomm.jsinterface.NativeAPIResultCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Use {@link LocationManager} to request location service. The location change is acquired from
 * {@link LocationListener}.
 */
public class LocationUtils {
    private static final String TAG = "LocationUtils";
    /**
     * the interval time setting for location request.
     */
    private static final int INTERVAL_MS = 3000;
    /**
     * the minimal distance setting for location request.
     */
    private static final int DISTANCE_MIN = 5;

    @SuppressLint("StaticFieldLeak")
    private static LocationUtils mInstance;
    private Context mContext;
    private LocationManager mLocationManager;
    private String mLocationProvider;
    /**
     * TODO define string format
     */
    private String mLonLat;

    private LocationUtils() {
        // nothing
    }

    public static synchronized LocationUtils getInstance() {
        if (mInstance == null) {
            mInstance = new LocationUtils();
        }
        return mInstance;
    }

    /**
     * 必须在所有调用执行之前调用。
     */
    public void init(@NonNull Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * 获取经纬度。
     */
    @SuppressLint("MissingPermission")
    public void requestLocation() {
        if (isLocationEnable()) {
            getProviders();
            mLocationManager.requestLocationUpdates(mLocationProvider, INTERVAL_MS, DISTANCE_MIN,
                    mLocationListener);
        }
    }

    /**
     * Determine if location provider is open or closed.
     *
     * @return true if location provider is open, otherwise false.
     */
    private boolean isLocationEnable() {
        int mode = Settings.Secure.LOCATION_MODE_OFF;
        try {
            mode = Settings.Secure.getInt(mContext.getContentResolver(),
                    Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "isLocationEnable: ", e);
        }

        return mode != Settings.Secure.LOCATION_MODE_OFF;
    }

    /**
     * 获取位置提供器，GPS或是NetWork
     */
    private void getProviders() {
        List<String> providers = mLocationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            mLocationProvider = LocationManager.GPS_PROVIDER;
            Log.d(TAG, "定位方式GPS");
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            mLocationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            for (String name : providers) {
                Log.d(TAG, "不支持的方式: " + name);
            }
            mLocationProvider = null;
        }
    }


    private final LocationListener mLocationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onLocationChanged(@NonNull List<Location> locations) {
            // nothing
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: ");
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: ");
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            synchronized (LocationUtils.this) {
                mLonLat = new Gson().toJson(new NativeAPILocationResult(
                        NativeAPIResultCode.SUCCESS, new NativeAPILocationResult.Location(
                        location.getLongitude(), location.getLatitude(),
                        location.getAltitude())));
            }
            Log.d(TAG, "onLocationChanged: " + "经度: " + location.getLongitude()
                    + "维度：" + location.getLatitude());
        }
    };

    /**
     * 获取最新的位置经纬度信息。没有位置信息就请求最后一次已知纬度信息，如果还没有纬度信息返回
     * {@link NativeAPIResultCode#LOCATION_NOT_PREPARED}.
     * @return 返回经纬度信息。
     */
    @SuppressLint("MissingPermission")
    @NonNull
    public synchronized String getLocation() {
        if (mLonLat == null && isLocationEnable()) {
            getProviders();
            Location location = mLocationManager.getLastKnownLocation(mLocationProvider);
            if (location != null) {
                mLonLat = new GsonBuilder().serializeSpecialFloatingPointValues()
                        .create().toJson(new NativeAPILocationResult(
                                NativeAPIResultCode.SUCCESS, new NativeAPILocationResult.Location(
                                location.getLongitude(), location.getLatitude(),
                                location.getAltitude())));

                mLonLat = new Gson().toJson(new NativeAPILocationResult(
                        NativeAPIResultCode.SUCCESS, new NativeAPILocationResult.Location(
                        location.getLongitude(), location.getLatitude(),
                        location.getAltitude())));
            }
        }

        if (mLonLat == null) {
            mLonLat = new Gson().toJson(new NativeAPILocationResult(
                    NativeAPIResultCode.LOCATION_NOT_PREPARED, new NativeAPILocationResult
                    .Location(Double.NaN, Double.NaN, Double.NaN)));
        }
        return mLonLat;
    }

    /**
     * remove listener of {@link LocationManager}, set {@link Context} reference to null.
     */
    public void destroy() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
        mContext = null;
    }
}
