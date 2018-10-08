package com.skyeye.common.interceptor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.ObjectConstant;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.ToolUtil;


/**
 * 
     * @ClassName: HandlerInterceptorMain
     * @Description: 后台拦截器
     * @author 卫志强
     * @date 2018年6月8日
     *
 */
public class HandlerInterceptorMain implements HandlerInterceptor{
	
	//在进入Handler方法之前执行  
	//用于身份认证、身份授权、  
	//比如身份认证，IP黑名单过滤，如果认证不通过表示当前用户没有登录，需要此方法拦截不再向下执行
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	new PutObject(request, response);
    	/**IP黑名单过滤开始**/
    	String ip;
		ip = request.getHeader("x-forwarded-for");
		// 针对IP是否使用代理访问进行处理
		if (ToolUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ToolUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ToolUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(0, ip.indexOf(","));
		}
		for(String str : Constants.FILTER_FILE_IP_OPTION) {
			if (ip.indexOf(str) != -1) {
				ip = request.getParameter("loginPCIp");
				break;
			}
		}
		
		Properties prop = new Properties();
		String path = ToolUtil.getIPPropertiesPath();
		InputStream in = new BufferedInputStream(new FileInputStream(path));
		prop.load(in); /// 加载属性列表
		Iterator<String> it = prop.stringPropertyNames().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (prop.getProperty(key).equals(ip)) {
				return false;
			}
		}
		in.close();
		/**IP黑名单过滤结束**/
		
    	HttpServletRequest servletRequest = (HttpServletRequest) request;
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
//		String url = servletRequest.getContextPath() + servletRequest.getServletPath();
		if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")){
        	//如果是ajax请求响应头会有x-requested-with
    		return true;
        }else{
            //非ajax请求时
        	return true;
        }
    }

    //进入Handler方法之后，在返回ModelAndView之前执行  
    //应用场景从modelAndView对象出发：将公用的模型数据(比如菜单的导航)在这里传到视图，也可以在这里面来统一指定视图
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    	request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
        if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")){
        	//如果是ajax请求响应头会有x-requested-with
        	if(OutputObject.getCode() == ObjectConstant.WRONG){
        		OutputObject.setCode(0);
        		if(OutputObject.getMessage().equals(ObjectConstant.WRONG_MESSAGE)){
        			OutputObject.setMessage("成功");
        		}else{
        			OutputObject.setCode(ObjectConstant.WRONG);
        		}
        	}
        	OutputObject.put();
        	PutObject.removeRequest();
        }else{
        	//非ajax请求时
        	if(OutputObject.getCode()==ObjectConstant.WRONG){
        		OutputObject.setCode(0);
        		if(OutputObject.getMessage().equals(ObjectConstant.WRONG_MESSAGE)){
        			OutputObject.setMessage("成功");
        		}else{
        			OutputObject.setCode(ObjectConstant.WRONG);
        		}
        	}
        	OutputObject.put();
        	PutObject.removeRequest();
        }
    }

    //执行Handler完成后执行此方法  
  	//应用场景：统一的异常处理，统一的日志处理  
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    	
    }

}
