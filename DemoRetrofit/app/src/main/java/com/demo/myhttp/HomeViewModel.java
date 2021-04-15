package com.demo.myhttp;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.demo.myhttp.apiApps.AppsOnStore;
import com.demo.myhttp.apiApps.ReqAppsOnStore;
import com.demo.myhttp.apiApps.RespAppsOnStore;
import com.demo.myhttp.utils.GsonUtils;
import com.demo.myhttp.utils.net.RetrofitUtil;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private final MutableLiveData<String> json = new MutableLiveData<>();

    public MutableLiveData<String> getJson() {
        return json;
    }

    public void respToString() {
        ReqAppsOnStore appsOnStore = new ReqAppsOnStore();
        appsOnStore.setSn("000024P43511970100008589");
        appsOnStore.setPn("P20LH");
        appsOnStore.setPageSize(10);
        appsOnStore.setCurrentPageNo(1);

//                mBinding.tvHttpRequest.setText(toJsonStringTest(appsOnStore));
        RetrofitUtil retrofitUtil = RetrofitUtil.getInstance();
        Retrofit retrofit = retrofitUtil.getRetrofit("https://app.whty.com.cn:8443/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(retrofitUtil.createOkHttps())
                .build();


        retrofit.create(AppsOnStore.class).loadAllApps(appsOnStore)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RespAppsOnStore>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //todo release resource
                    }

                    @Override
                    public void onNext(@NonNull RespAppsOnStore respAppsOnStore) {
                        Log.d(TAG, "onNext: ");

                        json.setValue(GsonUtils.toJsonString(respAppsOnStore));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError: " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }
}
