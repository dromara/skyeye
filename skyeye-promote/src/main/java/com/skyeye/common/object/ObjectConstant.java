package com.skyeye.common.object;

public class ObjectConstant {

	public static final String CONENT_TYPE_WEB_REQ = "application/x-www-form-urlencoded";//网页请求发送的contentType格式
	
	public static final String CONENT_TYPE_JSON_REQ = "application/json";//json数据请求发送的数据格式
	
	public static final int WRONG = -9999;
	
	public static final String WRONG_MESSAGE = "失败";
	
	public static final String REQUIRED = "required";//请求参数非空
	public static final String NUM = "num";//请求参数数字校验
	public static final String DATE = "date";//请求参数时间校验
	public static final String EMAIL = "email";//请求参数邮箱校验
	public static final String IDCARD = "idcard";//请求参数证件号校验
	public static final String PHONE = "phone";//请求参数手机号校验
	public static final String URL = "url";//请求参数url校验
	public static final String IP = "ip";//请求参数ip校验
	public static final String POSTCODE = "postcode";//请求参数国内邮编校验
	public static final String DOUBLE = "double";//请求参数验证小数点后两位,一般用于金钱验证
	
}
