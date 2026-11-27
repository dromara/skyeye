/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.opportunity.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.opportunity.entity.CrmOpportunity;
import com.skyeye.opportunity.service.CrmOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CrmOpportunityController
 * @Description: 商机管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/26 12:18
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "商机管理", tags = "商机管理", modelName = "商机管理")
public class CrmOpportunityController {

    @Autowired
    private CrmOpportunityService crmOpportunityService;

    @ApiOperation(id = "queryCrmOpportunityList", value = "获取商机列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CrmOpportunityController/queryCrmOpportunityList")
    public void queryCrmOpportunityList(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeCrmOpportunity", value = "新增/编辑商机信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CrmOpportunity.class)
    @RequestMapping("/post/CrmOpportunityController/writeCrmOpportunity")
    public void writeCrmOpportunity(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCrmOpportunityById", value = "删除商机信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmOpportunityController/deleteCrmOpportunityById")
    public void deleteCrmOpportunityById(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCrmOpportunityListByObjectId", value = "根据客户id获取指定状态的商机列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "所属第三方业务数据id")})
    @RequestMapping("/post/CrmOpportunityController/queryCrmOpportunityListByObjectId")
    public void queryCrmOpportunityListByObjectId(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.queryCrmOpportunityListByObjectId(inputObject, outputObject);
    }

    @ApiOperation(id = "opportunity013", value = "作废商机信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmOpportunityController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.invalid(inputObject, outputObject);
    }

    @ApiOperation(id = "opportunity017", value = "商机提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/CrmOpportunityController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "opportunity027", value = "撤销商机审批", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/CrmOpportunityController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "opportunity019", value = "根据商机Id初期沟通", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmOpportunityController/conmunicateOpportunity")
    public void conmunicateOpportunity(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.conmunicateOpportunity(inputObject, outputObject);
    }

    @ApiOperation(id = "opportunity020", value = "根据商机Id方案与报价", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmOpportunityController/quotedPriceOpportunity")
    public void quotedPriceOpportunity(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.quotedPriceOpportunity(inputObject, outputObject);
    }

    @ApiOperation(id = "opportunity021", value = "根据商机Id竞争与投标", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmOpportunityController/tenderOpportunity")
    public void tenderOpportunity(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.tenderOpportunity(inputObject, outputObject);
    }

    @ApiOperation(id = "opportunity022", value = "根据商机Id商务谈判", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmOpportunityController/negotiateOpportunity")
    public void negotiateOpportunity(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.negotiateOpportunity(inputObject, outputObject);
    }

    @ApiOperation(id = "opportunity023", value = "根据商机Id成交", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmOpportunityController/turnoverOpportunity")
    public void turnoverOpportunity(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.turnoverOpportunity(inputObject, outputObject);
    }

    @ApiOperation(id = "opportunity024", value = "根据商机Id丢单", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmOpportunityController/losingTableOpportunity")
    public void losingTableOpportunity(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.losingTableOpportunity(inputObject, outputObject);
    }

    @ApiOperation(id = "opportunity025", value = "根据商机Id搁置", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmOpportunityController/layAsideOpportunity")
    public void layAsideOpportunity(InputObject inputObject, OutputObject outputObject) {
        crmOpportunityService.layAsideOpportunity(inputObject, outputObject);
    }

}
