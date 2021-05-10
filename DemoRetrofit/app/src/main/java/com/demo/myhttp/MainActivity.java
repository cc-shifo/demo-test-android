package com.demo.myhttp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.demo.basemodule.BusinessSDK;
import com.demo.myhttp.common.RouterPathRest;
import com.demo.myhttp.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        List<String> items = new ArrayList<>(2);
        items.add("HttpApi1");
        items.add("HttpApi2");
        items.add("external module");
        items.add("get AModule service");
        items.add("get BModule service");
        items.add("IPC: CModule and AppModule");
        ApiRvAdapter apiRvAdapter = new ApiRvAdapter(mMainBinding.rvApi, R.layout.rv_api_item,
                items);
        apiRvAdapter.setOnItemClick(new ApiRvAdapter.OnItemClick() {
            @Override
            public void click(int item) {
                if (item == 0) {
                    startActivity(new Intent(MainActivity.this, AppStoreHomeActivity.class));
                } else if (item == 1) {
                    startActivity(new Intent(MainActivity.this, DownloadApkActivity.class));
                } else if (item == 2) {
                    startActivity(new Intent(MainActivity.this, CallExtModuleActivity.class));
                } else if (item == 3) {
                    BusinessSDK businessSDK = (BusinessSDK)ARouter.getInstance()
                            .build(RouterPathRest.A_MODULE_SERVICE)
                            .navigation();
                    String r;
                    if (businessSDK != null) {
                        r = businessSDK.printPackName(MainActivity.this);
                    } else {
                        r = "null" + RouterPathRest.A_MODULE_SERVICE;
                    }
                    Snackbar.make(MainActivity.this, mMainBinding.rvApi, r,
                            BaseTransientBottomBar.LENGTH_SHORT).show();
                } else if (item == 4) {
                    BusinessSDK businessSDK = (BusinessSDK)ARouter.getInstance()
                            .build(RouterPathRest.B_MODULE_SERVICE)
                            .navigation();
                    String r;
                    if (businessSDK != null) {
                        r = businessSDK.printPackName(MainActivity.this);
                    } else {
                        r = "null" + RouterPathRest.B_MODULE_SERVICE;
                    }
                    Snackbar.make(MainActivity.this, mMainBinding.rvApi, r,
                            BaseTransientBottomBar.LENGTH_SHORT).show();
                }else if (item == 5) {
                    ARouter.getInstance().build(RouterPathRest.C_MODULE_SERVICE).navigation();
                }
            }
        });
        mMainBinding.rvApi.addItemDecoration(new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL));
        mMainBinding.rvApi.setAdapter(apiRvAdapter);
    }
}