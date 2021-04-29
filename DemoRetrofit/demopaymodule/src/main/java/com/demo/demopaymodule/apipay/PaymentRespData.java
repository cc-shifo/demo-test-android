package com.demo.demopaymodule.apipay;

import com.google.gson.annotations.SerializedName;

public class PaymentRespData {
    @SerializedName("code")
    private int mRespCode;

    @SerializedName("message")
    private String msg;

    @SerializedName("data")
    private OrderInfo mOrderInfo;

    public int getRespCode() {
        return mRespCode;
    }

    public void setRespCode(int respCode) {
        mRespCode = respCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public OrderInfo getOrderInfo() {
        return mOrderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        mOrderInfo = orderInfo;
    }

    public static class OrderInfo {
        @SerializedName("payOrderId")
        private String mOrderId;
        @SerializedName("payChannel")
        private String mChannel; // WXP, UNP ...

        public String getOrderId() {
            return mOrderId;
        }

        public void setOrderId(String orderId) {
            mOrderId = orderId;
        }

        public String getChannel() {
            return mChannel;
        }

        public void setChannel(String channel) {
            mChannel = channel;
        }
    }
}
