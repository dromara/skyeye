/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.JedisClientService;

/**
 *
 * @ClassName: AOPOutputObjectput
 * @Description: 切面
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/24 12:52
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Aspect
@Component
public class AOPOutputObjectput {
	
	private static final Logger logger = LoggerFactory.getLogger(AOPOutputObjectput.class);
	
	@Autowired
	public JedisClientService jedisClient;
	
	/**
	 * 在进入service前为inputObject与outputObject复值
	 *
	 * @param joinPoint
	 * @throws Exception 
	 */
	@Before("execution(* com.skyeye.*.service.impl.*.*(..)) or execution(* com.skyeye.service.impl.*.*(..))")  
	public void Before(JoinPoint joinPoint) throws Exception {
	}
	
	/**
	 * 环绕通知，该方法的返回值不能设置为void，否则所有的接口调用的方法(例如：service，controller)返回结果都为null
	 *
	 * @param pjp
	 * @throws Throwable 
	 */
	@Around("execution(* com.skyeye.*.service.impl.*.*(..)) or execution(* com.skyeye.service.impl.*.*(..))")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		String methodName = pjp.getSignature().getName();
		if(methodName.endsWith("DownLoad")){
			return pjp.proceed();
		}else{
			if(InputObject.getRequest() == null){
				// 任务调用会没有request，这里直接放行
				return pjp.proceed();
			}else{
				String result = InputObject.setParams();
				if(ToolUtil.isBlank(result)){
					return pjp.proceed();
				}else{
					logger.info("result is: {}, time is: {}, this methodName is: {}", result, DateUtil.getTimeAndToString(), methodName);
					OutputObject.setMessage(result);
					return null;
				}
			}
		}
	}
	
	/**
	 * 在调用service后为将outputObject里的值输出至前端
	 * 表示这是一个返回通知，即有目标方法有返回值的时候才会触发，该注解中的 returning 属性表示目标方法返回值的变量名，这个需要和参数一一对应吗，
	 * 注意：目标方法的返回值类型要和这里方法返回值参数的类型一致，否则拦截不到，如果想拦截所有（包括返回值为 void），则方法返回值参数可以为 Object
	 *
	 * @param joinPoint
	 */
	@AfterReturning("execution(* com.skyeye.*.service.impl.*.*(..)) or execution(* com.skyeye.service.impl.*.*(..))")
	public void after(JoinPoint joinPoint) throws Exception {
	}
	
	/**
	 * 通过throwing属性指定连接点方法出现异常信息存储在ex变量中，在异常通知方法中就可以从ex变量中获取异常信息了
	 *
	 * @param joinPoint
	 * @param e
	 */
	@AfterThrowing(value="execution(* com.skyeye.*.service.impl.*.*(..)) or execution(* com.skyeye.service.impl.*.*(..))", throwing="e")
	public void afterThrowing(JoinPoint joinPoint, Exception e) {
		if(InputObject.getRequest() != null){
			OutputObject.setMessage(e.getMessage());
		}
	}
}
