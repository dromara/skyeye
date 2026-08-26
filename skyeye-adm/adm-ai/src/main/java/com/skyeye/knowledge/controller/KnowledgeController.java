/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.service.KnowledgeService;
import com.skyeye.knowledge.service.KnowledgeSyncHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "AI知识库", tags = "AI知识库", modelName = "AI知识库")
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    @Autowired
    private KnowledgeSyncHistoryService knowledgeSyncHistoryService;

    @ApiOperation(id = "writeAiKnowledge", value = "新增/编辑AI知识库", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Knowledge.class)
    @RequestMapping("/post/knowledgeController/writeAiKnowledge")
    public void writeAiKnowledge(InputObject inputObject, OutputObject outputObject) {
        knowledgeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAiKnowledge", value = "分页查询AI知识库", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/knowledgeController/queryAiKnowledge")
    public void queryAiKnowledge(InputObject inputObject, OutputObject outputObject) {
        knowledgeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "selectAiKnowledgeById", value = "根据id获取AI知识库", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/knowledgeController/selectAiKnowledgeById")
    public void selectAiKnowledgeById(InputObject inputObject, OutputObject outputObject) {
        knowledgeService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAiKnowledgeById", value = "删除AI知识库", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/knowledgeController/deleteAiKnowledgeById")
    public void deleteAiKnowledgeById(InputObject inputObject, OutputObject outputObject) {
        knowledgeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAiKnowledgeList", value = "获取全部AI知识库", method = "POST", allUse = "2")
    @RequestMapping("/post/knowledgeController/queryAiKnowledgeList")
    public void queryAiKnowledgeList(InputObject inputObject, OutputObject outputObject) {
        knowledgeService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAiKnowledgeSyncList", value = "保存AI知识库同步表配置", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "知识库id", required = "required"),
        @ApiImplicitParam(id = "syncList", name = "syncList", value = "同步表配置JSON数组", required = "required")})
    @RequestMapping("/post/knowledgeController/writeAiKnowledgeSyncList")
    public void writeAiKnowledgeSyncList(InputObject inputObject, OutputObject outputObject) {
        knowledgeService.writeSyncList(inputObject, outputObject);
    }

    @ApiOperation(id = "testKnowledgeDbConnection", value = "测试知识库同步数据库连接", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "知识库id，编辑时可不传密码"),
        @ApiImplicitParam(id = "jdbcUrl", name = "jdbcUrl", value = "JDBC地址"),
        @ApiImplicitParam(id = "jdbcUser", name = "jdbcUser", value = "用户名"),
        @ApiImplicitParam(id = "jdbcPassword", name = "jdbcPassword", value = "密码"),
        @ApiImplicitParam(id = "driverClass", name = "driverClass", value = "驱动类")})
    @RequestMapping("/post/knowledgeController/testKnowledgeDbConnection")
    public void testKnowledgeDbConnection(InputObject inputObject, OutputObject outputObject) {
        knowledgeService.testDbConnection(inputObject, outputObject);
    }

    @ApiOperation(id = "queryKnowledgeDbTables", value = "读取同步库的表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "知识库id，编辑时可不传密码"),
        @ApiImplicitParam(id = "jdbcUrl", name = "jdbcUrl", value = "JDBC地址"),
        @ApiImplicitParam(id = "jdbcUser", name = "jdbcUser", value = "用户名"),
        @ApiImplicitParam(id = "jdbcPassword", name = "jdbcPassword", value = "密码"),
        @ApiImplicitParam(id = "driverClass", name = "driverClass", value = "驱动类")})
    @RequestMapping("/post/knowledgeController/queryKnowledgeDbTables")
    public void queryKnowledgeDbTables(InputObject inputObject, OutputObject outputObject) {
        knowledgeService.queryDbTables(inputObject, outputObject);
    }

    @ApiOperation(id = "queryKnowledgeTableColumns", value = "读取同步表的字段", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "知识库id，编辑时可不传密码"),
        @ApiImplicitParam(id = "jdbcUrl", name = "jdbcUrl", value = "JDBC地址"),
        @ApiImplicitParam(id = "jdbcUser", name = "jdbcUser", value = "用户名"),
        @ApiImplicitParam(id = "jdbcPassword", name = "jdbcPassword", value = "密码"),
        @ApiImplicitParam(id = "driverClass", name = "driverClass", value = "驱动类"),
        @ApiImplicitParam(id = "tableName", name = "tableName", value = "表名", required = "required")})
    @RequestMapping("/post/knowledgeController/queryKnowledgeTableColumns")
    public void queryKnowledgeTableColumns(InputObject inputObject, OutputObject outputObject) {
        knowledgeService.queryTableColumns(inputObject, outputObject);
    }

    @ApiOperation(id = "syncAiKnowledgeNow", value = "立即同步AI知识库", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "知识库id", required = "required")})
    @RequestMapping("/post/knowledgeController/syncAiKnowledgeNow")
    public void syncAiKnowledgeNow(InputObject inputObject, OutputObject outputObject) {
        knowledgeService.syncNow(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAiKnowledgeSyncHistory", value = "分页查询知识库同步历史", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/knowledgeController/queryAiKnowledgeSyncHistory")
    public void queryAiKnowledgeSyncHistory(InputObject inputObject, OutputObject outputObject) {
        knowledgeSyncHistoryService.queryPageList(inputObject, outputObject);
    }

}
