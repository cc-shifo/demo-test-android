package com.example.demoeventbus;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.example.demoeventbus.databinding.ActivityMainBinding;
import com.example.demoeventbus.ittobus.AsyncBus;
import com.example.demoeventbus.ittobus.OttoAsyncTest;
import com.example.demoeventbus.ittobus.OttoMainTest;
import com.example.demoeventbus.ittobus.ev.AsyncMsgEvent;
import com.example.demoeventbus.ittobus.ev.MainMsgEvent;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * 结论：因为用的是Main Looper的Handler来发送数据，所以不会出现类似LiveData post的数据丢失现象。
 * 正是因为用的是Main Looper的Handler来发送数据，所以LiveEventBus不宜用于数据分发场景。
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;
    private CompositeDisposable mCompositeDisposable;
    private Disposable mDisposable;
    private AtomicBoolean mRunning;
    private Thread mThread;
    private long mTestNum;
    private MainMsgEvent mMainMsgEvent;
    private AsyncMsgEvent mAsyncMsgEvent;
    private OttoAsyncTest mOttoAsyncTest;
    private OttoMainTest mOttoMainTest;
    private Thread mOttoThread;
    private AtomicBoolean mOttoRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        EventBusMngr.getInstance().init();

        testLiveDataBusDelegateData();
        initTestOtto();

        mBinding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mThread.isInterrupted() || !mThread.isAlive()) {
                    mRunning.set(true);
                    mThread.start();
                }
            }
        });
        mBinding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mThread.isAlive()) {
                    mRunning.set(false);
                    mThread.interrupt();
                }
            }
        });

        mTestNum = 0;
        mAsyncMsgEvent = new AsyncMsgEvent("hello-0");
        mMainMsgEvent = new MainMsgEvent(mTestNum);
        mOttoAsyncTest = new OttoAsyncTest();
        mOttoMainTest = new OttoMainTest();
        AsyncBus.getInstance().register(mOttoAsyncTest);
        AsyncBus.getInstance().register(mOttoMainTest);
        mBinding.btnOttoPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOttoThread.isInterrupted() || !mOttoThread.isAlive()) {
                    mOttoRunning.set(true);
                    mOttoThread.start();
                }
            }
        });
        mBinding.btnOttoPostStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOttoThread.isAlive()) {
                    mOttoRunning.set(false);
                    mOttoThread.interrupt();
                }
            }
        });
        LiveEventBus.get(EventBusMngr.KEY_MAIN_MSG, Long.class).observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                mBinding.tvOttoMain.setText("main: " + aLong);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AsyncBus.getInstance().unregister(mOttoAsyncTest);
        AsyncBus.getInstance().unregister(mOttoMainTest);
    }

    private void testLiveDataBusDelegateData() {
        LiveEventBus.get(TAG, Long.class).observe(MainActivity.this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                mBinding.tv.setText(String.valueOf(aLong));
                Log.d(TAG, "onChanged: " + aLong);
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    Log.e(TAG, "onChanged: ", e);
                }
            }
        });
        mRunning = new AtomicBoolean(true);
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long text = 0;
                while (mRunning.get()) {
                    LiveEventBus.get(TAG, Long.class).post(text++);
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                        Log.e(TAG, "run: ", e);
                    }
                }
            }
        });


    }


    private void initTestOtto() {
        mOttoRunning = new AtomicBoolean(true);
        mOttoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mOttoRunning.get()) {
                    testOtto();
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        Log.e(TAG, "run: ", e);
                    }
                }
            }
        });
    }
    private void testOtto() {
        mAsyncMsgEvent.setMsg("hello-" + mTestNum);
        mMainMsgEvent.setMsg(mTestNum);
        AsyncBus.getInstance().post(mAsyncMsgEvent);
        AsyncBus.getInstance().post(mMainMsgEvent);
        mTestNum++;
    }

    private void testRxDelegateData() {
        mDisposable = Observable.interval(0, 100, TimeUnit.MICROSECONDS)
                                .onTerminateDetach()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Throwable {

                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Throwable {
                                        Log.e(TAG, "accept: ", throwable);
                                    }
                                });
    }
}