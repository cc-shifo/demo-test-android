package com.example.democallerofamap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.democallerofamap.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private static final String BAIDU_MAP_PACKAGE = "com.baidu.BaiduMap";
    private static final String GAODE_MAP_PACKAGE = "com.autonavi.minimap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.btnCallBaiduMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBaiduMap();
            }
        });
        mBinding.btnCallAmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGaoDeMap();
            }
        });
    }

    /**
     * H5调用地图APP
     */
    public void startNavigation() {
        List<PackageInfo> list = getPackageManager().getInstalledPackages(0);
        for (PackageInfo pk : list) {
            if (pk.packageName.equals(BAIDU_MAP_PACKAGE)) {
                openBaiduMap();
                return;
            } else if (pk.packageName.equals(GAODE_MAP_PACKAGE)) {
                openGaoDeMap();
                return;
            }
        }

        Toast.makeText(this, "hello map", Toast.LENGTH_SHORT).show();
    }


    /**
     * https://lbs.amap.com/api/amap-mobile/guide/android/route
     * <p>
     * act=android.intent.action.VIEW
     * cat=android.intent.category.DEFAULT
     * dat=amapuri://route/plan/?sid=&slat=39.92848272&slon=116.39560823&sname=A&did=&dlat=39
     * .98848272&dlon=116.47560823&dname=B&dev=0&t=0
     * pkg=com.autonavi.minimap
     * 打开高德地图（公交出行，起点位置使用地图当前位置）
     * <p>
     * t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）
     * <p>
     * dLat  终点纬度
     * dLon  终点经度
     * dName 终点名称
     */
    private void openGaoDeMap() {
        // 高德地图要求:目的地的经纬度是必须的，目的地的地名是可选的
        double dLat = 30.607346;//"武汉火车站";
        double dLon = 114.4245;//"武汉火车站";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        String urlStr = "amapuri://route/plan/?" + "dlat=" + dLat + "&dlon=" + dLon
                + "&dev=1&t=2";
        intent.setData(Uri.parse(urlStr));
        startActivity(intent);
    }

    /**
     * https://lbsyun.baidu.com/index.php?title=uri/api/android
     * 打开百度地图（公交出行，起点位置使用地图当前位置）
     * <p>
     * mode = transit（公交）、driving（驾车）、walking（步行）和riding（骑行）. 默认:driving
     * 当 mode=transit 时 ： sy = 0：推荐路线 、 2：少换乘 、 3：少步行 、 4：不坐地铁 、 5：时间短 、 6：地铁优先
     * <p>
     * dLat  终点纬度
     * dLon  终点经度
     * dName 终点名称
     */
    private void openBaiduMap() {
        double dLat = 30.607346;
        double dLon = 114.4245;
        String dName = "武汉火车站";
        if (!Double.isNaN(dLat) && !Double.isNaN(dLon)) {
            dName = dLat + "," + dLon;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String sUri = "baidumap://map/direction?" +
                "destination=" + dName + "&coord_type=wgs84&mode=walking&sy=3&src="
                + R.string.app_name;
        intent.setData(Uri.parse(sUri));
        startActivity(intent);
    }
}