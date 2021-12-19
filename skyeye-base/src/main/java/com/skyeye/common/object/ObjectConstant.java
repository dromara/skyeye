/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.common.object;

import com.skyeye.common.util.ToolUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ObjectConstant {

	public static final String CONENT_TYPE_WEB_REQ = "application/x-www-form-urlencoded";//网页请求发送的contentType格式
	
	public static final String CONENT_TYPE_JSON_REQ = "application/json";//json数据请求发送的数据格式
	
	public static final int WRONG = -9999;
	
	public static final String WRONG_MESSAGE = "失败";
	
	public static enum MethodType {
		POST("POST"),
		GET("GET"),
		DELETE("DELETE"),
		PUT("PUT");
		
		private String type;
		
		MethodType(String type){
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}
	
	public static enum VerificationParams {
		REQUIRED("required", "参数非空校验", "不能为空", "isBlank", ""),
		NUM("num", "数字校验", "数字类型不正确", "regularCheck",
				"[0-9]*"),
		DATE("date", "时间校验,格式：yyyy-MM-dd HH:mm:ss", "时间类型不正确", "isDate", ""),
		EMAIL("email", "邮箱校验", "邮箱类型不正确", "regularCheck",
				"^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"),
        IDCARD("idcard", "证件号校验", "证件号类型不正确", "regularCheck",
        		"(^\\d{18}$)|(^\\d{15}$)"),
        PHONE("phone", "手机号校验", "手机号类型不正确", "regularCheck",
        		"^((13[0-9])|(15[^4])|(19[0-9])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$"),
        URL("url", "url校验", "url类型不正确", "regularCheck",
        		"http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?"),
        IP("ip", "ip校验", "ip类型不正确", "regularCheck",
        		getIpRegex()),
        POSTCODE("postcode", "国内邮编校验", "国内邮编类型不正确", "regularCheck",
        		"^\\d{6}$"),
        DOUBLE("double", "验证小数点后两位,一般用于金钱校验", "小数格式类型不正确", "regularCheck",
        		"^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"),
		JSON("json", "json字符串校验", "参数类型非json", "isJson", "");
		
        private String key;
        private String desc;
        private String resultMsg;
        private String method;
        private String regular;
		
        VerificationParams(String key, String desc, String resultMsg, String method,
        		String regular){
            this.key = key;
            this.desc = desc;
            this.resultMsg = resultMsg;
            this.method = method;
            this.regular = regular;
        }
	
        public static String getMethod(String str){
            for (VerificationParams q : VerificationParams.values()){
                if(q.getKey().equals(str)){
                    return q.getMethod();
                }
            }
            return "";
        }
        
        public static String getResultMsg(String str){
            for (VerificationParams q : VerificationParams.values()){
                if(q.getKey().equals(str)){
                    return q.getResultMsg();
                }
            }
            return "";
        }
        
        public static String getRegular(String str){
            for (VerificationParams q : VerificationParams.values()){
                if(q.getKey().equals(str)){
                    return q.getRegular();
                }
            }
            return "";
        }

		public static List<Map<String, Object>> getList(){
			List<Map<String, Object>> beans = new ArrayList<>();
			for (VerificationParams q : VerificationParams.values()){
				Map<String, Object> bean = new HashMap<>();
				bean.put("key", q.getKey());
				bean.put("value", q.getDesc());
				beans.add(bean);
			}
			return beans;
		}

		public String getKey() {
			return key;
		}

		public String getDesc() {
			return desc;
		}

		public String getMethod() {
			return method;
		}

		public String getResultMsg() {
			return resultMsg;
		}

		public String getRegular() {
			return regular;
		}
    }
	
	/**
	 * 
	 * @Title: getIpRegex
	 * @Description: 获取ip校验的正则
	 * @return
	 * @return: String
	 * @throws
	 */
	public static String getIpRegex(){
		String num = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
		return "^" + num + "\\." + num + "\\." + num + "\\." + num + "$";
	}
	
	/**
	 * 
	 * @Title: regularCheck
	 * @Description: 字符串类型校验
	 * @param str 字符串
	 * @param regular 正则表达式
	 * @return 如果符合正则，则返回true，否则返回false
	 * @return: boolean
	 * @throws
	 */
	public boolean regularCheck(String str, String regular){
		Pattern pattern = Pattern.compile(regular);
		return pattern.matcher(str).matches();
	}

	public boolean isBlank(String str, String regular){
		return !ToolUtil.isBlank(str);
	}
	
	public boolean isDate(String str, String regular){
		return ToolUtil.isDate(str);
	}

	public boolean isJson(String str, String regular) { return ToolUtil.isJson(str); }
	
	/**
	 * 
	     * @Title: containsBoolean
	     * @Description: 请求xml参数判断
	     * @param ref
	     * @param str
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public static String containsBoolean(String[] ref, String str){
		for(String key: ref){
			if(ToolUtil.isBlank(key)){
				continue;
			}
			if(!key.equals(VerificationParams.REQUIRED.getKey())){
				// 不是非空校验的需要加上非空处理
				if(ToolUtil.isBlank(str)){
					continue;
				}
			}
			try {
				String methodStr = VerificationParams.getMethod(key);
				if(!ToolUtil.isBlank(methodStr)){
					String regex = VerificationParams.getRegular(key);
					boolean result = (boolean) getResult(methodStr, str, regex);
					if(!result){
						return VerificationParams.getResultMsg(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @Title: getResult
	 * @Description: 通过反射的手法获取方法校验结果
	 * @param methodStr 方法名
	 * @param params 参数，支持多个
	 * @return
	 * @throws Exception
	 * @return: Object
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	private static Object getResult(String methodStr, Object... params) throws Exception{
		Class<?> classType = Class.forName(ObjectConstant.class.getName());
		Class[] argTypes = new Class[2];
		argTypes[0] = String.class;
		argTypes[1] = String.class;
		Method method = ObjectConstant.class.getMethod(methodStr, argTypes);
		return method.invoke(classType.newInstance(), params);
	}
	
}
