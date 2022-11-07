package com.example.democustomsizedialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.example.democustomsizedialog.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private RightEnterDialog mRightEnterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // mRightEnterDialog = new RightEnterDialog(this, R.style.Theme_Right_Dialog);
        mRightEnterDialog = new RightEnterDialog(this, 0);
        mRightEnterDialog.create();
        mBinding.btnRightDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mRightEnterDialog.isShowing()) {
                    mRightEnterDialog.show();
                } else {
                    mRightEnterDialog.cancel();
                }
            }
        });
    }
}