package com.example.demogetapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Application;
import android.os.Bundle;
import android.view.View;

import com.example.demogetapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.helloWorldApp.setText(getApplication().toString());
        mBinding.btnGetApp.setOnClickListener(v -> {
            Application app = AppUtil.getApplication1();
            if (app != null) {
                mBinding.helloWorld.setText(app.toString());
            } else {
                mBinding.helloWorld.setText("null application");
            }
        });
    }
}