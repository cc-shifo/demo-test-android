package com.maptiler.simplemap

import android.app.ActivityManager
import android.content.Intent
import android.content.pm.ConfigurationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap


class MainActivity : AppCompatActivity() {

    private var mapView: MapView? = null
    private var mHelloWorld: TextView? = null
    private lateinit var mMaplibreMap: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapTilerKey = getMapTilerKey()
        validateKey(mapTilerKey)
        // val styleUrl = "https://api.maptiler.com/maps/streets/style.json?key=${mapTilerKey}";
        // val styleUrl = "https://api.maptiler.com/maps/satellite/style.json?key=${mapTilerKey}";
        val styleUrl = "https://api.maptiler.com/maps/hybrid/style.json?key=${mapTilerKey}";

        // Get the MapBox context
        Mapbox.getInstance(this, null)

        // Set the map view layout
        setContentView(R.layout.activity_main)

        // Create map view
        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { map ->
            mMaplibreMap = map

            // Set the style after mapView was loaded
            map.setStyle(styleUrl) {
                map.uiSettings.setAttributionMargins(15, 0, 0, 15)
                // Set the map view center
                map.cameraPosition = CameraPosition.Builder()
                    // .target(LatLng(47.127757, 8.579139))
                    .target(LatLng(30.42491669227814, 114.41992218256276))
                    .zoom(14.0)
                    .build()
                addMarker()
            }
            map.uiSettings.isLogoEnabled = false
            map.uiSettings.isAttributionEnabled = false
        }


        val am: ActivityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val cf: ConfigurationInfo = am.deviceConfigurationInfo
        mHelloWorld = findViewById(R.id.hello_world)
        mHelloWorld?.apply { text = "glEs: " + cf.glEsVersion }

        val btnActivity001: Button = findViewById(R.id.btn_activity_001)
        btnActivity001?.setOnClickListener {
            val intent: Intent = Intent(this, Test01Activity::class.java)
            startActivity(intent)
        }

    }

    private fun getMapTilerKey(): String? {
        return packageManager.getApplicationInfo(
            packageName,
            PackageManager.GET_META_DATA
        ).metaData.getString("com.maptiler.simplemap.mapTilerKey")
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    private fun validateKey(mapTilerKey: String?) {
        if (mapTilerKey == null) {
            throw Exception("Failed to read MapTiler key from info.plist")
        }
        if (mapTilerKey.toLowerCase() == "placeholder") {
            throw Exception("Please enter correct MapTiler key in module-level gradle.build file in defaultConfig section")
        }
    }

    private fun addMarker() {
        val icon = IconFactory.getInstance(this)
            .fromResource(R.drawable.test)
        val latLng = LatLng(30.42491669227814, 114.41992218256276)
        // Use MarkerOptions and addMarker() to add a new marker in map
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title("dateString")
            .snippet("snippet")
            .icon(icon)
        mMaplibreMap?.addMarker(markerOptions)
    }
}
