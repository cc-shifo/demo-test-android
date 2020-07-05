package com.pax.helloworld;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pax.helloworld.second.SecondActivity;
import com.pax.helloworld.third.ThirdActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    TextView mTextView;
    Button mButton;
    Button mButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.txt);
        mButton = findViewById(R.id.btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
        mButton1 = findViewById(R.id.btnGson);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
                startActivity(intent);
            }
        });
        requestStoragePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + mTextView.getTop());
        Log.d(TAG, "onResume: " + mTextView.getBottom());
        Log.d(TAG, "onResume: " + mTextView.getMeasuredHeight());
        printPackageName();
        disposeReadApks();
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        Log.d(TAG, "onWindowFocusChanged: " + mTextView.getTop());
//        Log.d(TAG, "onWindowFocusChanged: " + mTextView.getBottom());
//        Log.d(TAG, "onWindowFocusChanged: " + mTextView.getMeasuredHeight());
//    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        Log.d("onWindowFocusChanged", "onWindowFocusChanged: " + mBinding.homeView.getTop());
//        Log.d("onWindowFocusChanged", "onWindowFocusChanged: " + mBinding.homeView.getBottom());
//        Log.d("onWindowFocusChanged", "onWindowFocusChanged: " + mBinding.homeView.getHeight());


        //屏幕
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        DisplayMetrics dmReal = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dmReal);
        Log.e("WangJ", "屏幕-正确方法heightPixels：" + dm.heightPixels);
        Log.e("WangJ", "屏幕-正确方法widthPixels：" + dm.widthPixels);
        Log.e("WangJ", "屏幕-正确方法real heightPixels：" + dmReal.heightPixels);
        Log.e("WangJ", "屏幕-正确方法real widthPixels：" + dmReal.widthPixels);
        Log.e("WangJ", "屏幕-正确方法density：" + dm.density);
        Log.e("WangJ", "屏幕-正确方法densityDpi：" + dm.densityDpi);
        float dp = dm.widthPixels/1.5f;// px = dp * (dpi/160)
        Log.e("WangJ", "屏幕-正确方法widthDp(dp * (dpi/160))：" + dp);
        float x = dm.densityDpi/160f;// px = dp * (dpi/160)
        dp = dm.widthPixels/x;// px = dp * (dpi/160)
        Log.e("WangJ", "屏幕-正确方法widthDp(dp * (dm.densityDpi/160))：" + dp);

        //应用区域
        Rect outRect1 = new Rect();
        /*
         *  *注意* 如果单单获取statusBar高度而不获取titleBar高度时，
         *  getWindow().getDecorView().getWindowVisibleDisplayFrame这种方法并不推荐大家使用，
         *  因为这种方法依赖于WMS（窗口管理服务的回调）。
         *  正是因为窗口回调机制，所以在Activity初始化时执行此方法得到的top是0，这就是很多人获取到statusBar高度为0
         *  的原因。这个方法推荐在回调方法onWindowFocusChanged()中执行，才能得到预期结果。
         *  */
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        int statusBar = dm.heightPixels - outRect1.height();  //状态栏高度=屏幕高度-应用区域高度
        Log.e("WangJ", "应用区域 正确方法 状态栏-方法4:" + statusBar);//48
        Log.e("WangJ", "应用区域 正确方法 top:" + outRect1.top);//48相对应屏幕的坐标
        Log.e("WangJ", "应用区域 正确方法 bottom:" + outRect1.bottom);//1184相对应屏幕的坐标
        Log.e("WangJ", "应用区域 v height:" + outRect1.height());//1136

        //View绘制区域
        Rect outRect2 = new Rect();
        getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect2);
        //错误方法，也不叫错误吧。相对应绘制区的坐标, outRect2.top=0, bottom=1024,outRect2.height=1024
        Log.e("WangJ", "View绘制区域顶部-错误方法：" + outRect2.top);
        //这个方法获取到的view就是程序不包括标题栏的部分
        View view = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        Log.e("WangJ", "View绘制区域顶部-正确方法top：" + view.getTop());//112，相对应屏幕的坐标，要用这种方法
        //那么相对应屏幕的坐标top=112 - outRect1.top=48相对应屏幕高度 是不是等于64=action bar
        Log.e("WangJ", "View绘制区域顶部-正确方法Bottom：" + view.getBottom());//1136，应用区域的坐标，如果是
        //相对应屏幕的坐标的话，1136坐标的下面是什么？1136坐标离1184还有一段距离(48)呢！但是view.getTop()=112为什么
        // 又是相对应屏幕的坐标呢？
        Log.e("WangJ", "View绘制区域顶部-正确方法Height：" + view.getHeight());//1024
        Log.e("WangJ", "View绘制区域顶部-正确方法MeasuredHeight：" + view.getMeasuredHeight());//1024


        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId != 0) {
            //根据资源ID获取响应的尺寸值
            int statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
            Log.e("WangJ", "状态栏 正确方法 statusBarHeight1：" + statusBarHeight1);//48
        }

        int rid = getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = getResources().getIdentifier("config_showNavigationBar", "dimen", "android");
            //根据资源ID获取响应的尺寸值
            if (resourceId != 0) {
                int NavigationBarrHeight1 = getResources().getDimensionPixelSize(resourceId);
                Log.e("WangJ", "导航栏 正确方法 NavigationBarrHeight1：" + NavigationBarrHeight1);
            }
        }

        rid = getResources().getIdentifier("navigation_bar_height","dimen","android");
        if (rid != 0) {
            //根据资源ID获取响应的尺寸值
            if (rid != 0) {
                int NavigationBarrHeight2 = getResources().getDimensionPixelSize(rid);
                Log.e("WangJ", "导航栏 正确方法 NavigationBarrHeight2：" + NavigationBarrHeight2);
            }
        }
    }

    private void printPackageName() {
        PackageManager pm = getPackageManager();
        getPackageName();
        List<PackageInfo> infoList = pm.getInstalledPackages(0);
        for (int i = 0; i < infoList.size(); i++) {

            PackageInfo tempInfo = infoList.get(i);
            String s = getPackageName();
            if (s.equals(tempInfo.packageName)
                    || s.equals(tempInfo.applicationInfo.name)
                    || s.equals(tempInfo.applicationInfo.packageName)) {
                //<manifest application name>
                Log.e(TAG, "PrintPackageName: PackageInfo=" + tempInfo.packageName);
                Log.e(TAG, "PrintPackageName: PackageInfo.applicationInfo.packageName="
                        + tempInfo.applicationInfo.name);
                Log.e(TAG, "PrintPackageName: PackageInfo.applicationInfo.packageName="
                        + tempInfo.applicationInfo.packageName);
            }
        }
    }

    private void readApks() {
        List<AppInfo> cache = AppInfoStorageCache.getInstance().getPackages();
        List<AppInfo> list = new ArrayList<>(cache);
        int size = list.size();
        if (size == 1) {
            PackageManager pm = getPackageManager();
            List<PackageInfo> infoList = pm.getInstalledPackages(0);
            for (int i = 0; i < infoList.size(); i++) {
                PackageInfo tempInfo = infoList.get(i);
                list.add(new AppInfo(tempInfo.applicationInfo.packageName, null));
                LogUtil.d(tempInfo.applicationInfo.packageName);
            }

            AppInfoStorageCache appInfoStorageCache =
                    AppInfoStorageCache.getInstance();
            if (list.size() != size) {
                appInfoStorageCache.updatePackagesCache(list);
            }
        }
    }


    private void disposeReadApks() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            readApks();
        } else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final String[] permission = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permission,
                        1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                LogUtil.e(new Exception("no write storage permission"));
            }
        }
    }
}
