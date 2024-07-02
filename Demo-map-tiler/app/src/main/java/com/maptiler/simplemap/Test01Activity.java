package com.maptiler.simplemap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.maptiler.simplemap.databinding.ActivityTest01Binding;

public class Test01Activity extends AppCompatActivity {

    private ActivityTest01Binding mBinding;
    private MapboxMap mMap;
    private final String mStyleUrl = "https://api.maptiler.com/maps/hybrid/style.json?key=" + BuildConfig.mapTilerKey;
    private final String mStreet = "https://api.maptiler.com/maps/streets/style.json?key=" + BuildConfig.mapTilerKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_test01);
        // val styleUrl = "https://api.maptiler.com/maps/streets/style.json?key=${mapTilerKey}";
        // val styleUrl = "https://api.maptiler.com/maps/satellite/style.json?key=${mapTilerKey}";

        // Get the MapBox context
        Mapbox.getInstance(this, null);
        mBinding = ActivityTest01Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.mapView.onCreate(savedInstanceState);
        mBinding.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                // final String styleUrl = "https://api.maptiler.com/maps/hybrid/style.json?key=" + BuildConfig.mapTilerKey;
                mMap = mapboxMap;
                mMap.setStyle(mStyleUrl);
                mMap.getUiSettings().setLogoEnabled(false);
                mMap.getUiSettings().setAttributionEnabled(false);
                CameraPosition cm = new CameraPosition.Builder()
                        // .target(LatLng(47.127757, 8.579139))
                        .target(new LatLng(30.42491669227814, 114.41992218256276))
                        .zoom(14.0)
                        .build();
                mMap.setCameraPosition(cm);
            }
        });

        mBinding.btnSatelliteHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.setStyle(mStreet);
                    mMap.getUiSettings().setLogoEnabled(false);
                    mMap.getUiSettings().setAttributionEnabled(false);
                }
            }
        });

        mBinding.btnStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.setStyle(mStyleUrl);
                    mMap.getUiSettings().setLogoEnabled(false);
                    mMap.getUiSettings().setAttributionEnabled(false);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBinding.mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mBinding.mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBinding.mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mBinding.mapView.onLowMemory();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.mapView.onDestroy();
    }

    private String getMapTilerKey() {
        return "";
    }

    private boolean validateKey(@Nullable String mapTilerKey) {
        if (mapTilerKey == null || mapTilerKey.isEmpty()) {
            throw new RuntimeException("Failed to read MapTiler key from info.plist");
        }

        return true;
    }
}