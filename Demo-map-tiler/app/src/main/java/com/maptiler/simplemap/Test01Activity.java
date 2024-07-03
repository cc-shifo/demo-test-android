package com.maptiler.simplemap;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mapbox.geojson.utils.PolylineUtils;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Polygon;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.maptiler.simplemap.databinding.ActivityTest01Binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test01Activity extends AppCompatActivity {

    private ActivityTest01Binding mBinding;
    private MapboxMap mMap;
    private final String mStyleUrl =
            "https://api.maptiler.com/maps/hybrid/style.json?key=" + BuildConfig.mapTilerKey;
    private final String mStreet =
            "https://api.maptiler.com/maps/streets/style.json?key=" + BuildConfig.mapTilerKey;

    private List<LatLng> mLatLngList = new ArrayList<>();
    private List<PolylineOptions> mPolylineOptionsList = new ArrayList<>();
    private Polygon mPolygon;
    private Polyline mPolyline;

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
                // final String styleUrl = "https://api.maptiler.com/maps/hybrid/style.json?key="
                // + BuildConfig.mapTilerKey;
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

        final int BLUE_COLOR = Color.parseColor("#3bb2d0");
        final int FILL_BLUE_COLOR = Color.parseColor("#9A2196F3");
        mBinding.btnPolygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLatLngList.clear();
                double lat = 30.42491669227814;
                double lng = 114.41992218256276;
                // for (int i = 0; i <= 4; i++) {
                //     if (i % 4 == 1) {
                //         mLatLngList.add(new LatLng(lat - 0.0001 * i, lng + 0.0001 * i));
                //     } else if (i % 4 == 2) {
                //         mLatLngList.add(new LatLng(lat - 0.0004 * i, lng - 0.0004 * i));
                //     } else if (i % 4 == 3) {
                //         mLatLngList.add(new LatLng(lat - 0.0006 * i, lng + 0.0006 * i));
                //     } else {
                //         mLatLngList.add(new LatLng(lat + 0.0008 * i, lng + 0.0008 * i));
                //     }
                // }

                List<LatLng> l1 = new ArrayList<>(4);
                List<LatLng> l2 = new ArrayList<>(4);
                for (int i = 0; i < 8; i++) {
                    if (i % 2 == 0) {
                        l2.add(new LatLng(lat + 0.0005 * i, lng + 0.0005 * i));
                    } else {
                        l1.add(new LatLng(lat - 0.0005 * i, lng + 0.0005 * i));
                    }
                }
                mLatLngList.addAll(l1);
                Collections.reverse(l2);
                mLatLngList.addAll(l2);
                mLatLngList.add(l1.get(0));
                PolygonOptions options = new PolygonOptions();
                options.addAll(mLatLngList);
                options.strokeColor(BLUE_COLOR);// 边界线
                options.fillColor(FILL_BLUE_COLOR);
                options.alpha(0.4f);

                if (mMap != null) {
                    // 方法一
                    if (mPolygon != null) {
                        mMap.removePolygon(mPolygon);
                        mPolygon = null;
                    }
                    mPolygon = mMap.addPolygon(options);
                    // 方法二
                    // mMap.clear();
                    // mMap.addPolygon(options);
                }
            }
        });

        mBinding.btnPolyline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLatLngList.clear();
                mPolylineOptionsList.clear();
                double lat = 30.42491669227814;
                double lng = 114.41992218256276;
                PolylineOptions line = new PolylineOptions();
                for (int i = 0; i < 8; i++) {
                    if (i % 2 == 0) {
                        mLatLngList.add(new LatLng(lat + 0.0005 * i, lng + 0.0005 * i));
                    } else {
                        mLatLngList.add(new LatLng(lat - 0.0005 * i, lng + 0.0005 * i));
                    }
                }
                line.addAll(mLatLngList);
                line.color(BLUE_COLOR);
                line.width(4);
                // 方法一
                if (mPolyline != null) {
                    mMap.removePolyline(mPolyline);
                    mPolygon = null;
                }
                mPolyline = mMap.addPolyline(line);
                // 方法二
                // mMap.clear();
                // mMap.addPolyline(line);
            }
        });

        mBinding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.clear();
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