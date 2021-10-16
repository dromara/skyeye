/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SealSeServiceMyPartsService;

@Controller
public class SealSeServiceMyPartsController {
	
	@Autowired
	private SealSeServiceMyPartsService sealSeServiceMyPartsService;
	
	/**
	 *
	 * @Title: queryMyApplyPartsList
	 * @Description: 获取我申领的未使用的配件
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceMyPartsController/queryMyApplyPartsList")
	@ResponseBody
	public void queryMyApplyPartsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceMyPartsService.queryMyApplyPartsList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryMyApplyUsePartsList
	 * @Description: 获取我使用的配件
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceMyPartsController/queryMyApplyUsePartsList")
	@ResponseBody
	public void queryMyApplyUsePartsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceMyPartsService.queryMyApplyUsePartsList(inputObject, outputObject);
	}
	
}
