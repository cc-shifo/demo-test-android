
package com.whty.smartpos.typaysdk.model;

public class ResponseCode {

	public final static String SUCCESS = "00";

	public final static String ERR_CODE_NORMAL = "Z1";//通用错误

	public final static String ERR_CODE_EXCEPTION = "Z2";//其他异常

	public final static String ERR_CODE_CANCEL = "Z3";//取消扫码，下主密钥取消输入终端号

	public final static String ERR_CODE_TIMEOUT = "Z4";//服务器通讯超时

	// 39返回码
}
