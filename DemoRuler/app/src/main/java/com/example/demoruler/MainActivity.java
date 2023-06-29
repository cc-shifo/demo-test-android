package com.example.demoruler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.demoruler.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private float mV1H = -30.6f;
    private float mV2V = -45f;
    private float mV3Dir = 85f;

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


        mBinding.btnCenterHValue.setOnClickListener(v -> {
            String s = mBinding.etRulerCurrentVal.getText().toString();
            if (s.isEmpty()) {
                s = "0";
            }
            float d = Float.parseFloat(s);
            mV1H += d;
            mBinding.demoCenterView.setCurrentValue(mV1H, mV2V, mV3Dir);
        });
        mBinding.btnCenterVValue.setOnClickListener(v -> {
            String s = mBinding.etRulerCurrentVal.getText().toString();
            if (s.isEmpty()) {
                s = "0";
            }
            float d = Float.parseFloat(s);
            mV2V += d;
            mBinding.demoCenterView.setCurrentValue(mV1H, mV2V, mV3Dir);
        });
        mBinding.btnCenterDirectValue.setOnClickListener(v -> {
            String s = mBinding.etRulerCurrentVal.getText().toString();
            if (s.isEmpty()) {
                s = "0";
            }
            float d = Float.parseFloat(s);
            mV3Dir += d;
            mBinding.demoCenterView.setCurrentValue(mV1H, mV2V, mV3Dir);
        });
    }
}