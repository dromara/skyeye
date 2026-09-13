/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bom.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.bom.entity.Bom;
import com.skyeye.bom.service.BomService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: BomController
 * @Description: bom清单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/27 14:51
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "bom清单管理", tags = "bom清单管理", modelName = "bom清单管理")
public class BomController {

    @Autowired
    private BomService bomService;

    @ApiOperation(id = "erpbom001", value = "查询bom表列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/BomController/queryBomList")
    public void queryBomList(InputObject inputObject, OutputObject outputObject) {
        bomService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBomHistoryList", value = "根据versionNo查询bom得历史列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/BomController/queryBomHistoryList")
    public void queryBomHistoryList(InputObject inputObject, OutputObject outputObject) {
        bomService.queryBomHistoryList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeBom", value = "新增/编辑bom表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Bom.class)
    @RequestMapping("/post/BomController/writeBom")
    public void writeBom(InputObject inputObject, OutputObject outputObject) {
        bomService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBomById", value = "根据id查询bom表详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "方案id", required = "required")})
    @RequestMapping("/post/BomController/queryBomById")
    public void queryBomById(InputObject inputObject, OutputObject outputObject) {
        bomService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBomByIds", value = "根据ids批量查询bom表详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "方案id集合", required = "required")})
    @RequestMapping("/post/BomController/queryBomByIds")
    public void queryBomByIds(InputObject inputObject, OutputObject outputObject) {
        bomService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteBomById", value = "根据ID删除bom表信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/BomController/deleteBomById")
    public void deleteBomById(InputObject inputObject, OutputObject outputObject) {
        bomService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBomListByNormsId", value = "根据规格id获取方案列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "normsId", name = "normsId", value = "规格id")})
    @RequestMapping("/post/BomController/queryBomListByNormsId")
    public void queryBomListByNormsId(InputObject inputObject, OutputObject outputObject) {
        bomService.queryBomListByNormsId(inputObject, outputObject);
    }

    @ApiOperation(id = "material015", value = "根据商品信息以及bom方案信息获取商品树---用于生产模块", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "proList", name = "proList", value = "商品信息列表json串，需要包含materialId,normsId,bomId", required = "required,json")})
    @RequestMapping("/post/MaterialController/queryMaterialBomChildsToProduceByJson")
    public void queryMaterialBomChildsToProduceByJson(InputObject inputObject, OutputObject outputObject) {
        bomService.queryMaterialBomChildsToProduceByJson(inputObject, outputObject);
    }

    @ApiOperation(id = "publishBomVersionById", value = "根据id发布bom", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "方案id", required = "required")})
    @RequestMapping("/post/BomController/publishBomVersionById")
    public void publishBomVersionById(InputObject inputObject, OutputObject outputObject) {
        bomService.publishVersionById(inputObject, outputObject);
    }

    @ApiOperation(id = "editBomCadContentById", value = "编辑BOM二维CAD图纸", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "方案id", required = "required"),
        @ApiImplicitParam(id = "cadContent", name = "cadContent", value = "二维CAD图纸JSON", required = "required,json")})
    @RequestMapping("/post/BomController/editBomCadContentById")
    public void editBomCadContentById(InputObject inputObject, OutputObject outputObject) {
        bomService.editBomCadContentById(inputObject, outputObject);
    }

}
