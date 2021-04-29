package com.demo.demopaymodule.apicommon;


import com.demo.demopaymodule.utils.LogUtils;

public class ServerRespCode {
    public static final int REQUIRE_PAYMENT_PWD = 900200;

    public static final int SUCCESS = 0;

    public static final int SYSTEM_ERROR = -1;

    public static boolean isServerError(int respCode) {
        return !(respCode == ServerRespCode.SUCCESS
                || respCode == ServerRespCode.REQUIRE_PAYMENT_PWD);
    }


    public static class InquiryStatus {
        // never responded status
        public static final int INQUIRY_STATUS_REFUNDING = -4;
        public static final int INQUIRY_STATUS_REFUND_COMPLETED = -5;
        // final status that txn process should be close.
        public static final int INQUIRY_STATUS_SUCCESS_PAYMENT = 2;
        public static final int INQUIRY_STATUS_FAILED_PAYMENT = -1;
        public static final int INQUIRY_STATUS_CANCELED_PAYMENT = -3;
        public static final int INQUIRY_STATUS_UNKNOWN_ERROR = -99;
        // keep inquiring
        public static final int INQUIRY_STATUS_WAIT_PWD = 1;
        public static final int INQUIRY_STATUS_DURING_CANCELING = -2;

        public static boolean shouldInquiring(int status) {
            LogUtils.d("response status: " + status);
            return (status == INQUIRY_STATUS_WAIT_PWD
                    || status == INQUIRY_STATUS_DURING_CANCELING);
        }
    }
}
