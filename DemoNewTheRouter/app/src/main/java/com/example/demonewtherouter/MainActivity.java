package com.example.demonewtherouter;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demonewtherouter.databinding.ActivityMainBinding;
import com.example.mod0.RouterPath;
import com.example.modinter.Hello1Provider;
import com.therouter.TheRouter;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        // setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initTest();
    }

    private void initTest() {
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
                Hello1Provider p0 = TheRouter.get(Hello1Provider.class);
                // 内部单例key使用了参数，不同参数将导致不同对象。
                Hello1Provider p1 = TheRouter.get(Hello1Provider.class, 4, 5, "6");
                // Hello1Provider p = TheRouter.get(Hello1Provider.class, 1, 2, "3");
                if (p1 != null) {
                    int a = p1.test1ParamA();
                    int b = p1.test1ParamB();
                    String c = p1.test1ParamC();
                    mBinding.tvHelloWorld.setText(p1.test1() + ": single?" + p1 +
                            "," + a + ", " + b + ", " + c);
                }
                Hello1Provider p2 = TheRouter.get(Hello1Provider.class, 1, 2, "3");
                if (p2 != null) {
                    int a = p2.test1ParamA();
                    int b = p2.test1ParamB();
                    String c = p2.test1ParamC();
                    mBinding.tvHelloWorld.setText(p2.test1() + ": single?" + p2 +
                            "," + a + ", " + b + ", " + c);
                }

                // Hello1Provider p1 = TestClassConstructor.create(1, 2, "3");
                // if (p1 != null) {
                //     int a = p.test1ParamA();
                //     int b = p.test1ParamB();
                //     String c = p.test1ParamC();
                //     mBinding.tvHelloWorld.setText(p1.test1() + ": single?" + p1 +
                //             "," + a + ", " + b + ", " + c);
                // }
            }
        });
    }
}