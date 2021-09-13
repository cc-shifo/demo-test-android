package com.demo.commapp;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whty.smartpos.qbcommapp.R;
import com.whty.smartpos.typaysdk.BuildConfig;
import com.whty.smartpos.typaysdk.model.RequestParams;
import com.whty.smartpos.typaysdk.model.ResponseCode;
import com.whty.smartpos.typaysdk.model.ResponseParams;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommService extends LifecycleService {
    private static final String TAG = "CommService";
    private static final int MAX_SIZE = 2048;
    private static final String FLAG_QB_COMM_APP_DEBUG = "FLAG_QB_COMM_APP_DEBUG";
    private static boolean mEnableDebug;
    private static final RemoteCallbackList<IMessageListener> mRemoteCallbackList =
            new RemoteCallbackList<>();
    private ExecutorService mExecutor;
    private AtomicBoolean mAlive;
    private String mTranId;
    private Socket mWorkingSocket;
    private IServerBinder mIServerBinder;
    private StringBuilder mMessageBuilder;

    public static boolean isEnableDebug() {
        return mEnableDebug;
    }

    public static void setEnableDebug(boolean enableDebug) {
        mEnableDebug = enableDebug;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        mMessageBuilder = new StringBuilder();
        mIServerBinder = new IServerBinder();
        mAlive = new AtomicBoolean(false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            setEnableDebug(intent.getBooleanExtra(FLAG_QB_COMM_APP_DEBUG, false));
        }
        start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        Log.i(TAG, "onBind: ");
        start();
        return mIServerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: ");
        stop();
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "onRebind: ");
        start();
        super.onRebind(intent);
    }

    private void start() {
        if (mAlive.get()) {
            return;
        }

        mAlive.set(true);
        mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (mAlive.get()) {
                    Log.d(TAG, "run: ");
                    init();
                }
            }
        });
    }

    private void stop() {
        mAlive.set(false);
        ServerUtil.getInstance().cancelAccept();
        if (mWorkingSocket == null) {
            ServerUtil.getInstance().close(mWorkingSocket);
            mWorkingSocket = null;
        }
        TransUtils.getInstance().destroy();
        mExecutor.shutdown();
    }

    private void init() {
        postMessage(this.getString(R.string.socket_init));
        if (!ServerUtil.getInstance().init()) {
            postMessage(this.getString(R.string.socket_init_failed));
            return;
        }
        boolean listened = ServerUtil.getInstance().listen(-1);
        if (!listened) {
            postMessage(this.getString(R.string.socket_listen_failed));
            return;
        }

        while (mAlive.get()) {
            Log.i(TAG, "init: ");
            postMessage(this.getString(R.string.socket_waiting_for_connection));
            Socket newSocket = ServerUtil.getInstance().accept();
            if (newSocket == null) {
                mAlive.set(false);
                break;
            }

            mWorkingSocket = newSocket;
            doWork();
            SystemClock.sleep(3000);
            mMessageBuilder.setLength(0);
        }
    }

    private void postMessage(String message) {
        mMessageBuilder.append(message).append("\n");
        int n = mRemoteCallbackList.beginBroadcast();
        if (n > 0) {
            IMessageListener listener = mRemoteCallbackList.getBroadcastItem(0);
            try {
                listener.postMessage(mMessageBuilder.toString());
            } catch (RemoteException remoteException) {
                Log.e(TAG, "postMessage: ", remoteException);
            }
        }
        mRemoteCallbackList.finishBroadcast();
    }

    private void setMessage(String message) {
        mMessageBuilder.append(message).append("\n");
        int n = mRemoteCallbackList.beginBroadcast();
        if (n > 0) {
            IMessageListener listener = mRemoteCallbackList.getBroadcastItem(0);
            try {
                listener.setMessage(message);
            } catch (RemoteException remoteException) {
                Log.e(TAG, "postMessage: ", remoteException);
            }
        }
        mRemoteCallbackList.finishBroadcast();
    }

    private void doWork() {
        byte[] bytes = new byte[MAX_SIZE];
        postMessage(this.getString(R.string.socket_reading));
        int n = ServerUtil.getInstance().read(mWorkingSocket, bytes);
        if (n < 0) {
            postMessage(this.getString(R.string.socket_read_failed));
            return;
        }

        postMessage(this.getString(R.string.trans_request));
        String jsonRequest = new String(Arrays.copyOf(bytes, n), StandardCharsets.UTF_8);
        if (BuildConfig.DEBUG || mEnableDebug) {
            Log.d(TAG, "read: " + jsonRequest);
        }

        ResponseParams responseParams = startTrans(jsonRequest);
        String jsonResponse = new Gson().toJson(responseParams);
        if (BuildConfig.DEBUG || mEnableDebug) {
            Log.d(TAG, "write: " + jsonResponse);
        }

        postMessage(this.getString(R.string.socket_writing));
        byte[] data = jsonResponse.getBytes(StandardCharsets.UTF_8);
        if (data.length > MAX_SIZE) {
            Log.e(TAG, "doWork: " + data.length + " > MAX_SIZE");
            postMessage(this.getString(R.string.socket_writing_failed_over_limit));
            responseParams.setTransId(mTranId);
            responseParams.setRespCode(ResponseCode.ERR_CODE_EXCEPTION);
            return;
        }

        n = ServerUtil.getInstance().write(mWorkingSocket, data);
        if (n != data.length) {
            Log.e(TAG, "doWork: write " + n + "bytes, but length of data is " + data.length);
            postMessage(this.getString(R.string.socket_writing_failed));
        }

        // write(socket, jsonResponse.getBytes(StandardCharsets.UTF_8));
        ServerUtil.getInstance().close(mWorkingSocket);
        mWorkingSocket = null;
    }

    private ResponseParams startTrans(String jsonRequest) {
        RequestParams requestParams = new Gson().fromJson(jsonRequest,
                new TypeToken<RequestParams>() {
                }.getType());
        mTranId = requestParams.getTransId();
        // boolean result = TransUtils.getInstance().init(QBCommAppService.this);
        // if (!result) {
        //     ResponseParams responseParams = new ResponseParams();
        //     responseParams.setTransId(mTranId);
        //     responseParams.setRespCode(ResponseCode.ERR_CODE_EXCEPTION);
        //     return responseParams;
        // }

        return TransUtils.getInstance().doTrans(CommService.this, requestParams,
                new TransUtils.IListener() {
            @Override
            public void postMessage(@NonNull String message) {
                CommService.this.postMessage(message);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        stop();
    }

    private static class IServerBinder extends IServerEndCaller.Stub {
        @Override
        public void registerMessageListener(IMessageListener listener) throws RemoteException {
            mRemoteCallbackList.register(listener);
        }

        @Override
        public void unregisterMessageListener(IMessageListener listener) throws RemoteException {
            mRemoteCallbackList.unregister(listener);
        }
    }
}