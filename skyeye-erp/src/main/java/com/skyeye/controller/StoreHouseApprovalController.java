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
import com.skyeye.service.StoreHouseApprovalService;

@Controller
public class StoreHouseApprovalController {
	
	@Autowired
	private StoreHouseApprovalService storeHouseApprovalService;
	
    /**
     * 获取待审核其他单列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseApprovalController/queryNotApprovedOtherOrderList")
    @ResponseBody
    public void queryNotApprovedOtherOrderList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	storeHouseApprovalService.queryNotApprovedOtherOrderList(inputObject, outputObject);
    }
    
    /**
     * 其他单据单信息审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseApprovalController/editOtherOrderStateToExamineById")
    @ResponseBody
    public void editOtherOrderStateToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	storeHouseApprovalService.editOtherOrderStateToExamineById(inputObject, outputObject);
    }
    
    /**
     * 获取已审核其他单列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/StoreHouseApprovalController/queryPassApprovedOtherOrderList")
    @ResponseBody
    public void queryPassApprovedOtherOrderList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	storeHouseApprovalService.queryPassApprovedOtherOrderList(inputObject, outputObject);
    }
    
}
