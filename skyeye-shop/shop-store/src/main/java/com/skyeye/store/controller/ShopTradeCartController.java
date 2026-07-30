/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.store.entity.ShopTradeCart;
import com.skyeye.store.service.ShopTradeCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopAreaController
 * @Description: 购物车管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "购物车管理", tags = "购物车管理", modelName = "购物车管理")
public class ShopTradeCartController {

    @Autowired
    private ShopTradeCartService shopTradeCartService;

    @ApiOperation(id = "queryShopTradeCartList", value = "根据状态获取购物车信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "selected", name = "selected", value = "选中状态", enumClass = WhetherEnum.class)})
    @RequestMapping("/post/ShopTradeCartController/queryShopTradeCartList")
    public void queryShopTradeCartList(InputObject inputObject, OutputObject outputObject) {
        shopTradeCartService.queryShopTradeCartList(inputObject, outputObject);
    }

    @ApiOperation(id = "insertShopTradeCart", value = "新增购物车信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ShopTradeCart.class)
    @RequestMapping("/post/ShopTradeCartController/insertShopTradeCart")
    public void insertShopTradeCart(InputObject inputObject, OutputObject outputObject) {
        shopTradeCartService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteShopTradCartByIds", value = "批量删除购物车信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个id用逗号隔开", required = "required")})
    @RequestMapping("/post/ShopTradeCartController/deleteShopTradCartByIds")
    public void deleteShopTradCartByIds(InputObject inputObject, OutputObject outputObject) {
        shopTradeCartService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "changeCount", value = "更新购物车商品数量", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "sign", name = "sign", value = "修改标志0为减少，1为增加", required = "required")})
    @RequestMapping("/post/ShopTradeCartController/changeCount")
    public void changeCount(InputObject inputObject, OutputObject outputObject) {
        shopTradeCartService.changeCount(inputObject, outputObject);
    }

    @ApiOperation(id = "changeSelected", value = "更新购物车商品选中", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopTradeCartController/changeSelected")
    public void changeSelected(InputObject inputObject, OutputObject outputObject) {
        shopTradeCartService.changeSelected(inputObject, outputObject);
    }

    @ApiOperation(id = "batchChangeSelectedStatus", value = "批量更新购物车商品选中/不选中", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id，多个id用逗号隔开", required = "required"),
        @ApiImplicitParam(id = "selected", name = "selected", value = "状态", enumClass = WhetherEnum.class, required = "required,num")})
    @RequestMapping("/post/ShopTradeCartController/batchChangeSelectedStatus")
    public void batchChangeSelectedStatus(InputObject inputObject, OutputObject outputObject) {
        shopTradeCartService.batchChangeSelectedStatus(inputObject, outputObject);
    }

    @ApiOperation(id = "resetShopTradeCart", value = "重置购物车信息", method = "POST", allUse = "2")
    @RequestMapping("/post/ShopTradeCartController/resetShopTradeCart")
    public void resetShopTradeCart(InputObject inputObject, OutputObject outputObject) {
        shopTradeCartService.resetShopTradeCart(inputObject, outputObject);
    }

    @ApiOperation(id = "calculateTotalPrices", value = "计算购物车总价", method = "POST", allUse = "2")
    @RequestMapping("/post/ShopTradeCartController/calculateTotalPrices")
    public void calculateTotalPrices(InputObject inputObject, OutputObject outputObject) {
        shopTradeCartService.calculateTotalPrices(inputObject, outputObject);
    }
}