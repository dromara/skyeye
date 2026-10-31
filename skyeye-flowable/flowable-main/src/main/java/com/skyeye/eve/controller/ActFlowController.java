/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.ActFlowMation;
import com.skyeye.eve.service.ActFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ActFlowController
 * @Description: 流程模型管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/4 22:51
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "流程模型管理", tags = "流程模型管理", modelName = "工作流模块")
public class ActFlowController {

    @Autowired
    private ActFlowService actFlowService;

    @ApiOperation(id = "queryActFlowList", value = "获取流程模型列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ActFlowController/queryActFlowList")
    public void queryActFlowList(InputObject inputObject, OutputObject outputObject) {
        actFlowService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeActFlowMation", value = "新增/编辑流程模型", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ActFlowMation.class)
    @RequestMapping("/post/ActFlowController/writeActFlowMation")
    public void writeActFlowMation(InputObject inputObject, OutputObject outputObject) {
        actFlowService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryActFlowMationById", value = "根据ID获取流程模型信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ActFlowController/queryActFlowMationById")
    public void queryActFlowMationById(InputObject inputObject, OutputObject outputObject) {
        actFlowService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteActFlowMationById", value = "根据ID删除流程模型", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ActFlowController/deleteActFlowMationById")
    public void deleteActFlowMationById(InputObject inputObject, OutputObject outputObject) {
        actFlowService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryActFlowListByClassName", value = "根据适用对象获取流程模型列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ActFlowController/queryActFlowListByClassName")
    public void queryActFlowListByClassName(InputObject inputObject, OutputObject outputObject) {
        actFlowService.queryActFlowListByClassName(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllActFlowListByClassName", value = "根据适用对象获取流程模型列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "appId", name = "appId", value = "业务对象所属的appId"),
        @ApiImplicitParam(id = "serviceClassName", name = "serviceClassName", value = "业务对象信息", required = "required")})
    @RequestMapping("/post/ActFlowController/queryAllActFlowListByClassName")
    public void queryAllActFlowListByClassName(InputObject inputObject, OutputObject outputObject) {
        actFlowService.queryAllActFlowListByClassName(inputObject, outputObject);
    }

    @ApiOperation(id = "copyActFlowMationById", value = "复制流程模型", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ActFlowMation.class)
    @RequestMapping("/post/ActFlowController/copyActFlowMationById")
    public void copyActFlowMationById(InputObject inputObject, OutputObject outputObject) {
        actFlowService.copyActFlowMationById(inputObject, outputObject);
    }

    @ApiOperation(id = "applyActFlowAiDraft", value = "AI 流程草案写入设计器（不发布）", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "流程配置主键", required = "required"),
        @ApiImplicitParam(id = "approveSteps", name = "approveSteps", value = "审批节点 JSON 数组，元素为 name 或 {name}", required = "required")})
    @RequestMapping("/post/ActFlowController/applyActFlowAiDraft")
    public void applyActFlowAiDraft(InputObject inputObject, OutputObject outputObject) {
        actFlowService.applyActFlowAiDraft(inputObject, outputObject);
    }

}
