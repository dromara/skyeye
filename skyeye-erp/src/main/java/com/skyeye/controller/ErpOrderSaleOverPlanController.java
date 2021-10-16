/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;


import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ErpOrderSaleOverPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ERP销售订单统筹
 *
 * @Author: 卫志强
 * @Date: 2021/02/27 15:43
 */
@Controller
public class ErpOrderSaleOverPlanController {

    @Autowired
    private ErpOrderSaleOverPlanService erpOrderSaleOverPlanService;

    /**
     * 销售订单统筹信息列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpOrderSaleOverPlanController/queryOrderSaleOverPlanList")
    @ResponseBody
    public void queryOrderSaleOverPlanList(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpOrderSaleOverPlanService.queryOrderSaleOverPlanList(inputObject, outputObject);
    }

    /**
     * 销售订单统筹
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpOrderSaleOverPlanController/editOrderSaleOverPlanMation")
    @ResponseBody
    public void editOrderSaleOverPlanMation(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpOrderSaleOverPlanService.editOrderSaleOverPlanMation(inputObject, outputObject);
    }

    /**
     * 根据销售订单id获取统筹信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpOrderSaleOverPlanController/queryOrderSaleOverPlanMationByOrderId")
    @ResponseBody
    public void queryOrderSaleOverPlanMationByOrderId(InputObject inputObject, OutputObject outputObject) throws Exception{
        erpOrderSaleOverPlanService.queryOrderSaleOverPlanMationByOrderId(inputObject, outputObject);
    }

}
