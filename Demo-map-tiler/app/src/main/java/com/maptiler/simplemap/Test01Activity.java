package com.maptiler.simplemap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LongSparseArray;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Annotation;
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
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.engine.LocationEngine;
import com.mapbox.mapboxsdk.location.engine.LocationEngineCallback;
import com.mapbox.mapboxsdk.location.engine.LocationEngineDefault;
import com.mapbox.mapboxsdk.location.engine.LocationEngineRequest;
import com.mapbox.mapboxsdk.location.engine.LocationEngineResult;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.maptiler.simplemap.databinding.ActivityTest01Binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Test01Activity extends AppCompatActivity {
    private static final String TAG = "Test01Activity";
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
                initMapEle(mapboxMap, mStyleUrl);
            }
        });
        mBinding.mapView.addOnCanRemoveUnusedStyleImageListener(
                new MapView.OnCanRemoveUnusedStyleImageListener() {
                    @Override
                    public boolean onCanRemoveUnusedStyleImage(@NonNull String id) {
                        Log.d(TAG, "onCanRemoveUnusedStyleImage: " + id);
                        return false;
                    }
                });
        mBinding.mapView.addOnStyleImageMissingListener(new MapView.OnStyleImageMissingListener() {
            @Override
            public void onStyleImageMissing(@NonNull String id) {
                Log.d(TAG, "onStyleImageMissing: " + id);
            }
        });
        mBinding.mapView.addOnSourceChangedListener(new MapView.OnSourceChangedListener() {
            @Override
            public void onSourceChangedListener(String id) {
                Log.d(TAG, "onSourceChangedListener: " + id);
            }
        });
        mBinding.mapView.addOnDidFinishLoadingStyleListener(() -> {
            // This above code may not work sometimes when you add the marker in the
            // onMapReady() callback. Because onMapReady() is called before all
            // styles are loaded. Hence add the marker in
            // addOnDidFinishLoadingStyleListener() callback.
            // https://stackoverflow.com/questions/53517370/rotate-and-change-position-for-markers-in-latest-mapbox-sdk-6-7/54124090#54124090
            Log.d(TAG, "onDidFinishLoadingStyle: onStyleLoaded: " +
                    mMap.getStyle().toString());
            // 此回调存在多次被调用的情况。调用顺序如下
            // 2024-07-05 19:30:23.106 19206-19206/com.maptiler.simplemap
            // E/Mbgl-MapChangeReceiver: Exception in onDidFinishLoadingStyle
            // java.lang.IllegalStateException: Calling removeImage when a newer
            // style is loading/has loaded.
            // at com.mapbox.mapboxsdk.maps.Style.validateState(Style.java:776)
            // at com.mapbox.mapboxsdk.maps.Style.removeImage(Style.java:614)
            // at com.maptiler.simplemap.Test01Activity.clearStyle(Test01Activity
            // .java:755)
            // at com.maptiler.simplemap.Test01Activity.access$200(Test01Activity
            // .java:49)
            // at com.maptiler.simplemap.Test01Activity$7$1$1.onStyleLoaded
            // (Test01Activity.java:151)
            // at com.mapbox.mapboxsdk.maps.MapboxMap.notifyStyleLoaded(MapboxMap
            // .java:962)
            // at com.mapbox.mapboxsdk.maps.MapboxMap.onFinishLoadingStyle(MapboxMap
            // .java:224)
            // at com.mapbox.mapboxsdk.maps.MapView$MapCallback
            // .onDidFinishLoadingStyle(MapView.java:1346)
            // at com.mapbox.mapboxsdk.maps.MapChangeReceiver.onDidFinishLoadingStyle
            // (MapChangeReceiver.java:198)
            // at com.mapbox.mapboxsdk.maps.NativeMapView.onDidFinishLoadingStyle
            // (NativeMapView.java:1124)
            // at android.os.MessageQueue.nativePollOnce(Native Method)
            // at android.os.MessageQueue.next(MessageQueue.java:336)
            // at android.os.Looper.loop(Looper.java:174)
            // at android.app.ActivityThread.main(ActivityThread.java:7386)
            // at java.lang.reflect.Method.invoke(Native Method)
            // at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run
            // (RuntimeInit.java:492)
        });

        mBinding.btnSatelliteHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull MapboxMap mapboxMap) {
                        mMap.setStyle(mStyleUrl, new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                Log.d(TAG, "btnSatelliteHybrid onStyleLoaded: " + style.toString());
                                // clearStyle();
                                mStyle = style;
                                addSymbolMarkers();
                                if (mmHeadingSymbolLayerInitiated) {
                                    addHeadingIcon();
                                }
                            }
                        });
                    }
                });
            }
        });

        mBinding.btnStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull MapboxMap mapboxMap) {
                        mMap.setStyle(mStreet, new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                Log.d(TAG, "btnStreet onStyleLoaded: " + style.toString());
                                mStyle = style;
                                addSymbolMarkers();
                                if (mmHeadingSymbolLayerInitiated) {
                                    addHeadingIcon();
                                }
                            }
                        });
                    }
                });
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

        mBinding.btnRotateAircraftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //避免快速点击多次
                mHeading = mHeading + 5f;
                if (mHeading >= 360f) {
                    mHeading = mHeading - 360f;
                }
                mDeltaLat = mDeltaLat + 0.001f;
                if (!mmHeadingSymbolLayerInitiated) {
                    addHeadingIcon();
                    mmHeadingSymbolLayerInitiated = true;
                }
                btnRotate();
            }
        });
        mBinding.btnDeleteAircraftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remoteHeadingIcon();
                mmHeadingSymbolLayerInitiated = false;
            }
        });

        btnAddOneMarker();
        btnRemoveOneMarker();
        btnGetAddress();


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
        testStartLocation();
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
        testStopLocation();
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


    private void initMapEle(@NonNull MapboxMap mapboxMap, @NonNull String style) {
        // final String styleUrl = "https://api.maptiler.com/maps/hybrid/style.json?key="
        // + BuildConfig.mapTilerKey;
        mMap = mapboxMap;
        // mMap.getUiSettings().setLogoEnabled(false); // set in xml
        // mMap.getUiSettings().setAttributionEnabled(false);  // set in xml
        mMap.setStyle(style, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                Log.d(TAG, "onStyleLoaded: " + style.toString());
                mStyle = style;
                addSymbolMarkers();
                if (mmHeadingSymbolLayerInitiated) {
                    addHeadingIcon();
                }
            }
        });


        moveToCurrentLocation();
        addClickListen();
        addMarker();
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
        testOnMarkerClicked();
        addTestMarkers();
    }


    private void testOnMarkerClicked() {
        mMap.setOnMarkerClickListener(marker -> {
            mClickedId = marker.getId();
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
    }

    private SymbolLayer mMarkerLayer;
    private List<Feature> mFeatureList = new ArrayList<>();

    private void addSymbolMarkers() {
        // FeatureCollection featureCollection = FeatureCollection.fromFeatures(mFeatureList);
        // featureCollection.features().addAll()
        // Feature.fromGeometry(Point.fromLngLat(30.42491669227814, 114.41992218256276));


        Bitmap dotIcon = BitmapFactory.decodeResource(
                getResources(), R.drawable.test_plus_icon);
        mStyle.addImage("PLUS_MARKER_ICON_ID", dotIcon);


        for (int i = 1; i < 5; i++) {
            // LatLng latLng = new LatLng(30.42491669227814 + 0.001 * (i + 1), 114.41992218256276);
            Feature feature = Feature.fromGeometry(
                    Point.fromLngLat(114.41992218256276 - 0.001 * (i + 1),
                            30.42491669227814 - 0.001 * (i + 1)));
            mFeatureList.add(feature);
            // op.getMarker().setIcon(); // 更新图标
            // op.getMarker().setPosition(); // 更新位置
        }
        GeoJsonSource source = new GeoJsonSource("PLUS_GEOJSON_SOURCE_ID",
                FeatureCollection.fromFeatures(mFeatureList));
        mStyle.addSource(source);
        mMarkerLayer = new SymbolLayer("PLUS_LAYER_ID", "PLUS_GEOJSON_SOURCE_ID")
                .withProperties(
                        PropertyFactory.textField("hello world"),
                        PropertyFactory.iconImage("PLUS_MARKER_ICON_ID"),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true));
        mStyle.addLayer(mMarkerLayer);

    }

    private void addTestMarkers() {
        addCustomInfoWindowAdapter(mMap); // 自定义提示框
        mMap.getUiSettings().setDeselectMarkersOnTap(false); // false 点击地图后，不自动关闭提示框。
        mMap.setAllowConcurrentMultipleOpenInfoWindows(true);

        Icon icon = IconFactory.getInstance(this)
                .fromResource(R.drawable.test_cat);
        mMarkerLatLng = new LatLng(30.42491669227814, 114.41992218256276);
        // Use MarkerOptions and addMarker() to add a new marker in map
        MarkerOptions options = new MarkerOptions()
                .position(mMarkerLatLng)
                // .title("dateString")
                // .snippet("snippet")
                .icon(icon);
        mMarker = mMap.addMarker(options);
        mMap.selectMarker(mMarker); // 打开提示框，默认点击地图后会自动关闭提示框。

        // InfoWindowAdapter自定义提示框
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
        mMap.selectMarker(mMarker2);


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

                updateMarkerHint(mMarker2.getInfoWindow(), point.toString());
                return result;
            }
        });
    }


    private void addHeadingIcon() {
        if (mStyle != null) {
            if (mStyle.getImage("AIRCRAFT_MARKER_ICON_ID") == null) {
                Bitmap compassNeedleSymbolLayerIcon = BitmapFactory.decodeResource(
                        getResources(), R.drawable.test_composs);
                mStyle.addImage("AIRCRAFT_MARKER_ICON_ID", compassNeedleSymbolLayerIcon);
            }

            if (mStyle.getSource("GEOJSON_SOURCE_ID") == null) {
                // 方法一，无初始化方向角。
                GeoJsonSource source = new GeoJsonSource("GEOJSON_SOURCE_ID",
                        Feature.fromGeometry(mRotate));
                mStyle.addSource(source);
            }


            // 方法二, 初始化方向角45
            // JsonObject jsonObject = new JsonObject();
            // jsonObject.addProperty(PROPERTY_BEARING, 45f);
            // Feature feature = Feature.fromGeometry(Point.fromLngLat(114.41992218256276 - 0.03,
            //         30.42491669227814 - 0.03), jsonObject);
            // GeoJsonSource source = new GeoJsonSource("GEOJSON_SOURCE_ID", feature);
            // mStyle.addSource(source);

            if (mStyle.getLayer("AIRCRAFT_LAYER_ID") == null) {
                mHeadingSymbolLayer = new SymbolLayer("AIRCRAFT_LAYER_ID", "GEOJSON_SOURCE_ID")
                        .withProperties(
                                PropertyFactory.iconImage("AIRCRAFT_MARKER_ICON_ID"),
                                PropertyFactory.iconRotate(mHeading),
                                // PropertyFactory.iconRotate((float) 45.0),/* 初始化角，也可以用*/
                                // PropertyFactory.iconRotate(Expression.get(PROPERTY_BEARING)),/*
                                // 初始化角，也可以用*/
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconAllowOverlap(true));
                mStyle.addLayer(mHeadingSymbolLayer);
            }
        }
    }

    private void remoteHeadingIcon() {
        mHeading = 0;
        mDeltaLat = 0;
        mStyle.removeImage("AIRCRAFT_MARKER_ICON_ID");
        mStyle.removeSource("GEOJSON_SOURCE_ID");
        mStyle.removeLayer("AIRCRAFT_LAYER_ID");
    }

    private boolean mmHeadingSymbolLayerInitiated;
    private SymbolLayer mHeadingSymbolLayer;
    private float mHeading = 0f;
    private float mDeltaLat = 0.0f;
    private static final String PROPERTY_BEARING = "bearing";

    private Point mRotate = Point.fromLngLat(114.41992218256276 - 0.008,
            30.42491669227814 - 0.008);

    private void btnRotate() {
        // 方法一
        GeoJsonSource source = mStyle.getSourceAs("GEOJSON_SOURCE_ID");
        if (source != null && mHeadingSymbolLayer != null) {
            mRotate = Point.fromLngLat(114.41992218256276 - mDeltaLat, 30.42491669227814 -
                    mDeltaLat);
            source.setGeoJson(Feature.fromGeometry(mRotate));
            mHeadingSymbolLayer.setProperties(PropertyFactory.iconRotate(mHeading));
        }
    }

    private void addCustomInfoWindowAdapter(MapboxMap map) {
        MapboxMap.InfoWindowAdapter adapter = new MapboxMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                TextView tv = new TextView(Test01Activity.this);
                tv.setWidth(200);
                tv.setHeight(100);
                tv.setText("info");
                // tv.setTextSize(R.dimen.plus_info_window_text_size);
                tv.setTextColor(Color.WHITE);
                // int p = (int) getResources().getDimension(R.dimen.plus_info_window_text_padding);
                // tv.setPadding(p, p, p, p);
                tv.setBackgroundColor(Color.BLACK);
                tv.setGravity(Gravity.NO_GRAVITY);
                return tv;
            }
        };
        map.setInfoWindowAdapter(adapter);

    }

    private void updateMarkerHint(@Nullable InfoWindow infoWindow, @Nullable String hint) {
        if (infoWindow != null) {
            View view = infoWindow.getView();
            if (view instanceof TextView) {
                ((TextView) view).setText(hint);
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        infoWindow.update();
                    }
                });
            }
        }
    }

    // 切换地图风格时不可以进行remove操作，否则出现崩溃异常。
    // private void clearStyle() {
    //     if (mStyle != null) {
    //         mStyle.removeImage("PLUS_MARKER_ICON_ID");
    //         mStyle.removeSource("PLUS_GEOJSON_SOURCE_ID");
    //         mStyle.removeLayer("PLUS_LAYER_ID");
    //         mStyle.removeImage("AIRCRAFT_MARKER_ICON_ID");
    //         mStyle.removeSource("GEOJSON_SOURCE_ID");
    //         mStyle.removeLayer("AIRCRAFT_LAYER_ID");
    //         mStyle = null;
    //     }
    // }

    private LongSparseArray<Annotation> mMarkerBtnAddedList = new LongSparseArray<>();
    private float mBtnAddMarkerOffSet = 0.0f;

    private void btnAddOneMarker() {
        mBinding.btnAddMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Icon icon = IconFactory.getInstance(Test01Activity.this)
                        .fromResource(R.drawable.test_dot);
                mBtnAddMarkerOffSet += 0.03f;
                LatLng latLng = new LatLng(30.42491669227814 - mBtnAddMarkerOffSet,
                        114.41992218256276 + mBtnAddMarkerOffSet);
                // Use MarkerOptions and addMarker() to add a new marker in map
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        // .title("dateString")
                        // .snippet("snippet")
                        .icon(icon);
                Marker marker = mMap.addMarker(options);
                mMap.selectMarker(marker);
                updateMarkerHint(marker.getInfoWindow(), String.valueOf(marker.getId()));
                mMarkerBtnAddedList.put(marker.getId(), marker);
            }
        });
    }

    private static final long NO_MARKER = -1;
    private long mClickedId = NO_MARKER;

    private void btnRemoveOneMarker() {
        mBinding.btnRemoveMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMarkerBtnAddedList.containsKey(mClickedId)) {
                    mMap.removeMarker((Marker) mMarkerBtnAddedList.get(mClickedId));
                }
            }
        });
    }

    private void btnGetAddress() {
        mBinding.btnGetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testReverseGeo();
            }
        });
    }


    private LocationEngine mLocationEngine;
    private LocationComponent mLocationComponent;
    private LocationEngineCallback mLocationEngineCallback =
            new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    Location location = result.getLastLocation();
                    Log.d(TAG, "current onSuccess: " + location);
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "current onFailure: " + exception.getMessage());
                }
            };
    private LocationEngineCallback mLastLocationEngineCallback =
            new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    Location location = result.getLastLocation();
                    Log.d(TAG, "Last onSuccess: " + location);
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "Last onFailure: " + exception.getMessage());
                }
            };


    @SuppressLint("MissingPermission")
    private void testLocation() {
        // private LocationEngine locationEngine;
        // @NonNull
        // private LocationEngineRequest locationEngineRequest =
        //         new LocationEngineRequest.Builder(5000)
        //                 .setFastestInterval(5000)
        //                 .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
        //                 .build();
        // private LocationEngineCallback<LocationEngineResult> currentLocationEngineListener
        //         = new LocationComponent.CurrentLocationEngineCallback(this);
        // private LocationEngineCallback<LocationEngineResult> lastLocationEngineListener
        //         = new LocationComponent.LastLocationEngineCallback(this);

        mLocationEngine = LocationEngineDefault.INSTANCE.getDefaultLocationEngine(this);
        mLocationComponent = mMap.getLocationComponent();
        LocationComponentOptions locationComponentOptions =
                LocationComponentOptions.builder(this)
                        .pulseEnabled(false)
                        .build();
        LocationEngineRequest request = new LocationEngineRequest.Builder(5000)
                .setFastestInterval(5000)
                .setMaxWaitTime(10000)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY).build();
        LocationComponentActivationOptions activationOptions =
                new LocationComponentActivationOptions.Builder(this, mStyle)
                        .locationComponentOptions(locationComponentOptions)
                        .locationEngine(mLocationEngine)
                        .locationEngineRequest(request)
                        .build();
        mLocationComponent.activateLocationComponent(activationOptions);
        mLocationComponent.setLocationComponentEnabled(false);
    }

    // 获取定位信息
    @SuppressLint("MissingPermission")
    private void testStartLocation() {
        mLocationEngine = LocationEngineDefault.INSTANCE.getDefaultLocationEngine(this);
        LocationEngineRequest request = new LocationEngineRequest.Builder(5000)
                .setFastestInterval(5000)
                .setMaxWaitTime(10000)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .build();
        mLocationEngine.requestLocationUpdates(request, mLocationEngineCallback
                , Looper.getMainLooper());
        mLocationEngine.getLastLocation(mLastLocationEngineCallback);

    }

    private void testStopLocation() {
        if (mLocationEngine != null) {
            mLocationEngine.removeLocationUpdates(mLocationEngineCallback);
        }
    }

    private void testReverseGeo() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger()
        {
            @Override
            public void log(String message)
            {
                if (BuildConfig.DEBUG) Log.d("Http----", message+"");
            }
        });
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)//设置日志打印
                .retryOnConnectionFailure(true)//失败重连
                .connectTimeout(30, TimeUnit.SECONDS)//网络请求超时时间单位为秒
                .build();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://api.maptiler.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                        .client(okHttpClient)
                        .build();
        APIReverseGeo apiReverseGeo = retrofit.create(APIReverseGeo.class);
        // apiReverseGeo.getAddress(114.41992218256276, 30.42491669227814, BuildConfig.mapTilerKey
        apiReverseGeo.getAddress(114.420270,30.425054, BuildConfig.mapTilerKey
                /*APIReverseGeo.LIMIT APIReverseGeo.LNG, APIReverseGeo.TYPES, false*/)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SearchJsonObj>() {
                    @Override
                    public void accept(SearchJsonObj searchJsonObj) throws Throwable {
                        Log.d(TAG, "accept: " + searchJsonObj);
                        List<Features> features = searchJsonObj.getFeatures();
                        if (features != null && !features.isEmpty()) {
                            Log.d(TAG, "accept: address[114.41992218256276, 30.42491669227814] " +
                                    features.get(0).getPlaceName());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.e(TAG, "accept: ", throwable);

                    }
                }, new Action() {
                    @Override
                    public void run() throws Throwable {
                        Log.i(TAG, "run: complete");
                    }
                });

        // Maptiler Cloud API调用手册
        // https://docs.maptiler.com/cloud/api/geocoding/#search-by-coordinates-reverse

        // 开发手册
        // https://www.jawg.io/docs/integration/maplibre-gl-android/add-marker/
        // https://stackoverflow.com/questions/53517370/rotate-and-change-position-for-markers-in-latest-mapbox-sdk-6-7/54124090#54124090

        //
        // OkHttpClient client = new OkHttpClient();
        // OkHttpClient builder = new OkHttpClient.Builder()
        //         .connectTimeout(5000, TimeUnit.MILLISECONDS)
        //         .build();
        //
        // Request request = new Request.Builder().url(
        //                 " https://api.maptiler.com/geocoding/{longitude},{latitude}.json")
        //         .get().build();
        // client.newCall(request).enqueue(new Callback() {
        //     @Override
        //     public void onFailure(@NonNull Call call, @NonNull IOException e) {
        //
        //     }
        //
        //     @Override
        //     public void onResponse(@NonNull Call call, @NonNull Response response)
        //             throws IOException {
        //         if (response.isSuccessful()) {
        //             ResponseBody body = response.body();
        //             body.
        //         }
        //     }
        // });

    }
}