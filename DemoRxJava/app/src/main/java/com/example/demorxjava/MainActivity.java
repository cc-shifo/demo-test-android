package com.example.demorxjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.demorxjava.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.btnTakeUntil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testTakeUntil();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.isDisposed();
        }
    }

    private void testTakeUntil() {
        mDisposable = Observable.interval(0, 15, TimeUnit.SECONDS)
                                // .flatMap(new Function<Long, ObservableSource<Long>>() {
                                //     @Override
                                //     public ObservableSource<Long> apply(Long aLong)
                                //             throws Throwable {
                                //         return aLong;
                                //     }
                                // })
                                .takeUntil(new Predicate<Long>() {
                                    @Override
                                    public boolean test(Long aLong) throws Throwable {
                                        Log.d(TAG, "test: " + aLong);
                                        if (aLong == 5) {
                                            return  true;
                                        } else {
                                            Thread.sleep(17000);
                                        }
                                        return false;
                                    }
                                })
                                .onTerminateDetach()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Throwable {
                                        Log.d(TAG, "accept: onNext= " + aLong);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Throwable {

                                        Log.e(TAG, "accept: throwable", throwable);
                                    }
                                }, new Action() {
                                    @Override
                                    public void run() throws Throwable {
                                        Log.d(TAG, "run: complete");
                                    }
                                });

    }
}