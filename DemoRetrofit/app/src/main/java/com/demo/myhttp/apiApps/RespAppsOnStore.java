package com.demo.myhttp.apiApps;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RespAppsOnStore {
    @SerializedName("responseCode")
    private String respCode;
    @SerializedName("remarks")
    private String respMark;

    private List<AppInfo> appList;

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

    public List<AppInfo> getAppList() {
        return appList;
    }

    public void setAppList(List<AppInfo> appList) {
        this.appList = appList;
    }
}
