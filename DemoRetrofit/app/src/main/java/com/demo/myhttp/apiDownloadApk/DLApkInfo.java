package com.demo.myhttp.apiDownloadApk;

import com.google.gson.annotations.SerializedName;

public class DLApkInfo {
    @SerializedName("isrecommend")
    private int recommended;

    @SerializedName("appid")
    private String appId;

    private String signatureApk;

    @SerializedName("signature_apk")
    private String signedInfo;

    @SerializedName("signedApkPath")
    private String downloadUrl;

    @SerializedName("apkPath")
    private String apkUrl;

    public int getRecommended() {
        return recommended;
    }

    public void setRecommended(int recommended) {
        this.recommended = recommended;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSignatureApk() {
        return signatureApk;
    }

    public void setSignatureApk(String signatureApk) {
        this.signatureApk = signatureApk;
    }

    public String getSignedInfo() {
        return signedInfo;
    }

    public void setSignedInfo(String signedInfo) {
        this.signedInfo = signedInfo;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }
}
