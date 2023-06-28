package com.example.demoruler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.example.demoruler.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.btnPlus.setOnClickListener(v -> {
            String s = mBinding.etRulerCurrentVal.getText().toString();
            mBinding.demoRulerView.setCurrentValue(Float.parseFloat(s));
        });

        mBinding.btnMinus.setOnClickListener(v -> {
            String s = mBinding.etRulerCurrentVal.getText().toString();
            mBinding.demoRulerView.setCurrentValue(Float.parseFloat(s));
        });

        mBinding.btnRPlus.setOnClickListener(v -> {
            String s = mBinding.etRulerCurrentVal.getText().toString();
            mBinding.demoRulerRightView.setCurrentValue(Float.parseFloat(s));
        });

        mBinding.btnRMinus.setOnClickListener(v -> {
            String s = mBinding.etRulerCurrentVal.getText().toString();
            mBinding.demoRulerRightView.setCurrentValue(Float.parseFloat(s));
        });
    }
}