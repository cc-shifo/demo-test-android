
package com.whty.smartpos.typaysdk.model;

public class RequestParams {

    private String agentId;// 代理商id
    private String transId;// 交易类型
    private boolean isneedPrint;// 签购单打印 是否打印签购单（不填默认打印）
    private int totalPrint;// 打印联数 取值范围：0-3，不填默认为2
    private boolean forceLogin;// 强制签到 交易前是否强制签到（不填默认不签到)
    private boolean forcePin;// 强制输密 交易时，是否强制输密（不填默认不强制)
    private String amt;// 交易金额 单位：分（不填则在SDK输入）
    private String scanDatas;// 扫描数据 扫支付宝、微信、银联条码或二维码得到的数据，码付交易时可传入
    private String transNo;// 原交易流水号 撤销等交易时用
    private String sysrefNo;// 原交易参考号 退货等交易时用
    private String tradeDate;// 原交易日期 退货、预授权等交易时用，格式：月月日日，即MMDD
    private String authCode;// 原交易授权码 预授权完成交易时用
    private String remark;// 备注 交易成功后会原样返回
    private String cardNum;


    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public boolean isIsneedPrint() {
        return isneedPrint;
    }

    public void setIsneedPrint(boolean isneedPrint) {
        this.isneedPrint = isneedPrint;
    }

    public int getTotalPrint() {
        return totalPrint;
    }

    public void setTotalPrint(int totalPrint) {
        this.totalPrint = totalPrint;
    }

    public boolean isForceLogin() {
        return forceLogin;
    }

    public void setForceLogin(boolean forceLogin) {
        this.forceLogin = forceLogin;
    }

    public boolean isForcePin() {
        return forcePin;
    }

    public void setForcePin(boolean forcePin) {
        this.forcePin = forcePin;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getScanDatas() {
        return scanDatas;
    }

    public void setScanDatas(String scanDatas) {
        this.scanDatas = scanDatas;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public String getSysrefNo() {
        return sysrefNo;
    }

    public void setSysrefNo(String sysrefNo) {
        this.sysrefNo = sysrefNo;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }
}
