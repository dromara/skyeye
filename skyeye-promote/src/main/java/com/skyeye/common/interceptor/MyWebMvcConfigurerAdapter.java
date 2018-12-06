package com.skyeye.common.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
	
	@Value("${IMAGES_PATH}")
	private String IMAGES_PATH;

	/**
	 * 配置静态资源
	 */
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/template/**").addResourceLocations("classpath:/template/");
		registry.addResourceHandler("/images/**").addResourceLocations("file:" + IMAGES_PATH);
		super.addResourceHandlers(registry);
	}

	public void addInterceptors(InterceptorRegistry registry) {
		// addPathPatterns 用于添加拦截规则
		// excludePathPatterns 用于排除拦截
		registry.addInterceptor(new HandlerInterceptorMain()).addPathPatterns("/post/**");
		super.addInterceptors(registry);
	}

}
