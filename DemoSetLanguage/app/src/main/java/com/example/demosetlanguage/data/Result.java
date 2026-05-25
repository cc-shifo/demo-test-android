package com.example.demosetlanguage.data;

public class Result {
    private String mMsg;
    private int mCode;

    public Result() {
    }

    public Result(String msg, int code) {
        mMsg = msg;
        mCode = code;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }
}
