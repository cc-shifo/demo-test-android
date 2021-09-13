package com.demo.commapp;

import android.content.Context;
import android.os.ConditionVariable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.whty.smartpos.qbcommapp.R;
import com.whty.smartpos.typaysdk.impl.TYSmartPosApiImpl;
import com.whty.smartpos.typaysdk.inter.ITYPayManagerListener;
import com.whty.smartpos.typaysdk.inter.ITYSmartPosSdkManager;
import com.whty.smartpos.typaysdk.model.RequestParams;
import com.whty.smartpos.typaysdk.model.ResponseCode;
import com.whty.smartpos.typaysdk.model.ResponseParams;

public class TransUtils {
    private static final String TAG = "TransUtils";
    private static final String SUCCESS_CODE = "00";
    private ITYSmartPosSdkManager itySmartPosSdkManager;
    private ConditionVariable mVariable;
    private ResponseParams[] mResponseParams;
    private static TransUtils mInstance;

    public interface IListener {
        void postMessage(@NonNull String message);
    }

    private TransUtils() {
    }

    public static synchronized TransUtils getInstance() {
        if (mInstance == null) {
            mInstance = new TransUtils();
        }

        return mInstance;
    }

    // step1
    private boolean init(@NonNull Context context) {
        if (mVariable == null) {
            mVariable = new ConditionVariable(false);
        }
        mResponseParams = new ResponseParams[1];
        itySmartPosSdkManager = TYSmartPosApiImpl.getInstance();
        itySmartPosSdkManager.setTYPayManagerListener(new ITYPayManagerListener() {
            @Override
            public void onResult(ResponseParams respParams) {
                if (BuildConfig.DEBUG || CommService.isEnableDebug()) {
                    Log.d(TAG, "onResult thread" + Thread.currentThread().getId() +
                            "respParams = " + new Gson().toJson(respParams));
                }

                mResponseParams[0] = respParams;
                mVariable.open();
            }
        });
        if (BuildConfig.DEBUG || CommService.isEnableDebug()) {
            Log.d(TAG, "init thread" + Thread.currentThread().getId());
        }
        itySmartPosSdkManager.init(context);
        mVariable.block();
        mVariable.close();
        return (/*opened &&*/ mResponseParams[0] != null && SUCCESS_CODE.equals(mResponseParams[0]
                .getRespCode()));
    }

    // step2
    @NonNull
    public ResponseParams doTrans(@NonNull Context context,
                                  @NonNull RequestParams requestParams,
                                  @NonNull IListener listener) {
        listener.postMessage(context.getString(R.string.trans_binding_payment_app));
        cancelBlocking();
        boolean result = init(context);
        if (!result) {
            ResponseParams responseParams = new ResponseParams();
            responseParams.setTransId(requestParams.getTransId());
            responseParams.setRespCode(ResponseCode.ERR_CODE_EXCEPTION);
            return responseParams;
        }

        listener.postMessage(context.getString(R.string.trans_making_trans));
        mResponseParams[0] = null;
        itySmartPosSdkManager.doTrans(requestParams);
        // 当前支付APP框架无法解决通讯APP收不到支付APP响应的问题，通讯APP也不能确定最大阻塞时间。
        mVariable.block();
        mVariable.close();
        itySmartPosSdkManager.release();
        itySmartPosSdkManager = null;
        mVariable = null;
        if (mResponseParams[0] == null) {
            ResponseParams responseParams = new ResponseParams();
            responseParams.setTransId(requestParams.getTransId());
            responseParams.setRespCode(ResponseCode.ERR_CODE_EXCEPTION);
            return responseParams;
        }
        return mResponseParams[0];
    }

    // step exception
    public void cancelBlocking() {
        if (mVariable != null) {
            mVariable.open();
        }
        if (itySmartPosSdkManager != null) {
            itySmartPosSdkManager.release();
        }
    }

    // step3
    public void destroy() {
        cancelBlocking();
    }

}
