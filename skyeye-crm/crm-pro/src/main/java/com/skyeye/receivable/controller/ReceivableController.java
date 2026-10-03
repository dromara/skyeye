package com.skyeye.receivable.controller;


import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.receivable.entity.Receivable;
import com.skyeye.receivable.service.ReceivableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ReceivableController
 * @Description: 应收事项管理
 * @author: skyeye云系列--lqy
 * @date: 2024/5/2 20:38
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "应收事项管理", tags = "应收事项管理", modelName = "应收事项管理")
public class ReceivableController {

    @Autowired
    private ReceivableService receivableService;

    @ApiOperation(id = "writeCrmReceivable", value = "新增/编辑应收事项", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Receivable.class)
    @RequestMapping("/post/ReceivableController/writeCrmReceivable")
    public void writeCrmReceivable(InputObject inputObject, OutputObject outputObject) {
        receivableService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReceivableById", value = "根据id获取应收事项详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/ReceivableController/queryReceivableById")
    public void queryReceivableById(InputObject inputObject, OutputObject outputObject) {
        receivableService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReceivableList", value = "根据objectId(客户id)获取应收事项列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ReceivableController/queryReceivableList")
    public void queryReceivableList(InputObject inputObject, OutputObject outputObject) {
        receivableService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "submitReceivableToApproval", value = "应收事项提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ReceivableController/submitReceivableToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        receivableService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeReceivable", value = "撤销应收事项", method = "PUT", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ReceivableController/revokeReceivable")
    public void revokeReceivable(InputObject inputObject, OutputObject outputObject) {
        receivableService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReceivableByContractId", value = "根据合同id获取应收事项列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "contractId", name = "contractId", value = "合同id")})
    @RequestMapping("/post/ReceivableController/queryReceivableByContractId")
    public void queryReceivableByContractId(InputObject inputObject, OutputObject outputObject) {
        receivableService.queryReceivableByContractId(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteReceivableById", value = "删除应收事项", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReceivableController/deleteReceivableById")
    public void deleteReceivableById(InputObject inputObject, OutputObject outputObject) {
        receivableService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReceivableByIds", value = "根据ids获取应收事项详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "ids", name = "ids", value = "主键ids", required = "required")
    })
    @RequestMapping("/post/ReceivableController/queryReceivableByIds")
    public void queryReceivableByIds(InputObject inputObject, OutputObject outputObject) {
        receivableService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "updateReceivableById", value = "根据id修改回收金额", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
            @ApiImplicitParam(id = "price", name = "price", value = "回收金额", required = "required")
    })
    @RequestMapping("/post/ReceivableController/updateReceivableById")
    public void updateReceivableById(InputObject inputObject, OutputObject outputObject) {
        receivableService.updateReceivableById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReceivableCount", value = "应收事项总笔数", method = "POST", allUse = "0")
    @RequestMapping("/post/ReceivableController/queryReceivableCount")
    public void queryReceivableCount(InputObject inputObject, OutputObject outputObject) {
        receivableService.queryReceivableCount(inputObject, outputObject);
    }

}
