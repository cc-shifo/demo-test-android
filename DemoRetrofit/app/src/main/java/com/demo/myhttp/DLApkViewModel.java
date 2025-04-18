package com.demo.myhttp;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.demo.myhttp.apiDownloadApk.DLReqBody;
import com.demo.myhttp.apiDownloadApk.DLRespBody;
import com.demo.myhttp.apiDownloadApk.IDownloadApk;
import com.demo.myhttp.utils.FileUtils;
import com.demo.myhttp.utils.net.RetrofitUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DLApkViewModel extends ViewModel {
    private static final String TAG = "DLApkViewModel";
    private final MutableLiveData<String> mRespBodyToLog = new MutableLiveData<>();
    private Disposable mDisposable;
    private Call<ResponseBody> mCall;


    public MutableLiveData<String> getRespBodyToLog() {
        return mRespBodyToLog;
    }

    public void downloadApk() {
        RetrofitUtil retrofitUtil = RetrofitUtil.getInstance();
        OkHttpClient client = retrofitUtil.createOkHttps();
        Retrofit retrofit = retrofitUtil.getRetrofit("your url")
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
                            String url = dlRespBody.getAppInfo().getApkUrl();
                            if (url != null && !url.isEmpty()) {
                                realDownload(url);
                            }
                            return url;
                        }
                        return null;
                    }
                })
                /*.doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String url) throws Throwable {

                    }
                })*/
                .subscribeOn(Schedulers.io())
                .subscribe();

    }

    public void realDownload(@NonNull String url) {
        String baseUrl = "http://posmarket.oss-cn-hangzhou.aliyuncs.com/";

        String path = url.replace(baseUrl, "");

        String reg = "http[s]?:(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*/";
        url.split(reg, 1);


        RetrofitUtil retrofitUtil = RetrofitUtil.getInstance();

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(
                        HttpLoggingInterceptor.Level.BODY))
                //.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel
                // (HttpLoggingInterceptor.Level.BODY))
                // .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(
                //         HttpLoggingInterceptor.Level.HEADERS))
                .build();
        Retrofit retrofit = retrofitUtil.getRetrofit(baseUrl)
                .client(client)
                .build();


        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "hw.apk");
        //todo add range, check ETag, Content-MD5, check HEAD Method
        mCall = retrofit.create(IDownloadApk.class).download("bytes=0-", path);
        Response<ResponseBody> response;
        try {
            response = mCall.execute();
        } catch (IOException e) {
            Log.e(TAG, "realDownload: ", e);
            return;
        }
        // todo check return code to see if the call is successful.
        try (ResponseBody body = response.body()) {
            assert body != null;
            try (InputStream inputStream = body.byteStream()) {
                FileUtils.writeFromStream(file, inputStream);
            }
        } catch (Exception e) {
            Log.e(TAG, "realDownload: ", e);
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
