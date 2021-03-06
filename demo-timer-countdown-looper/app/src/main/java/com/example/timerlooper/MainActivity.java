package com.example.timerlooper;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final int mTimeoutSec = 10;
    private ProcessStateDialog mProcessStateDialog;
    private Scheduler mScheduler;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScheduler = Schedulers.newThread();
        mCompositeDisposable = new CompositeDisposable();
        mProcessStateDialog = new ProcessStateDialog(this);

        Button button = findViewById(R.id.hello_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCompositeDisposable.add(
                        Observable.create(new ObservableOnSubscribe<Integer>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                                Random random = new Random();
                                random.setSeed(10);
                                LogUtils.d("[onClick] showProgressDialog Observable: thread= "
                                        + Thread.currentThread().getId());
                                for (int i = 0; i < 50 && !mCompositeDisposable.isDisposed(); i++) {
                                    int r = random.nextInt(5);
                                    if (r > 0) {
                                        SystemClock.sleep(r * 1000);
                                        if (!emitter.isDisposed() && !mCompositeDisposable.isDisposed()) {
                                            emitter.onNext(r);
                                        }
                                    }
                                }

                                if (!emitter.isDisposed()) {
                                    emitter.onComplete();
                                }
                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer aInteger) throws Throwable {
                                        mProcessStateDialog.setState(ProcessStateView.State.STATE_PROCESSING,
                                                "hello: " + aInteger, null
                                                , true, mTimeoutSec * 1000);
                                        if (!mCompositeDisposable.isDisposed() && !mProcessStateDialog.isShowing()) {
                                            mProcessStateDialog.show();
                                        }
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Throwable {
                                        //nothing
                                    }
                                }, new Action() {
                                    @Override
                                    public void run() throws Throwable {
                                        if (mProcessStateDialog.isShowing()) {
                                            mProcessStateDialog.cancel();
                                        }
                                    }
                                }));


            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }

        if (mProcessStateDialog.isShowing()) {
            mProcessStateDialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }

        /*if (mProcessStateDialog.isShowing()) {
            mProcessStateDialog.cancel();
        }*/
        super.onBackPressed();

    }
}