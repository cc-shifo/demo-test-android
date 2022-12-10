package com.example.demomultiviewmodel.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.example.demomultiviewmodel.R;
import com.example.demomultiviewmodel.databinding.ActivitySampleBinding;

public class SampleActivity extends AppCompatActivity {
    protected ActivitySampleBinding mBinding;
    protected SampleViewModel mVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample);
        initViewData();
        initView();
    }

    protected void initViewData() {
        mVM = SampleViewModel.getInstance(this.getViewModelStore(), 1, false);

    }

    protected void initView() {
        mVM.observeText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mBinding.sampleActivityHelloTv.setText(s);
            }
        });
        mVM.getText();
    }
}