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
import com.skyeye.service.ErpCommonService;

@Controller
public class ErpCommonController {
	
	@Autowired
	private ErpCommonService erpCommonService;
	
	/**
     * 获取单据详情信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpCommonController/queryDepotHeadDetailsMationById")
    @ResponseBody
    public void queryDepotHeadDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpCommonService.queryDepotHeadDetailsMationById(inputObject, outputObject);
    }
    
    /**
     * 删除单据信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpCommonController/deleteErpOrderById")
    @ResponseBody
    public void deleteErpOrderById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpCommonService.deleteErpOrderById(inputObject, outputObject);
    }

    /**
     * 获取订单流水线信息-方便实时监控进度
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpCommonController/queryDepotFlowLineDetailsMationById")
    @ResponseBody
    public void queryDepotFlowLineDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpCommonService.queryDepotFlowLineDetailsMationById(inputObject, outputObject);
    }

    /**
     * erp相关单据撤销审批
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpCommonController/editDepotHeadToRevoke")
    @ResponseBody
    public void editDepotHeadToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpCommonService.editDepotHeadToRevoke(inputObject, outputObject);
    }

    /**
     * erp相关单据根据订单类型获取数据提交类型
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpCommonController/queryDepotHeadSubmitTypeByOrderType")
    @ResponseBody
    public void queryDepotHeadSubmitTypeByOrderType(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpCommonService.queryDepotHeadSubmitTypeByOrderType(inputObject, outputObject);
    }

    /**
     * 订单信息提交审核
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpCommonController/orderSubmitToApproval")
    @ResponseBody
    public void orderSubmitToApproval(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpCommonService.orderSubmitToApproval(inputObject, outputObject);
    }
    
}
