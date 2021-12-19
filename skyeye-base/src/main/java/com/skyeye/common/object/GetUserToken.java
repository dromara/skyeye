/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.object;

import com.skyeye.common.util.ToolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class GetUserToken {
	
	private static final String PHONE_TOKEN_SUFFIX = "-APP";
	
	private static final String USERTOKEN_KEY = "userToken";
	
	/**
	 * 从cookies获取内容
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getCookiesByName(HttpServletRequest request, String name){
		Cookie[] cookies =  request.getCookies();
		if(cookies != null){
			for(Cookie cookie : cookies){
				if(cookie.getName().equals(name)){
					return cookie.getValue();
				}
			}
		}
		return "";
	}
	
	/**
	 * 获取userToken
	 * @param request
	 * @return userToken
	 * @throws Exception 
	 */
	public static String getUserToken(HttpServletRequest request) throws Exception{
		// 1.从请求信息中获取
		String userToken = request.getParameter(USERTOKEN_KEY);
		// 2.从cookies中获取
		if(StringUtils.isBlank(userToken)){
			userToken = getCookiesByName(request, USERTOKEN_KEY);
		}
		// 3.从header中获取
		if(StringUtils.isBlank(userToken)){
			userToken = request.getHeader(USERTOKEN_KEY);
		}
		if(ToolUtil.isBlank(userToken)){
			throw new Exception("登录超时，请重新登录");
		}else{
			// 获取请求类型
			String requestType = request.getHeader("requestType");
			// 判断接口请求类型，如果是手机端请求，需要加上-APP标识
			if("2".equals(requestType)){
				if(userToken.lastIndexOf(PHONE_TOKEN_SUFFIX) < 0){
					userToken += PHONE_TOKEN_SUFFIX;
				}
			}
		}
		return userToken;
	}

}
