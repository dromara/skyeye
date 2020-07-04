/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.common.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfigurerAdapter implements WebMvcConfigurer {
	
	@Value("${IMAGES_PATH}")
	private String IMAGES_PATH;

	/**
	 * 配置静态资源
	 */
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/template/**").addResourceLocations("classpath:/template/");
		registry.addResourceHandler("/images/**").addResourceLocations("file:" + IMAGES_PATH);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// addPathPatterns 用于添加拦截规则
		// excludePathPatterns 用于排除拦截
		registry.addInterceptor(new HandlerInterceptorMain()).addPathPatterns("/post/**");
	}
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//设置允许跨域的路径
                .allowedOrigins("*")//设置允许跨域请求的域名
                .allowCredentials(true)//是否允许证书 不再默认开启
                .allowedMethods("GET", "POST", "PUT", "DELETE")//设置允许的方法
                .maxAge(3600);//跨域允许时间
    }

}
