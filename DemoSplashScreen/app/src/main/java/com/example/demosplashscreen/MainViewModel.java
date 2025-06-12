package com.example.demosplashscreen;

import android.content.res.Resources;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";
    private MutableLiveData<Boolean> _isInitializationCompleted = new MutableLiveData(false);

    private Disposable mDisposable;

    public MainViewModel() {
    }

    public LiveData<Boolean> getInitializationCompleted() {
        return _isInitializationCompleted;
    }

    public void startInitialization() {
        mDisposable = Observable.fromAction(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Thread.sleep(7000);

                    }
                }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Throwable {
                        Log.d(TAG, "accept: " + o);
                    }
                }, throwable -> Log.e(TAG, "accept: ", throwable), () -> {
                    Log.d(TAG, "run: ");
                    _isInitializationCompleted.setValue(true);
                });
    }

}
