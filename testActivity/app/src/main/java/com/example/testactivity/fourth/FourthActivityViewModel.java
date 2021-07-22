package com.example.testactivity.fourth;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;

public class FourthActivityViewModel extends ViewModel {
    private static final String TAG = "FourthActivity VM";
    private final MutableLiveData<Long> mData;
    private final CompositeDisposable mDisposable;

    public FourthActivityViewModel() {
        mData  = new MutableLiveData<>();
        mDisposable = new CompositeDisposable();
    }

    public void initSource() {
        mDisposable.clear();
        mDisposable.add(Observable.interval(2, 10, TimeUnit.SECONDS)
                .onTerminateDetach()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        Log.d(TAG, "accept: " + aLong);
                        mData.setValue(aLong);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.e(TAG, "accept: ", throwable);
                    }
                })
        );
    }

    public LiveData<Long> getData() {
        return mData;
    }

    public void destroyViewModel() {
        mDisposable.clear();
    }

}
