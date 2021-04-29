package com.demo.demopaymodule;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.demo.demopaymodule.apicommon.CustomGsonConverter;
import com.demo.demopaymodule.apicommon.NetConfig;
import com.demo.demopaymodule.apicommon.ServerRespCode;
import com.demo.demopaymodule.apicommon.ServerRespException;
import com.demo.demopaymodule.apicommon.ServerUrl;
import com.demo.demopaymodule.apiinquiry.ApiInquiry;
import com.demo.demopaymodule.apiinquiry.InquiryRespData;
import com.demo.demopaymodule.apiinquiry.RetryInterceptor;
import com.demo.demopaymodule.apipay.ApiPay;
import com.demo.demopaymodule.apipay.PaymentReqData;
import com.demo.demopaymodule.apipay.PaymentRespData;
import com.demo.demopaymodule.component.ImplCnPayment;
import com.demo.demopaymodule.utils.AssetsUtils;
import com.demo.demopaymodule.utils.HttpUtils;
import com.demo.demopaymodule.utils.LogUtils;
import com.demo.demopaymodule.utils.SHAUtils;
import com.demo.demopaymodule.utils.SSLUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

public class PayViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> mPaymentResult = new MutableLiveData<>();
    private final CompositeDisposable mDisposables = new CompositeDisposable();
    private Disposable mRetryDisposable;
    private String mSN;
    private String mOrderId;
    private String mChannel;

    public PayViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Integer> getPaymentResult() {
        return mPaymentResult;
    }

    public void startPay(@NonNull String amt, @NonNull String qrCode,
                         @NonNull String sn) {
        // todo: add http client callback to listen connection event.
        // todo: add custom gson interceptor to deal with BigDecimal type.
        ApiPay apiPay = createApi(ApiPay.class);
        // apiPay.startPayment(ServerUrl.PAYMENT_PATH, createReqData(amt, qrCode, sn))
        apiPay.startPayment(createPaymentQuery(amt, qrCode, sn))
                .subscribeOn(Schedulers.io())
                .map(new Function<PaymentRespData, Integer>() {
                    @Override
                    public Integer apply(PaymentRespData paymentRespData) throws Throwable {
                        if (paymentRespData.getRespCode() == ServerRespCode.REQUIRE_PAYMENT_PWD
                                || paymentRespData.getRespCode() == ServerRespCode.SUCCESS) {
                            mSN = sn;
                            mOrderId = paymentRespData.getOrderInfo().getOrderId();
                            mChannel = paymentRespData.getOrderInfo().getChannel();
                        }
                        return paymentRespData.getRespCode();
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
                        mPaymentResult.setValue(integer);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.e("", e);
                        if (e instanceof HttpException) {
                            //todo
                            mPaymentResult.setValue(ImplCnPayment.ErrorCode.HTTP_ERROR);
                        } else if (e instanceof ServerRespException) {
                            mPaymentResult.setValue(((ServerRespException) e).getRespCode());
                        } else {
                            //nothing
                            mPaymentResult.setValue(ImplCnPayment.ErrorCode.OTHER_ERROR);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void startInquiry() {
        if (TextUtils.isEmpty(mOrderId) || TextUtils.isEmpty(mSN)) {
            mPaymentResult.setValue(ImplCnPayment.ErrorCode.DATA_ERROR);
            LogUtils.e("sn: " + mSN + "orderId: " + mOrderId);
            return;
        }

        RetryInterceptor retryInterceptor = new RetryInterceptor(10,
                3000, new RetryInterceptor.KeepInquiryCallback() {
            @Override
            public void continueInquiring() {
                if (mRetryDisposable != null) {
                    mDisposables.remove(mRetryDisposable);
                }
                mRetryDisposable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(ImplCnPayment.ErrorCode.SHOULD_INQUIRE_ERROR);
                            emitter.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Throwable {
                                mPaymentResult.setValue(integer);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Throwable {
                                mPaymentResult.setValue(ImplCnPayment.ErrorCode.EXCEPTION_ERROR);
                            }
                        });
                mDisposables.add(mRetryDisposable);
            }
        });
        ApiInquiry apiInquiry = createTryApi(ApiInquiry.class, retryInterceptor);
        apiInquiry.inquireStatus(createInquiryQuery(mOrderId, mSN))
                .subscribeOn(Schedulers.io())
                .map(new Function<InquiryRespData, Integer>() {
                    @Override
                    public Integer apply(InquiryRespData inquiryRespData) throws Throwable {
                        if (inquiryRespData.getRespCode() != ServerRespCode.SUCCESS
                                || inquiryRespData.getOrderInfo() == null) {
                            return ServerRespCode.SYSTEM_ERROR;
                        }

                        /*int status = inquiryRespData.getOrderInfo().getOrderStatus();
                        if (ServerRespCode.InquiryStatus.shouldInquiring(status)) {
                            // todo
                        }*/
                        return inquiryRespData.getRespCode();
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
                        mPaymentResult.setValue(integer);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.e("", e);
                        /*if ("closed".equals(e.getMessage())) {
                            mPaymentResult.setValue(ServerRespCode.SUCCESS);
                        } else */if (e instanceof HttpException) {
                            //todo
                            mPaymentResult.setValue(ImplCnPayment.ErrorCode.HTTP_ERROR);
                        } else if (e instanceof ServerRespException) {
                            mPaymentResult.setValue(((ServerRespException) e).getRespCode());
                        } else {
                            //nothing
                            mPaymentResult.setValue(ImplCnPayment.ErrorCode.OTHER_ERROR);
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

    private <T> T createTryApi(final Class<T> service, @NonNull RetryInterceptor interceptor) {
        SSLUtils.SSLParams sslParams = SSLUtils.setSslConfig(null, null, null);
        OkHttpClient client = HttpUtils.getInstance().getOkHttpClient().newBuilder()
                .readTimeout(NetConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(NetConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(NetConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                /*.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)*/
                .addNetworkInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = HttpUtils.getInstance().getRetrofit(ServerUrl.BASE_URL)
                .addConverterFactory(CustomGsonConverter.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client)
                .build();

        return retrofit.create(service);
    }

    /**
     * method POST
     */
    private PaymentReqData createReqData(@NonNull String amt, @NonNull String qrCode,
                                         @NonNull String sn) {
        Properties properties = AssetsUtils.getProperties("signKeyProp", getApplication());
        String shaKey = (String) properties.get("signKey");
        if (shaKey == null) {
            //todo show key file error.
            LogUtils.e("sign key Null(No key)");
            return null;
        }

        PaymentReqData reqData = new PaymentReqData();
        reqData.setAmt(new BigDecimal(amt));
        reqData.setQrc(qrCode);
        reqData.setSN(sn);

        String data = "amount=" + amt + "&" +
                "scanCode=" + qrCode + "&" +
                "sn=" + sn +
                shaKey;
        data = SHAUtils.sha256(data);
        reqData.setSign(data);

        return reqData;
    }

    /**
     * method GET
     */
    private Map<String, Object> createPaymentQuery(@NonNull String amt, @NonNull String qrCode,
                                                   @NonNull String sn) {
        Properties properties = AssetsUtils.getProperties("signKeyProp", getApplication());
        String shaKey = (String) properties.get("signKey");
        if (shaKey == null) {
            //todo show key file error.
            LogUtils.e("sign key Null(No key)");
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(ApiPay.KEY_AMOUNT, amt);
        map.put(ApiPay.KEY_SCAN_CODE, qrCode);
        map.put(ApiPay.KEY_SN, sn);
        String data = SHAUtils.sha256(ApiPay.KEY_AMOUNT + "=" + amt + "&"
                + ApiPay.KEY_SCAN_CODE + "=" + qrCode + "&"
                + ApiPay.KEY_SN + "=" + sn
                + shaKey);
        map.put(ApiPay.KEY_SIGN, data);

        return map;
    }

    public void releaseViewModel() {
        if (mDisposables != null && !mDisposables.isDisposed()) {
            mDisposables.clear();
        }
    }

    public String getOrderId() {
        return mOrderId;
    }

    public String getChannel() {
        return mChannel;
    }

    /**
     * method GET
     */
    private Map<String, Object> createInquiryQuery(@NonNull String orderId, @NonNull String sn) {
        Properties properties = AssetsUtils.getProperties("signKeyProp", getApplication());
        String shaKey = (String) properties.get("signKey");
        if (shaKey == null) {
            //todo show key file error.
            LogUtils.e("sign key Null(No key)");
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(ApiInquiry.KEY_ORDER_ID, orderId);
        map.put(ApiInquiry.KEY_SN, sn);
        String data = SHAUtils.sha256(ApiInquiry.KEY_ORDER_ID + "=" + orderId + "&"
                + ApiInquiry.KEY_SN + "=" + sn
                + shaKey);
        map.put(ApiPay.KEY_SIGN, data);

        return map;
    }

}
