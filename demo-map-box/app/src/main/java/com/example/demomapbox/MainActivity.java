package com.example.demomapbox;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demomapbox.databinding.ActivityMainBinding;
import com.mapbox.common.MapboxOptions;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapLoaded;
import com.mapbox.maps.MapLoadedCallback;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.attribution.AttributionPlugin;
import com.mapbox.maps.plugin.attribution.AttributionUtils;
import com.mapbox.maps.plugin.logo.LogoUtils;
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin;
import com.mapbox.maps.plugin.scalebar.ScaleBarUtils;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private ActivityMainBinding mBinding;

    /**
     * Android 11及以上。
     */
    private static final String[] API30_REQUIRED_PERMISSION_LIST = new String[]{
            // Manifest.permission.VIBRATE, // Gimbal rotation
            android.Manifest.permission.INTERNET, // API requests
            android.Manifest.permission.ACCESS_WIFI_STATE, // WIFI connected products
            android.Manifest.permission.ACCESS_COARSE_LOCATION, // Maps
            android.Manifest.permission.ACCESS_NETWORK_STATE, // WIFI connected products
            android.Manifest.permission.ACCESS_FINE_LOCATION, // Maps
            android.Manifest.permission.CHANGE_WIFI_STATE,
            // Changing between WIFI and USB connection
            // Manifest.permission.WRITE_EXTERNAL_STORAGE, // Log files
            // Manifest.permission.BLUETOOTH, // Bluetooth connected products
            // Manifest.permission.BLUETOOTH_ADMIN, // Bluetooth connected products
            android.Manifest.permission.READ_EXTERNAL_STORAGE, // Log files
            android.Manifest.permission.READ_PHONE_STATE, // Device UUID accessed upon registration

            // DJI 13
            // Manifest.permission.RECORD_AUDIO // Speaker accessory
            // Manifest.permission.READ_MEDIA_IMAGES,
            // Manifest.permission.READ_MEDIA_VIDEO,
            // Manifest.permission.READ_MEDIA_AUDIO,
    };

    /**
     * Android 10及以下。Manifest中requestLegacyExternalStorage=true
     */
    private static final String[] API29_REQUIRED_PERMISSION_LIST = new String[]{
            // Manifest.permission.VIBRATE, // Gimbal rotation
            android.Manifest.permission.INTERNET, // API requests
            android.Manifest.permission.ACCESS_WIFI_STATE, // WIFI connected products
            android.Manifest.permission.ACCESS_COARSE_LOCATION, // Maps
            android.Manifest.permission.ACCESS_NETWORK_STATE, // WIFI connected products
            android.Manifest.permission.ACCESS_FINE_LOCATION, // Maps
            android.Manifest.permission.CHANGE_WIFI_STATE,
            // Changing between WIFI and USB connection
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, // Log files
            // Manifest.permission.BLUETOOTH, // Bluetooth connected products
            // Manifest.permission.BLUETOOTH_ADMIN, // Bluetooth connected products
            android.Manifest.permission.READ_EXTERNAL_STORAGE, // Log files
            Manifest.permission.READ_PHONE_STATE, // Device UUID accessed upon registration
            // Manifest.permission.RECORD_AUDIO // Speaker accessory
    };

    private static final int RC_ALL_PERMISSION = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxOptions.setAccessToken(BuildConfig.MAP_KEY);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        // Intent intent = getIntent();
        // if (!isTaskRoot() && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
        //         && Intent.ACTION_MAIN.equals(intent.getAction())) {
        //     finish();
        // }
        requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && EasyPermissions.hasPermissions(this, API30_REQUIRED_PERMISSION_LIST))
                || (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
                EasyPermissions.hasPermissions(this,
                        API29_REQUIRED_PERMISSION_LIST))) {
            init();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        requestPermissions();
    }

    private void requestPermissions() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && EasyPermissions.hasPermissions(this, API30_REQUIRED_PERMISSION_LIST))
                || (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
                EasyPermissions.hasPermissions(this,
                        API29_REQUIRED_PERMISSION_LIST))) {
            init();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && !EasyPermissions.hasPermissions(this, API30_REQUIRED_PERMISSION_LIST)) {
            EasyPermissions.requestPermissions(this,
                    getString(
                            R.string.activity_main_request_permission),
                    RC_ALL_PERMISSION, API30_REQUIRED_PERMISSION_LIST);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
                !EasyPermissions.hasPermissions(this,
                        API29_REQUIRED_PERMISSION_LIST)) {
            EasyPermissions.requestPermissions(this,
                    getString(
                            R.string.activity_main_request_permission),
                    RC_ALL_PERMISSION, API29_REQUIRED_PERMISSION_LIST);
        }
    }

    private MapboxMap mMap;

    /**
     * initialization.
     */
    private void init() {

        ScaleBarPlugin s = mBinding.activityMainMapView
                .getPlugin(Plugin.MAPBOX_SCALEBAR_PLUGIN_ID);
        if (s != null) {
            s.setEnabled(false);
        }
        // ScaleBarUtils.getScaleBar()
        AttributionPlugin p = mBinding.activityMainMapView
                .getPlugin(Plugin.MAPBOX_ATTRIBUTION_PLUGIN_ID);
        if (p != null) {
            p.setEnabled(false);
        }
        // mBinding.activityMainMapView.createPlugin();
        // AttributionUtils.getAttribution();
        LogoUtils.getLogo(mBinding.activityMainMapView).setEnabled(false);

        mMap = mBinding.activityMainMapView.getMapboxMap();
        mMap.loadStyle(Style.SATELLITE_STREETS);
        mMap.subscribeMapLoaded(new MapLoadedCallback() {
            @Override
            public void run(@NonNull MapLoaded mapLoaded) {
                CameraOptions.Builder builder = new CameraOptions.Builder();
                CameraOptions options = builder.center(
                                Point.fromLngLat(114.41992218256276, 30.42491669227814))
                        .zoom(14.0).bearing(0.0)
                        .pitch(0.0)
                        .build();
                mMap.setCamera(options);

            }
        });
        mBinding.btnSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.loadStyle(Style.SATELLITE_STREETS);
            }
        });
        mBinding.btnStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.loadStyle(Style.MAPBOX_STREETS);
            }
        });
    }

    /**
     * @param success the initial result. true means initializing successfully.
     */
    private void doBusiness(boolean success) {
    }

}