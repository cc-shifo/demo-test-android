package com.demo.demopaymodule.component;

import android.content.Context;

import androidx.annotation.NonNull;

public interface ICnPayment {
    void startPay(@NonNull Context context, String amt, String sn);

    void registerPaymentInfoForUpdate(PaymentInfoUpdateCallback callback);
    void unregisterPaymentInfoForUpdate();

    /**
     * run on the main thread
     */
    interface PaymentInfoUpdateCallback {
        int WE_PAY = 1;
        int ALI_PAY = 2;
        int UNION_PAY = 3;
        int BEST_PAY = 4; // 翼支付/YiZhiFu
        void onPaymentFinish(int resultCode, String channel, String orderId);
    }
}
