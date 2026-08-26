/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.upload.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.upload.entity.FileConfig;
import com.skyeye.upload.service.FileConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FileConfigController
 * @Description: 文件配置控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 17:20
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "文件配置", tags = "文件配置", modelName = "文件配置")
public class FileConfigController {

    @Autowired
    private FileConfigService fileConfigService;

    @ApiOperation(id = "queryFileConfigList", value = "获取文件配置列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/FileConfigController/queryFileConfigList")
    public void queryFileConfigList(InputObject inputObject, OutputObject outputObject) {
        fileConfigService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFileConfigSelectList", value = "获取文件配置下拉列表（不分页）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/FileConfigController/queryFileConfigSelectList")
    public void queryFileConfigSelectList(InputObject inputObject, OutputObject outputObject) {
        fileConfigService.queryFileConfigSelectList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeFileConfig", value = "新增/编辑文件配置", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = FileConfig.class)
    @RequestMapping("/post/FileConfigController/writeFileConfig")
    public void writeFileConfig(InputObject inputObject, OutputObject outputObject) {
        fileConfigService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFileConfigById", value = "根据id查询文件配置", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FileConfigController/queryFileConfigById")
    public void queryFileConfigById(InputObject inputObject, OutputObject outputObject) {
        fileConfigService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteFileConfigById", value = "删除文件配置", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FileConfigController/deleteFileConfigById")
    public void deleteFileConfigById(InputObject inputObject, OutputObject outputObject) {
        fileConfigService.deleteById(inputObject, outputObject);
    }

}
