package com.demo.myhttp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.demo.myhttp.databinding.ActivityDownloadApkBinding;

public class DownloadApkActivity extends AppCompatActivity {
    private ActivityDownloadApkBinding mBinding;
    private DLApkViewModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(DLApkViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_download_apk);
        mBinding.btnStartRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.tvHttpResponse.setText(null);
                mModel.downloadApk();
            }
        });

        mBinding.btnStopRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo cancel
                mModel.cancelDownload();
            }
        });
    }
}