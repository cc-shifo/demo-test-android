package com.demo.myhttp.apiDownloadApk;

import com.google.gson.annotations.SerializedName;

public class DLApkInfo {


    @SerializedName("apkPath")
    private String apkUrl;


    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }
}
