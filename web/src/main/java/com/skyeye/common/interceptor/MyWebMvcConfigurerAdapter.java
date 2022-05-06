/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 
 * @ClassName: MyWebMvcConfigurerAdapter
 * @Description: 拦截器配置
 * @author: skyeye云系列--卫志强
 * @date: 2020年9月22日 下午9:13:16
 *   
 * @Copyright: 2020 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Configuration
public class MyWebMvcConfigurerAdapter implements WebMvcConfigurer {

	/**
	 * 配置静态资源
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/template/**").addResourceLocations("classpath:/template/");
	}

}
