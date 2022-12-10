package com.example.demomultiviewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.example.demomultiviewmodel.base.BaseActivity;
import com.example.demomultiviewmodel.databinding.ActivityMainBinding;
import com.example.demomultiviewmodel.sample.SampleActivity;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViewData();
        initView();
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViewData() {
        // mMainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // mMainViewModel = new ViewModelProvider(this, BaseViewModelFactory.getInstance())
        //         .get(MainViewModel.class);
        // mMainAndroidViewModel = ViewModelProvider.AndroidViewModelFactory
        //         .getInstance(getApplication()).create(MainAndroidViewModel.class);

    }

    @Override
    protected void initView() {
        mVM.observeText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mBinding.helloTv.setText(s);
            }
        });
        mVM.getText();

        mBinding.btnSampleViewModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SampleActivity.class);
                startActivity(intent);
            }
        });
    }
}