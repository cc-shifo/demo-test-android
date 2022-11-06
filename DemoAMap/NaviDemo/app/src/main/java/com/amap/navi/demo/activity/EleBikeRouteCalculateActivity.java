package com.amap.navi.demo.activity;

import android.os.Bundle;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.TravelStrategy;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.NaviPoi;
import com.amap.navi.demo.R;


public class EleBikeRouteCalculateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_basic_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

        AMapNaviViewOptions aMapNaviViewOptions = mAMapNaviView.getViewOptions();
        aMapNaviViewOptions.setAfterRouteAutoGray(true);
        mAMapNaviView.setViewOptions(aMapNaviViewOptions);
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();

//        // 无起点算路
//        mAMapNavi.calculateEleBikeRoute(new NaviLatLng(39.925846, 116.435765));

//        // 起终点算路
//        mAMapNavi.calculateEleBikeRoute(new NaviLatLng(39.925846, 116.435765), new NaviLatLng(39.925846, 116.532765));


        // 构造起点POI
        NaviPoi fromPoi = new NaviPoi("起点", new LatLng(39.925846, 116.435765), "");
        // 构造终点POI
        NaviPoi toPoi = new NaviPoi("终点", new LatLng(39.925846, 116.532765), "");
        // 进行骑行(电动车)算路
        mAMapNavi.calculateEleBikeRoute(fromPoi, toPoi, TravelStrategy.SINGLE);
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        super.onCalculateRouteSuccess(aMapCalcRouteResult);
        mAMapNavi.startNavi(NaviType.GPS);
    }
}
