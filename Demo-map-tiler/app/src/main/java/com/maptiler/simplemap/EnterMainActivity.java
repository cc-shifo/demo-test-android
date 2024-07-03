package com.maptiler.simplemap;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.maptiler.simplemap.databinding.ActivityTest01Binding;

public class EnterMainActivity extends AppCompatActivity {
    private ActivityTest01Binding mBinding;
    private MapView mapView;
    private String mStyleUrl = "https://api.maptiler.com/maps/hybrid/style.json?key=";

    private TextView mHelloWorld;
    private MapboxMap mMaplibreMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStyleUrl = mStyleUrl + getMapTilerKey();

        // Get the MapBox context
        Mapbox.getInstance(this);

        mBinding = ActivityTest01Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        // setContentView(R.layout.activity_enter_main);

        // val styleUrl = "https://api.maptiler.com/maps/streets/style.json?key=${mapTilerKey}";
        // val styleUrl = "https://api.maptiler.com/maps/satellite/style.json?key=${mapTilerKey}";



        // Set the map view layout
        setContentView(R.layout.activity_enter_main);

        // Create map view
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap map) {
                mMaplibreMap = map;
                // Set the style after mapView was loaded
                // map.setStyle(styleUrl, new Style.OnStyleLoaded() {
                //     @Override
                //     public void onStyleLoaded(@NonNull Style style) {
                //
                //     }
                // });
                map.setStyle(new Style.Builder().fromUri(mStyleUrl));
                map.getUiSettings().setAttributionEnabled(false);
                map.getUiSettings().setLogoEnabled(false);
                // map.getUiSettings().setAttributionMargins(15, 0, 0, 15);
                // Set the map view center
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        // .target(LatLng(47.127757, 8.579139))
                        .target(new LatLng(30.42491669227814, 114.41992218256276))
                        .zoom(14.0)
                        .build();
                map.setCameraPosition(cameraPosition);
                addMarker();
            }
        });



        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo cf = am.getDeviceConfigurationInfo();
        mHelloWorld = findViewById(R.id.hello_world);
        mHelloWorld.setText("glEs: " + cf.getGlEsVersion());

        Button btnActivity001 = findViewById(R.id.btn_activity_001);
        btnActivity001.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterMainActivity.this, Test01Activity.class);
                startActivity(intent);
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
        try {
            return getPackageManager().getApplicationInfo(getPackageName(), PackageManager
                    .GET_META_DATA).metaData.getString("com.maptiler.simplemap.mapTilerKey");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void addMarker() {
        Icon icon = IconFactory.getInstance(this)
                .fromResource(R.drawable.test);
        LatLng latLng = new LatLng(30.42491669227814, 114.41992218256276);
        // Use MarkerOptions and addMarker() to add a new marker in map
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("dateString")
                .snippet("snippet")
                .icon(icon);
        mMaplibreMap.addMarker(markerOptions);
    }
}