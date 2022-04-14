package com.example.demofilemediastore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.demofilemediastore.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initExtPri();
    }

    private void initExtPri() {
        TestAppSpecificFile testAppSpecificFile = new TestAppSpecificFile();
        mMainBinding.btnCreateExtPri.setOnClickListener(view ->
                testAppSpecificFile.createExternalStoragePrivateFile(MainActivity.this));
        mMainBinding.btnCreateExtPriSub.setOnClickListener(view ->
                testAppSpecificFile.createExternalStoragePrivatePicture(MainActivity.this));
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
        }
    }
}