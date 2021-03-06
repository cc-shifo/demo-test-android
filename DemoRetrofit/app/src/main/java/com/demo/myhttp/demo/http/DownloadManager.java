package com.demo.myhttp.demo.http;

import android.os.Environment;
import android.util.Log;


import com.demo.myhttp.demo.DownloadInfo;
import com.demo.myhttp.demo.utils.CommonUtils;
import com.demo.myhttp.demo.utils.FileUtil;
import com.demo.myhttp.demo.utils.MyConstants;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 下载管理
 * Created by ${R.js} on 2018/3/22.
 */

public class DownloadManager implements DownloadProgressListener {

    private DownloadInfo info;
    private ProgressListener progressObserver;
    private File outFile;
    private Disposable mDisposable;
    private DownLoadService service;
    private long currentRead;

    private DownloadManager() {
        info = new DownloadInfo();
        outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "yaoshi.apk");
        info.setSavePath(outFile.getAbsolutePath());
    }

    public static DownloadManager getInstance() {
        return Holder.manager;
    }

    public static class Holder {
        private static DownloadManager manager = new DownloadManager();
    }

    @Override
    public void progress(long read, final long contentLength, final boolean done) {
        Log.e("progress : ", "read = " + read + "contentLength = " + contentLength);
        // 该方法仍然是在子线程，如果想要调用进度回调，需要切换到主线程，否则的话，会在子线程更新UI，直接错误
        // 如果断电续传，重新请求的文件大小是从断点处到最后的大小，不是整个文件的大小，info中的存储的总长度是
        // 整个文件的大小，所以某一时刻总文件的大小可能会大于从某个断点处请求的文件的总大小。此时read的大小为
        // 之前读取的加上现在读取的
        if (info.getContentLength() > contentLength) {
            read = read + (info.getContentLength() - contentLength);
        } else {
            info.setContentLength(contentLength);
        }
        info.setReadLength(read);

        Observable.just(1).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        if (progressObserver != null) {
                            progressObserver.progressChanged(info.getReadLength(), info.getContentLength(), done);
                        }
                    }
                });
    }

    /**
     * 开始下载
     * @param url
     */
    public void start(String url) {
        info.setUrl(url);
        final DownloadInterceptor interceptor = new DownloadInterceptor(this);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(8, TimeUnit.SECONDS);
        builder.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(CommonUtils.getBasUrl(MyConstants.DOWNLOAD_URL))
                .build();
        if (service == null) {
            service = retrofit.create(DownLoadService.class);
            info.setService(service);
        } else {
            service = info.getService();
        }

        downLoad();
    }

    /**
     * 开始下载
     */
    private void downLoad() {
        Log.e("下载：", info.toString());
        service.download("bytes=" + info.getReadLength() + "-", info.getUrl())

                /*指定线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*.retryWhen(new RetryWhenNetworkException())*/
                /* 读取下载写入文件，并把ResponseBody转成DownInfo */
                .map(new Function<ResponseBody, DownloadInfo>() {
                    @Override
                    public DownloadInfo apply(ResponseBody responseBody) throws Throwable {
                        try {
                            //写入文件
                            FileUtil.writeCache(responseBody, new File(info.getSavePath()), info);
                        } catch (IOException e) {
                            Log.e("异常:", e.toString());
                        }
                        return info;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull DownloadInfo downloadInfo) {
                        Log.e("下载", "onNext");

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("下载", "onError" + e.toString());

                    }

                    @Override
                    public void onComplete() {
                        Log.e("下载", "onCompleted");

                    }
                });
    }

    /**
     * 暂停下载
     */
    public void pause() {
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    /**
     * 继续下载
     */
    public void reStart() {
        downLoad();
    }

    /**
     * 进度监听
     */
    public interface ProgressListener {
        void progressChanged(long read, long contentLength, boolean done);
    }

    public void setProgressListener(ProgressListener progressObserver) {
        this.progressObserver = progressObserver;
    }
}
