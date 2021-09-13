package com.whty.smartpos.typaysdk.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whty.smartpos.pay.aidl.IPosTradeListener;
import com.whty.smartpos.pay.aidl.ITYPayManager;
import com.whty.smartpos.typaysdk.inter.ITYPayManagerListener;
import com.whty.smartpos.typaysdk.inter.ITYSmartPosSdkManager;
import com.whty.smartpos.typaysdk.model.RequestParams;
import com.whty.smartpos.typaysdk.model.ResponseCode;
import com.whty.smartpos.typaysdk.model.ResponseParams;

import java.util.concurrent.atomic.AtomicBoolean;

public class TYSmartPosApiImpl implements ITYSmartPosSdkManager {

    private static final String TAG = "TYSmartPosApiImpl";
    // private final static String TY_SDK_FAIL_ACTION = "com.whty.smartpos.pay.failaction";

    private static TYSmartPosApiImpl mInstance;
    private Context mContext;
    private String mTransId;
    private ITYPayManagerListener mTyPayManagerListener;
    private AtomicBoolean mNeedNotifyResult;


    private ITYPayManager mInterface;
    private IBinder mBinder;
    private final IPosTradeListener posTradeListener = new IPosTradeListener.Stub() {
        @Override
        public void onResult(String msg) throws RemoteException {
            ResponseParams responseParams;
            if (msg != null) {
                responseParams = new Gson().fromJson(msg, new TypeToken<ResponseParams>() {
                }.getType());
            } else {
                responseParams = new ResponseParams();
                responseParams.setTransId(mTransId);
                responseParams.setRespCode(ResponseCode.ERR_CODE_EXCEPTION);
            }

            // 正常逻辑执行回调
            if (mTyPayManagerListener != null && mNeedNotifyResult.get()) {
                mTyPayManagerListener.onResult(responseParams);
                mNeedNotifyResult.set(false);
            }
        }
    };
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "binderDied: ");
            try {
                mBinder.unlinkToDeath(mDeathRecipient, 0);
                mInterface.releasePosTradeListener(posTradeListener);
                mContext.unbindService(connection);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mBinder = null;
            mInterface = null;

            // 从doTrans执行后到结果回调被调用之前的这个期间，支付APP端进程挂了，SDK通知调用者交易失败。
            if (mNeedNotifyResult.get()) {
                ResponseParams responseParams = new ResponseParams();
                responseParams.setTransId(mTransId);
                responseParams.setRespCode(ResponseCode.ERR_CODE_EXCEPTION);
                mTyPayManagerListener.onResult(responseParams);
                mNeedNotifyResult.set(false);
            }

        }
    };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInterface = ITYPayManager.Stub.asInterface(service);
            mBinder = mInterface.asBinder();
            // Intent i = new Intent(TYPayManagerAction.TY_SERVICE_INIT_RESULT_ACTION);
            // i.getBooleanExtra("initResult", false);
            // i.putExtra("initResult", true);
            // mContext.sendBroadcast(i);
            try {
                mBinder.linkToDeath(mDeathRecipient, 0);
                mInterface.setPosTradeListener(posTradeListener);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mTyPayManagerListener != null) {
                ResponseParams responseParams = new ResponseParams();
                responseParams.setRespCode("00");
                responseParams.setRespMsg("初始化成功！");
                mTyPayManagerListener.onResult(responseParams);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
        }
    };
    // private final BroadcastReceiver receiver;

    private TYSmartPosApiImpl(/*Context ctx*/) {
        // mContext = ctx.getApplicationContext();
        // IntentFilter intentFilter = new IntentFilter();
        // intentFilter.addAction(TY_SDK_FAIL_ACTION);
        // receiver = new BroadcastReceiver() {
        //     @Override
        //     public void onReceive(Context context, Intent intent) {
        //         String action = intent.getAction();
        //         if (TY_SDK_FAIL_ACTION.equals(action)) {
        //             String code = intent.getStringExtra("resultCode");
        //             String msg = intent.getStringExtra("resultMsg");
        //             // returnTradeInfo(code, msg);
        //         }
        //     }
        // };
        // mContext.registerReceiver(receiver, intentFilter);
    }

    public static TYSmartPosApiImpl getInstance(/*Context ctx*/) {
        if (mInstance == null) {
            mInstance = new TYSmartPosApiImpl(/*ctx*/);
        }

        return mInstance;
    }

    @Override
    public void setTYPayManagerListener(ITYPayManagerListener tyPayManagerListener) {
        mTyPayManagerListener = tyPayManagerListener;
    }

    @Override
    public void init(@NonNull Context ctx) {
        mContext = ctx.getApplicationContext();
        Intent intent = new Intent();
        intent.setAction("com.whty.smartpos.paysdk.TYPayService.AIDL");
        intent.setPackage("com.whty.smartpos.pay");
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        mNeedNotifyResult = new AtomicBoolean(false);
        // 错误用法：返回值并不能说明绑定成功
        // boolean result = mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        // if (result) {
        //     Log.e(TAG, "绑定服务成功！");
        // } else {
        //     Log.e(TAG, "绑定服务失败！");
        //     // Intent i = new Intent(TYPayManagerAction.TY_SERVICE_INIT_RESULT_ACTION);
        //     // i.putExtra("initResult", false);
        //     // mContext.sendBroadcast(i);
        //     // isServiceBind = false;
        //     if (mTyPayManagerListener != null) {
        //         ResponseParams responseParams = new ResponseParams();
        //         responseParams.setRespCode("FF");
        //         responseParams.setRespMsg("初始化失败！");
        //         mTyPayManagerListener.onResult(responseParams);
        //     }
        // }
    }

    @Override
    public void doTrans(@NonNull RequestParams params) {
        if (params.getTransId() == null) {
            ResponseParams responseParams = new ResponseParams();
            responseParams.setTransId(params.getTransId());
            responseParams.setRespCode(ResponseCode.ERR_CODE_EXCEPTION);
            mTyPayManagerListener.onResult(responseParams);
            return;
        }

        mNeedNotifyResult.set(true);
        mTransId = params.getTransId();
        try {
            mInterface.doTrans(new Gson().toJson(params));
        } catch (RemoteException e) {
            e.printStackTrace();
            ResponseParams responseParams = new ResponseParams();
            responseParams.setTransId(params.getTransId());
            responseParams.setRespCode(ResponseCode.ERR_CODE_EXCEPTION);
            mTyPayManagerListener.onResult(responseParams);
            mNeedNotifyResult.set(false);
        }
    }

    @Override
    public void release() {
        try {
            if (mTyPayManagerListener != null && mNeedNotifyResult.get()) {
                ResponseParams responseParams = new ResponseParams();
                responseParams.setTransId(mTransId);
                responseParams.setRespCode(ResponseCode.ERR_CODE_EXCEPTION);
                mTyPayManagerListener.onResult(responseParams);
                mNeedNotifyResult.set(false);
            }

            if (mBinder != null) {
                mBinder.unlinkToDeath(mDeathRecipient,0);
            }
            if (mInterface != null) {
                mInterface.releasePosTradeListener(posTradeListener);
            }
            // mContext.unregisterReceiver(receiver);
            mContext.unbindService(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBinder = null;
        mInterface = null;
        mTyPayManagerListener = null;
        mContext = null;
    }

    // protected void returnTradeInfo(String resultCode, String msg) {
    //     if (mTyPayManagerListener == null) {
    //         return;
    //     }
    //
    // }
}
