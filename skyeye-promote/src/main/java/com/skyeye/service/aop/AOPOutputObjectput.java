package com.skyeye.service.aop;

import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Before;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.JedisClientService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Aspect
@Component
public class AOPOutputObjectput {
	
	private static final Logger logger = LoggerFactory.getLogger(AOPOutputObjectput.class);
	
	@Autowired
	public JedisClientService jedisClient;

	/**
	 * 在进入service前为inputObject与outputObject复值
	 * @param joinPoint
	 * @throws Exception 
	 */
	@Before("execution(* com.skyeye.*.service.impl.*.*(..))")  
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
	@SuppressWarnings("unchecked")
	@Around("execution(* com.skyeye.*.service.impl.*.*(..))")
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
					Map<String, Object> params = InputObject.getMap();
					if("1".equals(params.get("urlUseJurisdiction").toString())){//是否需要登录才能使用   1是   0否    默认为否
						if(!jedisClient.exists("userMation:" + params.get("userToken").toString())){
							OutputObject.setMessage("登录超时，请重新登录。");
						}else{
							Map<String, Object> userMation = JSONObject.fromObject(jedisClient.get("userMation:" + params.get("userToken").toString()));//用户信息
							List<Map<String, Object>> deskTops = JSONArray.fromObject(jedisClient.get("deskTopsMation:" + params.get("userToken").toString()));//桌面菜单信息
							List<Map<String, Object>> allMenu = JSONArray.fromObject(jedisClient.get("allMenuMation:" + params.get("userToken").toString()));//所有菜单信息
							InputObject.setUSER_MATION(userMation);
							InputObject.setUSER_DESKTOP_MENU_MATION(deskTops);
							InputObject.setUSER_ALL_MENU_MATION(allMenu);
							pjp.proceed();
						}
					}else{
						pjp.proceed();
					}
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
	@AfterReturning("execution(* com.skyeye.*.service.impl.*.*(..))")  
	public void after(JoinPoint joinPoint) throws Exception {
		System.gc();
	}
	
	/**
	 * 通过throwing属性指定连接点方法出现异常信息存储在ex变量中，在异常通知方法中就可以从ex变量中获取异常信息了
	 * @param joinPoint
	 * @param e
	 */
	@AfterThrowing(value="execution(* com.skyeye.*.service.impl.*.*(..))", throwing="e")
	public void afterThrowing(JoinPoint joinPoint, Exception e) {
//		String methodName = joinPoint.getSignature().getName();//方法名
//        List<Object> args = Arrays.asList(joinPoint.getArgs());//参数
        OutputObject.setMessage(e.getMessage());
	}
}
