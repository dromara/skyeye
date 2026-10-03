package com.skyeye.payable.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.payable.entity.Payable;
import com.skyeye.payable.service.PayableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PayableController
 * @Description: 应付事项管理
 * @author: skyeye云系列--lqy
 * @date: 2024/5/2 20:38
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "应付事项管理", tags = "应付事项管理", modelName = "应付事项管理")
public class PayableController {

    @Autowired
    private PayableService payableService;


    @ApiOperation(id = "writeErpPayable", value = "新增/编辑应付事项", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Payable.class)
    @RequestMapping("/post/PayableController/writeErpPayable")
    public void writeErpPayable(InputObject inputObject, OutputObject outputObject) {
        payableService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPayableById", value = "根据id获取应付事项详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/PayableController/queryPayableById")
    public void queryPayableById(InputObject inputObject, OutputObject outputObject) {
        payableService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPayableList", value = "根据objectId(供应商id)获取应付事项列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PayableController/queryPayableList")
    public void queryPayableList(InputObject inputObject, OutputObject outputObject) {
        payableService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "submitPayableToApproval", value = "应付事项提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/PayableController/submitPayableToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        payableService.submitToApproval(inputObject, outputObject);
    }


    @ApiOperation(id = "revokePayable", value = "撤销应付事项", method = "PUT", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/PayableController/revokePayable")
    public void revokePayable(InputObject inputObject, OutputObject outputObject) {
        payableService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPayableByContractId", value = "根据合同id获取应付事项列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "contractId", name = "contractId", value = "合同id")})
    @RequestMapping("/post/PayableController/queryPayableByContractId")
    public void queryPayableByContractId(InputObject inputObject, OutputObject outputObject) {
        payableService.queryPayableByContractId(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePayableById", value = "删除应付事项", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PayableController/deletePayableById")
    public void deletePayableById(InputObject inputObject, OutputObject outputObject) {
        payableService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPayableByIds", value = "根据ids获取应付事项详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")
    })
    @RequestMapping("/post/PayableController/queryPayableByIds")
    public void queryPayableByIds(InputObject inputObject, OutputObject outputObject) {
        payableService.selectByIds(inputObject, outputObject);
    }


    @ApiOperation(id = "updatePayableById", value = "根据id修改已付金额", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
            @ApiImplicitParam(id = "price", name = "price", value = "回收金额", required = "required")
    })
    @RequestMapping("/post/ReceivableController/updatePayableById")
    public void updatePayableById(InputObject inputObject, OutputObject outputObject) {
        payableService.updatePayableById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPayableCount", value = "应付事项总笔数", method = "POST", allUse = "0")
    @RequestMapping("/post/PayableController/queryPayableCount")
    public void queryPayableCount(InputObject inputObject, OutputObject outputObject) {
        payableService.queryPayableCount(inputObject, outputObject);
    }

}
