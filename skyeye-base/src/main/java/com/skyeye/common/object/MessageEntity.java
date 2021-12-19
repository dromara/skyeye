/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.object;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MessageEntity implements Serializable {
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	public Map<String, Object> object;

	public MessageEntity(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
		object = new HashMap<>();
		setNull();
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setNull(){
		object.put("returnCode", "-9999");
		object.put("error", "0");
		object.put("returnMessage", "失败");
		object.put("total", 0);
		object.put("bean", "");
		object.put("rows", "");
	}
	
}
