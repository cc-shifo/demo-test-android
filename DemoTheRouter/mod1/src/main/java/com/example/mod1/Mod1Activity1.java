package com.example.mod1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mod0.RouterPath;
import com.example.mod1.databinding.Activity1Mod1Binding;
import com.example.modinter.Hello1Provider;
import com.therouter.TheRouter;
import com.therouter.router.Route;

@Route(path = RouterPath.MOD1_ACTIVITY1)
public class Mod1Activity1 extends AppCompatActivity {
    private Activity1Mod1Binding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_mod1);
        mBinding = Activity1Mod1Binding.inflate(LayoutInflater.from(this));
        setContentView(mBinding.tvHelloWorld.getRootView());
        // DataBindingUtil.setContentView(this, R.layout.activity_1_mod1);
        mBinding.activity1Mod1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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