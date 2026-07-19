/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.accessory.controller;

import com.skyeye.accessory.service.ServiceUserStockService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ServiceUserStockController
 * @Description: 用户配件申领单审核通过后的库存信息控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/26 22:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "我的配件", tags = "我的配件", modelName = "售后服务模块")
public class ServiceUserStockController {

    @Autowired
    private ServiceUserStockService serviceUserStockService;

    @ApiOperation(id = "sealseservice031", value = "获取我申领的配件库存信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ServiceUserStockController/queryServiceUserStockList")
    public void queryServiceUserStockList(InputObject inputObject, OutputObject outputObject) {
        serviceUserStockService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyPartsNumByNormsId", value = "根据规格id获取我的库存", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "normsId", name = "normsId", value = "规格id", required = "required")})
    @RequestMapping("/post/ServiceUserStockController/queryMyPartsNumByNormsId")
    public void queryMyPartsNumByNormsId(InputObject inputObject, OutputObject outputObject) {
        serviceUserStockService.queryMyPartsNumByNormsId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryUserStockByNormsIds", value = "批量查询当前登录人规格库存（服务间调用）", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "normsIds", name = "normsIds", value = "规格id集合", required = "required")})
    @RequestMapping("/post/ServiceUserStockController/queryUserStockByNormsIds")
    public void queryUserStockByNormsIds(InputObject inputObject, OutputObject outputObject) {
        serviceUserStockService.queryUserStockByNormsIds(inputObject, outputObject);
    }

    @ApiOperation(id = "editMaterialNormsUserStock", value = "增减我的配件库存（服务间调用）", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "userId", name = "userId", value = "库存所属用户id", required = "required"),
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required"),
        @ApiImplicitParam(id = "normsId", name = "normsId", value = "规格id", required = "required"),
        @ApiImplicitParam(id = "operNumber", name = "operNumber", value = "数量", required = "required"),
        @ApiImplicitParam(id = "type", name = "type", value = "类型", required = "required,num")})
    @RequestMapping("/post/ServiceUserStockController/editMaterialNormsUserStock")
    public void editMaterialNormsUserStock(InputObject inputObject, OutputObject outputObject) {
        serviceUserStockService.editMaterialNormsUserStockForFeign(inputObject, outputObject);
    }

}
