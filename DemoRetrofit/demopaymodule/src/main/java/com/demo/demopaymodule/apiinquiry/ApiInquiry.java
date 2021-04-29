package com.demo.demopaymodule.apiinquiry;


import com.demo.demopaymodule.apicommon.ServerUrl;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiInquiry {
    @Headers({"Connection: close"})
    @POST
    Observable<InquiryRespData> inquireStatus(@Url String url, @Body InquiryReqData reqData);

    String KEY_ORDER_ID = "payOrderId";
    String KEY_SN = "sn";
    String KEY_SIGN = "sign";
    @Headers({"Connection: close"})
    @GET(ServerUrl.INQUIRY_PATH)
    Observable<InquiryRespData> inquireStatus(@QueryMap Map<String, Object> map);
}
