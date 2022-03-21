package com.example.democlipoutrect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.example.democlipoutrect.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private int mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mProgress = 0;
        mBinding.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress += 10;
                if (mProgress > 100) {
                    mProgress %= 100;
                }
                mBinding.progress.setProgress(mProgress);
            }
        });
    }


}