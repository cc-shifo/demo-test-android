package com.demo.demopaymodule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.demo.demopaymodule.apicommon.ServerRespCode;
import com.demo.demopaymodule.common.RouterJumpPath;
import com.demo.demopaymodule.component.ICnPayment;
import com.demo.demopaymodule.component.ImplCnPayment;
import com.demo.demopaymodule.databinding.AQrcPayBinding;
import com.demo.demopaymodule.dialog.ProcessStateDialog;
import com.demo.demopaymodule.dialog.ProcessStateView;
import com.demo.demopaymodule.utils.LogUtils;


@Route(path = RouterJumpPath.PAYMENT)
public class QRCPayActivity extends BasicActivity<AQrcPayBinding> {
    private static final String SCAN_STARTED = "SCAN_STARTED";
    private static final String TXN_AMT = "TXN_AMT";
    private static final String TXN_SN = "TXN_SN";
    private static final String IS_BACK_CAMERA = "IS_BACK_CAMERA";
    private PayViewModel mModel;
    private ProcessStateDialog mProcessStateDialog;

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
        mProcessStateDialog = new ProcessStateDialog(this);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScanStarted = savedInstanceState.getBoolean(SCAN_STARTED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mScanStarted) {
            mScanStarted = true;
            mModel.getPaymentResult().observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    mCallbackExecuted = false;
                    if (integer == ServerRespCode.SUCCESS) {
                        mProcessStateDialog.dismiss();
                        if (mUpdateCallback != null) {
                            mProcessStateDialog.dismiss();
                            mUpdateCallback.onPaymentFinish(ImplCnPayment.ErrorCode.SUCCESS,
                                    mModel.getChannel(),
                                    mModel.getOrderId());
                            mCallbackExecuted = true;
                            finish();
                        }
                    } else if (integer == ServerRespCode.REQUIRE_PAYMENT_PWD) {
                        //todo inquire txt status
                        mProcessStateDialog.setState(ProcessStateView.State.STATE_PROCESSING,
                                "等待用户输入密码", null);
                        mModel.startInquiry();
                    } else if (integer == ImplCnPayment.ErrorCode.SHOULD_INQUIRE_ERROR) {
                        mProcessStateDialog.setState(ProcessStateView.State.STATE_PROCESSING,
                                "等待用户输入密码", null);
                    } else {
                        mProcessStateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        });
                        mProcessStateDialog.setState(ProcessStateView.State.STATE_FAILED,
                                "error: " + integer.toString(), null);
                    }
                }
            });
            startScanQRCApp(mAmt, mUseBackCamera);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState,
                                    @NonNull PersistableBundle outPersistentState) {
        outState.putBoolean(SCAN_STARTED, mScanStarted);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // value integer 2 is defined by external activity.
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            String qrc = data.getStringExtra("result");
            if (qrc != null && !qrc.isEmpty()) {
                LogUtils.d("barcode: " + qrc);
                mProcessStateDialog.setState(ProcessStateView.State.STATE_PROCESSING, "processing"
                        , null);
                mProcessStateDialog.show();
                mModel.startPay(mAmt, qrc, mSN);
            } else {
                QRCPayActivity.this.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProcessStateDialog != null && mProcessStateDialog.isShowing()) {
            mProcessStateDialog.dismiss();
        }
        if (!mCallbackExecuted && mUpdateCallback != null) {
            mUpdateCallback.onPaymentFinish(ImplCnPayment.ErrorCode.OTHER_ERROR, null, null);
        }
        mModel.releaseViewModel();
    }

    private void startScanQRCApp(String amt, boolean backCamera) {
        LogUtils.e("startScanQRCApp");
        Intent it = new Intent("android.intent.action.MAIN");
        it.setClassName("com.whty.smartpos.tycodescan",
                "com.whty.smartpos.tycodescan.ScanActivity");
        it.putExtra("type", getString(R.string.qrc_pay));
        it.putExtra("amount", amt);
        int cameraType = backCamera ? 0 : 1;
        it.putExtra("SCAN_CAMERA_ID", cameraType);
        startActivityForResult(it, 2);
    }
}