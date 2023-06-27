package com.example.demoruler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.example.demoruler.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private float mVal = 1.0f;

    private long mCnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.btnSetVal.setOnClickListener(v -> {
            if (mCnt % 1 == 0) {
                mVal += 0.1;
            } else if (mCnt % 2 == 0) {
                mVal += 0.2;
            } else if (mCnt % 5 == 0) {
                mVal += 1;
            }
            mCnt++;
            mBinding.demoRulerView.setCurrentValue(mVal);
        });
    }
}