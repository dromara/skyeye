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
import com.skyeye.shop.entity.ShopConfirmReturn;
import com.skyeye.shop.service.ShopConfirmReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopConfirmReturnController
 * @Description: 物料退货单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/27 10:19
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "物料退货单", tags = "物料退货单", modelName = "门店")
public class ShopConfirmReturnController {

    @Autowired
    private ShopConfirmReturnService shopConfirmReturnService;

    @ApiOperation(id = "queryShopConfirmReturnList", value = "获取物料退货单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "门店id")})
    @RequestMapping("/post/ShopConfirmReturnController/queryShopConfirmReturnList")
    public void queryShopConfirmReturnList(InputObject inputObject, OutputObject outputObject) {
        shopConfirmReturnService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeShopConfirmReturn", value = "新增/编辑物料退货单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ShopConfirmReturn.class)
    @RequestMapping("/post/ShopConfirmReturnController/writeShopConfirmReturn")
    public void writeShopConfirmReturn(InputObject inputObject, OutputObject outputObject) {
        shopConfirmReturnService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopConfirmReturnTransById", value = "转仓库入库单时，根据id查询物料退货信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopConfirmReturnController/queryShopConfirmReturnTransById")
    public void queryShopConfirmReturnTransById(InputObject inputObject, OutputObject outputObject) {
        shopConfirmReturnService.queryShopConfirmReturnTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertShopConfirmReturnToTurnDepot", value = "物料退货单信息转仓库入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopConfirmReturnController/insertShopConfirmReturnToTurnDepot")
    public void insertShopConfirmReturnToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        shopConfirmReturnService.insertShopConfirmReturnToTurnDepot(inputObject, outputObject);
    }

}
