package com.demo.myhttp.apiDownloadApk;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface IDownloadApk {
    @Headers({
            "Connection: close"
    })
    @POST("/mam-ms/android/downloadApp")
    Observable<DLRespBody> getApkUrl(@Body DLReqBody body);

    @Streaming
    @GET
    Call<ResponseBody> download(@Header("RANGE") String range, @Url String url);
}
