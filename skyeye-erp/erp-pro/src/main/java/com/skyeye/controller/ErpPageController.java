/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ErpPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ErpPageController
 * @Description: ERP统计模块控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/2 11:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "ERP统计模块", tags = "ERP统计模块", modelName = "ERP统计模块")
public class ErpPageController {

    @Autowired
    private ErpPageService erpPageService;

    @ApiOperation(id = "erppage001", value = "获取本月累计销售，本月累计零售，本月累计采购，本月利润（已审核通过）", method = "POST", allUse = "2")
    @RequestMapping("/post/ErpPageController/queryFourTypeMoneyList")
    public void queryFourTypeMoneyList(InputObject inputObject, OutputObject outputObject) {
        erpPageService.queryFourTypeMoneyList(inputObject, outputObject);
    }

    @ApiOperation(id = "erppage002", value = "获取近半年的采购统计", method = "POST", allUse = "2")
    @RequestMapping("/post/ErpPageController/querySixMonthPurchaseMoneyList")
    public void querySixMonthPurchaseMoneyList(InputObject inputObject, OutputObject outputObject) {
        erpPageService.querySixMonthPurchaseMoneyList(inputObject, outputObject);
    }

    @ApiOperation(id = "erppage003", value = "获取近半年的销售统计", method = "POST", allUse = "2")
    @RequestMapping("/post/ErpPageController/querySixMonthSealsMoneyList")
    public void querySixMonthSealsMoneyList(InputObject inputObject, OutputObject outputObject) {
        erpPageService.querySixMonthSealsMoneyList(inputObject, outputObject);
    }

    @ApiOperation(id = "erppage004", value = "获取近十二个月的利润统计", method = "POST", allUse = "2")
    @RequestMapping("/post/ErpPageController/queryTwelveMonthProfitMoneyList")
    public void queryTwelveMonthProfitMoneyList(InputObject inputObject, OutputObject outputObject) {
        erpPageService.queryTwelveMonthProfitMoneyList(inputObject, outputObject);
    }

    /**
     * 一次性返回 ERP 业务流程各节点数量，key 为节点 code（A01~I06），value 为笔数。
     * 默认均为对应单据类型的总笔数（当前租户、不限审批状态、不含搜索条件）；
     * E01 为待出库单据笔数，E03 为待入库单据笔数。
     */
    @ApiOperation(id = "erppage005", value = "获取ERP业务流程各节点单据数量（单据总笔数；出库管理/入库管理为待出入库笔数）", method = "POST", allUse = "2")
    @RequestMapping("/post/ErpPageController/queryProcessFlowCount")
    public void queryProcessFlowCount(InputObject inputObject, OutputObject outputObject) {
        erpPageService.queryProcessFlowCount(inputObject, outputObject);
    }

}
