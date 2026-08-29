/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bug.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.bug.entity.AutoBug;
import com.skyeye.bug.service.AutoBugService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoBugController
 * @Description: bug管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/18 22:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "bug管理", tags = "bug管理", modelName = "bug管理")
public class AutoBugController {

    @Autowired
    private AutoBugService autoBugService;

    @ApiOperation(id = "queryAutoBugList", value = "获取bug列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AutoBugController/queryAutoBugList")
    public void queryAutoBugList(InputObject inputObject, OutputObject outputObject) {
        autoBugService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoBugAllList", value = "获取bug全部列表(不分页)", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class, value = {
        @ApiImplicitParam(id = "isPaging", name = "isPaging", value = "是否分页", defaultValue = "false")})
    @RequestMapping("/post/AutoBugController/queryAutoBugAllList")
    public void queryAutoBugAllList(InputObject inputObject, OutputObject outputObject) {
        autoBugService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAutoBug", value = "新增/编辑bug", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoBug.class)
    @RequestMapping("/post/AutoBugController/writeAutoBug")
    public void writeAutoBug(InputObject inputObject, OutputObject outputObject) {
        autoBugService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAutoBugById", value = "删除bug信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoBugController/deleteAutoBugById")
    public void deleteAutoBugById(InputObject inputObject, OutputObject outputObject) {
        autoBugService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoBugById", value = "根据id查询bug信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoBugController/queryAutoBugById")
    public void queryAutoBugById(InputObject inputObject, OutputObject outputObject) {
        autoBugService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "aiGenerateBugDraft", value = "AI生成Bug草稿", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "name", name = "name", value = "一句话描述，可与截图二选一"),
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目objectKey", required = "required"),
        @ApiImplicitParam(id = "moduleId", name = "moduleId", value = "模块id"),
        @ApiImplicitParam(id = "versionId", name = "versionId", value = "版本id"),
        @ApiImplicitParam(id = "content", name = "content", value = "已有问题描述"),
        @ApiImplicitParam(id = "remark", name = "remark", value = "已有备注"),
        @ApiImplicitParam(id = "images", name = "images", value = "截图地址列表"),
        @ApiImplicitParam(id = "severityOptions", name = "severityOptions", value = "可选严重性"),
        @ApiImplicitParam(id = "necessaryOptions", name = "necessaryOptions", value = "可选必现类型"),
        @ApiImplicitParam(id = "terminalOptions", name = "terminalOptions", value = "可选终端"),
        @ApiImplicitParam(id = "moduleOptions", name = "moduleOptions", value = "可选模块")})
    @RequestMapping("/post/AutoBugController/aiGenerateBugDraft")
    public void aiGenerateBugDraft(InputObject inputObject, OutputObject outputObject) {
        autoBugService.aiGenerateBugDraft(inputObject, outputObject);
    }

    @ApiOperation(id = "aiGenerateCaseFailureBugDraft", value = "AI根据用例执行失败生成Bug草稿", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目objectKey"),
        @ApiImplicitParam(id = "moduleId", name = "moduleId", value = "模块id"),
        @ApiImplicitParam(id = "caseName", name = "caseName", value = "用例名称"),
        @ApiImplicitParam(id = "stepName", name = "stepName", value = "步骤名称"),
        @ApiImplicitParam(id = "resultKey", name = "resultKey", value = "步骤编码"),
        @ApiImplicitParam(id = "failMessage", name = "failMessage", value = "失败摘要"),
        @ApiImplicitParam(id = "inputParams", name = "inputParams", value = "步骤入参JSON"),
        @ApiImplicitParam(id = "output", name = "output", value = "步骤输出JSON"),
        @ApiImplicitParam(id = "assertList", name = "assertList", value = "断言结果JSON"),
        @ApiImplicitParam(id = "apiDetail", name = "apiDetail", value = "API详情JSON"),
        @ApiImplicitParam(id = "severityOptions", name = "severityOptions", value = "可选严重性"),
        @ApiImplicitParam(id = "necessaryOptions", name = "necessaryOptions", value = "可选必现类型"),
        @ApiImplicitParam(id = "terminalOptions", name = "terminalOptions", value = "可选终端")})
    @RequestMapping("/post/AutoBugController/aiGenerateCaseFailureBugDraft")
    public void aiGenerateCaseFailureBugDraft(InputObject inputObject, OutputObject outputObject) {
        autoBugService.aiGenerateCaseFailureBugDraft(inputObject, outputObject);
    }

    @ApiOperation(id = "aiParseBugDraft", value = "解析AI Bug草稿", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "answer", name = "answer", value = "AI完整返回文本", required = "required")})
    @RequestMapping("/post/AutoBugController/aiParseBugDraft")
    public void aiParseBugDraft(InputObject inputObject, OutputObject outputObject) {
        autoBugService.aiParseBugDraft(inputObject, outputObject);
    }

}
