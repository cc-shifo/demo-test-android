package com.example.demoeventbus.ittobus;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.demoeventbus.EventBusMngr;
import com.example.demoeventbus.ittobus.ev.MainMsgEvent;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.squareup.otto.Subscribe;

public class OttoMainTest {
    private static final String TAG = "OttoMainTest";

    public OttoMainTest() {
        // nothing
    }

    @Subscribe
    public void toMsg(@NonNull MainMsgEvent ev) {
        LiveEventBus.get(EventBusMngr.KEY_MAIN_MSG).post(ev.getMsg());
        Log.d(TAG, "msg: " + ev.getMsg() + ", tid=" + Thread.currentThread().getId());
    }
}
