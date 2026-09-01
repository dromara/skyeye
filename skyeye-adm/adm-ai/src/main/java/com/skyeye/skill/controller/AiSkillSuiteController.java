/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.skill.entity.AiSkillSuite;
import com.skyeye.skill.service.AiSkillSuiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "AI技能套件", tags = "AI技能套件", modelName = "AI技能")
public class AiSkillSuiteController {

    @Autowired
    private AiSkillSuiteService aiSkillSuiteService;

    @ApiOperation(id = "writeAiSkillSuite", value = "新增/编辑AI技能套件", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AiSkillSuite.class)
    @RequestMapping("/post/AiSkillSuiteController/writeAiSkillSuite")
    public void writeAiSkillSuite(InputObject inputObject, OutputObject outputObject) {
        aiSkillSuiteService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAiSkillSuitePageList", value = "分页查询AI技能套件", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AiSkillSuiteController/queryAiSkillSuitePageList")
    public void queryAiSkillSuitePageList(InputObject inputObject, OutputObject outputObject) {
        aiSkillSuiteService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAiSkillSuiteList", value = "获取全部AI技能套件", method = "POST", allUse = "2")
    @RequestMapping("/post/AiSkillSuiteController/queryAiSkillSuiteList")
    public void queryAiSkillSuiteList(InputObject inputObject, OutputObject outputObject) {
        aiSkillSuiteService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "selectAiSkillSuiteById", value = "根据id获取AI技能套件", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AiSkillSuiteController/selectAiSkillSuiteById")
    public void selectAiSkillSuiteById(InputObject inputObject, OutputObject outputObject) {
        aiSkillSuiteService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAiSkillSuiteById", value = "删除AI技能套件", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AiSkillSuiteController/deleteAiSkillSuiteById")
    public void deleteAiSkillSuiteById(InputObject inputObject, OutputObject outputObject) {
        aiSkillSuiteService.deleteById(inputObject, outputObject);
    }
}
