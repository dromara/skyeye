/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.seal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.seal.service.SalesOrderStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 销售订单统计控制层：现查现算
 */
@RestController
@Api(value = "销售订单统计", tags = "销售订单统计", modelName = "销售模块")
public class SalesOrderStatisticsController {

    @Autowired
    private SalesOrderStatisticsService salesOrderStatisticsService;

    @ApiOperation(id = "querySalesOrderTotalPrice", value = "销售订单总金额", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SalesOrderStatisticsController/querySalesOrderTotalPrice")
    public void querySalesOrderTotalPrice(InputObject inputObject, OutputObject outputObject) {
        salesOrderStatisticsService.querySalesOrderTotalPrice(inputObject, outputObject);
    }

    @ApiOperation(id = "querySalesOrderCustomerCount", value = "签单客户数", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SalesOrderStatisticsController/querySalesOrderCustomerCount")
    public void querySalesOrderCustomerCount(InputObject inputObject, OutputObject outputObject) {
        salesOrderStatisticsService.querySalesOrderCustomerCount(inputObject, outputObject);
    }

    @ApiOperation(id = "querySalesOrderSignCount", value = "销售订单签署数量", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SalesOrderStatisticsController/querySalesOrderSignCount")
    public void querySalesOrderSignCount(InputObject inputObject, OutputObject outputObject) {
        salesOrderStatisticsService.querySalesOrderSignCount(inputObject, outputObject);
    }

    @ApiOperation(id = "querySalesOrderTrend", value = "销售订单日度趋势（数量+金额）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SalesOrderStatisticsController/querySalesOrderTrend")
    public void querySalesOrderTrend(InputObject inputObject, OutputObject outputObject) {
        salesOrderStatisticsService.querySalesOrderTrend(inputObject, outputObject);
    }

}
