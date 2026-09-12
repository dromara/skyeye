/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialChooseQueryDo;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MaterialController
 * @Description: 商品管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/23 16:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "商品管理", tags = "商品管理", modelName = "商品管理")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @ApiOperation(id = "material001", value = "获取商品信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MaterialController/queryMaterialList")
    public void queryMaterialList(InputObject inputObject, OutputObject outputObject) {
        materialService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeMaterialMation", value = "新增/编辑商品信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Material.class)
    @RequestMapping("/post/MaterialController/writeMaterialMation")
    public void writeMaterialMation(InputObject inputObject, OutputObject outputObject) {
        materialService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "material006", value = "删除商品信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MaterialController/deleteMaterialById")
    public void deleteMaterialById(InputObject inputObject, OutputObject outputObject) {
        materialService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "material010", value = "获取商品列表信息展示为表格方便选择", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = MaterialChooseQueryDo.class)
    @RequestMapping("/post/MaterialController/queryMaterialListToTable")
    public void queryMaterialListToTable(InputObject inputObject, OutputObject outputObject) {
        materialService.queryMaterialListToTable(inputObject, outputObject);
    }

    @ApiOperation(id = "material011", value = "根据商品规格id以及仓库id获取库存", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "normsIds", name = "normsIds", value = "规格id，多个用逗号隔开", required = "required"),
        @ApiImplicitParam(id = "depotId", name = "depotId", value = "仓库id")})
    @RequestMapping("/post/MaterialController/queryMaterialTockByNormsId")
    public void queryMaterialTockByNormsId(InputObject inputObject, OutputObject outputObject) {
        materialService.queryMaterialTockByNormsId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaterialListById", value = "根据id获取商品信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MaterialController/queryMaterialListById")
    public void queryMaterialListById(InputObject inputObject, OutputObject outputObject) {
        materialService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaterialListByIds", value = "根据id批量获取商品信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/MaterialController/queryMaterialListByIds")
    public void queryMaterialListByIds(InputObject inputObject, OutputObject outputObject) {
        materialService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaterialNormsListByIds", value = "根据规格id批量获取产品规格信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/MaterialController/queryMaterialNormsListByIds")
    public void queryMaterialNormsListById(InputObject inputObject, OutputObject outputObject) {
        materialNormsService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "material016", value = "获取商品库存信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = MaterialChooseQueryDo.class)
    @RequestMapping("/post/MaterialController/queryMaterialReserveList")
    public void queryMaterialReserveList(InputObject inputObject, OutputObject outputObject) {
        materialService.queryMaterialReserveList(inputObject, outputObject);
    }

    @ApiOperation(id = "material017", value = "获取预警商品库存信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MaterialController/queryMaterialInventoryWarningList")
    public void queryMaterialInventoryWarningList(InputObject inputObject, OutputObject outputObject) {
        materialService.queryMaterialInventoryWarningList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllMaterialList", value = "获取所有商品列表", method = "GET", allUse = "2")
    @RequestMapping("/post/MaterialController/queryAllMaterialList")
    public void queryAllMaterialList(InputObject inputObject, OutputObject outputObject) {
        materialService.queryAllMaterialList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNormsListByMaterialId", value = "根据产品id获取规格信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "产品id")})
    @RequestMapping("/post/MaterialController/queryNormsListByMaterialId")
    public void queryNormsListByMaterialId(InputObject inputObject, OutputObject outputObject) {
        materialNormsService.queryNormsListByMaterialId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllNormsList", value = "获取所有规格列表", method = "GET", allUse = "2")
    @RequestMapping("/post/MaterialController/queryAllNormsList")
    public void queryAllNormsList(InputObject inputObject, OutputObject outputObject) {
        materialNormsService.queryAllNormsList(inputObject, outputObject);
    }

}
