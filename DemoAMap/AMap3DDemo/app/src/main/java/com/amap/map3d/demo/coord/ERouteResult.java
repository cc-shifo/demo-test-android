package com.amap.map3d.demo.coord;

public enum ERouteResult {
    RESULT_SUCCESS(0, "成功", "success");
    /**
     * 错误码
     */
    private int mCode;
    private String mZhDescription;
    private String mEngDescription;

    ERouteResult() {
        this(0, "成功", "success");
    }

    ERouteResult(int code, String zhDescription, String engDescription) {
        mCode = code;
        mZhDescription = zhDescription;
        mEngDescription = engDescription;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public String getZhDescription() {
        return mZhDescription;
    }

    public void setZhDescription(String zhDescription) {
        mZhDescription = zhDescription;
    }

    public String getEngDescription() {
        return mEngDescription;
    }

    public void setEngDescription(String engDescription) {
        mEngDescription = engDescription;
    }
}
