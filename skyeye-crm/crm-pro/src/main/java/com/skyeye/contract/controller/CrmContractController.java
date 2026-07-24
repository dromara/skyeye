/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.contract.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.contract.classenum.CrmContractChildStateEnum;
import com.skyeye.contract.entity.CrmContract;
import com.skyeye.contract.service.CrmContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CrmContractController
 * @Description: 合同管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/24 16:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "合同管理", tags = "合同管理", modelName = "合同管理")
public class CrmContractController {

    @Autowired
    private CrmContractService crmContractService;

    @ApiOperation(id = "queryCrmContractList", value = "获取合同列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CrmContractController/queryCrmContractList")
    public void queryCrmContractList(InputObject inputObject, OutputObject outputObject) {
        crmContractService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeCrmContract", value = "新增/编辑合同信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CrmContract.class)
    @RequestMapping("/post/CrmContractController/writeCrmContract")
    public void writeCrmContract(InputObject inputObject, OutputObject outputObject) {
        crmContractService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCrmContractById", value = "删除合同信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmContractController/deleteCrmContractById")
    public void deleteCrmContractById(InputObject inputObject, OutputObject outputObject) {
        crmContractService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCrmContractByIds", value = "根据id批量获取客户合同信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmContractController/queryCrmContractByIds")
    public void queryCrmContractById(InputObject inputObject, OutputObject outputObject) {
        crmContractService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "mycrmcontract008", value = "根据客户id获取合同管理列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "所属第三方业务数据id")})
    @RequestMapping("/post/CrmContractController/queryCrmContractListByObjectId")
    public void queryCrmContractListByObjectId(InputObject inputObject, OutputObject outputObject) {
        crmContractService.queryCrmContractListByObjectId(inputObject, outputObject);
    }

    @ApiOperation(id = "mycrmcontract009", value = "合同提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/CrmContractController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        crmContractService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "mycrmcontract010", value = "合同执行", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmContractController/performCrmContract")
    public void performCrmContract(InputObject inputObject, OutputObject outputObject) {
        crmContractService.performCrmContract(inputObject, outputObject);
    }

    @ApiOperation(id = "mycrmcontract011", value = "合同关闭", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmContractController/closeCrmContract")
    public void closeCrmContract(InputObject inputObject, OutputObject outputObject) {
        crmContractService.closeCrmContract(inputObject, outputObject);
    }

    @ApiOperation(id = "mycrmcontract012", value = "合同搁置", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmContractController/shelveCrmContract")
    public void shelveCrmContract(InputObject inputObject, OutputObject outputObject) {
        crmContractService.shelveCrmContract(inputObject, outputObject);
    }

    @ApiOperation(id = "mycrmcontract013", value = "合同恢复", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmContractController/recoveryCrmContract")
    public void recoveryCrmContract(InputObject inputObject, OutputObject outputObject) {
        crmContractService.recoveryCrmContract(inputObject, outputObject);
    }

    @ApiOperation(id = "mycrmcontract015", value = "作废合同信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmContractController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        crmContractService.invalid(inputObject, outputObject);
    }

    @ApiOperation(id = "mycrmcontract016", value = "撤销合同审批", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/CrmContractController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        crmContractService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "editCrmContractChildState", value = "修改合同产品状态", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "childState", name = "childState", value = "合同产品状态", enumClass = CrmContractChildStateEnum.class, required = "required")})
    @RequestMapping("/post/CrmContractController/editChildState")
    public void editChildState(InputObject inputObject, OutputObject outputObject) {
        crmContractService.editChildState(inputObject, outputObject);
    }

}
