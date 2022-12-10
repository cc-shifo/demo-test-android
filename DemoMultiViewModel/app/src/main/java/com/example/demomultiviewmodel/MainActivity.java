package com.example.demomultiviewmodel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.demomultiviewmodel.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding, Level1ViewModel1> {
    // private ActivityMainBinding mBinding;
    // private MainViewModel mMainViewModel;
    // private MainAndroidViewModel mMainAndroidViewModel;

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
        // mMainViewModel.observeText().observe(this, new Observer<String>() {
        //     @Override
        //     public void onChanged(String s) {
        //         mBinding.helloTv.setText(s);
        //     }
        // });
        // mMainViewModel.getText();
    }
}