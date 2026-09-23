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
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.shop.entity.ShopOutLets;
import com.skyeye.shop.service.ShopOutLetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopOutLetsController
 * @Description: 门店申领单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:07
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "门店申领单", tags = "门店申领单", modelName = "门店")
public class ShopOutLetsController {

    @Autowired
    private ShopOutLetsService shopOutLetsService;

    @ApiOperation(id = "queryShopOutLetsList", value = "获取门店申领单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopOutLetsController/queryShopOutLetsList")
    public void queryShopOutLetsList(InputObject inputObject, OutputObject outputObject) {
        shopOutLetsService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeShopOutLets", value = "新增/编辑门店申领单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ShopOutLets.class)
    @RequestMapping("/post/ShopOutLetsController/writeShopOutLets")
    public void writeShopOutLets(InputObject inputObject, OutputObject outputObject) {
        shopOutLetsService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopOutLetsTransById", value = "转仓库出库单时，根据id查询门店申领单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopOutLetsController/queryShopOutLetsTransById")
    public void queryShopOutLetsTransById(InputObject inputObject, OutputObject outputObject) {
        shopOutLetsService.queryShopOutLetsTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertShopOutLetsToTurnDepot", value = "门店申领单信息转仓库出库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotOut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopOutLetsController/insertShopOutLetsToTurnDepot")
    public void insertShopOutLetsToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        shopOutLetsService.insertShopOutLetsToTurnDepot(inputObject, outputObject);
    }

}
