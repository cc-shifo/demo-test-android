package com.demo.myhttp.apiDownloadApk;

import com.google.gson.annotations.SerializedName;

public class DLRespBody {
    @SerializedName("responseCode")
    private String respCode;

    @SerializedName("remarks")
    private String respMark;

    @SerializedName("downloadApp")
    private DLApkInfo appInfo;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMark() {
        return respMark;
    }

    public void setRespMark(String respMark) {
        this.respMark = respMark;
    }

    public DLApkInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(DLApkInfo appInfo) {
        this.appInfo = appInfo;
    }
}
