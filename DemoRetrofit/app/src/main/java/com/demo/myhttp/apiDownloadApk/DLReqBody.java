package com.demo.myhttp.apiDownloadApk;

import com.google.gson.annotations.SerializedName;

public class DLReqBody {

    @SerializedName("appid")
    private String appId;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
