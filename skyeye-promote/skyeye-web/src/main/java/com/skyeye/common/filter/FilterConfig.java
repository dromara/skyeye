/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.common.filter;

import javax.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置
 * 
 * @author 卫志强
 *
 */
@Configuration
public class FilterConfig {
	/**
	 * 配置过滤器
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean someFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(sessionFilter());
		registration.addUrlPatterns("/*");
		registration.addInitParameter("paramName", "paramValue");
		registration.setName("sessionFilter");
		return registration;
	}

	/**
	 * 创建一个bean
	 * 
	 * @return
	 */
	@Bean(name = "sessionFilter")
	public Filter sessionFilter() {
		return new SessionFilter();
	}
}
