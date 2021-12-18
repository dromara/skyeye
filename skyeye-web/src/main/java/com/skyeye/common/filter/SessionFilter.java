/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.filter;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.impl.JedisClientServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SessionFilter
 * @Description: 系统过滤器
 * @author: skyeye云系列--卫志强
 * @date: 2021/9/25 19:16
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class SessionFilter implements Filter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	/**
	 * 过滤器
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 获得在下面代码中要用的request,response,session对象
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		HttpServletResponse servletResponse = (HttpServletResponse) response;
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		setResponseMation(servletResponse);
		// 获取请求路径
		String url = servletRequest.getContextPath() + servletRequest.getServletPath();
		
		// 系统资源过滤/转换请求过滤
		if(resourceFiltering(url) || requestFiltering(request, url)){
			chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
			return;
		}
		
		// 如果不是设定的请求类型，则根据mapping.xml配置信息转化为请求信息
		if(ToolUtil.isBlank(url.replaceAll("/", "")) || Constants.REQUEST_MAPPING == null
				|| !Constants.REQUEST_MAPPING.containsKey(url.replaceAll("/", ""))){
			servletResponse.sendRedirect(Constants.LOGIN_PAGE);
		}else{
			String key = url.replaceAll("/", "");
			String allUse = Constants.REQUEST_MAPPING.get(key).get("allUse").toString();
			if("1".equals(allUse) || "2".equals(allUse)){
				// 是否需要登录才能使用   1是   0否  2需要登陆才能访问，但无需授权    默认为否
				JedisClientServiceImpl jedisClient = SpringUtils.getBean(JedisClientServiceImpl.class);
				// 获取token
				String userToken = "";
				try {
					userToken = GetUserToken.getUserToken(servletRequest);
				} catch (Exception e) {
					servletResponse.setHeader("SESSIONSTATUS", "TIMEOUT");
					return;
				}
				// 判断用户是否登录
				if(!jedisClient.exists("userMation:" + userToken)){
					servletResponse.setHeader("SESSIONSTATUS", "TIMEOUT");
					return;
				}
				// 重置redis时间--用户信息
				Map<String, Object> userMation = JSONUtil.toBean(jedisClient.get("userMation:" + userToken), null);
				String authPointsMationStr = jedisClient.get("authPointsMation:" + userToken);
				// 所有权限信息
				List<Map<String, Object>> authPoints = ToolUtil.isBlank(authPointsMationStr) ? null : JSONUtil.toList(JSONUtil.parseArray(authPointsMationStr), null);
				// 权限校验通过
				if(authPoints(authPoints, key) || "2".equals(allUse)){
					url = Constants.REQUEST_MAPPING.get(key).get("path").toString();
					request.getRequestDispatcher(url + "?sessionKey=" + key + "&allUse=" + allUse + "&userToken=" + userToken).forward(request, response);
				}else{
					LOGGER.info("key[{}] is Forced visit, real url is {}", key, Constants.REQUEST_MAPPING.get(key).get("val").toString());
					LOGGER.info("{} 被强行访问.", key);
					servletResponse.setHeader("SESSIONSTATUS", "NOAUTHPOINT");
					return;
				}
			}else{
				url = Constants.REQUEST_MAPPING.get(key).get("path").toString();
				request.getRequestDispatcher(url + "?sessionKey=" + key + "&allUse=" + allUse).forward(request, response);
			}
		}
		return;
	}
	
	private void setResponseMation(HttpServletResponse servletResponse){
		servletResponse.setCharacterEncoding("UTF-8");
		servletResponse.setContentType("text/html;charset=UTF-8");
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
		servletResponse.setHeader("Access-Control-Allow-Methods", "*");
		// userToken为用户的token
		// requestType为请求类型，2为手机端请求，1或者空为PC端请求
		servletResponse.setHeader("Access-Control-Allow-Headers", "Content-Type,userToken,requestType,x-prototype-version,x-requested-with,X_Requested_With");
		// 解决跨域时，前台控制台报 Refused to get unsafe header的错
		servletResponse.addHeader("Access-Control-Expose-Headers", "SESSIONSTATUS,REQUESTMATION,X-JSON");
		servletResponse.setHeader("Set-Cookie", "HttpOnly;Secure;SameSite=None");
	}

	/**
	 * 
	 * @Title: resourceFiltering
	 * @Description: 系统资源过滤
	 * @param url 资源类型
	 * @return: boolean
	 * @throws
	 */
	private boolean resourceFiltering(String url) {
		return Arrays.asList(Constants.FILTER_FILE_CATALOG_OPTION).stream()
				.anyMatch(resourceType -> url.indexOf(resourceType) >= 0);
	}
	
	private boolean requestFiltering(ServletRequest request, String url) {
		if(request.getParameter("sessionKey") != null){
			return Arrays.asList(Constants.FILTER_FILE_REQUEST_OPTION).stream()
					.anyMatch(requestUrl -> url.indexOf(requestUrl) >= 0);
		}
		return false;
	}

	/**
	 * 权限点校验
	 * @param authPoints
	 * @param key
	 * @return
	 */
	public boolean authPoints(List<Map<String, Object>> authPoints, String key){
		if(authPoints == null){
			return false;
		}
		for(Map<String, Object> bean : authPoints){
			if(key.equals(bean.get("menuUrl").toString())){
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy() {
		LOGGER.info("system is destory!!!!");
	}
	
	
}
