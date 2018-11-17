package com.skyeye.common.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.alibaba.fastjson.JSON;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.JedisClient;
import com.skyeye.jedis.impl.JedisClientCluster;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


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
	@SuppressWarnings("unchecked")
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
			//用户是否为空判断
			if("1".equals(request.getParameter("allUse").toString())){//是否需要登录才能使用   1是   0否    默认为否
				ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(servletRequest.getSession().getServletContext());
				JedisClient jedisClient = (JedisClient)ac.getBean("jedisClient");
				if(!jedisClient.exists("userMation:" + request.getParameter("userToken").toString())){
					servletResponse.setHeader("SESSIONSTATUS", "TIMEOUT");
					return;
				}else{
					//重置redis时间
					Map<String, Object> userMation = JSONObject.fromObject(jedisClient.get("userMation:" + request.getParameter("userToken").toString()));//用户信息
					List<Map<String, Object>> deskTops = JSONArray.fromObject(jedisClient.get("deskTopsMation:" + request.getParameter("userToken").toString()));//桌面菜单信息
					List<Map<String, Object>> allMenu = JSONArray.fromObject(jedisClient.get("allMenuMation:" + request.getParameter("userToken").toString()));//所有菜单信息
					jedisClient.set("userMation:" + request.getParameter("userToken").toString(), JSON.toJSONString(userMation));
					jedisClient.expire("userMation:" + request.getParameter("userToken").toString(), 1800);//时间为30分钟
					jedisClient.set("deskTopsMation:" + request.getParameter("userToken").toString(), JSON.toJSONString(deskTops));
					jedisClient.expire("deskTopsMation:" + request.getParameter("userToken").toString(), 1800);//时间为30分钟
					jedisClient.set("allMenuMation:" + request.getParameter("userToken").toString(), JSON.toJSONString(allMenu));
					jedisClient.expire("allMenuMation:" + request.getParameter("userToken").toString(), 1800);//时间为30分钟
					chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
				}
			}else{
				chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
			}
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
						String allUse = Constants.REQUEST_MAPPING.get(key).get("allUse").toString();
						if("1".equals(allUse)){//是否需要登录才能使用   1是   0否    默认为否
							ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(servletRequest.getSession().getServletContext());
							JedisClientCluster jedisClient = (JedisClientCluster)ac.getBean("jedisClientCluster");
							if(ToolUtil.isBlank(request.getParameter("userToken"))){
								servletResponse.setHeader("SESSIONSTATUS", "TIMEOUT");
								return;
							}
							if(!jedisClient.exists("userMation:" + request.getParameter("userToken").toString())){
								servletResponse.setHeader("SESSIONSTATUS", "TIMEOUT");
								return;
							}else{
								//重置redis时间
								Map<String, Object> userMation = JSONObject.fromObject(jedisClient.get("userMation:" + request.getParameter("userToken").toString()));//用户信息
								List<Map<String, Object>> deskTops = JSONArray.fromObject(jedisClient.get("deskTopsMation:" + request.getParameter("userToken").toString()));//桌面菜单信息
								List<Map<String, Object>> allMenu = JSONArray.fromObject(jedisClient.get("allMenuMation:" + request.getParameter("userToken").toString()));//所有菜单信息
								jedisClient.set("userMation:" + request.getParameter("userToken").toString(), JSON.toJSONString(userMation));
								jedisClient.expire("userMation:" + request.getParameter("userToken").toString(), 1800);//时间为30分钟
								jedisClient.set("deskTopsMation:" + request.getParameter("userToken").toString(), JSON.toJSONString(deskTops));
								jedisClient.expire("deskTopsMation:" + request.getParameter("userToken").toString(), 1800);//时间为30分钟
								jedisClient.set("allMenuMation:" + request.getParameter("userToken").toString(), JSON.toJSONString(allMenu));
								jedisClient.expire("allMenuMation:" + request.getParameter("userToken").toString(), 1800);//时间为30分钟
								
								url = Constants.REQUEST_MAPPING.get(key).get("path").toString();
								String queryString = servletRequest.getQueryString();
								if(ToolUtil.isBlank(queryString)){
									request.getRequestDispatcher(url + "?sessionKey=" + key + "&allUse=" + allUse).forward(request, response);
								}else{
									request.getRequestDispatcher(url + "?sessionKey=" + key + "&allUse=" + allUse).forward(request, response);
								}
							}
						}else{
							url = Constants.REQUEST_MAPPING.get(key).get("path").toString();
							String queryString = servletRequest.getQueryString();
							if(ToolUtil.isBlank(queryString)){
								request.getRequestDispatcher(url + "?sessionKey=" + key + "&allUse=" + allUse).forward(request, response);
							}else{
								request.getRequestDispatcher(url + "?sessionKey=" + key + "&allUse=" + allUse).forward(request, response);
							}
						}
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
