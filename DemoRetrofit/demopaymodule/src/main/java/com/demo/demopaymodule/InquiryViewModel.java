package com.demo.demopaymodule;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.demo.demopaymodule.apicommon.CustomGsonConverter;
import com.demo.demopaymodule.apicommon.NetConfig;
import com.demo.demopaymodule.apicommon.ServerRespException;
import com.demo.demopaymodule.apicommon.ServerUrl;
import com.demo.demopaymodule.apiinquiry.ApiInquiry;
import com.demo.demopaymodule.apiinquiry.InquiryRespData;
import com.demo.demopaymodule.apipay.ApiPay;
import com.demo.demopaymodule.component.ImplCnPayment;
import com.demo.demopaymodule.utils.AssetsUtils;
import com.demo.demopaymodule.utils.HttpUtils;
import com.demo.demopaymodule.utils.LogUtils;
import com.demo.demopaymodule.utils.SHAUtils;
import com.demo.demopaymodule.utils.SSLUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

public class InquiryViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> mInquiryResult = new MutableLiveData<>();
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    public InquiryViewModel(@NonNull Application application) {
        super(application);
    }

    public void startInquire(@NonNull String orderId, @NonNull String sn) {
        ApiInquiry apiInquiry = createApi(ApiInquiry.class);
        apiInquiry.inquireStatus(createQuery(orderId, sn))
                .subscribeOn(Schedulers.io())
                .map(new Function<InquiryRespData, Integer>() {
                    @Override
                    public Integer apply(InquiryRespData inquiryRespData) throws Throwable {
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Integer integer) {
                        mInquiryResult.setValue(integer);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.e("", e);
                        if (e instanceof HttpException) {
                            //todo
                            mInquiryResult.setValue(ImplCnPayment.ErrorCode.HTTP_ERROR);
                        } else if (e instanceof ServerRespException) {
                            mInquiryResult.setValue(((ServerRespException) e).getRespCode());
                        } else {
                            //nothing
                            mInquiryResult.setValue(ImplCnPayment.ErrorCode.OTHER_ERROR);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private <T> T createApi(final Class<T> service) {
        SSLUtils.SSLParams sslParams = SSLUtils.setSslConfig(null, null, null);
        OkHttpClient client = HttpUtils.getInstance().getOkHttpClient().newBuilder()
                .readTimeout(NetConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(NetConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(NetConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                /*.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)*/
                .addNetworkInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .build();

        Retrofit retrofit = HttpUtils.getInstance().getRetrofit(ServerUrl.BASE_URL)
                .addConverterFactory(CustomGsonConverter.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client)
                .build();

        return retrofit.create(service);
    }

    /**
     * method GET
     */
    private Map<String, Object> createQuery(@NonNull String orderId, @NonNull String sn) {
        Properties properties = AssetsUtils.getProperties("signKeyProp", getApplication());
        String shaKey = (String)properties.get("signKey");
        if (shaKey == null) {
            //todo show key file error.
            LogUtils.e("sign key Null(No key)");
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(ApiInquiry.KEY_ORDER_ID, orderId);
        map.put(ApiInquiry.KEY_SN, sn);
        String data = SHAUtils.sha256(ApiInquiry.KEY_ORDER_ID + "=" + orderId + "&"
                + ApiInquiry.KEY_SN + "=" + sn + "&"
                + ApiInquiry.KEY_SN + "=" + sn
                + shaKey);
        map.put(ApiPay.KEY_SIGN, data);

        return map;
    }

    public void releaseViewModel() {
        if (mDisposables != null && !mDisposables.isDisposed()) {
            mDisposables.clear();
        }
    }
}
