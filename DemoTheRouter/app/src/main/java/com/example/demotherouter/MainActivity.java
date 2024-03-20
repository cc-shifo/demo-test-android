package com.example.demotherouter;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demotherouter.databinding.ActivityMainBinding;
import com.example.mod0.RouterPath;
import com.therouter.TheRouter;

// 比较全的AGP和Gradle, KGP，Kotlin版本对应关系
// https://juejin.cn/post/7316698702118813747
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.btnActivity0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TheRouter.build(RouterPath.MOD0_ACTIVITY1).navigation();
            }
        });
        mBinding.btnActivity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TheRouter.build(RouterPath.MOD1_ACTIVITY1).navigation();
            }
        });
        mBinding.btnActivity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TheRouter.build(RouterPath.MOD2_ACTIVITY1).navigation();
            }
        });
    }
}