package com.skyeye.service.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Before;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;


@Component
@Aspect
public class AOPOutputObjectput {
	
	private static final Logger logger = LoggerFactory.getLogger(AOPOutputObjectput.class);

	/**
	 * 在进入service前为inputObject与outputObject复值
	 * @param joinPoint
	 * @throws Exception 
	 */
	@Before("execution (* com.skyeye.*.service.impl.*.*(..))")  
	public void Before(JoinPoint joinPoint) throws Exception {
		String methodName = joinPoint.getSignature().getName();
		if(methodName.endsWith("DownLoad")){
			
		}else{
			InputObject.setParams();
		}
	}
	
	/**
	 * 环绕通知
	 * @param pjp
	 * @throws Exception
	 */
	@Around("execution (* com.skyeye.*.service.impl.*.*(..))")
	public void doAround(ProceedingJoinPoint pjp) throws Exception {
		String methodName = pjp.getSignature().getName();
		if(methodName.endsWith("DownLoad")){
			try {
				pjp.proceed();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}else{
			String result = InputObject.setParams();
			try {
				if(ToolUtil.isBlank(result)){
					pjp.proceed();
				}else{
					System.out.println("result:" + result + "         错误时间：" + ToolUtil.getTimeAndToString() + "       方法名：" + pjp.getSignature().getName());
					logger.info("result:" + result + "         错误时间：" + ToolUtil.getTimeAndToString() + "       方法名：" + pjp.getSignature().getName());
					OutputObject.setMessage(result);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 在调用service后为将outputObject里的值输出至前端
	 * @param joinPoint
	 */
	@AfterReturning("execution (* com.skyeye.*.service.impl.*.*(..))")  
	public void after(JoinPoint joinPoint) throws Exception {
		System.gc();
	}
}
