package com.demo.cmoduleipc;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.demo.basemodule.IPCWithAppModule;
import com.demo.cmoduleipc.databinding.CActivityMainBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

@Route(path = "/cModule/serviceC")
public class MainActivity extends AppCompatActivity {
    CActivityMainBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.c_activity_main);
        IPCWithAppModule ipc = (IPCWithAppModule)ARouter.getInstance().build("/appModule/serviceApp")
                .navigation();
        if (ipc != null) {
            String r = ipc.printPackName(this);
            mBinding.txtHello.setText(r);
            Snackbar.make(MainActivity.this, mBinding.txtHello, r,
                    BaseTransientBottomBar.LENGTH_SHORT).show();
        }

    }
}