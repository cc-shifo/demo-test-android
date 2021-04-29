package com.demo.demopaymodule.apipay;


import com.demo.demopaymodule.apicommon.ServerUrl;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiPay {
    @Headers({
            "Connection: close"
    })
    @POST
    Observable<PaymentRespData> startPayment(@Url String url, @Body PaymentReqData reqData);

    String KEY_AMOUNT = "amount";
    String KEY_SCAN_CODE = "scanCode";
    String KEY_SN = "sn";
    String KEY_SIGN = "sign";
    @Headers({
            "Connection: close"
    })
    @GET(ServerUrl.PAYMENT_PATH)
    Observable<PaymentRespData> startPayment(@QueryMap Map<String, Object> map);
}
