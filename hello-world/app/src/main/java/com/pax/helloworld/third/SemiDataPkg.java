/*
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2020-? Computer Technology(Shenzhen) CO., LTD All rights reserved.
 *
 * Revision History:
 * Date	                 Author	                Action
 * 20200212 	         liujian                  Create
 */

package com.pax.helloworld.third;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SemiDataPkg {
    private String task;
    private Data data;
    private String next;

    public SemiDataPkg() {
        //nothing
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public static class Data {
        /**
         * all tasks need
         */
        private String request;
        private String response;
        private String result;

        /**
         * task: pre-authorization
         *
         * amt
         */

        /**
         * task: sale
         */
        private String amt;
        private String tips;

        /**
         * task: void
         */
        private String voucher;

        /**
         * task: online refund
         * <p>
         * amt for(EDC)
         */
        @SerializedName("refer_no")
        private String referNO; // CUP
        @SerializedName("ori_date")
        private String oriTxnDate;  // CUP

        /**
         * task: offline
         * <p>
         * amt
         */
        @SerializedName("auth_no")
        private String authNO;

        /**
         * task: adjust
         * <p>
         * original transaction voucher number
         */
        private String amtAfterAdjusted;

        /**
         * task: settlement
         */
        private List<String> acq;

        public Data() {
            //nothing
        }

        public String getRequest() {
            return request;
        }

        public void setRequest(String request) {
            this.request = request;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getAmt() {
            return amt;
        }

        public void setAmt(String amt) {
            this.amt = amt;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }

        public String getVoucher() {
            return voucher;
        }

        public void setVoucher(String voucher) {
            this.voucher = voucher;
        }

        public String getReferNO() {
            return referNO;
        }

        public void setReferNO(String referNO) {
            this.referNO = referNO;
        }

        public String getOriTxnDate() {
            return oriTxnDate;
        }

        public void setOriTxnDate(String oriTxnDate) {
            this.oriTxnDate = oriTxnDate;
        }

        public String getAuthNO() {
            return authNO;
        }

        public void setAuthNO(String authNO) {
            this.authNO = authNO;
        }

        public String getAmtAfterAdjusted() {
            return amtAfterAdjusted;
        }

        public void setAmtAfterAdjusted(String amtAfterAdjusted) {
            this.amtAfterAdjusted = amtAfterAdjusted;
        }

        public List<String> getAcq() {
            return acq;
        }

        public void setAcq(List<String> acq) {
            this.acq = acq;
        }
    }
}
