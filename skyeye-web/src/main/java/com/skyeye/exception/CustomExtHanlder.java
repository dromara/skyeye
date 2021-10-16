/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常捕获
 * 
 * @author Lenovo
 *
 */
@RestControllerAdvice
public class CustomExtHanlder {

	public static final Logger log = LoggerFactory.getLogger(CustomExtHanlder.class);

	/**
	 * 捕获全局异常进行统一处理
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public Object handleException(Exception e, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		map.put("returnCode", -9999);
		String message;
		if (e instanceof BadSqlGrammarException) {
			printErrorLog(e, request);
			message = "数据库操作异常";
		} else if (e instanceof CustomException) {
			message = e.getMessage();
		} else {
			printErrorLog(e, request);
			message = "系统异常";
		}
		map.put("returnMessage", message);
		return map;
	}

	private void printErrorLog(Exception e, HttpServletRequest request) {
		// 得到异常栈的首个元素
		StackTraceElement stackTraceElement = e.getStackTrace()[0];
		log.error("url {}, msg {}, method {}, line {}", request.getRequestURI(), e.getMessage(),
				stackTraceElement.getClassName(), stackTraceElement.getLineNumber());
		e.printStackTrace();

	}

}
