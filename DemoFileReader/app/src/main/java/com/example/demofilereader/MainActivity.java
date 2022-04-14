package com.example.demofilereader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.demofilereader.databinding.ActivityMainBinding;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initTestFilePath();
        initExtPri();
        initCheckFreeSpace();
    }

    private void initTestFilePath() {
        TestFilePath testFilePath = new TestFilePath();
        mMainBinding.btnExtPriPath.setOnClickListener(view -> {
            File file = testFilePath.getExtPriFile(MainActivity.this);
            mMainBinding.tvInfo.setText(file.getPath());
        });
        mMainBinding.btnInternalPriPath.setOnClickListener(view -> {
            File file = testFilePath.getInternalPriFile(MainActivity.this);
            mMainBinding.tvInfo.setText(file.getPath());
        });
        mMainBinding.btnContextFileList.setOnClickListener(view -> {
            List<String> list = testFilePath.getFileList(MainActivity.this);
            StringBuilder builder = new StringBuilder();
            for (String s : list) {
                builder.append(s).append('\n');
            }
            builder.append("");
            mMainBinding.tvInfo.setText(builder.toString());
        });
    }

    private void initCheckFreeSpace() {
        TestCheckFreeSpace testCheckFreeSpace = new TestCheckFreeSpace();
        mMainBinding.btnCheckFree.setOnClickListener(view -> {
            testCheckFreeSpace.checkFreeSpace(MainActivity.this);
        });
    }

    private void initExtPri() {
        // TestAppSpecificFile testAppSpecificFile = new TestAppSpecificFile();
        // mMainBinding.btnCreateExtPri.setOnClickListener(view ->
        //         testAppSpecificFile.createExternalStoragePrivateFile(MainActivity.this));
        // mMainBinding.btnCreateExtPriSub.setOnClickListener(view ->
        //         testAppSpecificFile.createExternalStoragePrivatePicture(MainActivity.this));
    }
}