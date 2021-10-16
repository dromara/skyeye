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
import com.skyeye.service.StatisticsService;

@Controller
public class StatisticsController {
	
	@Autowired
	private StatisticsService statisticsService;
	
	/**
     * 入库明细
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StatisticsController/queryWarehousingDetails")
    @ResponseBody
    public void queryWarehousingDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
    	statisticsService.queryWarehousingDetails(inputObject, outputObject);
    }
    
    /**
     * 出库明细
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StatisticsController/queryOutgoingDetails")
    @ResponseBody
    public void queryOutgoingDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
    	statisticsService.queryOutgoingDetails(inputObject, outputObject);
    }
    
    /**
     * 进货统计
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StatisticsController/queryInComimgDetails")
    @ResponseBody
    public void queryInComimgDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
    	statisticsService.queryInComimgDetails(inputObject, outputObject);
    }
    
    /**
     * 销售统计
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StatisticsController/querySalesDetails")
    @ResponseBody
    public void querySalesDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
    	statisticsService.querySalesDetails(inputObject, outputObject);
    }
    
    /**
     * 客户对账
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StatisticsController/queryCustomerReconciliationDetails")
    @ResponseBody
    public void queryCustomerReconciliationDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
    	statisticsService.queryCustomerReconciliationDetails(inputObject, outputObject);
    }
    
    /**
     * 供应商对账
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StatisticsController/querySupplierReconciliationDetails")
    @ResponseBody
    public void querySupplierReconciliationDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
    	statisticsService.querySupplierReconciliationDetails(inputObject, outputObject);
    }
    
    /**
     * 入库汇总
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StatisticsController/queryInComimgAllDetails")
    @ResponseBody
    public void queryInComimgAllDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
    	statisticsService.queryInComimgAllDetails(inputObject, outputObject);
    }
    
    /**
     * 出库汇总
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StatisticsController/querySalesAllDetails")
    @ResponseBody
    public void querySalesAllDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
    	statisticsService.querySalesAllDetails(inputObject, outputObject);
    }
	
}
