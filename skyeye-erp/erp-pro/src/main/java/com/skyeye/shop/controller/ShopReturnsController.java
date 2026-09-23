/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.shop.entity.ShopReturns;
import com.skyeye.shop.service.ShopReturnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopReturnsController
 * @Description: 门店退货单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "门店退货单", tags = "门店退货单", modelName = "门店")
public class ShopReturnsController {

    @Autowired
    private ShopReturnsService shopReturnsService;

    @ApiOperation(id = "queryShopReturnsList", value = "获取门店退货单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopReturnsController/queryShopReturnsList")
    public void queryShopReturnsList(InputObject inputObject, OutputObject outputObject) {
        shopReturnsService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeShopReturns", value = "新增/编辑门店退货单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ShopReturns.class)
    @RequestMapping("/post/ShopReturnsController/writeShopReturns")
    public void writeShopReturns(InputObject inputObject, OutputObject outputObject) {
        shopReturnsService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopReturnsTransById", value = "转仓库入库单时，根据id查询门店退货单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopReturnsController/queryShopReturnsTransById")
    public void queryShopReturnsTransById(InputObject inputObject, OutputObject outputObject) {
        shopReturnsService.queryShopReturnsTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertShopReturnsToTurnDepot", value = "门店退货单信息转仓库入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopReturnsController/insertShopReturnsToTurnDepot")
    public void insertShopReturnsToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        shopReturnsService.insertShopReturnsToTurnDepot(inputObject, outputObject);
    }

}
