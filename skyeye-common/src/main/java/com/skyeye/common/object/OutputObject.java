/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.common.object;

import cn.hutool.json.JSONUtil;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputObject extends PutObject implements Serializable{
	
    /**
     * @Fields serialVersionUID : 标识
     */
	private static final long serialVersionUID = 1L;
	
	public void settotal(Object total){
		getModelEntity().object.put("total", total);
	}
	
	public void setreturnMessage(Object returnMessage){
		getModelEntity().object.put("returnMessage", returnMessage);
	}
	
	public void setreturnMessage(Object returnMessage, Object returnCode) throws Exception{
		setreturnMessage(returnMessage);
		setreturnCode(returnCode);
	}
	
	public void setreturnCode(Object returnCode){
		getModelEntity().object.put("returnCode", returnCode);
	}
	
	public void setErroCode(int code){
		getModelEntity().object.put("error", code);
	}
	
	public void setCustomBean(String key, Map<String,Object> bean) throws Exception {
		getModelEntity().object.put(key, bean);
	}
	
	public void setCustomBeans(String key, List<Map<String,Object>> bean) throws Exception {
		getModelEntity().object.put(key, bean);
	}
	
	public void setBean(Map<String,Object> bean) throws Exception {
		getModelEntity().object.put("bean", bean);
	}
	
	public void setBeans(List<Map<String,Object>> beans) throws Exception {
		getModelEntity().object.put("rows", beans);
	}
	
	public static void setCode(int code){
		getModelEntity().object.put("returnCode", code);
	}
	
	public static void setMessage(String Message){
		if(getModelEntity().object != null){
			getModelEntity().object.put("returnMessage", Message);
		}
	}
	
	public static int getCode(){
		if(getModelEntity().object == null){
			getModelEntity().object = new HashMap<>();
		}
		return Integer.parseInt(getModelEntity().object.get("returnCode").toString());
	}
	
	public static String getMessage(){
		return getModelEntity().object.get("returnMessage").toString();
	}
	
	public static void put(){
		Object context = JSONUtil.toJsonStr(getModelEntity().object);
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
	
	public Map<String, Object> getObject() {
		return getModelEntity().object;
	}
	
	public void setObject(Map<String, Object> map) {
		getModelEntity().object = map;
	}
	
}
