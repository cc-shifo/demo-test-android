package com.demo.myhttp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.demo.myhttp.databinding.ActivityDownloadApkBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.security.acl.Permission;

public class DownloadApkActivity extends AppCompatActivity {
    private ActivityDownloadApkBinding mBinding;
    private DLApkViewModel mModel;
    private final int WRITE_EXT_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(DLApkViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_download_apk);
        checkPermission();
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

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(this, permissions, WRITE_EXT_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (WRITE_EXT_PERMISSION == requestCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mBinding.tvHttpRequest, "Permission",
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }

            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mModel.cancelDownload();
    }
}