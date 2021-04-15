package com.demo.myhttp.apiApps;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AppsOnStore {
    @Headers({
            "Connection: close"
    })
    @POST("/mam-ms/android/reqPublishedApps")
    Observable<RespAppsOnStore> loadAllApps(@Body ReqAppsOnStore body);
}
