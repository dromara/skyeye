/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.impexp.entity.ImportExportConfig;
import com.skyeye.impexp.enums.ImportExportConfigTypeEnum;
import com.skyeye.impexp.service.ImportExportConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ImportExportConfigController
 * @Description: 业务对象导入导出配置控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/4/8 22:15
 */
@RestController
@Api(value = "业务对象导入导出配置", tags = "业务对象导入导出配置", modelName = "系统公共模块")
public class ImportExportConfigController {

    @Autowired
    private ImportExportConfigService importExportConfigService;

    @ApiOperation(id = "queryImportExportFieldOptions", value = "根据业务对象获取可配置导入导出字段项", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "appId", name = "appId", value = "应用的appId", required = "required"),
        @ApiImplicitParam(id = "className", name = "className", value = "业务对象className", required = "required")})
    @RequestMapping("/post/ImportExportConfigController/queryImportExportFieldOptions")
    public void queryImportExportFieldOptions(InputObject inputObject, OutputObject outputObject) {
        importExportConfigService.queryImportExportFieldOptions(inputObject, outputObject);
    }

    @ApiOperation(id = "queryImportExportConfigList", value = "根据业务对象获取导入导出配置列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "appId", name = "appId", value = "应用的appId", required = "required"),
        @ApiImplicitParam(id = "className", name = "className", value = "业务对象className", required = "required"),
        @ApiImplicitParam(id = "configType", name = "configType", value = "配置类型", enumClass = ImportExportConfigTypeEnum.class, required = "required")})
    @RequestMapping("/post/ImportExportConfigController/queryImportExportConfigList")
    public void queryImportExportConfigList(InputObject inputObject, OutputObject outputObject) {
        importExportConfigService.queryImportExportConfigList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryImportExportConfigPageList", value = "根据业务对象获取导入导出配置", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ImportExportConfigController/queryImportExportConfigPageList")
    public void queryImportExportConfigPageList(InputObject inputObject, OutputObject outputObject) {
        importExportConfigService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeImportExportConfig", value = "新增/编辑业务对象导入导出配置", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ImportExportConfig.class)
    @RequestMapping("/post/ImportExportConfigController/writeImportExportConfig")
    public void writeImportExportConfig(InputObject inputObject, OutputObject outputObject) {
        importExportConfigService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryImportExportConfigById", value = "根据ID查询导入导出配置详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ImportExportConfigController/queryImportExportConfigById")
    public void queryImportExportConfigById(InputObject inputObject, OutputObject outputObject) {
        importExportConfigService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "downloadImportTemplate", value = "按配置下载导入Excel模板", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "appId", name = "appId", value = "应用的appId", required = "required"),
        @ApiImplicitParam(id = "className", name = "className", value = "业务对象className", required = "required"),
        @ApiImplicitParam(id = "id", name = "id", value = "配置id", required = "required")})
    @RequestMapping("/post/ImportExportConfigController/downloadImportTemplate")
    public void downloadImportTemplate(InputObject inputObject, OutputObject outputObject) {
        importExportConfigService.downloadImportTemplate(inputObject, outputObject);
    }

    @ApiOperation(id = "exportByConfig", value = "按配置导出数据（不接收rows，后端按filters查询）", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "appId", name = "appId", value = "应用的appId", required = "required"),
        @ApiImplicitParam(id = "className", name = "className", value = "业务对象className", required = "required"),
        @ApiImplicitParam(id = "id", name = "id", value = "配置id", required = "required"),
        @ApiImplicitParam(id = "filters", name = "filters", value = "筛选条件JSON字符串，可选；可与顶层 page/limit 二选一，顶层优先", required = "json"),
        @ApiImplicitParam(id = "limit", name = "limit", value = "导出条数：-1 或省略表示全部；正整数表示最多导出条数（本页）", required = "required,num")})
    @RequestMapping("/post/ImportExportConfigController/exportByConfig")
    public void exportByConfig(InputObject inputObject, OutputObject outputObject) {
        importExportConfigService.exportByConfig(inputObject, outputObject);
    }

    @ApiOperation(id = "importByConfig", value = "按导入配置上传Excel并落库", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "appId", name = "appId", value = "应用的appId", required = "required"),
        @ApiImplicitParam(id = "className", name = "className", value = "业务对象className", required = "required"),
        @ApiImplicitParam(id = "id", name = "id", value = "配置id", required = "required")})
    @RequestMapping("/post/ImportExportConfigController/importByConfig")
    public void importByConfig(InputObject inputObject, OutputObject outputObject) {
        importExportConfigService.importByConfig(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteImportExportConfigById", value = "根据id删除导入导出配置", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ImportExportConfigController/deleteImportExportConfigById")
    public void deleteImportExportConfigById(InputObject inputObject, OutputObject outputObject) {
        importExportConfigService.deleteById(inputObject, outputObject);
    }
}

