package com.demo.myhttp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.demo.myhttp.databinding.ActivityAppStoreHomeBinding;

public class AppStoreHomeActivity extends AppCompatActivity {
    private static final String TAG = "AppStoreHomeActivity";
    private ActivityAppStoreHomeBinding mBinding;
    private HomeViewModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel =  new ViewModelProvider(this).get(HomeViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_app_store_home);
        mBinding.btnStartRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.tvHttpResponse.setText(null);
                mModel.respToString();
            }
        });

        mBinding.btnStopRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo cancel
            }
        });

        mModel.getJson().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mBinding.tvHttpResponse.setText(s);
            }
        });
    }

}