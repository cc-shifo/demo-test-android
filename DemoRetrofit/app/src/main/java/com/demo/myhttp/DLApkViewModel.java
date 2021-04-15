package com.demo.myhttp;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.demo.myhttp.apiDownloadApk.DLReqBody;
import com.demo.myhttp.apiDownloadApk.DLRespBody;
import com.demo.myhttp.apiDownloadApk.IDownloadApk;
import com.demo.myhttp.utils.net.RetrofitUtil;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DLApkViewModel extends ViewModel {
    private final MutableLiveData<String> mRespBodyToLog = new MutableLiveData<>();
    private Disposable mDisposable;
    private Call<ResponseBody> mCall;

    public MutableLiveData<String> getRespBodyToLog() {
        return mRespBodyToLog;
    }

    public void downloadApk() {
        RetrofitUtil retrofitUtil = RetrofitUtil.getInstance();
        OkHttpClient client = retrofitUtil.createOkHttps();
        Retrofit retrofit = retrofitUtil.getRetrofit("https://app.whty.com.cn:8443/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client)
                .build();

        DLReqBody body = new DLReqBody();
        body.setSn("000024P43511970100008589");
        body.setPn("P20LH");
        body.setAppId("325332984125194242");
        retrofit.create(IDownloadApk.class).getApkUrl(body)
                .map(new Function<DLRespBody, String>() {
                    @Override
                    public String apply(DLRespBody dlRespBody) throws Throwable {
                        if (dlRespBody != null && dlRespBody.getAppInfo() != null) {
                            return dlRespBody.getAppInfo().getApkUrl();
                        }
                        return null;
                    }
                })
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String url) throws Throwable {
                        if (url != null && !url.isEmpty()) {
                            realDownload(url);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();

    }

    public void realDownload(@NonNull String url) {
        String baseUrl = "http://posmarket.oss-cn-hangzhou.aliyuncs.com/";

        String path = url.replace(baseUrl, "");


        RetrofitUtil retrofitUtil = RetrofitUtil.getInstance();
        OkHttpClient client = retrofitUtil.createOkHttp();
        Retrofit retrofit = retrofitUtil.getRetrofit(baseUrl)
                .client(client)
                .build();

        mCall = retrofit.create(IDownloadApk.class).download(path);
        try {
            //todo add range, check ETag, Content-MD5, check HEAD Method
            Response<ResponseBody> body = mCall.execute();
            InputStream inputStream = body.body().byteStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelDownload() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            if (mCall != null && !mCall.isCanceled()) {
                mCall.cancel();
            }
            mDisposable.dispose();
        }
    }
}
