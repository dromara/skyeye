/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.usercase.entity.AutoCase;
import com.skyeye.usercase.entity.AutoUserCaseQueryDo;
import com.skyeye.usercase.service.AutoCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoCaseController
 * @Description: 用例管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/16 20:27
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用例管理", tags = "用例管理", modelName = "用例管理")
public class AutoCaseController {

    @Autowired
    private AutoCaseService autoCaseService;

    /**
     * 获取case列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAutoCaseList", value = "获取用例列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoUserCaseQueryDo.class)
    @RequestMapping("/post/AutoCaseController/queryAutoCaseList")
    public void queryAutoCaseList(InputObject inputObject, OutputObject outputObject) {
        autoCaseService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑case
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeAutoCase", value = "新增/编辑管理列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoCase.class)
    @RequestMapping("/post/AutoCaseController/writeAutoCase")
    public void writeAutoCase(InputObject inputObject, OutputObject outputObject) {
        autoCaseService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除case信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteAutoCaseById", value = "根据ID删除用例信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoCaseController/deleteAutoCaseById")
    public void deleteAutoCaseById(InputObject inputObject, OutputObject outputObject) {
        autoCaseService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id查询case信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAutoCaseById", value = "查询管理列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoCaseController/queryAutoCaseById")
    public void queryAutoCaseById(InputObject inputObject, OutputObject outputObject) {
        autoCaseService.selectById(inputObject, outputObject);
    }

    /**
     * 根据id执行用例
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "executeCase", value = "根据id执行用例", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoCaseController/executeCase")
    public void executeCase(InputObject inputObject, OutputObject outputObject) {
        autoCaseService.executeCase(inputObject, outputObject);
    }

    /**
     * 按前端提交的步骤配置试跑（可未保存），不写入执行历史
     */
    @ApiOperation(id = "executeStep", value = "试跑单个步骤（不落历史，可未保存）", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "stepJson", name = "stepJson", value = "当前步骤JSON", required = "required"),
        @ApiImplicitParam(id = "preStepList", name = "preStepList", value = "前序步骤JSON数组（可选，用于表达式上下文）")})
    @RequestMapping("/post/AutoCaseController/executeStep")
    public void executeStep(InputObject inputObject, OutputObject outputObject) {
        autoCaseService.executeStep(inputObject, outputObject);
    }

    @ApiOperation(id = "aiGenerateStepAssert", value = "AI生成步骤断言建议", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "resultKey", name = "resultKey", value = "步骤编码", required = "required"),
        @ApiImplicitParam(id = "stepName", name = "stepName", value = "步骤名称"),
        @ApiImplicitParam(id = "output", name = "output", value = "试跑输出JSON", required = "required"),
        @ApiImplicitParam(id = "existingAssertList", name = "existingAssertList", value = "已有断言JSON数组")})
    @RequestMapping("/post/AutoCaseController/aiGenerateStepAssert")
    public void aiGenerateStepAssert(InputObject inputObject, OutputObject outputObject) {
        autoCaseService.aiGenerateStepAssert(inputObject, outputObject);
    }

    @ApiOperation(id = "aiParseStepAssert", value = "解析AI步骤断言建议", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "answer", name = "answer", value = "AI完整返回文本", required = "required"),
        @ApiImplicitParam(id = "resultKey", name = "resultKey", value = "步骤编码", required = "required")})
    @RequestMapping("/post/AutoCaseController/aiParseStepAssert")
    public void aiParseStepAssert(InputObject inputObject, OutputObject outputObject) {
        autoCaseService.aiParseStepAssert(inputObject, outputObject);
    }

}
