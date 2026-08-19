/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.demand.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.demand.entity.AutoDemand;
import com.skyeye.demand.service.AutoDemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoDemandController
 * @Description: 需求表控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@RestController
@Api(value = "需求管理", tags = "需求管理", modelName = "需求管理")
public class AutoDemandController {

    @Autowired
    private AutoDemandService autoDemandService;

    @ApiOperation(id = "queryAutoDemandList", value = "获取需求列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AutoDemandController/queryAutoDemandList")
    public void queryAutoDemandList(InputObject inputObject, OutputObject outputObject) {
        autoDemandService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoDemandAllList", value = "获取需求全部列表(不分页)", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class, value = {
        @ApiImplicitParam(id = "isPaging", name = "isPaging", value = "是否分页", defaultValue = "false")})
    @RequestMapping("/post/AutoDemandController/queryAutoDemandAllList")
    public void queryAutoDemandAllList(InputObject inputObject, OutputObject outputObject) {
        autoDemandService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAutoDemand", value = "新增/编辑需求信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoDemand.class)
    @RequestMapping("/post/AutoDemandController/writeAutoDemand")
    public void writeAutoDemand(InputObject inputObject, OutputObject outputObject) {
        autoDemandService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAutoDemandById", value = "根据id删除需求信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoDemandController/deleteAutoDemandById")
    public void deleteAutoDemandById(InputObject inputObject, OutputObject outputObject) {
        autoDemandService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoDemandById", value = "根据id查询需求信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoDemandController/queryAutoDemandById")
    public void queryAutoDemandById(InputObject inputObject, OutputObject outputObject) {
        autoDemandService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "updateStateAutoDemandById", value = "按负责人角色推进需求状态", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "roleKey", name = "roleKey", value = "角色：front/back/test", required = "required")})
    @RequestMapping("/post/UserController/updateStateAutoDemandById")
    public void updateStateAutoDemandById(InputObject inputObject, OutputObject outputObject) {
        autoDemandService.updateStateAutoDemandById(inputObject, outputObject);
    }

    @ApiOperation(id = "invalidAutoDemandById", value = "作废需求", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/UserController/invalidAutoDemandById")
    public void invalidAutoDemandById(InputObject inputObject, OutputObject outputObject) {
        autoDemandService.invalidAutoDemandById(inputObject, outputObject);
    }

    @ApiOperation(id = "updateAutoDemandEstimateTime", value = "更新需求角色预计时间", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "需求id", required = "required"),
        @ApiImplicitParam(id = "roleKey", name = "roleKey", value = "角色：front/back/test", required = "required"),
        @ApiImplicitParam(id = "startTime", name = "startTime", value = "预计开始时间", required = "required"),
        @ApiImplicitParam(id = "endTime", name = "endTime", value = "预计结束时间", required = "required")})
    @RequestMapping("/post/AutoDemandController/updateAutoDemandEstimateTime")
    public void updateAutoDemandEstimateTime(InputObject inputObject, OutputObject outputObject) {
        autoDemandService.updateAutoDemandEstimateTime(inputObject, outputObject);
    }

    @ApiOperation(id = "aiGenerateDemandDraft", value = "AI生成需求草稿", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "name", name = "name", value = "标题", required = "required"),
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目objectKey", required = "required"),
        @ApiImplicitParam(id = "id", name = "id", value = "需求id，编辑时传入"),
        @ApiImplicitParam(id = "moduleId", name = "moduleId", value = "模块id"),
        @ApiImplicitParam(id = "versionId", name = "versionId", value = "版本id"),
        @ApiImplicitParam(id = "content", name = "content", value = "已有内容"),
        @ApiImplicitParam(id = "remark", name = "remark", value = "已有备注"),
        @ApiImplicitParam(id = "testJoinAnalysis", name = "testJoinAnalysis", value = "测试是否参与需求分析")})
    @RequestMapping("/post/AutoDemandController/aiGenerateDemandDraft")
    public void aiGenerateDemandDraft(InputObject inputObject, OutputObject outputObject) {
        autoDemandService.aiGenerateDemandDraft(inputObject, outputObject);
    }

    @ApiOperation(id = "aiParseDemandDraft", value = "解析AI需求草稿", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "answer", name = "answer", value = "AI完整返回文本", required = "required")})
    @RequestMapping("/post/AutoDemandController/aiParseDemandDraft")
    public void aiParseDemandDraft(InputObject inputObject, OutputObject outputObject) {
        autoDemandService.aiParseDemandDraft(inputObject, outputObject);
    }

}

