/*
 *  Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.demoarcgis;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.location.SimulatedLocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.navigation.RouteTracker;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.example.demoarcgis.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextToSpeech mTextToSpeech;
    private boolean mIsTextToSpeechInitialized = false;

    private SimulatedLocationDataSource mSimulatedLocationDataSource;

    // private MapView mMapView;
    private RouteTracker mRouteTracker;
    private Graphic mRouteAheadGraphic;
    private Graphic mRouteTraveledGraphic;
    private Button mRecenterButton;

    private MainActivityViewModel mViewModel;
    private ActivityMainBinding mBinding;
    private SpatialReference mMapSpatialReference;
    private GraphicsOverlay mTrackGraphicsOverlay;
    private SimpleRenderer mSimpleRenderer;
    private Graphic mTrackGraphic;
    private GraphicsOverlay mSceneTrackGraphicsOverlay;
    private SimpleRenderer mSceneSimpleRenderer;
    private Graphic mSceneTrackGraphic;

    /**
     * Creates a list of stops along a route.
     */
    private static List<Stop> getStops() {
        List<Stop> stops = new ArrayList<>(3);
        // San Diego Convention Center
        Stop conventionCenter = new Stop(new Point(-117.160386, 32.706608,
                SpatialReferences.getWgs84()));
        stops.add(conventionCenter);
        // USS San Diego Memorial
        Stop memorial = new Stop(new Point(-117.173034, 32.712327, SpatialReferences.getWgs84()));
        stops.add(memorial);
        // RH Fleet Aerospace Museum
        Stop aerospaceMuseum = new Stop(new Point(-117.147230, 32.730467,
                SpatialReferences.getWgs84()));
        stops.add(aerospaceMuseum);
        return stops;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && permissions.length > 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
            runTask();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(MainActivityViewModel.class);
        // setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        } else {
            runTask();
        }

        initBtn();
    }

    private void runTask() {
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY);

        // test1
        showBaseMap();
        initMapTrackGraphicOverlay();
        updateTrackOnViewMap();
        // test2
        // showNavigationTask();


        // test3
        showSceneMap();
        // test4
        initSceneTrackGraphicOverlay();
        updateTrackOnScene();


    }

    private void showBaseMap() {

        // get a reference to the map view
        // mMapView = findViewById(R.id.mapView);
        // create a map and set it to the map view
        ArcGISMap map = new ArcGISMap(Basemap.createImagery());
        // ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY);
        // mMapSpatialReference = map.getSpatialReference();
        //
        // Log.d(TAG, "getSpatialReference: "+ mMapSpatialReference.toString());
        mBinding.mapView.setMap(map);

        final Viewpoint viewpoint = new Viewpoint(30.457091, 114.398683, 10000);
        Log.d(TAG, "getLocationDisplay viewpoint: " + viewpoint.toString());
        mBinding.mapView.setViewpointAsync(viewpoint, 1);
        LocationDisplay display = mBinding.mapView.getLocationDisplay();
        display.setShowLocation(false);
        display.setShowAccuracy(false);
        display.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        display.addLocationChangedListener(locationChangedEvent -> {
            LocationDataSource.Location location = locationChangedEvent.getLocation();
            Log.d(TAG, "getLocationDisplay: " + location.getPosition());

        });
        display.startAsync();
    }

    private void showSceneMap() {
        ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
        mBinding.sceneView.setScene(scene);
        mBinding.sceneView.setViewpointCameraAsync(new Camera(30.457091, 114.398683, 1000, 0, 0,
                0));
    }

    private void observeTrack() {
        mViewModel.startTrack().observe(this, new Observer<TrackData>() {
            @Override
            public void onChanged(TrackData trackData) {

            }
        });

    }

    private void initMapTrackGraphicOverlay() {
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
                Color.BLUE, 5);

        // create simple renderer
        mSimpleRenderer = new SimpleRenderer(lineSymbol);
        // create graphic overlay for polyline
        mTrackGraphicsOverlay = new GraphicsOverlay();
// create graphic for polyline
        mTrackGraphic = new Graphic();

        // mTrackGraphicsOverlay.getGraphics().add(mTrackGraphic);
        // mBinding.mapView.getGraphicsOverlays().add(mTrackGraphicsOverlay);
    }

    private void updateTrackOnViewMap() {
        // create Graphic
        PolylineBuilder lineGeometry = new PolylineBuilder(SpatialReferences.getWebMercator());
        // SpatialReference spatialReference = mBinding.mapView.getSpatialReference();
        // **************************
        // NOTE **** return null ****
        // **************************
        // if (spatialReference != null) {
        //     Log.d(TAG, "drawTrackOnSceneMap: getWkid " + spatialReference.getWkid());
        //     Log.d(TAG, "drawTrackOnSceneMap: getWKText " + spatialReference.getWKText());
        //     Log.d(TAG, "drawTrackOnSceneMap: getUnit " + spatialReference.getUnit());
        //     Log.d(TAG, "drawTrackOnSceneMap: toString " + spatialReference.toString());
        // } else {
        //     Log.e(TAG, "updateTrackOnViewMap: spatialReference null");
        // }


        // // Create a geometry located in London, UK, with British National Grid spatial reference
        // Point britishNationalGridPt = new Point(538985.355, 177329.516, SpatialReference
        // .create(27700));
        // // Create a GeographicTransformation with a single step using WKID for
        // OSGB_1936_To_WGS_1984_NGA_7PAR transformation
        // GeographicTransformation transform = GeographicTransformation.create
        // (GeographicTransformationStep.create(108336));
        // // Project the point to WGS84, using the transformation
        // Point wgs84Pt = (Point) GeometryEngine.project(britishNationalGridPt,
        // SpatialReferences.getWgs84(), transform);
        // DatumTransformation wgs2Mercator = TransformationCatalog.getTransformation(
        //         SpatialReferences.getWgs84(), SpatialReferences.getWebMercator());


        Point point0 = (Point) GeometryEngine.project(new Point(-10e5, 40e5, SpatialReferences
                .getWebMercator()), SpatialReferences.getWgs84());
        Log.e(TAG, "updateTrackOnViewMap0: " + point0.toString());
        point0 = (Point) GeometryEngine.project(new Point(20e5, 50e5, SpatialReferences
                .getWebMercator()), SpatialReferences.getWgs84());
        Log.e(TAG, "updateTrackOnViewMap0: " + point0.toString());


        // Company latitude=30.456622, longitude=114.397293
        // x == longitude, y == latitude.

        // test data 30.45709540000 114.47904745400
        Point wgs84 = new Point(114.397293, 30.456622, SpatialReferences.getWgs84());
        Point point = (Point) GeometryEngine.project(wgs84, SpatialReferences.getWebMercator());
        Log.e(TAG, "updateTrackOnViewMap1: " + point.toString());
        lineGeometry.addPoint(point);

        // test data 30.45709550000 114.47904744700
        wgs84 = new Point(114.397293, 30.456682, SpatialReferences.getWgs84());
        point = (Point) GeometryEngine.project(wgs84, SpatialReferences.getWebMercator());
        Log.e(TAG, "updateTrackOnViewMap2: " + point.toString());
        lineGeometry.addPoint(point);

        wgs84 = new Point(114.397293, 30.456702, SpatialReferences.getWgs84());
        point = (Point) GeometryEngine.project(wgs84, SpatialReferences.getWebMercator());
        Log.e(TAG, "updateTrackOnViewMap2: " + point.toString());
        lineGeometry.addPoint(point);
        wgs84 = new Point(114.397293, 30.456722, SpatialReferences.getWgs84());
        point = (Point) GeometryEngine.project(wgs84, SpatialReferences.getWebMercator());
        Log.e(TAG, "updateTrackOnViewMap2: " + point.toString());
        lineGeometry.addPoint(point);
        wgs84 = new Point(114.397293, 30.456742, SpatialReferences.getWgs84());
        point = (Point) GeometryEngine.project(wgs84, SpatialReferences.getWebMercator());
        Log.e(TAG, "updateTrackOnViewMap2: " + point.toString());
        lineGeometry.addPoint(point);
        mTrackGraphic.setGeometry(lineGeometry.toGeometry());


        mBinding.mapView.getGraphicsOverlays().remove(mTrackGraphicsOverlay);
        // mTrackGraphicsOverlay.getGraphics().remove(mTrackGraphic);
        // add graphic to overlay
        // PolylineBuilder lineGeometry = new PolylineBuilder(SpatialReferences.getWebMercator());
        // lineGeometry.addPoint(new Point(30.45710321100, 114.47905690600, SpatialReferences
        // .getWgs84()));
        // lineGeometry.addPoint(new Point(30.45709544000, 114.47904744700, SpatialReferences
        // .getWgs84()));

        // add graphic to overlay
        mTrackGraphicsOverlay.setRenderer(mSimpleRenderer);
        mTrackGraphicsOverlay.getGraphics().add(mTrackGraphic);
        // add graphics overlay to the sceneView
        mBinding.mapView.getGraphicsOverlays().add(mTrackGraphicsOverlay);
    }

    private void initSceneTrackGraphicOverlay() {
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
                Color.BLUE, 5);
        // create simple renderer
        mSceneSimpleRenderer = new SimpleRenderer(lineSymbol);

        // create graphic overlay for polyline
        mSceneTrackGraphicsOverlay = new GraphicsOverlay();

        // create graphic for polyline
        mSceneTrackGraphic = new Graphic();
    }

    private void updateTrackOnScene() {
        // create Graphic
        PolylineBuilder lineGeometry = new PolylineBuilder(SpatialReferences.getWebMercator());
        // SpatialReference spatialReference = mBinding.mapView.getSpatialReference();
        // **************************
        // NOTE **** return null ****
        // **************************
        // if (spatialReference != null) {
        //     Log.d(TAG, "drawTrackOnSceneMap: getWkid " + spatialReference.getWkid());
        //     Log.d(TAG, "drawTrackOnSceneMap: getWKText " + spatialReference.getWKText());
        //     Log.d(TAG, "drawTrackOnSceneMap: getUnit " + spatialReference.getUnit());
        //     Log.d(TAG, "drawTrackOnSceneMap: toString " + spatialReference.toString());
        // } else {
        //     Log.e(TAG, "updateTrackOnViewMap: spatialReference null");
        // }


        // // Create a geometry located in London, UK, with British National Grid spatial reference
        // Point britishNationalGridPt = new Point(538985.355, 177329.516, SpatialReference
        // .create(27700));
        // // Create a GeographicTransformation with a single step using WKID for
        // OSGB_1936_To_WGS_1984_NGA_7PAR transformation
        // GeographicTransformation transform = GeographicTransformation.create
        // (GeographicTransformationStep.create(108336));
        // // Project the point to WGS84, using the transformation
        // Point wgs84Pt = (Point) GeometryEngine.project(britishNationalGridPt,
        // SpatialReferences.getWgs84(), transform);
        // DatumTransformation wgs2Mercator = TransformationCatalog.getTransformation(
        //         SpatialReferences.getWgs84(), SpatialReferences.getWebMercator());


        //  use PointCollection PointBuilder

        Point point0 = (Point) GeometryEngine.project(new Point(-10e5, 40e5, SpatialReferences
                .getWebMercator()), SpatialReferences.getWgs84());
        Log.e(TAG, "updateTrackOnViewMap0: " + point0.toString());
        point0 = (Point) GeometryEngine.project(new Point(20e5, 50e5, SpatialReferences
                .getWebMercator()), SpatialReferences.getWgs84());
        Log.e(TAG, "updateTrackOnViewMap0: " + point0.toString());


        // Company latitude=30.456622, longitude=114.397293
        // x == longitude, y == latitude.

        // test data 30.45709540000 114.47904745400
        // test data 30.456622 114.397293
        Point wgs84 = new Point(114.397293, 30.456622, SpatialReferences.getWgs84());
        Point point = (Point) GeometryEngine.project(wgs84, SpatialReferences.getWebMercator());
        Log.e(TAG, "updateTrackOnViewMap1: " + point.toString());
        lineGeometry.addPoint(point);

        // test data 30.45709550000 114.47904744700
        // test data 30.456682 114.397293
        wgs84 = new Point(114.397293, 30.456682, SpatialReferences.getWgs84());
        point = (Point) GeometryEngine.project(wgs84, SpatialReferences.getWebMercator());
        Log.e(TAG, "updateTrackOnViewMap2: " + point.toString());
        lineGeometry.addPoint(point);

        // test data 30.456702 114.397293
        wgs84 = new Point(114.397293, 30.456702, SpatialReferences.getWgs84());
        point = (Point) GeometryEngine.project(wgs84, SpatialReferences.getWebMercator());
        Log.e(TAG, "updateTrackOnViewMap2: " + point.toString());
        lineGeometry.addPoint(point);
        // test data 30.456722 114.397293
        wgs84 = new Point(114.397293, 30.456722, SpatialReferences.getWgs84());
        point = (Point) GeometryEngine.project(wgs84, SpatialReferences.getWebMercator());
        Log.e(TAG, "updateTrackOnViewMap2: " + point.toString());
        lineGeometry.addPoint(point);
        // test data 30.456742 114.397293
        wgs84 = new Point(114.397293, 30.456742, SpatialReferences.getWgs84());
        point = (Point) GeometryEngine.project(wgs84, SpatialReferences.getWebMercator());
        Log.e(TAG, "updateTrackOnViewMap2: " + point.toString());
        lineGeometry.addPoint(point);
        mSceneTrackGraphic.setGeometry(lineGeometry.toGeometry());

        mBinding.sceneView.getGraphicsOverlays().remove(mSceneTrackGraphicsOverlay);
        // mTrackGraphicsOverlay.getGraphics().remove(mTrackGraphic);
        // add graphic to overlay
        // PolylineBuilder lineGeometry = new PolylineBuilder(SpatialReferences.getWebMercator());
        // lineGeometry.addPoint(new Point(30.45710321100, 114.47905690600, SpatialReferences
        // .getWgs84()));
        // lineGeometry.addPoint(new Point(30.45709544000, 114.47904744700, SpatialReferences
        // .getWgs84()));

        mSceneTrackGraphicsOverlay.setRenderer(mSceneSimpleRenderer);
        // add graphic to overlay
        mSceneTrackGraphicsOverlay.getGraphics().add(mSceneTrackGraphic);
        // add graphics overlay to the sceneView
        mBinding.sceneView.getGraphicsOverlays().add(mSceneTrackGraphicsOverlay);
    }

    private void initBtn() {
        mViewModel.observeReadStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                String v = Boolean.TRUE.equals(aBoolean) ? getString(R.string.successful)
                        : getString(R.string.failed);
                mBinding.navigationControls.timeRemainingTextView.setText(v);
                mBinding.navigationControls.readBtn.setEnabled(true);
            }
        });
        mViewModel.observeWriteStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                String v = Boolean.TRUE.equals(aBoolean) ? getString(R.string.successful)
                        : getString(R.string.failed);
                mBinding.navigationControls.timeRemainingTextView.setText(v);
                mBinding.navigationControls.writeBtn.setEnabled(true);
            }
        });

        mBinding.navigationControls.readBtn.setOnClickListener(v -> {
            mBinding.navigationControls.readBtn.setEnabled(false);
            mViewModel.textToBinaryTest();
        });

        mBinding.navigationControls.writeBtn.setOnClickListener(v -> {
            mBinding.navigationControls.writeBtn.setEnabled(false);
            mViewModel.binaryToTextTest();
        });
    }

    @Override
    protected void onPause() {
        mBinding.mapView.pause();
        mBinding.sceneView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.mapView.resume();
        mBinding.sceneView.resume();
    }

    @Override
    protected void onDestroy() {
        mBinding.mapView.dispose();
        mBinding.sceneView.dispose();
        super.onDestroy();
        mViewModel.destroy();
    }
}
