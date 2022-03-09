/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.filter;

import javax.servlet.FilterConfig;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

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

	public static final String[] FILTER_FILE_CATALOG_OPTION = { "/html", "/css", "/js", "/assets", "/tpl", "/images",
			"/template", "/static", ".json", ".css", ".js", ".gif", ".jpg", ".eot", ".svg", ".ttf", ".woff", ".woff2",
			".mp4", ".rmvb", ".avi", "3gp", ".html", ".less", ".otf", ".scss", ".ico", "/upload", "/actuator",
			"/service", "/talkwebsocket", "/phonetalkwebsocket" };

	public static final String[] FILTER_FILE_REQUEST_OPTION = { "/post", "/websocket", "/service" };

	/**
	 * 登录页面
	 */
	public static final String LOGIN_PAGE = "/tpl/index/login.html";

	public static final String WEB_API_URL = "http://localhost:8081/";

	public static final String CONFIG_URL = "configRation.json";
	
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

		// 如果不是设定的请求类型
		if(isBlank(url.replaceAll("/", ""))){
			servletResponse.sendRedirect(LOGIN_PAGE);
		}

		if(url.startsWith("/images")){
			servletResponse.sendRedirect(WEB_API_URL + url);
			return;
		}

		if(CONFIG_URL.equals(url.replaceAll("/", ""))){
			request.getRequestDispatcher("/getConfigRation").forward(request, response);
			return;
		}

		// 系统资源过滤/转换请求过滤
		if(resourceFiltering(url) || requestFiltering(request, url)){
			chain.doFilter(request, response);
			return;
		}

		return;
	}

	@Override
	public void destroy() {

	}

	private void setResponseMation(HttpServletResponse servletResponse){
		servletResponse.setCharacterEncoding("UTF-8");
		servletResponse.setContentType("text/html;charset=UTF-8");
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
		servletResponse.setHeader("Access-Control-Allow-Methods", "*");
		// userToken为用户的token
		// requestType为请求类型，2为手机端请求，1或者空为PC端请求
		servletResponse.setHeader("Access-Control-Allow-Headers", "Content-Type,userToken,requestType");//这里“userToken”是我要传到后台的内容key  
		servletResponse.setHeader("Access-Control-Expose-Headers", "*");
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
		return Arrays.asList(FILTER_FILE_CATALOG_OPTION).stream()
				.anyMatch(resourceType -> url.indexOf(resourceType) >= 0);
	}

	private boolean requestFiltering(ServletRequest request, String url) {
		if(request.getParameter("sessionKey") != null){
			return Arrays.asList(FILTER_FILE_REQUEST_OPTION).stream()
					.anyMatch(requestUrl -> url.indexOf(requestUrl) >= 0);
		}
		return false;
	}

	/**
	 *
	 * @Title: isBlank
	 * @Description: 判断字符串是否为空
	 * @param str
	 * @param @return    参数
	 * @return boolean    返回类型
	 * @throws
	 */
	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

}
