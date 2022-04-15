package com.example.demofilemediastore;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.demofilemediastore.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        checkPermissionTest();
        initExtPri();
    }

    private void initExtPri() {
        TestAppSpecificFile testAppSpecificFile = new TestAppSpecificFile();
        mMainBinding.btnCreateExtPri.setOnClickListener(view ->
                testAppSpecificFile.createExternalStoragePrivateFile(MainActivity.this));
        mMainBinding.btnCreateExtPriSub.setOnClickListener(view ->
                testAppSpecificFile.createExternalStoragePrivatePicture(MainActivity.this));
        mMainBinding.btnCreateInRoot.setOnClickListener(view ->
                testAppSpecificFile.createFileInRoot(MainActivity.this));

    }

    private void initSAF() {
        TestStorageAccessFramework testStorageAccessFramework = new TestStorageAccessFramework();
        mMainBinding.btnSafCreate.setOnClickListener(view -> {
            testStorageAccessFramework.createFile(MainActivity.this, null);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TestStorageAccessFramework.CREATE_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                // Perform operations on the document using its URI.
            }
        } else if (requestCode == MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // TODO 已获取全局权限，可使用File进行文件操作
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RQC_LEGACY_READ_WRITE) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You must allow all the permissions.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } else if (requestCode == RQC_Q_READ_WRITE) {

        }
    }


    private static final int RQC_LEGACY_READ_WRITE = 0;
    private static final int RQC_Q_READ_WRITE = 1;
    private static final int MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 3;

    private void checkPermissionTest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestStoragePermissionApi30();
        } else {
            List<String> permissionsToRequire = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission
                    .READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequire.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequire.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            String[] list = new String[permissionsToRequire.size()];
            if (!permissionsToRequire.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequire
                        .toArray(list), RQC_LEGACY_READ_WRITE);
            }
        }

    }

    @RequiresApi(30)
    private void requestStoragePermissionApi30() {
        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
        }
    }


}