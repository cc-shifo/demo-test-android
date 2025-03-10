package com.example.testmaplibre11;


import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import org.maplibre.android.MapLibre;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;
import org.maplibre.android.style.layers.RasterLayer;
import org.maplibre.android.style.sources.RasterSource;
import org.maplibre.android.style.sources.TileSet;


public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private MapLibreMap mMaplibreMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        MapLibre.getInstance(this);
        // MapLibre.getInstance(this);
        // MapLibre.setApiKey(BuildConfig.mapTilerKey);
        setContentView(R.layout.activity_main);

        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        // mMapView.getMapAsync(new OnMapReadyCallback() {
        //     @Override
        //     public void onMapReady(@NonNull MapLibreMap map) {
        //         mMaplibreMap = map;
        //
        //         // 设置天地图的样式
        //         mMaplibreMap.setStyle(new Style.Builder().fromUri("https://api.maptiler.com/maps/streets/style.json"),
        //                 new Style.OnStyleLoaded() {
        //             @Override
        //             public void onStyleLoaded(@NonNull Style style) {
        //                 // 在这里可以添加其他图层或标记
        //                 addTianDiSource(style);
        //             }
        //         });
        //     }
        // });
        // mMapView.getMapAsync(new OnMapReadyCallback() {
        //     @Override
        //     public void onMapReady(@NonNull MapLibreMap map) {
        //         mMaplibreMap = map;
        //
        //         // 使用天地图的样式
        //         // 使用天地图的样式URL
        //         String tiandituStyleUrl = "http://t0.tianditu.gov.cn/vec_w/wmts?service=wmts"
        //                 + "&request=GetTile&version=1.0.0&layer=vec&style=default"
        //                 + "&tilematrixset=w&format=tiles&tilematrix={z}&tilerow={y}&tilecol={x}"
        //                 + "&tk=a619a7c0afec01defe7db60428e49f1a";
        //         map.setStyle(new Style.Builder().fromUri(tiandituStyleUrl), new Style.OnStyleLoaded() {
        //             @Override
        //             public void onStyleLoaded(@NonNull Style style) {
        //                 // 在这里可以添加其他图层或标记
        //                 addTianDiSource(style);
        //             }
        //         });
        //         // mMaplibreMap.setStyle(new Style.Builder().fromUri("https://api.maptiler.com/maps/streets/style.json"),
        //         //         new Style.OnStyleLoaded() {
        //         //     @Override
        //         //     public void onStyleLoaded(@NonNull Style style) {
        //         //         // 在这里可以添加其他图层或标记
        //         //         addTianDiSource(style);
        //         //     }
        //         // });
        //     }
        // });

        // testCopilotMethod(mMapView);
        testDeepSeek(mMapView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void addTianDiSource(@NonNull Style style) {
        // 添加天地图矢量图层
        style.addSource(new RasterSource("tianditu-vec-source",
                new TileSet("tianditu-vec", "http://t0.tianditu.gov.cn/vec_w/wmts?service=wmts&request=GetTile"
                        + "&version=1.0.0&layer=vec&style=default&tilematrixset=w&format=tiles"
                        + "&tilematrix={z}&tilerow={y}&tilecol={x}&tk="
                        + "a619a7c0afec01defe7db60428e49f1a"),
                256));

        style.addLayer(new RasterLayer("tianditu-vec-layer", "tianditu-vec-source"));

        // 添加天地图标注图层
        style.addSource(new RasterSource("tianditu-cva-source",
                new TileSet("tianditu-cva", "http://t0.tianditu.gov.cn/cva_w/wmts?service=wmts&request=GetTile"
                        + "&version=1.0.0&layer=cva&style=default&tilematrixset=w&format=tiles"
                        + "&tilematrix={z}&tilerow={y}&tilecol={x}&tk="
                        + "a619a7c0afec01defe7db60428e49f1a"),
                256));

        style.addLayer(new RasterLayer("tianditu-cva-layer", "tianditu-cva-source"));
    }

    private void testCopilotMethod(@NonNull MapView mapView) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapLibreMap mapLibreMap) {
                copilotAddTiandituLayer(mapLibreMap);
            }
        });
    }

    private void copilotAddTiandituLayer(MapLibreMap mapLibreMap) {
        String tiandituUrl = "https://t{s}.tianditu.gov.cn/img_w/wmts?"
                + "tk=a619a7c0afec01defe7db60428e49f1a"
                + "&service=wmts&request=GetTile&version=1.0.0&layer=img&style=default&tilematrixset=w"
                + "&format=tiles&tilematrix={z}&tilerow={y}&tilecol={x}";
        TileSet tileSet = new TileSet("tileset", tiandituUrl);
        tileSet.setScheme("xyz");
        RasterSource rasterSource = new RasterSource("tianditu-source", tileSet, 256);
        mapLibreMap.getStyle().addSource(rasterSource);

        RasterLayer rasterLayer = new RasterLayer("tianditu-layer", "tianditu-source");
        mapLibreMap.getStyle().addLayer(rasterLayer);
    }

    private void testDeepSeek(@NonNull MapView mapView) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapLibreMap map) {
                // 使用 MapLibre 的默认样式（或自定义样式）
                // map.setStyle(Style.getPredefinedStyle("Outdoors"), style -> {
                // map.setStyle(Style.getPredefinedStyle("Streets"), style -> {
                map.setStyle(new Style.Builder().fromUri("asset://empty.json"), style -> {
                    // 添加天地图矢量图层
                    RasterSource vecSource = new RasterSource(
                            "tianditu-vec-source",
                            new TileSet("tianditu-vec", "http://t0.tianditu.gov.cn/vec_w/wmts?service=wmts&request=GetTile&version=1.0.0"
                                    + "&layer=vec&style=default&tilematrixset=w&format=tiles&tilematrix={z}&tilerow={y}&tilecol={x}&tk="
                                    + "a619a7c0afec01defe7db60428e49f1a"),
                            256
                    );
                    style.addSource(vecSource);

                    RasterLayer vecLayer = new RasterLayer("tianditu-vec-layer", "tianditu-vec-source");
                    style.addLayer(vecLayer);

                    // 添加天地图标注图层
                    RasterSource cvaSource = new RasterSource(
                            "tianditu-cva-source",
                            new TileSet("tianditu-cva", "http://t0.tianditu.gov.cn/cva_w/wmts?service=wmts&request=GetTile&version=1.0.0"
                                    + "&layer=cva&style=default&tilematrixset=w&format=tiles&tilematrix={z}&tilerow={y}&tilecol={x}&tk="
                                    + "a619a7c0afec01defe7db60428e49f1a"),
                            256
                    );
                    style.addSource(cvaSource);

                    RasterLayer cvaLayer = new RasterLayer("tianditu-cva-layer", "tianditu-cva-source");
                    style.addLayerAbove(cvaLayer, "tianditu-vec-layer"); // 将标注图层放在矢量图层之上
                });
            }
        });
    }
}