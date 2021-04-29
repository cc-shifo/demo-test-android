package com.demo.demopaymodule;

public enum TxnResult {
    SERVER_ERROR(-1, null, null),
    REQUIRED_PAYMENT_PWD(-2, null, null),


    SUCCESS(0, null, null);

    private int mCode;
    private String mMsg;
    private String mCause;

    TxnResult(int code, String msg, String cause) {
        mCode = code;
        mMsg = msg;
        mCause = cause;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public String getCause() {
        return mCause;
    }

    public void setCause(String cause) {
        mCause = cause;
    }
}
