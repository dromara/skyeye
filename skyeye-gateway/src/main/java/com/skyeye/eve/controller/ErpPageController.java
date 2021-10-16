/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.ErpPageService;

@Controller
public class ErpPageController {
	
	@Autowired
	private ErpPageService erpPageService;
	
	/**
     * 获取本月累计销售，本月累计零售，本月累计采购，本月利润（已审核通过）
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPageController/queryFourTypeMoneyList")
    @ResponseBody
    public void queryFourTypeMoneyList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPageService.queryFourTypeMoneyList(inputObject, outputObject);
    }
    
    /**
     * 获取近半年的采购统计
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPageController/querySixMonthPurchaseMoneyList")
    @ResponseBody
    public void querySixMonthPurchaseMoneyList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPageService.querySixMonthPurchaseMoneyList(inputObject, outputObject);
    }
    
    /**
     * 获取近半年的销售统计
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPageController/querySixMonthSealsMoneyList")
    @ResponseBody
    public void querySixMonthSealsMoneyList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPageService.querySixMonthSealsMoneyList(inputObject, outputObject);
    }
    
    /**
     * 获取近十二个月的利润统计
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpPageController/queryTwelveMonthProfitMoneyList")
    @ResponseBody
    public void queryTwelveMonthProfitMoneyList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpPageService.queryTwelveMonthProfitMoneyList(inputObject, outputObject);
    }
	
}
