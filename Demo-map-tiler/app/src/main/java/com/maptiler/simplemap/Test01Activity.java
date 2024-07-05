package com.maptiler.simplemap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.InfoWindow;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polygon;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.maptiler.simplemap.databinding.ActivityTest01Binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test01Activity extends AppCompatActivity {

    private ActivityTest01Binding mBinding;
    private MapboxMap mMap;
    private Style mStyle;
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
                // 避免之前Activity下map的InfoWindow没有关闭，再进入到当前Activity时出现InfoWindow null的
                // 崩溃现象。
                mapboxMap.clear();
                initMapEle(mapboxMap);
            }
        });
        mBinding.mapView.addOnDidFinishLoadingStyleListener(
                new MapView.OnDidFinishLoadingStyleListener() {
                    @Override
                    public void onDidFinishLoadingStyle() {
                        addHeadingIcon();
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

        mBinding.btnRotateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeading = mHeading + 5f;
                if (mHeading >= 360f) {
                    mHeading = mHeading - 360f;
                }
                mDeltaLat = mDeltaLat + 0.001f;
                btnRotate();
            }
        });


        // touch event
        // 1.MapView#onTouchEvent
        // 2.MapGestureDetector#onTouchEvent mapGestureDetector
        // 3.AndroidGesturesManager#onTouchEvent gesturesManager; || super#onTouchEvent from
        // View#TouchEvent
        // 4.all BaseGesture#onTouchEvent
        //  RotateGestureDetector, StandardScaleGestureDetector
        //  ShoveGestureDetector, SidewaysShoveGestureDetector
        //  MultiFingerTapGestureDetector, MoveGestureDetector
        //  StandardGestureDetector
        // 5.BaseGesture#analyze
        // 6.BaseGesture#analyzeEvent
        //  all RotateGestureDetector#analyzeEvent, StandardScaleGestureDetector#analyzeEvent,
        //  ShoveGestureDetector#analyzeEvent, SidewaysShoveGestureDetector#analyzeEvent,
        //  MultiFingerTapGestureDetector#analyzeEvent, MoveGestureDetector#analyzeEvent,
        //  StandardGestureDetector#analyzeEvent, MoveGestureDetector#analyzeEvent,
        // 7. e.g StandardGestureDetector#analyzeEvent
        //  StandardGestureDetector#gestureDetector.onTouchEvent(motionEvent)
        //  BaseGesture#listener.
        //  MapGestureDetector#StandardGestureDetector#listener. from
        //  MapGestureDetector#initializeGestureListeners.
        //  from gesturesManager#setStandardGestureListener(standardGestureListener),
        //  from gesturesManager#setMoveGestureListener(moveGestureListener),
        //  from gesturesManager#setStandardScaleGestureListener(scaleGestureListener),
        //  from gesturesManager#setRotateGestureListener(setRotateGestureListener),
        //  from gesturesManager#setShoveGestureListener(shoveGestureListener),
        //  from gesturesManager#setMultiFingerTapGestureListener(tapGestureListener).

        //  from AndroidGesturesManager#setStandardGestureListener(standardGestureListener),
        //  from AndroidGesturesManager#setMoveGestureListener(moveGestureListener),
        //  from AndroidGesturesManager#setStandardScaleGestureListener(scaleGestureListener),
        //  from AndroidGesturesManager#setRotateGestureListener(setRotateGestureListener),
        //  from AndroidGesturesManager#setShoveGestureListener(shoveGestureListener),
        //  from AndroidGesturesManager#setMultiFingerTapGestureListener(tapGestureListener),
        //  listener#onSingleTapConfirmed
        //  MapGestureDetector#StandardGestureListener#onSingleTapConfirmed
        //  MapGestureDetector#StandardGestureListener#notifyOnMapClickListeners(LatLng)

        // 8. e.g MoveGestureDetector#analyzeEvent
        //  MoveGestureListener#onMoveBegin
        //  MoveGestureListener#onMove
        //  MoveGestureListener#onMoveEnd#notifyOnMoveEndListeners


        // mMap.getProjection().toScreenLocation()
        // DraggableMarkerActivity.kt查看MoveGestureDetector#getMoveObject获取到按下时候的x,y
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


    private void initMapEle(@NonNull MapboxMap mapboxMap) {
        // final String styleUrl = "https://api.maptiler.com/maps/hybrid/style.json?key="
        // + BuildConfig.mapTilerKey;
        mMap = mapboxMap;
        mMap.setStyle(mStyleUrl, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mStyle = style;
            }
        });
        moveToCurrentLocation();
        addMarker();
        addClickListen();
    }

    private void moveToCurrentLocation() {
        // mMap.getUiSettings().setLogoEnabled(false);
        // mMap.getUiSettings().setAttributionEnabled(false);
        CameraPosition cm = new CameraPosition.Builder()
                // .target(LatLng(47.127757, 8.579139))
                .target(new LatLng(30.42491669227814, 114.41992218256276))
                .zoom(14.0)
                .build();
        mMap.setCameraPosition(cm);
    }

    private LatLng mMarkerLatLng;
    private LatLng mMarkerLatLng2;
    private Marker mMarker;
    private Marker mMarker2;

    private void addMarker() {
        mMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                LatLng latLng = marker.getPosition();
                PointF pointF = mMap.getProjection().toScreenLocation(latLng);
                mTextBlackboard.setLength(0);
                mTextBlackboard.append("onMapClick: marker id=").append(marker.getId())
                        .append(", latLng=")
                        .append(latLng)
                        .append(", marker x=").append(pointF.x).append(",y=").append(pointF.y)
                        .append("\n");
                mBinding.tvMessageBlackboard.setText(mTextBlackboard.toString());
                return true; // 返回true消耗掉事件，将不显示窗口
            }
        });
        // MapGestureDetector#StandardGestureListener#onSingleTapConfirmed
        // annotationManager.onTap(tapPoint)
        // AnnotationManager#onTap(tapPoint)
        // AnnotationManager#isClickHandledForMarker(markerId)
        // handledDefaultClick = AnnotationManager#onClickMarker() // false就显示点击后的窗口
        // AnnotationManager#toggleMarkerSelectionState(marker) // 显示点击后的窗口
        // AnnotationManager#selectMarker(marker) // 显示点击后的窗口
        // AnnotationManager#infoWindowManager.add(marker.showInfoWindow(mapboxMap, mapView))
        // Marker#showInfoWindow(mapboxMap, mapView)
        // Marker#showInfoWindow(infoWindow, mapView) //
        // Marker#MapboxMap.InfoWindowAdapter infoWindowAdapter = getMapboxMap()
        // .getInfoWindowAdapter() // 获取自定义窗口适配。这种适配器可以自定义窗口。
        // 如果没有创建这种适配器，就获取默认的窗口，即maplibre_infowindow_content.xml。
        // Marker#infoWindow = new InfoWindow(content, mapboxMap) // 获取默认窗口 InfoWindow
        // Marker#iw.open(mapView, this, getPosition(), rightOffsetPixels, topOffsetPixels)
        // Marker#mapView.addView(view, lp) // 将窗口添加到mapView上，显示。

        addTestMarkers();



    }

    private SymbolLayer mMarkerLayer;
    private List<Feature> mFeatureList = new ArrayList<>();
    private void addSymbolMarkers() {
        // FeatureCollection featureCollection = FeatureCollection.fromFeatures(mFeatureList);
        // featureCollection.features().addAll()
        // Feature.fromGeometry(Point.fromLngLat(30.42491669227814, 114.41992218256276));

        Bitmap dotIcon = BitmapFactory.decodeResource(
                getResources(), R.drawable.test_dot);
        mStyle.addImage("DOT_MARKER_ICON_ID", dotIcon);


        for (int i = 1; i < 5; i++) {
            // LatLng latLng = new LatLng(30.42491669227814 + 0.001 * (i + 1), 114.41992218256276);
            Feature feature = Feature.fromGeometry(Point.fromLngLat(114.41992218256276,
                    30.42491669227814 + 0.001 * (i + 1)));
            mFeatureList.add(feature);
            // op.getMarker().setIcon(); // 更新图标
            // op.getMarker().setPosition(); // 更新位置
        }
        GeoJsonSource source = new GeoJsonSource("DOT_GEOJSON_SOURCE_ID",
                FeatureCollection.fromFeatures(mFeatureList));

        mHeadingSymbolLayer = new SymbolLayer("DOT_LAYER_ID", "DOT_GEOJSON_SOURCE_ID")
                .withProperties(
                        PropertyFactory.iconImage("DOT_MARKER_ICON_ID"),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true)
                               );
        mStyle.addLayer(mHeadingSymbolLayer);
    }

    private void addTestMarkers() {
        Icon icon = IconFactory.getInstance(this)
                .fromResource(R.drawable.test_cat);
        mMarkerLatLng = new LatLng(30.42491669227814, 114.41992218256276);
        // Use MarkerOptions and addMarker() to add a new marker in map
        MarkerOptions options = new MarkerOptions()
                .position(mMarkerLatLng)
                .title("dateString")
                .snippet("snippet")
                .icon(icon);
        mMarker = mMap.addMarker(options);
        InfoWindow infoWindow = mMarker.getInfoWindow();
        // .getView().setClickable(false);
        // maplibre_infowindow_content.xml // 点击后显示的窗口

        Icon icon2 = IconFactory.getInstance(this)
                .fromResource(R.drawable.test);
        mMarkerLatLng2 = new LatLng(30.42121669227814, 114.41932218256276);
        // Use MarkerOptions and addMarker() to add a new marker in map
        MarkerOptions options2 = new MarkerOptions()
                .position(mMarkerLatLng2)
                .title("dateString")
                .snippet("snippet")
                .icon(icon2);
        mMarker2 = mMap.addMarker(options2);


        Icon iconDot = IconFactory.getInstance(this)
                .fromResource(R.drawable.test_dot);
        List<MarkerOptions> markerOptionsList = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            LatLng latLng = new LatLng(30.42491669227814 + 0.001 * (i + 1), 114.41992218256276);
            MarkerOptions op = new MarkerOptions()
                    .position(latLng)
                    .title("dateString" + i)
                    .snippet("snippet" + i)
                    .icon(iconDot);
            markerOptionsList.add(op);
            // op.getMarker().setIcon(); // 更新图标
            // op.getMarker().setPosition(); // 更新位置
        }
        Icon iconTest00 = IconFactory.getInstance(this)
                .fromResource(R.drawable.test_01);
        LatLng latLngTest00 = new LatLng(30.42491669227814, 114.41932218256276 + 0.002);
        MarkerOptions opTest00 = new MarkerOptions()
                .position(latLngTest00)
                .title("test01")
                .snippet("snippet test01")
                .icon(iconTest00);
        markerOptionsList.add(opTest00);
        iconTest00 = IconFactory.getInstance(this)
                .fromResource(R.drawable.test_02);
        latLngTest00 = new LatLng(30.42491669227814, 114.41932218256276 + 0.003);
        opTest00 = new MarkerOptions()
                .position(latLngTest00)
                .title("test02")
                .snippet("snippet test02")
                .icon(iconTest00);
        markerOptionsList.add(opTest00);
        iconTest00 = IconFactory.getInstance(this)
                .fromResource(R.drawable.test_03);
        latLngTest00 = new LatLng(30.42491669227814, 114.41932218256276 + 0.004);
        opTest00 = new MarkerOptions()
                .position(latLngTest00)
                .title("test03")
                .snippet("snippet test03")
                .icon(iconTest00);
        markerOptionsList.add(opTest00);
        iconTest00 = IconFactory.getInstance(this)
                .fromResource(R.drawable.test_04);
        latLngTest00 = new LatLng(30.42491669227814, 114.41932218256276 + 0.005);
        opTest00 = new MarkerOptions()
                .position(latLngTest00)
                .title("test04")
                .snippet("snippet test04")
                .icon(iconTest00);
        markerOptionsList.add(opTest00);
        mMap.addMarkers(markerOptionsList);
    }

    private StringBuilder mTextBlackboard = new StringBuilder();

    private void addClickListen() {
        mMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull LatLng point) {

                boolean result = false;
                mTextBlackboard.setLength(0);
                PointF scr = mMap.getProjection().toScreenLocation(point);
                List<Feature> features = mMap.queryRenderedFeatures(scr);
                mTextBlackboard.append("onMapClick: ").append(point.toString()).append("\n")
                        .append("on screen x=").append(scr.x).append(",y=").append(scr.y)
                        .append("\n").append("The marker is latLng=");
                if (mMarkerLatLng != null) {
                    // int distance = (deltaX * deltaX) + (deltaY * deltaY);
                    // if (distance > mTouchSlopSquare)
                    PointF mrk = mMap.getProjection().toScreenLocation(mMarkerLatLng);
                    mTextBlackboard.append(mMarkerLatLng.toString())
                            .append(" on screen x=").append(mrk.x).append(",y=").append(mrk.y)
                            .append("\n");
                    final ViewConfiguration configuration = ViewConfiguration.get(
                            Test01Activity.this);
                    final int touchSlop = configuration.getScaledTouchSlop();
                    if (Math.abs(mrk.x - scr.x) < touchSlop && Math.abs(mrk.y - scr.y) <
                            touchSlop) {
                        mTextBlackboard.append("*** marker clicked ***").append("\n");
                        result = true;
                    }
                } else {
                    mTextBlackboard.append("null").append("\n");
                }

                if (mMarker2 != null) {
                    mMarker2.setPosition(point);
                }
                mBinding.tvMessageBlackboard.setText(mTextBlackboard.toString());
                return result;
            }
        });
    }


    private void addHeadingIcon() {
        if (mStyle != null) {
            Bitmap compassNeedleSymbolLayerIcon = BitmapFactory.decodeResource(
                    getResources(), R.drawable.test_composs);
            mStyle.addImage("AIRCRAFT_MARKER_ICON_ID", compassNeedleSymbolLayerIcon);

            // 方法一，无初始化方向角。
            GeoJsonSource source = new GeoJsonSource("GEOJSON_SOURCE_ID",
                    Feature.fromGeometry(Point.fromLngLat(114.41992218256276 - 0.03,
                            30.42491669227814 - 0.03)));
            mStyle.addSource(source);


            // 方法二, 初始化方向角45
            // JsonObject jsonObject = new JsonObject();
            // jsonObject.addProperty(PROPERTY_BEARING, 45f);
            // Feature feature = Feature.fromGeometry(Point.fromLngLat(114.41992218256276 - 0.03,
            //         30.42491669227814 - 0.03), jsonObject);
            // GeoJsonSource source = new GeoJsonSource("GEOJSON_SOURCE_ID", feature);
            // mStyle.addSource(source);

            mHeadingSymbolLayer = new SymbolLayer("AIRCRAFT_LAYER_ID", "GEOJSON_SOURCE_ID")
                    .withProperties(
                            PropertyFactory.iconImage("AIRCRAFT_MARKER_ICON_ID"),
                            // PropertyFactory.iconRotate((float) 45.0),/* 初始化角，也可以用*/
                            // PropertyFactory.iconRotate(Expression.get(PROPERTY_BEARING)),/* 初始化角，也可以用*/
                            PropertyFactory.iconIgnorePlacement(true),
                            PropertyFactory.iconAllowOverlap(true)
                                   );
            mStyle.addLayer(mHeadingSymbolLayer);
        }
    }

    private SymbolLayer mHeadingSymbolLayer;
    private float mHeading;
    private float mDeltaLat = 0.001f;
    private static final String PROPERTY_BEARING = "bearing";

    private void btnRotate() {
        // 方法一
        GeoJsonSource source = mStyle.getSourceAs("GEOJSON_SOURCE_ID");
        if (source != null && mHeadingSymbolLayer != null) {
            source.setGeoJson(Feature.fromGeometry(
                    Point.fromLngLat(114.41992218256276 - mDeltaLat, 30.42491669227814 -
                    mDeltaLat)));
            mHeadingSymbolLayer.setProperties(PropertyFactory.iconRotate(mHeading));
        }
    }
}