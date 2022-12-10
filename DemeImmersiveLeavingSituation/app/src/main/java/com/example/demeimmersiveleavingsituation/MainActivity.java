package com.example.demeimmersiveleavingsituation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.demeimmersiveleavingsituation.databinding.ActivityMainBinding;

import timber.log.Timber;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private AnchorEndDialog mAnchorEndDialog;
    private AlertDialog mAlertDialog;

    @Override
    protected int initLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViewData() {

    }

    @Override
    protected void initView() {
        initBtn();
    }

    private void initBtn() {
        mAlertDialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("second activity")
                .setCancelable(true)
                .create();
        mAnchorEndDialog = new AnchorEndDialog(MainActivity.this);
        mBinding.btnSecondActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
        mBinding.btnSecondDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAlertDialog.isShowing()) {
                    mAlertDialog.show();
                }
            }
        });
        mBinding.btnCusSizeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAnchorEndDialog.isShowing()) {
                    mAnchorEndDialog.show();
                }
            }
        });
    }

}