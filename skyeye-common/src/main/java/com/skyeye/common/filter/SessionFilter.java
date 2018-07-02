package com.skyeye.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skyeye.common.constans.Constants;
import com.skyeye.common.util.ToolUtil;


/**
 * 
    * @ClassName: SessionFilter
    * @Description: 系统过滤器
    * @author 卫志强
    * @date 2018年6月7日
    *
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
		servletResponse.setCharacterEncoding("UTF-8");  
		servletResponse.setContentType("text/html;charset=UTF-8");
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		//获取请求路径
		String url = servletRequest.getContextPath() + servletRequest.getServletPath();
		LOGGER.info("请求链接" + url);
		//文件目录过滤
		for(String str : Constants.FILTER_FILE_CATALOG_OPTION){
			if (url.indexOf(str) != -1) {
				chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
				return;
			}
		}
		
		//文件后缀过滤
		for(String str : Constants.FILTER_FILE_SUFFIX_OPTION){
			if (url.contains(str)) {
				chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
				return;
			}
		}
		//请求过滤
		boolean pass = false;
		//判断是否是设定的请求类型
		for(String str : Constants.FILTER_FILE_REQUEST_OPTION){
			if (url.contains(str)) {
				pass = true;
				break;
			}
		}
		if(pass){
			chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
		}else{
			//如果不是设定的请求类型，则根据mapping.xml配置信息转化为请求信息
			if(ToolUtil.isBlank(url.replaceAll("/", ""))){
				servletResponse.sendRedirect(Constants.LOGIN_PAGE);
			}else{
				if(Constants.REQUEST_MAPPING == null){
					servletResponse.sendRedirect(Constants.LOGIN_PAGE);
				}else{
					if(Constants.REQUEST_MAPPING.containsKey(url.replaceAll("/", ""))){
						String key = url.replaceAll("/", "");
						url = Constants.REQUEST_MAPPING.get(url.replaceAll("/", "")).get("path").toString();
						request.getRequestDispatcher(url + "?sessionKey=" + key).forward(request, response);
					}else{
						servletResponse.sendRedirect(Constants.LOGIN_PAGE);
					}
				}
			}
		}
		return;
	}

	@Override
	public void destroy() {
		System.out.println("结束");
	}
	
	
}
