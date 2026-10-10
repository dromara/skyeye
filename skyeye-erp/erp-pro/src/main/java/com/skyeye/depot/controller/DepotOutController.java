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
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.service.DepotOutService;
import com.skyeye.pickconfirm.entity.ConfirmPut;
import com.skyeye.pickconfirm.entity.ConfirmReturn;
import com.skyeye.shop.entity.ShopConfirmPut;
import com.skyeye.shop.entity.ShopConfirmReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DepotOutController
 * @Description: 仓库出库单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/26 9:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "仓库出库单", tags = "仓库出库单", modelName = "仓库出入库")
public class DepotOutController {

    @Autowired
    private DepotOutService depotOutService;

    @ApiOperation(id = "queryDepotOutList", value = "获取仓库出库列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DepotOutController/queryDepotOutList")
    public void queryDepotOutList(InputObject inputObject, OutputObject outputObject) {
        depotOutService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDepotOutOrderList", value = "获取仓库出库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
        @ApiImplicitParam(id = "type", name = "type", value = "类型", required = "required", defaultValue = "DepotOut")})
    @RequestMapping("/post/DepotOutController/queryDepotOutOrderList")
    public void queryDepotOutOrderList(InputObject inputObject, OutputObject outputObject) {
        depotOutService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDepotOut", value = "新增/编辑仓库出库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = DepotOut.class)
    @RequestMapping("/post/DepotOutController/writeDepotOut")
    public void writeDepotOut(InputObject inputObject, OutputObject outputObject) {
        depotOutService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNeedConfirmDepotOutList", value = "获取需要物料确认的仓库出库列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DepotOutController/queryNeedConfirmDepotOutList")
    public void queryNeedConfirmDepotOutList(InputObject inputObject, OutputObject outputObject) {
        depotOutService.queryNeedConfirmDepotOutList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDepotOutTransById", value = "转物料接收单/物料退货单时，根据id查询仓库出库单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DepotOutController/queryDepotOutTransById")
    public void queryDepotOutTransById(InputObject inputObject, OutputObject outputObject) {
        depotOutService.queryDepotOutTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertDepotOutToTurnPut", value = "仓库出库单转物料接收单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ConfirmPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DepotOutController/insertDepotOutToTurnPut")
    public void insertDepotOutToTurnPut(InputObject inputObject, OutputObject outputObject) {
        depotOutService.insertDepotOutToTurnPut(inputObject, outputObject);
    }

    @ApiOperation(id = "insertDepotOutToSealsReturns", value = "仓库出库单转物料退货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ConfirmReturn.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DepotOutController/insertDepotOutToSealsReturns")
    public void insertDepotOutToSealsReturns(InputObject inputObject, OutputObject outputObject) {
        depotOutService.insertDepotOutToSealsReturns(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNeedStoreConfirmDepotOutList", value = "获取需要门店物料确认的仓库出库列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DepotOutController/queryNeedStoreConfirmDepotOutList")
    public void queryNeedStoreConfirmDepotOutList(InputObject inputObject, OutputObject outputObject) {
        depotOutService.queryNeedStoreConfirmDepotOutList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDepotOutTransStoreById", value = "转门店物料接收单/门店物料退货单时，根据id查询仓库出库单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DepotOutController/queryDepotOutTransStoreById")
    public void queryDepotOutTransStoreById(InputObject inputObject, OutputObject outputObject) {
        depotOutService.queryDepotOutTransStoreById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertDepotOutToTurnStorePut", value = "仓库出库单转门店物料接收单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ShopConfirmPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DepotOutController/insertDepotOutToTurnStorePut")
    public void insertDepotOutToTurnStorePut(InputObject inputObject, OutputObject outputObject) {
        depotOutService.insertDepotOutToTurnStorePut(inputObject, outputObject);
    }

    @ApiOperation(id = "insertDepotOutToStoreSealsReturns", value = "仓库出库单转门店物料退货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ShopConfirmReturn.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DepotOutController/insertDepotOutToStoreSealsReturns")
    public void insertDepotOutToStoreSealsReturns(InputObject inputObject, OutputObject outputObject) {
        depotOutService.insertDepotOutToStoreSealsReturns(inputObject, outputObject);
    }

}
