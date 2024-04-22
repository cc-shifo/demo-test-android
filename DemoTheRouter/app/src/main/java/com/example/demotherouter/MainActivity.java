package com.example.demotherouter;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demotherouter.databinding.ActivityMainBinding;
import com.example.mod0.RouterPath;
import com.example.modinter.Hello1Provider;
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

        mBinding.btnModInterface1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hello1Provider p = TheRouter.get(Hello1Provider.class);
                Hello1Provider p = TheRouter.get(Hello1Provider.class, 1, 2, "3");
                if (p != null) {
                    int a = p.test1ParamA();
                    int b = p.test1ParamB();
                    String c = p.test1ParamC();
                    mBinding.tvHelloWorld.setText(p.test1() + ": " + p +
                            "," + a + ", " + b + ", " + c);
                }
            }
        });
        mBinding.btnModInterface1Single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hello1Provider p = TheRouter.get(Hello1Provider.class);
                // 内部单例key使用了参数，不同参数将导致不同对象。
                Hello1Provider p = TheRouter.get(Hello1Provider.class, 4, 5, "6");
                // Hello1Provider p = TheRouter.get(Hello1Provider.class, 1, 2, "3");
                if (p != null) {
                    int a = p.test1ParamA();
                    int b = p.test1ParamB();
                    String c = p.test1ParamC();
                    mBinding.tvHelloWorld.setText(p.test1() + ": single?" + p +
                            "," + a + ", " + b + ", " + c);
                }
            }
        });
    }
}