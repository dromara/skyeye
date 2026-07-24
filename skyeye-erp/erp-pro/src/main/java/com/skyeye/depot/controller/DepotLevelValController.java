/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.classenum.GenerateDepotLevelValType;
import com.skyeye.depot.entity.DepotLevelVal;
import com.skyeye.depot.service.DepotLevelValService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DepotLevelValController
 * @Description: 仓库级别的值控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/6 10:13
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "仓库级别的值管理", tags = "仓库级别的值管理", modelName = "仓库级别管理")
public class DepotLevelValController {

    @Autowired
    private DepotLevelValService depotLevelValService;

    /**
     * 获取仓库级别的值信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDepotLevelValList", value = "获取仓库级别的值信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DepotLevelValController/queryDepotLevelValList")
    public void queryDepotLevelValList(InputObject inputObject, OutputObject outputObject) {
        depotLevelValService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑仓库级别的值信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDepotLevelVal", value = "新增/编辑仓库级别的值信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotLevelVal.class)
    @RequestMapping("/post/DepotLevelValController/writeDepotLevelVal")
    public void writeDepotLevelVal(InputObject inputObject, OutputObject outputObject) {
        depotLevelValService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id获取仓库级别的值信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDepotLevelValById", value = "根据id获取仓库级别的值信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DepotLevelValController/queryDepotLevelValById")
    public void queryDepotLevelValById(InputObject inputObject, OutputObject outputObject) {
        depotLevelValService.selectById(inputObject, outputObject);
    }

    /**
     * 删除仓库级别的值信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDepotLevelValById", value = "删除仓库级别的值信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DepotLevelValController/deleteDepotLevelValById")
    public void deleteDepotLevelValById(InputObject inputObject, OutputObject outputObject) {
        depotLevelValService.deleteById(inputObject, outputObject);
    }

    /**
     * 批量生成仓库级别的值信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "batchGenerateDepotLevelVal", value = "批量生成仓库级别的值信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "type", name = "type", value = "生成的编号类型", enumClass = GenerateDepotLevelValType.class, required = "required,num"),
        @ApiImplicitParam(id = "number", name = "number", value = "生成的数量", required = "required,num"),
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "父节点id", required = "required"),
        @ApiImplicitParam(id = "depotId", name = "depotId", value = "仓库id", required = "required")})
    @RequestMapping("/post/DepotLevelValController/batchGenerateDepotLevelVal")
    public void batchGenerateDepotLevelVal(InputObject inputObject, OutputObject outputObject) {
        depotLevelValService.batchGenerateDepotLevelVal(inputObject, outputObject);
    }

}
