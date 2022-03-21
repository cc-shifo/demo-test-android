package com.example.demoh5jsnativecomm;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.demoh5jsnativecomm.databinding.ActivityMainBinding;
import com.example.demoh5jsnativecomm.jsinterface.NativeAPIFileSelector;
import com.example.demoh5jsnativecomm.jsinterface.NativeAPILocation;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;
    private String mMainWebUrl;

    private NativeAPILocation mNativeAPILocation;
    private NativeAPIFileSelector mNativeAPIFileSelector;
    private Disposable mDisposable;

    private final String[] LocPermissionString = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViewData();
        initView();
    }

    private void initViewData() {
        // mMainWebUrl = "http://192.168.201.82/testAndroid/testAndroid.html";
        mMainWebUrl = "file:///android_asset/test.html";
        mNativeAPILocation = new NativeAPILocation(this);
        mNativeAPIFileSelector = new NativeAPIFileSelector(this);
        requestPermissions();
    }

    private void initView() {
        initWeb();
    }


    private void initWeb() {
        setInternalWebClient();
        mBinding.demoWeb.getSettings().setJavaScriptEnabled(true);  //设置运行使用JS
        //这里添加JS的交互事件，这样H5就可以调用原生的代码
        mBinding.demoWeb.addJavascriptInterface(mNativeAPILocation,
                NativeAPILocation.API_NAME);
        mBinding.demoWeb.addJavascriptInterface(mNativeAPIFileSelector,
                NativeAPIFileSelector.API_NAME);
        mBinding.demoWeb.loadUrl(mMainWebUrl);
    }

    private void setInternalWebClient() {
        mBinding.demoWeb.setWebViewClient(new CustomWebViewClient(
                mBinding.viewMainActivity, mBinding.demoWeb));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode,
                                     @NonNull List<String> perms) {
        if (requestCode == NativeAPILocation.LOC_PERMISSIONS) {
            Log.d(TAG, "onPermissionsGranted: ");
            requestLocation();
        } else if (requestCode == NativeAPILocation.LOC_PERMISSIONS_FROM_JS) {
            setLocationForJsCall();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode,
                                    @NonNull List<String> perms) {
        // nothing
        Log.d(TAG, "onPermissionsDenied: ");
        Log.d(TAG, "onPermissionsDenied: ");
        Log.d(TAG, "onPermissionsDenied: ");
        Log.d(TAG, "onPermissionsDenied: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelLocationRequest();
        releaseDataResource();
    }

    /**
     * 发起定位请求。仅仅需要调用一次。
     */
    private void requestLocation() {
        LocationUtils.getInstance().init(this);
        LocationUtils.getInstance().requestLocation();
    }

    /**
     * 取消定位请求，是否资源。
     */
    private void cancelLocationRequest() {
        LocationUtils.getInstance().destroy();
    }

    /**
     * 有定位权限就发起定位请求，没有定位权限就就会请求权限。
     */
    public void requestPermissions() {
        if (EasyPermissions.hasPermissions(this, LocPermissionString)) {
            requestLocation();
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this,
                    getString(R.string.request_location_rationale),
                    NativeAPILocation.LOC_PERMISSIONS,
                    LocPermissionString);
        }
    }

    /**
     * release data resource.
     */
    private void releaseDataResource() {
        mNativeAPILocation.destroy();
        mNativeAPIFileSelector.destroy();
    }

    private void setLocationForJsCall() {
        requestLocation();
        mDisposable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            String loc = LocationUtils.getInstance().getLocation();
            if (!emitter.isDisposed()) {
                emitter.onNext(loc);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loc -> mBinding.demoWeb.loadUrl("javascript:jsGetLocationCallback('" +
                        loc + "')"), throwable -> Log.e(TAG, "accept: ", throwable));
    }
}