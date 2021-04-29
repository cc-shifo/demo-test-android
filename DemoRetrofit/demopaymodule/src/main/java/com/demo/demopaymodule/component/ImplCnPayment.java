package com.demo.demopaymodule.component;

import android.content.Context;

import androidx.annotation.NonNull;

import com.demo.demopaymodule.QRCPayActivity;


public final class ImplCnPayment implements ICnPayment {
    private static ImplCnPayment mInstance;
    private ImplCnPayment() {
        // nothing
    }

    public static synchronized ImplCnPayment getInstance() {
        if (mInstance == null) {
            mInstance = new ImplCnPayment();
        }

        return mInstance;
    }

    @Override
    public void startPay(@NonNull Context context, String amt, String sn) {
        QRCPayActivity.startQRCPayActivity(context, amt, sn);
    }

    @Override
    public void registerPaymentInfoForUpdate(PaymentInfoUpdateCallback callback) {
        QRCPayActivity.setUpdateCallback(callback);
    }

    @Override
    public void unregisterPaymentInfoForUpdate() {
        QRCPayActivity.setUpdateCallback(null);
    }

    public static class ErrorCode {
        public static final int SUCCESS = 0;
        public static final int UNKNOWN_ERROR = -10000;
        public static final int OTHER_ERROR = -10001;
        public static final int DATA_ERROR = -10002;
        public static final int EXCEPTION_ERROR = -10003;

        // http errors
        public static final int HTTP_ERROR = -10100;

        // inquiry
        public static final int SHOULD_INQUIRE_ERROR = -10200;

        private ErrorCode() {
            // nothing
        }
    }
}
