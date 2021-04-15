package com.demo.myhttp.apiDownloadApk;

import com.google.gson.annotations.SerializedName;

public class DLReqBody {
    private String pn;
    private String sn;
    @SerializedName("appid")
    private String appId;

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
