package com.example.demoeventbus.ittobus;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.demoeventbus.ittobus.ev.AsyncMsgEvent;
import com.squareup.otto.Subscribe;

public class OttoAsyncTest {
    private static final String TAG = "OttoAsyncTest";

    public OttoAsyncTest() {
        // nothing
    }

    @Subscribe
    public void msg(@NonNull AsyncMsgEvent ev) {
        // TODO switch to task thread.
        Log.d(TAG, "msg: " + ev.getMsg() + ", tid=" + Thread.currentThread().getId());
    }
}
