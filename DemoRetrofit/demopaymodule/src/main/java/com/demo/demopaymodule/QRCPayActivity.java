package com.demo.demopaymodule;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.demo.demopaymodule.common.RouterJumpPath;
import com.demo.demopaymodule.component.ICnPayment;
import com.demo.demopaymodule.component.ImplCnPayment;
import com.demo.demopaymodule.databinding.AQrcPayBinding;
import com.demo.demopaymodule.utils.LogUtils;


@Route(path = RouterJumpPath.PAYMENT)
public class QRCPayActivity extends BasicActivity<AQrcPayBinding> {
    private static final String SCAN_STARTED = "SCAN_STARTED";
    private static final String TXN_AMT = "TXN_AMT";
    private static final String TXN_SN = "TXN_SN";
    private static final String IS_BACK_CAMERA = "IS_BACK_CAMERA";
    private PayViewModel mModel;

    private boolean mScanStarted;
    @Autowired(name = "TXN_SN")
    public String mSN;
    @Autowired(name = "TXN_AMT")
    public String mAmt;
    private boolean mUseBackCamera;
    private static ICnPayment.PaymentInfoUpdateCallback mUpdateCallback;
    private boolean mCallbackExecuted;

    public static void startQRCPayActivity(@NonNull Context context, String amt, String sn) {
        Intent intent = new Intent(context, QRCPayActivity.class);
        // intent.setComponent(new ComponentName("demoyouxing",
        //         "QRCPayActivity"));

        intent.putExtra(TXN_AMT, amt);
        intent.putExtra(TXN_SN, sn);
        intent.putExtra(IS_BACK_CAMERA, true);
        context.startActivity(intent);
    }

    public static ICnPayment.PaymentInfoUpdateCallback getUpdateCallback() {
        return mUpdateCallback;
    }

    public static void setUpdateCallback(ICnPayment.PaymentInfoUpdateCallback updateCallback) {
        QRCPayActivity.mUpdateCallback = updateCallback;
    }

    @Override
    protected AQrcPayBinding bindContentView() {
        AQrcPayBinding binding = DataBindingUtil.setContentView(this,
                R.layout.a_qrc_pay);
        //mModel = new ViewModelProvider(this).get(PayViewModel.class);
        // mModel = new ViewModelProvider(this,
        //         ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
        //         .get(PayViewModel.class);
        mModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(PayViewModel.class);
        ARouter.getInstance().inject(this);
        return binding;
    }

    @Override
    protected void initData() {
        //nothing
        Intent intent = getIntent();
        if (intent != null) {
            mAmt = intent.getStringExtra(TXN_AMT);
            mSN = intent.getStringExtra(TXN_SN);
            mUseBackCamera = intent.getBooleanExtra(IS_BACK_CAMERA, true);

            //todo test
            // mAmt = "0.01";
            // mSN = "000024P43511970100008589";
            // mUseBackCamera = true;
        }
        LogUtils.d("getPackageCodePath: " + this.getPackageCodePath());
        LogUtils.d("getPackageName: " + this.getPackageName());
        LogUtils.d("getCallingPackage: " + this.getCallingPackage());
        LogUtils.d("getLocalClassName: " + this.getLocalClassName());
        LogUtils.d("getCallingActivity: " + this.getCallingActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LogUtils.d("getDataDir().getPath: " + this.getDataDir().getPath());
            LogUtils.d("getDataDir().getAbsolutePath: " + this.getDataDir().getAbsolutePath());
        }
        LogUtils.d("getPackageResourcePath: " + this.getPackageResourcePath());
        LogUtils.d("getCacheDir().getPath(): " + this.getCacheDir().getPath());
        LogUtils.d("getCacheDir().getAbsolutePath(): " + this.getCacheDir().getAbsolutePath());
        LogUtils.d("getComponentName().getShortClassName(): " + this.getComponentName().getShortClassName());
        LogUtils.d("etExternalCacheDir().getPath(): " + this.getExternalCacheDir().getPath());
        LogUtils.d("getExternalCacheDir().getAbsolutePath(): " + this.getExternalCacheDir().getAbsolutePath());

    }

    @Override
    protected void initView() {
        mBinding.activityCustomToolbar.navBackIv.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() == 0) {
                finish();
            } else {
                fragmentManager.popBackStack();
            }
        });
        mBinding.activityCustomToolbar.tittleTv.setText(R.string.qrc_pay);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScanStarted = savedInstanceState.getBoolean(SCAN_STARTED);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallbackExecuted && mUpdateCallback != null) {
            mUpdateCallback.onPaymentFinish(ImplCnPayment.ErrorCode.OTHER_ERROR, null, null);
        }
        mModel.releaseViewModel();
    }

}