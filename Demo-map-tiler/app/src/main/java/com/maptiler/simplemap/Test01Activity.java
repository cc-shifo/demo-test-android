package com.maptiler.simplemap;

import static com.mapbox.mapboxsdk.maps.widgets.CompassView.TIME_MAP_NORTH_ANIMATION;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

// @SuppressWarnings({"unused"})/*, "deprecation"*/
public class Test01Activity extends AppCompatActivity {
    private static final String TAG = "Test01Activity";
    private static final String TAGABC = "TAGABC";
    private ActivityTest01Binding mBinding;
    private MapboxMap mMap;
    private Style mStyle;
    private static final String mStyleUrl =
            "https://api.maptiler.com/maps/hybrid/style.json?key=" + BuildConfig.mapTilerKey;
    private static final String mStreet =
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
        mBinding.mapView.getMapAsync(mapboxMap -> {
            // 避免之前Activity下map的InfoWindow没有关闭，再进入到当前Activity时出现InfoWindow null的
            // 崩溃现象。
            mapboxMap.clear();
            initMapEle(mapboxMap);
        });
        mBinding.mapView.addOnCanRemoveUnusedStyleImageListener(
                id -> {
                    Log.d(TAG, "onCanRemoveUnusedStyleImage: " + id);
                    return false;
                });
        mBinding.mapView.addOnStyleImageMissingListener(
                id -> Log.d(TAG, "onStyleImageMissing: " + id));
        mBinding.mapView.addOnSourceChangedListener(
                id -> Log.d(TAG, "onSourceChangedListener: " + id));
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

        mBinding.btnSatelliteHybrid.setOnClickListener(v -> mBinding.mapView.getMapAsync(
                mapboxMap -> mMap.setStyle(mStyleUrl, style -> {
                    Log.d(TAG, "btnSatelliteHybrid onStyleLoaded: " + style.toString());
                    // clearStyle();
                    mStyle = style;
                    addSymbolMarkers();
                    if (mmHeadingSymbolLayerInitiated) {
                        addHeadingIcon();
                    }
                })));

        mBinding.btnStreet.setOnClickListener(
                v -> mBinding.mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull MapboxMap mapboxMap) {
                        mMap.setStyle(mStreet, style -> {
                            Log.d(TAG, "btnStreet onStyleLoaded: " + style.toString());
                            mStyle = style;
                            addSymbolMarkers();
                            if (mmHeadingSymbolLayerInitiated) {
                                addHeadingIcon();
                            }
                        });
                    }
                }));
        mBinding.btnSizeQuarter.setOnClickListener(v -> {
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
        });
        mBinding.btnSizeHalf.setOnClickListener(v -> {
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
        });
        mBinding.btnSizeFull.setOnClickListener(v -> {
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            mBinding.mapView.setLayoutParams(params);
        });

        final int BLUE_COLOR = Color.parseColor("#3bb2d0");
        final int FILL_BLUE_COLOR = Color.parseColor("#9A2196F3");
        mBinding.btnPolygon.setOnClickListener(v -> {
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
        });

        mBinding.btnPolyline.setOnClickListener(v -> {
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
        });

        mBinding.btnClear.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.clear();
            }
        });

        mBinding.btnRotateAircraftIcon.setOnClickListener(v -> { //避免快速点击多次
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
        });
        mBinding.btnDeleteAircraftIcon.setOnClickListener(v -> {
            remoteHeadingIcon();
            mmHeadingSymbolLayerInitiated = false;
        });

        btnAddOneMarker();
        btnRemoveOneMarker();
        btnGetAddress();
        btnCompassNorth();
        btnScreenshot();
        btnTestABC();


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
        // testStartLocation();
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
        // testStopLocation();
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
        if (!mDisposables.isDisposed()) {
            mDisposables.clear();
        }

        if (mOkHttpClient != null) {
            mOkHttpClient.connectionPool().evictAll();
        }
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
        // + BuildConfig.mapTilerKey
        mMap = mapboxMap;
        // mMap.getUiSettings().setLogoEnabled(false); // set in xml
        // mMap.getUiSettings().setAttributionEnabled(false);  // set in xml
        mMap.setStyle(mStyleUrl, style -> {
            Log.d(TAG, "onStyleLoaded: " + style);
            mStyle = style;
            addSymbolMarkers();
            if (mmHeadingSymbolLayerInitiated) {
                addHeadingIcon();
            }
        });

        mMap.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d(TAG, "onCameraIdle: 0");
                mIsMapGestureIdle = true;
            }
        });
        mMap.addOnCameraMoveStartedListener(new MapboxMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                Log.d(TAG, "onCameraMoveStarted: 1");
                mIsMapGestureIdle = false;
            }
        });
        mMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.d(TAG, "onCameraMove: 2");
                mIsMapGestureIdle = false;
            }
        });
        mMap.addOnCameraMoveCancelListener(new MapboxMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
                Log.d(TAG, "onCameraMoveCanceled: 3");
                mIsMapGestureIdle = false;
            }
        });
        mMap.addOnFlingListener(new MapboxMap.OnFlingListener() {
            @Override
            public void onFling() {
                Log.d(TAG, "onCamera onFling: 1");
                mIsMapGestureIdle = false;
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
    private final List<Feature> mFeatureList = new ArrayList<>();

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
            // PropertyFactory.iconImage(); // 更新图标
            // op.getMarker().setIcon(); // 也是更新图标
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

        initData();
    }

    private void initData(/*@NonNull String srcId, @NonNull String layerId,
            @NonNull String bdSrcId, @NonNull String bdLayerId*/) {
        String srcId = "MARKER_SOURCE_ID_PREFIX-1";
        String layerId = "MARKER_LAYER_ID_PREFIX-1";
        // 114.41903025347261, 30.425355477071445, 100.0
        GeoJsonSource mSource = new GeoJsonSource(srcId, Point.fromLngLat(
                114.41903025347261, 30.425355477071445, 100.0));

        Bitmap mUnselectedIcon = BitmapFactory.decodeResource(
                getResources(), R.drawable.ic_pot_white);
        Bitmap mSelectedIcon = BitmapFactory.decodeResource(
                getResources(), R.drawable.ic_pot_blue);
        Drawable drawable1 = getDrawable(R.drawable.ic_pot_white);
        Drawable drawable2 = getDrawable(R.drawable.ic_pot_blue);
        String mUnselectedIconId = "MARKER_ICON_ID_PREFIX-1";
        String mSelectedIconId = "MARKER_ICON_ID_PREFIX-2";
        Style style = mMap.getStyle();
        if (style != null && style.isFullyLoaded()) {
            style.addImage(mUnselectedIconId, drawable1);
            style.addImage(mSelectedIconId, drawable2);
            style.addSource(mSource);
        }
        //PropertyFactory.textAnchor(Property.TEXT_ANCHOR_BOTTOM),
        //                         PropertyFactory.textOffset(new Float[] {0f, 2f}),

        SymbolLayer mLayer = new SymbolLayer(layerId, mSource.getId())
                .withProperties(
                        // PropertyFactory.iconRotate(mAttrs.getRotation()),

                        PropertyFactory.textField("h1"),
                        //         PropertyFactory.textColor(Color.WHITE),
                        //         PropertyFactory.backgroundColor(Color.GRAY),
                        PropertyFactory.iconImage(mUnselectedIconId),
                        PropertyFactory.iconAllowOverlap(true),
                        PropertyFactory.iconIgnorePlacement(true));
        mStyle.addLayer(mLayer);
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

    private final StringBuilder mTextBlackboard = new StringBuilder();

    private void addClickListen() {
        mMap.addOnMapClickListener(point -> {

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
                                PropertyFactory.iconIgnorePlacement(false),
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
        MapboxMap.InfoWindowAdapter adapter = marker -> {
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
        };
        map.setInfoWindowAdapter(adapter);

    }

    private void updateMarkerHint(@Nullable InfoWindow infoWindow, @Nullable String hint) {
        if (infoWindow != null) {
            View view = infoWindow.getView();
            if (view instanceof TextView) {
                ((TextView) view).setText(hint);
                view.post(infoWindow::update);
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

    private final LongSparseArray<Annotation> mMarkerBtnAddedList = new LongSparseArray<>();
    private float mBtnAddMarkerOffSet = 0.0f;

    private void btnAddOneMarker() {
        mBinding.btnAddMarker.setOnClickListener(v -> {
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
        });
    }

    private static final long NO_MARKER = -1;
    private long mClickedId = NO_MARKER;

    private void btnRemoveOneMarker() {
        mBinding.btnRemoveMarker.setOnClickListener(v -> {
            if (mMarkerBtnAddedList.containsKey(mClickedId)) {
                mMap.removeMarker((Marker) mMarkerBtnAddedList.get(mClickedId));
            }
        });
    }

    private void btnGetAddress() {
        mOkHttpClient = OkHttpHelper.createClient();
        mBinding.btnGetAddress.setOnClickListener(v -> testReverseGeo());
    }


    private boolean mIsMapGestureIdle = true; // 判断地图是否在交互移动中

    private void btnCompassNorth() {
        mBinding.btnCompassNorth.setOnClickListener(v -> {
            if (mMap != null && mIsMapGestureIdle) {
                boolean enable = !mMap.getUiSettings().isRotateGesturesEnabled();
                mTextBlackboard.setLength(0);
                mTextBlackboard.append("rotation enabled: ").append(enable);
                mBinding.tvMessageBlackboard.setText(mTextBlackboard.toString());
                mMap.getUiSettings().setRotateGesturesEnabled(enable);
                mMap.setFocalBearing(0, mMap.getWidth() / 2, mMap.getHeight() / 2,
                        TIME_MAP_NORTH_ANIMATION);
            }
        });
    }

    private ImageView mScreenShotView;

    private void btnScreenshot() {
        mBinding.btnScreenShot.setOnClickListener(v -> {
            if (mMap != null && mIsMapGestureIdle) {
                mDisposables.add(Completable.create(emitter -> {
                            mMap.snapshot(snapshot -> { // 测试发现截屏操作必须在主线程中进行
                                Log.d(TAG,
                                        "btnScreenshot: my thread id=" + Thread.currentThread().getId());
                                if (mScreenShotView == null) {
                                    mScreenShotView = new ImageView(Test01Activity.this);
                                    ConstraintLayout.LayoutParams params =
                                            new ConstraintLayout.LayoutParams(
                                                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                                                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
                                    params.matchConstraintPercentWidth = 0.5f;
                                    params.matchConstraintDefaultWidth =
                                            ConstraintLayout.LayoutParams
                                            .MATCH_CONSTRAINT_PERCENT;
                                    params.matchConstraintPercentHeight = 0.5f;
                                    params.matchConstraintDefaultHeight =
                                            ConstraintLayout.LayoutParams
                                            .MATCH_CONSTRAINT_PERCENT;
                                    params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                                    params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                                    params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                                    mScreenShotView.setLayoutParams(params);
                                    mScreenShotView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                    mBinding.getRoot().addView(mScreenShotView);
                                    // mScreenShotView.setTag("mScreenShotView");
                                }
                                mScreenShotView.setImageBitmap(snapshot);
                            });
                            emitter.onComplete();
                        }).subscribeOn(AndroidSchedulers.mainThread())// 测试发现截屏操作必须在主线程中进行
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Throwable {
                                Log.d(TAG, "btnScreenshot onComplete");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Throwable {
                                Log.e(TAG, "btnScreenshot onError ", throwable);
                            }
                        }));

            }
        });
        mBinding.btnDelScreenShot.setOnClickListener(v -> {
            if (mMap != null && mIsMapGestureIdle && mScreenShotView != null) {
                // mBinding.getRoot().findViewWithTag("mScreenShotView");
                mBinding.getRoot().removeView(mScreenShotView);
                mScreenShotView = null;
            }
        });
    }

    private void btnTestABC() {
        mBinding.btnTestABC.setOnClickListener(v -> {
            testABC();
        });
    }


    private LocationEngine mLocationEngine;
    private LocationComponent mLocationComponent;
    private final LocationEngineCallback mLocationEngineCallback =
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
    private final LocationEngineCallback mLastLocationEngineCallback =
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

    private OkHttpClient mOkHttpClient;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    private void testReverseGeo() {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://api.maptiler.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                        .client(mOkHttpClient)
                        .build();
        APIReverseGeo apiReverseGeo = retrofit.create(APIReverseGeo.class);
        // apiReverseGeo.getAddress(114.41992218256276, 30.42491669227814, BuildConfig.mapTilerKey
        mDisposables.add(apiReverseGeo.getAddress(114.420270, 30.425054, BuildConfig.mapTilerKey
                        /*APIReverseGeo.LIMIT APIReverseGeo.LNG*/, APIReverseGeo.TYPES, false)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchJsonObj -> {
                            Log.d(TAG, "accept: " + searchJsonObj);
                            List<Features> features = searchJsonObj.getFeatures();
                            if (features != null && !features.isEmpty()) {
                                Log.d(TAG, "accept: address[114.41992218256276, 30" +
                                        ".42491669227814] " +
                                        features.get(0).getPlaceName());
                                Features f = features.get(0);
                                List<Context> cList = f.getContext();
                                if (cList != null) {
                                    mTextBlackboard.setLength(0);
                                    for (int i = 0; i < cList.size(); i++) {
                                        Context c = cList.get(i);
                                        mTextBlackboard.append(c.getText());
                                        if (i != cList.size() - 1) {
                                            mTextBlackboard.append(",");
                                        }
                                    }
                                }
                                mTextBlackboard.append("\n\n\n").append(features.get(0).getPlaceName());
                                mBinding.tvMessageBlackboard.setText(mTextBlackboard.toString());
                            }
                        }, throwable -> {
                            Log.e(TAG, "accept: ", throwable);
                            mBinding.tvMessageBlackboard.setText(throwable.getMessage());
                        },
                        () -> Log.i(TAG, "run: complete")));

        // Maptiler Cloud API调用手册
        // https://docs.maptiler.com/cloud/api/geocoding/#search-by-coordinates-reverse

        // 开发手册
        // https://www.jawg.io/docs/integration/maplibre-gl-android/add-marker/
        // https://stackoverflow.com/questions/53517370/rotate-and-change-position-for-markers-in-latest-mapbox-sdk-6-7/54124090#54124090

        // 在线坐标查找地址工具
        // https://docs.maptiler.com/sdk-js/examples/geocoding-reverse-json/
        // 总体数据结构
        // https://docs.maptiler.com/cloud/api/geocoding/#SearchResults
        // https://docs.maptiler.com/cloud/api/geocoding/#SearchResults

        // 在线json转java工具
        // https://www.lddgo.net/string/jsontojava
        // https://tool.lu/json/
    }

    private Disposable mTestABCDisposable;
    private void testABC() {
        if (mTestABCDisposable != null && !mTestABCDisposable.isDisposed()) {
            mTestABCDisposable.dispose();
            mTestABCDisposable = null;
        }
        Observable<Integer> observable1 = Observable.create(
                (ObservableOnSubscribe<Integer>) emitter -> {
                    Thread.sleep(1000);
                    Log.d(TAGABC, "subscribeLocation observable1: " + Thread.currentThread().getId());
                    Log.e(TAGABC, "emit 1");
                    emitter.onNext(1);

                    Thread.sleep(1000);
                    Log.e(TAGABC, "emit 2");
                    emitter.onNext(2);

                    Thread.sleep(1000);
                    Log.e(TAGABC, "emit 3");
                    emitter.onNext(3);

                    Thread.sleep(200);
                    Log.e(TAGABC, "emit 4");
                    emitter.onNext(4);

                    Log.e(TAGABC, "emit complete1");
                    emitter.onComplete();
                }).subscribeOn(Schedulers.newThread());    // 让上游1（第一个水管）在子线程中执行


        Observable<String> observable2 = Observable.create(
                (ObservableOnSubscribe<String>) emitter -> {
                    Thread.sleep(4000);
                    Log.d(TAGABC, "subscribeLocation observable2: " + Thread.currentThread().getId());
                    Log.e(TAGABC, "emit A");
                    emitter.onNext("A");

                    Thread.sleep(4000);
                    Log.e(TAGABC, "emit B");
                    emitter.onNext("B");

                    Thread.sleep(4000);
                    Log.e(TAGABC, "emit C");
                    emitter.onNext("C");

                    Log.e(TAGABC, "emit complete2");
                    emitter.onComplete();
                }).subscribeOn(Schedulers.newThread());      // 让上游2（第二个水管）在子线程中执行


        Observable.zip(observable1, observable2, (integer, s) -> {
            Log.d(TAGABC, "subscribeLocation zip: apply： " + Thread.currentThread().getId());
            return integer + s;
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAGABC, "subscribe");
                        mTestABCDisposable = d;
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(String value) {
                        Log.e(TAGABC, "next -> " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAGABC, "error");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAGABC, "complete");
                    }
                });
    }

}