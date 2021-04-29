package com.demo.demopaymodule.apicommon;

import androidx.annotation.Nullable;

public class ServerRespException extends RuntimeException {
    private final int mRespCode;

    public ServerRespException(int respCode, @Nullable String message) {
        super(message);
        mRespCode = respCode;
    }

    public int getRespCode() {
        return mRespCode;
    }
}
