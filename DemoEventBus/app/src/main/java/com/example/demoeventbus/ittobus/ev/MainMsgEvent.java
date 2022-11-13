package com.example.demoeventbus.ittobus.ev;

public class MainMsgEvent {

    private long msg;

    public long getMsg() {
        return msg;
    }

    public void setMsg(long msg) {
        this.msg = msg;
    }

    public MainMsgEvent(long msg) {
        this.msg = msg;
    }
}
