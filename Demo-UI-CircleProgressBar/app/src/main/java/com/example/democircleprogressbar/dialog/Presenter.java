package com.example.democircleprogressbar.dialog;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Presenter  {
    public void doProcess(@NonNull final SecondActivityListener listener) {
        Observable.intervalRange(1, 4, 1, 5, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {
                if (aLong == 1) {
                    listener.showDialog("Processing");
                } else if (aLong == 2) {
                    listener.showDialog("Sending");
                }
                else if (aLong == 3) {
                    listener.showDialog("Receiving");
                } else if (aLong == 4) {
                    listener.showDialog("Success");
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                listener.dismissDialog();
            }
        });
    }
}
