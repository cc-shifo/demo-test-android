package com.maptiler.simplemap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;


import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.maptiler.simplemap.databinding.ActivityTest01Binding;

import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.camera.CameraPosition;
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
        Mapbox.getInstance(this);
        mBinding = ActivityTest01Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.mapView.onCreate(savedInstanceState);
        mBinding.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                // final String styleUrl = "https://api.maptiler.com/maps/hybrid/style.json?key=" + BuildConfig.mapTilerKey;
                mMap = mapboxMap;
                mMap.setStyle(mStyleUrl);
                // mMap.getUiSettings().setLogoEnabled(false);
                // mMap.getUiSettings().setAttributionEnabled(false);
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
                    mMap.setStyle(mStyleUrl);
                    mMap.getUiSettings().setLogoEnabled(false);
                    mMap.getUiSettings().setAttributionEnabled(false);
                }
            }
        });

        mBinding.btnStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.setStyle(mStreet);
                    mMap.getUiSettings().setLogoEnabled(false);
                    mMap.getUiSettings().setAttributionEnabled(false);
                }
            }
        });
        mBinding.btnSizeQuarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
                params.matchConstraintPercentWidth = 0.25f;
                params.matchConstraintDefaultWidth = ConstraintLayout.LayoutParams
                        .MATCH_CONSTRAINT_PERCENT;
                params.matchConstraintPercentHeight = 0.25f;
                params.matchConstraintDefaultHeight = ConstraintLayout.LayoutParams
                        .MATCH_CONSTRAINT_PERCENT;
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                mBinding.mapView.setLayoutParams(params);
            }
        });
        mBinding.btnSizeHalf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
                params.matchConstraintPercentWidth = 0.5f;
                params.matchConstraintDefaultWidth = ConstraintLayout.LayoutParams
                        .MATCH_CONSTRAINT_PERCENT;
                params.matchConstraintPercentHeight = 0.5f;
                params.matchConstraintDefaultHeight = ConstraintLayout.LayoutParams
                        .MATCH_CONSTRAINT_PERCENT;
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                mBinding.mapView.setLayoutParams(params);
            }
        });
        mBinding.btnSizeFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                mBinding.mapView.setLayoutParams(params);
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