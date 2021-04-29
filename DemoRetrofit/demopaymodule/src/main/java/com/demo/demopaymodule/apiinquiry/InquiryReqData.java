package com.demo.demopaymodule.apiinquiry;

import com.google.gson.annotations.SerializedName;

public class InquiryReqData {
    @SerializedName("payOrderId")
    private String mOrderId;
    @SerializedName("sn")
    private String mSN;
    @SerializedName("sign")
    private String mSign;

    public String getOrderId() {
        return mOrderId;
    }

    public void setOrderId(String orderId) {
        mOrderId = orderId;
    }

    public String getSN() {
        return mSN;
    }

    public void setSN(String SN) {
        mSN = SN;
    }

    public String getSign() {
        return mSign;
    }

    public void setSign(String sign) {
        mSign = sign;
    }
}
