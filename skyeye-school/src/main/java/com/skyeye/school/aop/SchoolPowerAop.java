/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.aop;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.JedisClientService;


/**
 * 学校相关业务查询拦截是否有schoolId以及查看学校数据的权限
 * @author Lenovo
 *
 */
@Aspect
@Component
public class SchoolPowerAop {
	
	private static final Logger logger = LoggerFactory.getLogger(SchoolPowerAop.class);
	
	@Autowired
	public JedisClientService jedisClient;
	
	/**
	 * 环绕通知
	 * @param pjp
	 * @throws Throwable 
	 */
	@Around("execution(* com.skyeye.school.service.impl.*.*(..))")
	public void doAround(ProceedingJoinPoint pjp) throws Throwable {
		String methodName = pjp.getSignature().getName();
		logger.info("request school URI is [{}]", methodName);
		Map<String, Object> user = InputObject.getLogParamsStatic();
		//当前登录帐号包含某所学校的id
		if(user.containsKey("schoolPowerId") && !ToolUtil.isBlank(user.get("schoolPowerId").toString())){
			pjp.proceed();
		}else{
			OutputObject.setMessage("您还未分配学校，请联系教务人员分配学校。");
		}
	}
	
}
