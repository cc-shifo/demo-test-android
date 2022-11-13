package com.example.demoeventbus.ittobus.ev;

public class AsyncMsgEvent {
    private String msg;

    public AsyncMsgEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
