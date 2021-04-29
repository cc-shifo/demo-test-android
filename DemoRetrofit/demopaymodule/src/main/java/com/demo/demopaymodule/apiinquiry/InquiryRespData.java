package com.demo.demopaymodule.apiinquiry;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class InquiryRespData {
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

        @SerializedName("payOrderNo")
        private String mOrderNo; // not understand

        @SerializedName("status")
        private int mOrderStatus;

        @SerializedName("consumerPayChannel")
        private String mChannel; // WXP, UNP ...

        @SerializedName("amount")
        private BigDecimal mAmt;

        @SerializedName("createTime")
        private String mTimeOrder;
        @SerializedName("consumerPayTime")
        private String mTimePayment;

        public String getOrderId() {
            return mOrderId;
        }

        public void setOrderId(String orderId) {
            mOrderId = orderId;
        }

        public String getOrderNo() {
            return mOrderNo;
        }

        public void setOrderNo(String orderNo) {
            mOrderNo = orderNo;
        }

        public int getOrderStatus() {
            return mOrderStatus;
        }

        public void setOrderStatus(int orderStatus) {
            mOrderStatus = orderStatus;
        }

        public String getChannel() {
            return mChannel;
        }

        public void setChannel(String channel) {
            mChannel = channel;
        }

        public BigDecimal getAmt() {
            return mAmt;
        }

        public void setAmt(BigDecimal amt) {
            mAmt = amt;
        }

        public String getTimeOrder() {
            return mTimeOrder;
        }

        public void setTimeOrder(String timeOrder) {
            mTimeOrder = timeOrder;
        }

        public String getTimePayment() {
            return mTimePayment;
        }

        public void setTimePayment(String timePayment) {
            mTimePayment = timePayment;
        }
    }
}
