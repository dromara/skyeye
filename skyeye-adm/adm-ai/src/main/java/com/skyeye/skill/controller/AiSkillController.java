/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.skill.entity.AiSkill;
import com.skyeye.skill.service.AiSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "AI技能", tags = "AI技能", modelName = "AI技能")
public class AiSkillController {

    @Autowired
    private AiSkillService aiSkillService;

    @ApiOperation(id = "writeAiSkill", value = "新增/编辑AI技能", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AiSkill.class)
    @RequestMapping("/post/AiSkillController/writeAiSkill")
    public void writeAiSkill(InputObject inputObject, OutputObject outputObject) {
        aiSkillService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAiSkillPageList", value = "分页查询AI技能", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AiSkillController/queryAiSkillPageList")
    public void queryAiSkillPageList(InputObject inputObject, OutputObject outputObject) {
        aiSkillService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "selectAiSkillById", value = "根据id获取AI技能", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AiSkillController/selectAiSkillById")
    public void selectAiSkillById(InputObject inputObject, OutputObject outputObject) {
        aiSkillService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAiSkillById", value = "删除AI技能", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AiSkillController/deleteAiSkillById")
    public void deleteAiSkillById(InputObject inputObject, OutputObject outputObject) {
        aiSkillService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledAiSkillMatchList", value = "办公AI匹配用：启用中的套件与技能", method = "POST", allUse = "2")
    @RequestMapping("/post/AiSkillController/queryEnabledAiSkillMatchList")
    public void queryEnabledAiSkillMatchList(InputObject inputObject, OutputObject outputObject) {
        aiSkillService.queryMatchList(inputObject, outputObject);
    }
}
