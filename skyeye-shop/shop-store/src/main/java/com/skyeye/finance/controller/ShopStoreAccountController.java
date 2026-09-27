/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.finance.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.service.ShopStoreAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门店资金账户
 */
@RestController
@Api(value = "门店资金", tags = "门店资金", modelName = "门店管理")
public class ShopStoreAccountController {

    @Autowired
    private ShopStoreAccountService shopStoreAccountService;

    @ApiOperation(id = "queryPersonalStoreFundSummary", value = "查询门店资金概览", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopStoreAccountController/queryPersonalStoreFundSummary")
    public void queryPersonalStoreFundSummary(InputObject inputObject, OutputObject outputObject) {
        shopStoreAccountService.queryPersonalStoreFundSummary(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPersonalStoreLedgerPageList", value = "分页查询门店资金流水", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStoreAccountController/queryPersonalStoreLedgerPageList")
    public void queryPersonalStoreLedgerPageList(InputObject inputObject, OutputObject outputObject) {
        shopStoreAccountService.queryPersonalStoreLedgerPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPersonalStoreReconcileList", value = "门店订单对账列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStoreAccountController/queryPersonalStoreReconcileList")
    public void queryPersonalStoreReconcileList(InputObject inputObject, OutputObject outputObject) {
        shopStoreAccountService.queryPersonalStoreReconcileList(inputObject, outputObject);
    }

    @ApiOperation(id = "applyPersonalStoreWithdraw", value = "申请门店提现", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "amount", name = "amount", value = "提现金额（分）", required = "required"),
        @ApiImplicitParam(id = "accountName", name = "accountName", value = "收款户名", required = "required"),
        @ApiImplicitParam(id = "accountNo", name = "accountNo", value = "银行卡号", required = "required"),
        @ApiImplicitParam(id = "bankName", name = "bankName", value = "开户行", required = "required")})
    @RequestMapping("/post/ShopStoreAccountController/applyPersonalStoreWithdraw")
    public void applyPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject) {
        shopStoreAccountService.applyPersonalStoreWithdraw(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyPersonalStoreWithdrawList", value = "查询我的门店提现记录", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStoreAccountController/queryMyPersonalStoreWithdrawList")
    public void queryMyPersonalStoreWithdrawList(InputObject inputObject, OutputObject outputObject) {
        shopStoreAccountService.queryMyPersonalStoreWithdrawList(inputObject, outputObject);
    }

    @ApiOperation(id = "cancelMyPersonalStoreWithdraw", value = "取消我的门店提现申请", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "提现申请id", required = "required")})
    @RequestMapping("/post/ShopStoreAccountController/cancelMyPersonalStoreWithdraw")
    public void cancelMyPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject) {
        shopStoreAccountService.cancelMyPersonalStoreWithdraw(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPersonalStoreWithdrawList", value = "分页查询门店提现（平台审核）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStoreAccountController/queryPersonalStoreWithdrawList")
    public void queryPersonalStoreWithdrawList(InputObject inputObject, OutputObject outputObject) {
        shopStoreAccountService.queryPersonalStoreWithdrawList(inputObject, outputObject);
    }

    @ApiOperation(id = "approvePersonalStoreWithdraw", value = "通过门店提现并发起银行卡打款", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "提现申请id", required = "required"),
        @ApiImplicitParam(id = "auditRemark", name = "auditRemark", value = "审核备注")})
    @RequestMapping("/post/ShopStoreAccountController/approvePersonalStoreWithdraw")
    public void approvePersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject) {
        shopStoreAccountService.approvePersonalStoreWithdraw(inputObject, outputObject);
    }

    @ApiOperation(id = "rejectPersonalStoreWithdraw", value = "拒绝门店提现申请", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "提现申请id", required = "required"),
        @ApiImplicitParam(id = "auditRemark", name = "auditRemark", value = "审核备注", required = "required")})
    @RequestMapping("/post/ShopStoreAccountController/rejectPersonalStoreWithdraw")
    public void rejectPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject) {
        shopStoreAccountService.rejectPersonalStoreWithdraw(inputObject, outputObject);
    }

    @ApiOperation(id = "retryPersonalStoreWithdraw", value = "重试门店提现打款", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "提现申请id", required = "required")})
    @RequestMapping("/post/ShopStoreAccountController/retryPersonalStoreWithdraw")
    public void retryPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject) {
        shopStoreAccountService.retryPersonalStoreWithdraw(inputObject, outputObject);
    }

    @ApiOperation(id = "backfillPersonalStoreFinance", value = "回填门店历史资金账本", method = "POST", allUse = "2")
    @RequestMapping("/post/ShopStoreAccountController/backfillPersonalStoreFinance")
    public void backfillPersonalStoreFinance(InputObject inputObject, OutputObject outputObject) {
        shopStoreAccountService.backfillPersonalStoreFinance(inputObject, outputObject);
    }
}
