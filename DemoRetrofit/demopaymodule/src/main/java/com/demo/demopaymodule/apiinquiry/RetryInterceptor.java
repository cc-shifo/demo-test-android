package com.demo.demopaymodule.apiinquiry;

import android.os.SystemClock;

import com.demo.demopaymodule.apicommon.ServerRespCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import io.reactivex.rxjava3.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class RetryInterceptor implements Interceptor {

    private final int mMaxRetryCount;
    private final long mRetryInterval;
    private final KeepInquiryCallback mCallback;

    /**
     * @param retryInterval interval in milliseconds.
     */
    public RetryInterceptor(int maxRetryCount, long retryInterval, KeepInquiryCallback callback) {
        mMaxRetryCount = maxRetryCount;
        mRetryInterval = retryInterval;
        mCallback = callback;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response;
        String bodyString = null;
        MediaType mediaType = null;
        int retryNum = 1;
        do {
            response = doRequest(chain, request);
            if (!response.isSuccessful()) {
                break;
            }

            try (ResponseBody responseBody = response.body()) {
                BufferedSource source = responseBody.source();
                mediaType = responseBody.contentType();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.getBuffer();
                bodyString = buffer.clone().readString(StandardCharsets.UTF_8);
                Type type = new TypeToken<InquiryRespData>() {}.getType();
                Gson gson = new Gson();
                InquiryRespData respData = gson.fromJson(bodyString, type);
                if (respData != null) {
                    InquiryRespData.OrderInfo info = respData.getOrderInfo();
                    if (info != null && !ServerRespCode.InquiryStatus.shouldInquiring(info.getOrderStatus())) {
                        break;
                    }
                }
            }

            if (mCallback != null) {
                mCallback.continueInquiring();
            }
            SystemClock.sleep(mRetryInterval);
            retryNum++;
        } while (retryNum <= mMaxRetryCount);

        if (mediaType == null) {
            return response;
        } else {
            return response.newBuilder().body(ResponseBody.create(mediaType, bodyString)).build();
        }

    }

    private Response doRequest(Chain chain, Request request) throws IOException {
        return chain.proceed(request);
    }

    private boolean shouldInquiring(Response response)  throws IOException {
        try (ResponseBody responseBody = response.body()) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.getBuffer();
            String json = buffer.clone().readString(StandardCharsets.UTF_8);
            Type type = new TypeToken<InquiryRespData>() {}.getType();
            Gson gson = new Gson();
            InquiryRespData respData = gson.fromJson(json, type);
            if (respData != null) {
                InquiryRespData.OrderInfo info = respData.getOrderInfo();
                if (info != null) {
                    return ServerRespCode.InquiryStatus.shouldInquiring(info.getOrderStatus());
                }
            }
        }

        return false;
    }

    public interface KeepInquiryCallback {
        void continueInquiring();
    }

}