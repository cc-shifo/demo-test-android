package com.demo.commapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.whty.smartpos.qbcommapp.R;
import com.whty.smartpos.qbcommapp.databinding.ActivityMainBinding;
import com.whty.smartpos.typaysdk.inter.ITYSmartPosSdkManager;
import com.whty.smartpos.typaysdk.model.RequestParams;
import com.whty.smartpos.typaysdk.model.TransType;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity GSON";
    private ITYSmartPosSdkManager itySmartPosSdkManager;
    private ActivityMainBinding mBinding;
    private static final String KEY_IP = "KEY_IP";
    private static final String KEY_PORT = "KEY_PORT";
    private String mIP;
    private String mPort;
    private static final int REQUEST_CODE = 1;
    private MainViewModel mViewModel;
    private Disposable mDisposableReset;
    private long mMilliseconds;
    private static final long MIN_INTERVAL_MS = 3000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(MainViewModel.class);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            init();
        } /*else if (shouldShowRequestPermissionRationale(Manifest.permission
        .CHANGE_NETWORK_STATE)) {
            Toast.makeText(this, R.string.should_permission_allowed, Toast.LENGTH_LONG)
                    .show();
        } */ else {
            requestPermissions(new String[]{
                    Manifest.permission.CHANGE_NETWORK_STATE
            }, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted. Continue the action or workflow
                // in your app.
                init();
            } else {
                // Explain to the user that the feature is unavailable because
                // the features requires a permission that the user has denied.
                // At the same time, respect the user's decision. Don't link to
                // system settings in an effort to convince the user to change
                // their decision.
                Toast.makeText(this, R.string.should_permission_allowed, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyDown: CommService");
            moveTaskToBack(false);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: CommService");
        if (mDisposableReset != null && !mDisposableReset.isDisposed()) {
            mDisposableReset.dispose();
        }
        mViewModel.destroy();
        stopServer();
    }

    private void init() {
        // test();
        mBinding.portInput.setText(String.valueOf(ServerUtil.PORT));
        mViewModel.getIPv4().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mBinding.ipv4Input.setText(s);
                if (!s.isEmpty()) {
                    mBinding.txtMessage.setText("");
                    startServer();
                } else {
                    mBinding.txtMessage.setText(R.string.txt_disconnect);
                    stopServer();
                }
            }
        });
        mViewModel.getIPv6().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mBinding.ipv6Input.setText(s);
            }
        });

        CommStateManager.getInstance().getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mBinding.txtMessage.setText(s);
            }
        });
        mViewModel.getLocalIP();

        mBinding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long ms = SystemClock.uptimeMillis();
                long interval = ms - mMilliseconds;
                if (interval > MIN_INTERVAL_MS) {
                    mMilliseconds = ms;
                    mBinding.txtMessage.setText(R.string.reset_stopping);
                    reset();
                }
            }
        });
    }

    private void reset() {
        mDisposableReset = Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                stopServer();
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        }).onTerminateDetach()
                .delay(3, TimeUnit.MICROSECONDS)
                .onTerminateDetach()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        mBinding.txtMessage.setText(R.string.reset_starting);
                        startServer();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.e(TAG, "accept: ", throwable);
                    }
                });
    }

    private final IMessageListener mIMessageListener = new IMessageListener.Stub() {
        @Override
        public void postMessage(String message) throws RemoteException {
            CommStateManager.getInstance().getMessage().postValue(message);
        }

        @Override
        public void setMessage(String message) throws RemoteException {
            CommStateManager.getInstance().getMessage().setValue(message);
        }
    };

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            try {
                mServerEndCaller.asBinder().unlinkToDeath(mDeathRecipient, 0);
                mServerEndCaller.unregisterMessageListener(mIMessageListener);
                stopServer();
            } catch (RemoteException remoteException) {
                Log.e(TAG, "binderDied: ", remoteException);
            }
            mServerEndCaller = null;
        }
    };

    private final ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServerEndCaller = IServerEndCaller.Stub.asInterface(service);
            try {
                mServerEndCaller.asBinder().linkToDeath(mDeathRecipient, 0);
                mServerEndCaller.registerMessageListener(mIMessageListener);
            } catch (RemoteException remoteException) {
                Log.e(TAG, "onServiceConnected: ", remoteException);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected: " + name);
            mServerEndCaller = null;
        }
    };

    private IServerEndCaller mServerEndCaller;

    private void startServer() {
        Intent i = new Intent(MainActivity.this, CommService.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // startService(i);
        bindService(i, mConn, Context.BIND_AUTO_CREATE);
    }

    private void stopServer() {
        // Intent i = new Intent(MainActivity.this, CommService.class);
        // stopService(i);
        if (mServerEndCaller != null) {
            unbindService(mConn);
        }
    }

    private void test() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String gsonString;
                RequestParams requestParams = new RequestParams();
                requestParams.setTransId(TransType.LOGIN);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "LOGIN: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.SALE);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "SALE: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.UNSALE);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "UNSALE: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.REFUND);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "REFUND: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.PREAUTH);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "PREAUTH: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.UNPREAUTH);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "UNPREAUTH: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.COMPLETE);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "COMPLETE: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.UNCOMPLETE);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "UNCOMPLETE: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.SCANPAY);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "SCANPAY: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.SCANPAYREFUND);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "SCANPAYREFUND: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.SCANUNPAUTH);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "SCANUNPAUTH: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.SCANUNPREAUTH);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "SCANUNPREAUTH: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.SCANCOMPLETE);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "SCANCOMPLETE: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.TRADEQUERY);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "TRADEQUERY: ");
                Log.d(TAG, gsonString);

                requestParams = new RequestParams();
                requestParams.setTransId(TransType.SCAN);
                gsonString = new Gson().toJson(requestParams);
                Log.d(TAG, "SCAN: ");
                Log.d(TAG, gsonString);
            }
        });
        thread.start();
    }
}