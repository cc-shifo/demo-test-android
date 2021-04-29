package com.demo.demopaymodule.apipay;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class PaymentReqData {
    @SerializedName("amount")
    private BigDecimal mAmt;

    @SerializedName("scanCode")
    private String mQrc;

    @SerializedName("sn")
    private String mSN;
    @SerializedName("sign")
    private String mSign;

    public BigDecimal getAmt() {
        return mAmt;
    }

    public void setAmt(BigDecimal amt) {
        mAmt = amt;
    }

    public String getQrc() {
        return mQrc;
    }

    public void setQrc(String qrc) {
        mQrc = qrc;
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
