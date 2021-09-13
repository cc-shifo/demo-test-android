package com.whty.smartpos.qbcommapp;

import androidx.lifecycle.MutableLiveData;

public class CommStateManager {
    private final MutableLiveData<String> mMessage;
    private final MutableLiveData<String> mResult;
    private static CommStateManager mInstance;

    private CommStateManager() {
        // nothing
        mMessage = new MutableLiveData<>();
        mResult = new MutableLiveData<>();
    }

    public static synchronized CommStateManager getInstance() {
        if (mInstance == null) {
            mInstance = new CommStateManager();
        }

        return mInstance;
    }

    public MutableLiveData<String> getMessage() {
        return mMessage;
    }

    public MutableLiveData<String> getResult() {
        return mResult;
    }
}
