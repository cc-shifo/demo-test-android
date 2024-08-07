package com.example.demoh5jsnativecomm.fileexplorer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.demoh5jsnativecomm.R;
import com.example.demoh5jsnativecomm.databinding.ActivityFileExplorerBinding;
import com.example.demoh5jsnativecomm.utils.PermissionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class FileExplorerActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "FileExplorerActivity";
    private ActivityFileExplorerBinding mBinding;
    private FileExplorerRvAdapter mRvAdapter;
    private FileIEViewModel mViewModel;
    private List<ItemData> mFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_file_explorer);
        mViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(FileIEViewModel.class);
        initData();
        initView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtil.MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return;
            }
            if (Environment.isExternalStorageManager()) {
                mViewModel.loadFileList(Environment.getExternalStorageDirectory());
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                requestCode == PermissionUtil.MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST)
                || (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
                requestCode == PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST)) {
            mViewModel.loadFileList(Environment.getExternalStorageDirectory());
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // nothing
        Log.d(TAG, "onPermissionsDenied: ");
        Log.d(TAG, "onPermissionsDenied: ");
        Log.d(TAG, "onPermissionsDenied: ");
        Log.d(TAG, "onPermissionsDenied: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mViewModel != null) {
            mViewModel.destroy();
        }
    }

    private void initData() {
        mFileList = new ArrayList<>();
        mRvAdapter = new FileExplorerRvAdapter();
        mViewModel.observeFileList().observe(this, new Observer<List<ItemData>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<ItemData> itemDataList) {
                mFileList.clear();
                mFileList.addAll(itemDataList);
                mRvAdapter.setList(itemDataList);
                mRvAdapter.notifyDataSetChanged();
            }
        });
        if (PermissionUtil.checkManagerStoragePermission(this)) {
            mViewModel.loadFileList(Environment.getExternalStorageDirectory());
        } else {
            PermissionUtil.requestManagerStoragePermission(this);
        }
    }


    private void initView() {
        mRvAdapter.setItemOnClick(position -> {
            File file = mFileList.get(position).getFile();
            if (file.isFile()) {
                Toast.makeText(FileExplorerActivity.this, file.getAbsolutePath(),
                        Toast.LENGTH_SHORT).show();
                Log.e("onClick", "onClick: " + file.getAbsoluteFile());
                return;
            }
            mViewModel.loadFileList(file);
        });
        mBinding.fileManagerTitleRv.setAdapter(mRvAdapter);
    }


}