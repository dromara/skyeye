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
import com.skyeye.service.ErpDepartStockService;

@Controller
public class ErpDepartStockController {
	
	@Autowired
	private ErpDepartStockService erpDepartStockService;
	
	/**
     * 获取部门物料库存信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpDepartStockController/queryDepartStockReserveList")
    @ResponseBody
    public void queryDepartStockReserveList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpDepartStockService.queryDepartStockReserveList(inputObject, outputObject);
    }
	
}
