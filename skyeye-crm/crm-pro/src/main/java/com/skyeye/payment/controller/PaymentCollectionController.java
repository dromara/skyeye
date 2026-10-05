/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.payment.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.payment.entity.PaymentCollection;
import com.skyeye.payment.service.PaymentCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PaymentCollectionController
 * @Description: 回款管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/2 20:38
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "回款管理", tags = "回款管理", modelName = "回款管理")
public class PaymentCollectionController {

    @Autowired
    private PaymentCollectionService paymentCollectionService;

    @ApiOperation(id = "queryPaymentCollectionList", value = "获取回款列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PaymentCollectionController/queryPaymentCollectionList")
    public void queryPaymentCollectionList(InputObject inputObject, OutputObject outputObject) {
        paymentCollectionService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePaymentCollection", value = "新增/编辑回款信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = PaymentCollection.class)
    @RequestMapping("/post/PaymentCollectionController/writePaymentCollection")
    public void writePaymentCollection(InputObject inputObject, OutputObject outputObject) {
        paymentCollectionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePaymentCollectionById", value = "删除回款信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PaymentCollectionController/deletePaymentCollectionById")
    public void deletePaymentCollectionById(InputObject inputObject, OutputObject outputObject) {
        paymentCollectionService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "submitPaymentCollectionToApproval", value = "回款提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/PaymentCollectionController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        paymentCollectionService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "invalidPaymentCollection", value = "作废回款信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PaymentCollectionController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        paymentCollectionService.invalid(inputObject, outputObject);
    }

    @ApiOperation(id = "revokePaymentCollection", value = "撤销回款审批", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/PaymentCollectionController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        paymentCollectionService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPaymentCollectionByContractId", value = "根据合同id获取回款列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "contractId", name = "contractId", value = "合同id")})
    @RequestMapping("/post/PaymentCollectionController/queryPaymentCollectionByContractId")
    public void queryPaymentCollectionByContractId(InputObject inputObject, OutputObject outputObject) {
        paymentCollectionService.queryPaymentCollectionByContractId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPaymentCollectionByIds", value = "根据ids批量获取回款信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/PaymentCollectionController/queryPaymentCollectionByIds")
    public void queryPaymentCollectionByIds(InputObject inputObject, OutputObject outputObject) {
        paymentCollectionService.selectByIds(inputObject, outputObject);
    }
}
