package com.example.demoh5jsnativecomm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.demoh5jsnativecomm.databinding.ActivityMainBinding;
import com.example.demoh5jsnativecomm.fileexplorer.FileExplorerActivity;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViewData();
        initView();
    }

    private void initViewData() {
    }

    private void initView() {
        mBinding.btnFilePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileExplorerActivity.class);
                startActivity(intent);
            }
        });
    }

    // 移除后期
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if (requestCode == OpenFileWebChromeClient.REQUEST_FILE_PICKER) {
        //     if (mOpenFileWebChromeClient.mFilePathCallback != null) {
        //         Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
        //                 : intent.getData();
        //         if (result != null) {
        //             String path = MediaUtility.getPath(getApplicationContext(),
        //                     result);
        //             Uri uri = Uri.fromFile(new File(path));
        //             mOpenFileWebChromeClient.mFilePathCallback
        //                     .onReceiveValue(uri);
        //         } else {
        //             mOpenFileWebChromeClient.mFilePathCallback
        //                     .onReceiveValue(null);
        //         }
        //     }
        //
        //
        //     mOpenFileWebChromeClient.mFilePathCallbacks = null;
        // }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}