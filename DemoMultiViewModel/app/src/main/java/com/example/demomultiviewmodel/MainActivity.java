package com.example.demomultiviewmodel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.demomultiviewmodel.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private MainViewModel mMainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViewData();
        initView();
    }

    private void initViewData() {
        mMainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private void initView() {
        mMainViewModel.observeText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mBinding.helloTv.setText(s);
            }
        });
        mMainViewModel.getText();
    }
}