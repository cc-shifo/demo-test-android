package com.example.demofilemediastore;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import timber.log.Timber;

/**
 * https://juejin.cn/post/7012259637734948895
 * https://juejin.cn/post/7012262477828194340
 *
 * 1、写访问公共路径，如/sdcard/Download
 * 1)构建ContentValues
 * 2)通过ContentWrapper#getContentResolver得到ContentResolver，查询/insert（创建）得到Uri
 * 3)通过ContentResolver的open(Uri)调用得到OutputStream,即可写文件。
 *
 * 2、读取工具路径下的文件。如/sdcard/Download/com.example.helloworld/hello.txt
 * 入参参数：知道是Download下（即Environment.DIRECTORY_DOWNLOADS），知道相对路径为com.example.helloworld，
 * 知道文件名
 * 1)根据入参构建ContentValues,
 * 通过ContentResolver，和MediaStore.Downloads.EXTERNAL_CONTENT_URI先查询数据库，找到对应的图片Cursor，
 * 得到Cursor
 * 2、从Cursor里构造Uri
 * 3、从Uri构造输入流读取图片
 *
 *
 * 注意：
 * 1、Uri可以通过MediaStore或者SAF获取
 * 2、Storage Access Framework 简称SAF：存储访问框架。相当于系统内置了文件选择器，通过它可以拿到想要访问的文件
 * 信息。
 * 3、开启分区存储功能后，访问其它文件不能通过MediaStore获取，只能通过SAF
 * 4、访问其他APP在Download下创建的文件，必须使用SAF。
 * https://developer.android.google.cn/training/data-storage/shared/media#saf-other-apps-downloads
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        checkPermissionTest();
        // TODO 带判断权限后初始化
        initExtPri();
        // TODO 带判断权限后初始化
        initSelector();
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
        } else if (requestCode == TestFileSelector.PICK_TREE) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                // Perform operations on the document using its URI.
                Timber.d("uri: %s", uri.toString());
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


    private void initSelector() {
        mMainBinding.btnTreeSelector.setOnClickListener(view -> {
            TestFileSelector selector = new TestFileSelector();
            selector.openDirectory(this, null);
        });

    }

}