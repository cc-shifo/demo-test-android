
package com.demo.model;

public class ResponseParams {

    private String agentId;// 代理商id
    private String transId;// 交易类型
    private String amt;// 交易金额 单位：分
    private String transNo;// 流水号
    private String batchNo;// 批次号
    private String cardNo;// 卡号 银行卡交易有卡号（预授权显示全卡号）
    private String expireDate;// 卡片有效期 银行卡交易有，卡片有效期，MMDD，月月日日
    private String bankName;// 发卡行 银行卡交易有，发卡银行中文名
    private String acquireCode;// 收单行编号
    private String sysRefNo;// 系统参考号
    private String authCode;// 授权码 银行卡交易有，预授权类交易用
    private String dateTime;// 交易时间 精确到秒
    private String comment;// 备注信息 交易备注信息包含IC卡交易、外币卡交易特有参数、反向交易的原交易信息等。
    private String scanauthCode;// 码付预授权授权码
    private String remark;// 用户备注 与传入的数据一致
    private String scanDatas;// 扫码得到的数据

    private String respCode;
    private String respMsg;

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

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAcquireCode() {
        return acquireCode;
    }

    public void setAcquireCode(String acquireCode) {
        this.acquireCode = acquireCode;
    }

    public String getSysRefNo() {
        return sysRefNo;
    }

    public void setSysRefNo(String sysRefNo) {
        this.sysRefNo = sysRefNo;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getScanauthCode() {
        return scanauthCode;
    }

    public void setScanauthCode(String scanauthCode) {
        this.scanauthCode = scanauthCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getScanDatas() {
        return scanDatas;
    }

    public void setScanDatas(String scanDatas) {
        this.scanDatas = scanDatas;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }
}
