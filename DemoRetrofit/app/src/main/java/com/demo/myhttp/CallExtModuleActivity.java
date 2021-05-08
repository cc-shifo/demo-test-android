package com.demo.myhttp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.demo.myhttp.common.RouterPathRest;
import com.demo.myhttp.databinding.ActivityPlayAudioBinding;

public class CallExtModuleActivity extends AppCompatActivity {
    private ActivityPlayAudioBinding mBinding;
    private CallExtModuleViewModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(CallExtModuleViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_play_audio);
        mBinding.btnStartExtModuleActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // method 1
                ARouter.getInstance().build(RouterPathRest.PAYMENT)
                        .withString("TXN_AMT", "0.02")
                        .withString("TXN_SN", "000024P43511970100008589")
                        .navigation();

                // method 2
                /*Intent intent = new Intent();
                ComponentName name = new ComponentName("com.demo.myhttp", "com.demo.demopaymodule" +
                        ".QRCPayActivity");
                intent.setComponent(name);
                startActivity(intent);*/
            }
        });
    }
}