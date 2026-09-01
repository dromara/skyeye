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
import com.skyeye.skill.entity.AiSkillCategory;
import com.skyeye.skill.service.AiSkillCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "AI技能分类", tags = "AI技能分类", modelName = "AI技能")
public class AiSkillCategoryController {

    @Autowired
    private AiSkillCategoryService aiSkillCategoryService;

    @ApiOperation(id = "writeAiSkillCategory", value = "新增/编辑AI技能分类", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AiSkillCategory.class)
    @RequestMapping("/post/AiSkillCategoryController/writeAiSkillCategory")
    public void writeAiSkillCategory(InputObject inputObject, OutputObject outputObject) {
        aiSkillCategoryService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAiSkillCategoryPageList", value = "分页查询AI技能分类", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AiSkillCategoryController/queryAiSkillCategoryPageList")
    public void queryAiSkillCategoryPageList(InputObject inputObject, OutputObject outputObject) {
        aiSkillCategoryService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAiSkillCategoryList", value = "获取全部AI技能分类", method = "POST", allUse = "2")
    @RequestMapping("/post/AiSkillCategoryController/queryAiSkillCategoryList")
    public void queryAiSkillCategoryList(InputObject inputObject, OutputObject outputObject) {
        aiSkillCategoryService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAiSkillCategoryById", value = "删除AI技能分类", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AiSkillCategoryController/deleteAiSkillCategoryById")
    public void deleteAiSkillCategoryById(InputObject inputObject, OutputObject outputObject) {
        aiSkillCategoryService.deleteById(inputObject, outputObject);
    }
}
