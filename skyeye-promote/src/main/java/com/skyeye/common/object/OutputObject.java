package com.skyeye.common.object;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;

public class OutputObject extends PutObject implements Serializable{
	
    /**
     * @Fields serialVersionUID : 标识
     */
	private static final long serialVersionUID = 1L;
	public static Map<String, Object> object;
	
	public OutputObject(){
		object = new HashMap<String, Object>();
		setNull();
	}

	public void settotal(Object total){
		OutputObject.object.put("total", total);
	}
	
	public void setreturnMessage(Object returnMessage){
		OutputObject.object.put("returnMessage", returnMessage);
	}
	
	public void setreturnMessage(Object returnMessage, Object returnCode) throws Exception{
		OutputObject.object.put("returnMessage", "错误码:" + returnCode + "错误信息:" + returnMessage);
	}
	
	public void setreturnCode(Object returnCode){
		OutputObject.object.put("returnCode", returnCode);
	}
	
	public void setBean(Map<String,Object> bean) throws Exception {
		OutputObject.object.put("bean", bean);
	}
	
	public void setBeans(List<Map<String,Object>> beans) throws Exception {
		OutputObject.object.put("rows", beans);
	}
	
	public static void setCode(int code){
		OutputObject.object.put("returnCode", code);
	}
	
	public static void setMessage(String Message){
		OutputObject.object.put("returnMessage", Message);
	}
	
	public static int getCode(){
		if(OutputObject.object == null){
			object = new HashMap<String, Object>();
			setNull();
		}
		return Integer.parseInt(OutputObject.object.get("returnCode").toString());
	}
	
	public static String getMessage(){
		return OutputObject.object.get("returnMessage").toString();
	}
	
	public static void put(){
		Object context = JSON.toJSON(object);
		PrintWriter out = null;
		getResponse().setCharacterEncoding("UTF-8");
		getResponse().setContentType("text/html;charset=UTF-8");
		try {
			out = getResponse().getWriter();// 获取输入流
			if(out != null){
				out.print(context);// 将信息发送到前台
				out.flush();// 刷新
			}
		} catch (Exception e) {
			
		} finally {
			if(out != null){
				out.close();// 关闭输入流
			}
		}
	}
	
	public static void setNull(){
		object.put("returnCode", "-9999");
		object.put("returnMessage", "失败");
		object.put("total", 0);
		object.put("bean", "");
		object.put("rows", "");
	}

	public Map<String, Object> getObject() {
		return object;
	}
	
	@SuppressWarnings("static-access")
	public void setObject(Map<String, Object> map) {
		this.object = map;
	}
	
}
