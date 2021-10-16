/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service.aop;

import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.jedis.JedisClientService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author 卫志强
 * @description: 高并发下，防止接口短时间内重复调用
 */
@Aspect
@Component
public class DuplicateSubmitAspect {
 
	private static final Logger logger = LoggerFactory.getLogger(DuplicateSubmitAspect.class);
 
	@Autowired
	public JedisClientService jedisClient;
 
    @Around("execution(* com.skyeye.*.controller.*.*(..)) or execution(* com.skyeye.controller.*.*(..))")
    public void around(ProceedingJoinPoint pjp) throws Throwable{
    	if(judgeMoreSubmit()){
			pjp.proceed();
		}else{
			OutputObject.setMessage("请求过于频繁，请稍后重试");
		}
    }
    
    /**
	 * 防止表单重复提交
	 * @return
	 */
	public boolean judgeMoreSubmit() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
		HttpServletRequest request = attributes.getRequest();
		String sessionKey = request.getParameter("sessionKey");
		// sessionId+请求路径+请求时间
		String key = sessionId + "-" + request.getServletPath() + DateUtil.getTimeStrAndToString();
		logger.info("sessionId is {}, request name is: {}, url is: {}", sessionId, Constants.REQUEST_MAPPING.get(sessionKey).get("val").toString(), request.getServletPath());
		// 如果缓存中有这个url视为重复提交
		Long increment = jedisClient.incrByData(key, 1);
		if (increment == 1) {
			// 设置过期时间，默认1秒
			jedisClient.expire(key, 1);
			return true;
		}
		// 重复提交
		if (increment > 1) {
			logger.error("重复提交: {}", request.getServletPath());
			return false;
		}
		return true;
	}
}
