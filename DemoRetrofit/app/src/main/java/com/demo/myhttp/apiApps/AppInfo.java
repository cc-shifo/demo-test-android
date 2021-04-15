package com.demo.myhttp.apiApps;

import com.google.gson.annotations.SerializedName;

public class AppInfo {
    @SerializedName("appid")
    private String appId;

    @SerializedName("appname")
    private String appName;

    @SerializedName("packagename")
    private String packageName;

    @SerializedName("appauthor")
    private String author;

    @SerializedName("appversion")
    private String version;

    @SerializedName("appversioncode")
    private String versionCode;

    @SerializedName("description")
    private String desc;

    @SerializedName("isrecommend")
    private int recommended;

    @SerializedName("appuploadtime")
    private String uploadTime;

    @SerializedName("appIconPath")
    private String iconPath;

    @SerializedName("categoryid")
    private String category;

    @SerializedName("categoryname")
    private String categoryName;

    // todo download path?
    @SerializedName("signedApkPath")
    private String apkPath;

    @SerializedName("appScreenShotPath")
    private String screenShotPath;

    @SerializedName("apkSize")
    private String apkSize;

    // todo how is the business logic represented by every instruction from server.
    @SerializedName("appInstallStatus")
    private int instruction;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getRecommended() {
        return recommended;
    }

    public void setRecommended(int recommended) {
        this.recommended = recommended;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public String getScreenShotPath() {
        return screenShotPath;
    }

    public void setScreenShotPath(String screenShotPath) {
        this.screenShotPath = screenShotPath;
    }

    public String getApkSize() {
        return apkSize;
    }

    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }

    public int getInstruction() {
        return instruction;
    }

    public void setInstruction(int instruction) {
        this.instruction = instruction;
    }
}
