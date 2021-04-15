package com.demo.myhttp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.myhttp.databinding.ActivityMainBinding;

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
        ApiRvAdapter apiRvAdapter = new ApiRvAdapter(mMainBinding.rvApi, R.layout.rv_api_item,
                items);
        apiRvAdapter.setOnItemClick(new ApiRvAdapter.OnItemClick() {
            @Override
            public void click(int item) {
                if (item == 0) {
                    startActivity(new Intent(MainActivity.this, AppStoreHomeActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, DownloadApkActivity.class));
                }
            }
        });
        mMainBinding.rvApi.addItemDecoration(new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL));
        mMainBinding.rvApi.setAdapter(apiRvAdapter);
    }
}