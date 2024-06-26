package com.amap.map3d.demo.overlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.map3d.demo.overlay.wms.HeritageScopeTileProvider;
import com.amap.map3d.demo.util.Constants;
import com.amap.map3d.demo.util.ThreadUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 *
 */
public class ProvinceHoleActivity extends Activity implements DistrictSearch.OnDistrictSearchListener {
    private AMap aMap;
    private MapView mapView;
    private Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        setContentView(mapView);
        init();


    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {

        List<LatLng> latLngs = new ArrayList();
        //绘制一个全世界的多边形
        latLngs.add(new LatLng(84.9, -179.9));
        latLngs.add(new LatLng(84.9, 179.9));
        latLngs.add(new LatLng(-84.9, 179.9));
        latLngs.add(new LatLng(-84.9, -179.9));

        polygon = aMap.addPolygon(new PolygonOptions().addAll(latLngs).fillColor(Color.argb(255,
                245, 245, 245)).zIndex(10));
        // polygon = aMap.addPolygon(new PolygonOptions().addAll(latLngs).fillColor(Color.argb(255,
        //         0, 245, 0)).zIndex(10));


        // aMap.moveCamera(CameraUpdateFactory.zoomTo(4));
        // aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
        //         Constants.LUSHANXIAN, 10, 0/*30*/, 0/*30*/)));// LUSHAN
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                Constants.YANTING, 10, 0/*30*/, 0/*30*/)));
        searchDistrict();
        // //加载自定义wms
        // // HeritageScopeTileProvider tileProvider = new HeritageScopeTileProvider();
        // // tileProvider.setContext(this);
        // // aMap.addTileOverlay(new TileOverlayOptions()
        // //         .tileProvider(tileProvider));
        // HeritageScopeTileProvider tileProvider0 = new HeritageScopeTileProvider
        // ("geoserver/test/",
        //         "test:PLBDOM", HeritageScopeTileProvider.CR_4490);
        // tileProvider0.setContext(this);
        // aMap.addTileOverlay(new TileOverlayOptions()
        //         .tileProvider(tileProvider0));

        // 4490坐标
        // HeritageScopeTileProvider tileProvider1 = new HeritageScopeTileProvider
        // ("geoserver/test/",
        //         "test:PLBDOM", HeritageScopeTileProvider.TYPE_CR_4490);
        // tileProvider1.setContext(this);
        // aMap.addTileOverlay(new TileOverlayOptions()
        //         .tileProvider(tileProvider1));
        // HeritageScopeTileProvider tileProvider2 = new HeritageScopeTileProvider
        // ("geoserver/test/",
        //         "test:GF1DOM", HeritageScopeTileProvider.TYPE_CR_4490);
        // tileProvider2.setContext(this);
        // aMap.addTileOverlay(new TileOverlayOptions()
        //         .tileProvider(tileProvider2));
        // HeritageScopeTileProvider tileProvider3 = new HeritageScopeTileProvider
        // ("geoserver/test/",
        //         "test:GF2DOM");
        // tileProvider3.setContext(this);
        // aMap.addTileOverlay(new TileOverlayOptions()
        //         .tileProvider(tileProvider3));


        // wgs84
        HeritageScopeTileProvider tileProvider4 = new HeritageScopeTileProvider("geoserver" +
                "/wgs84yanting/",
                "wgs84yanting:wgs84yantingPLBDOM", HeritageScopeTileProvider.TYPE_CR_4326);
        tileProvider4.setContext(this);
        aMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider4));
        HeritageScopeTileProvider tileProvider5 = new HeritageScopeTileProvider("geoserver" +
                "/wgs84yanting/",
                "wgs84yanting:wgs84yantingGF1DOM", HeritageScopeTileProvider.TYPE_CR_4326);
        tileProvider5.setContext(this);
        aMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider5));
    }

    private void searchDistrict() {
        // String province = "庐山";
        String province = "四川省雅安市";

        DistrictSearch districtSearch = null;
        try {
            districtSearch = new DistrictSearch(getApplicationContext());
            DistrictSearchQuery districtSearchQuery = new DistrictSearchQuery();
            // districtSearchQuery.setKeywords(province);
            // districtSearchQuery.setKeywords("武汉市");
            // districtSearchQuery.setKeywords("四川省雅安市");
            // districtSearchQuery.setKeywords("511826");//芦山县
            districtSearchQuery.setKeywords("510723");//盐亭县
            districtSearchQuery.setShowBoundary(true);
            // districtSearchQuery.setShowChild(true);
            districtSearchQuery.setSubDistrict(0);
            districtSearch.setQuery(districtSearchQuery);

            districtSearch.setOnDistrictSearchListener(ProvinceHoleActivity.this);

            //请求边界数据 开始展示
            districtSearch.searchDistrictAsyn();
        } catch (AMapException e) {
            e.printStackTrace();
        }

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onDistrictSearched(DistrictResult districtResult) {
        if (districtResult == null || districtResult.getDistrict() == null) {
            return;
        }
        showDistrictBounds(districtResult);

    }


    protected void showDistrictBounds(DistrictResult districtResult) {
        //通过ErrorCode判断是否成功
        if (districtResult.getAMapException() != null && districtResult.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS) {
            final DistrictItem item = districtResult.getDistrict().get(0);

            if (item == null) {
                return;
            }

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    drawBound(item);
                }
            });
        } else {
            if (districtResult.getAMapException() != null) {
                Log.e("amap", "error " + districtResult.getAMapException().getErrorCode());
            }
        }
    }

    private void drawBound(DistrictItem item) {
        String[] polyStr = item.districtBoundary();
        if (polyStr == null || polyStr.length == 0) {
            return;
        }

        List<com.amap.api.maps.model.BaseHoleOptions> holeOptionsList =
                new ArrayList<>();
        for (String str : polyStr) {
            String[] lat = str.split(";");
            com.amap.api.maps.model.PolygonHoleOptions polygonHoleOptions =
                    new com.amap.api.maps.model.PolygonHoleOptions();
            List<LatLng> holeLatLngs = new ArrayList<>();
            boolean isFirst = true;
            LatLng firstLatLng = null;
            int index = 0;

            // 不跳过任何点，以最高精度显示边界
            // 可以过滤掉一些点，提升绘制效率
            /*int offset = 50;
            if (lat.length < 400) {
                offset = 20;
            }*/

            for (String latstr : lat) {
                index++;
                // 不跳过任何点，以最高精度显示边界
                /*if (index % offset != 0) {
                    continue;
                }*/
                String[] lats = latstr.split(",");
                if (isFirst) {
                    isFirst = false;
                    firstLatLng = new LatLng(Double
                            .parseDouble(lats[1]), Double
                            .parseDouble(lats[0]));
                }
                holeLatLngs.add(new LatLng(Double
                        .parseDouble(lats[1]), Double
                        .parseDouble(lats[0])));
            }
            if (firstLatLng != null) {
                holeLatLngs.add(firstLatLng);
            }

            polygonHoleOptions.addAll(holeLatLngs);

            holeOptionsList.add(polygonHoleOptions);
        }

        if (holeOptionsList.size() > 0) {
            ListComparator c = new ListComparator();
            // 将洞的内容排序，优先显示量大的洞，避免出现洞相互叠加，导致无法展示
            Collections.sort(holeOptionsList, c);
            for (com.amap.api.maps.model.BaseHoleOptions holeOptions :
                    holeOptionsList) {
                com.amap.api.maps.model.PolygonHoleOptions polygonHoleOption =
                        (com.amap.api.maps.model.PolygonHoleOptions) holeOptions;
                android.util.Log.e("amap",
                        "polygonHoleOption size " + polygonHoleOption.getPoints().size());
            }

            polygon.setHoleOptions(holeOptionsList);
        }
    }


    static class ListComparator implements Comparator<Object>,
            Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Object lhs, Object rhs) {
            com.amap.api.maps.model.PolygonHoleOptions fir =
                    (com.amap.api.maps.model.PolygonHoleOptions) lhs;
            com.amap.api.maps.model.PolygonHoleOptions sec =
                    (com.amap.api.maps.model.PolygonHoleOptions) rhs;
            try {
                if (fir != null && sec != null && fir.getPoints() != null) {
                    if (fir.getPoints().size() < sec.getPoints().size()) {
                        return 1;
                    } else if (fir.getPoints().size() > sec.getPoints().size()) {
                        return -1;
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            return 0;
        }
    }


}
