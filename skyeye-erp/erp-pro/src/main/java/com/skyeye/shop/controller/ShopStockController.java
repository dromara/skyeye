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
import com.skyeye.shop.entity.StoreInventoryCheckConfirm;
import com.skyeye.shop.entity.StoreProductTransferExecute;
import com.skyeye.shop.service.ShopStockService;
import com.skyeye.shop.service.ShopStoreInventoryCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopStockController
 * @Description: 门店物料库存信息管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/20 10:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "门店物料库存信息", tags = "门店物料库存信息", modelName = "门店")
public class ShopStockController {

    @Autowired
    private ShopStockService shopStockService;

    @Autowired
    private ShopStoreInventoryCheckService shopStoreInventoryCheckService;

    @ApiOperation(id = "queryShopStockList", value = "获取门店物料库存信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStockController/queryShopStockList")
    public void queryShopStockList(InputObject inputObject, OutputObject outputObject) {
        shopStockService.queryShopStockList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreInventoryCheckList", value = "门店库存盘点列表（含零库存商品）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStockController/queryStoreInventoryCheckList")
    public void queryStoreInventoryCheckList(InputObject inputObject, OutputObject outputObject) {
        shopStockService.queryStoreInventoryCheckList(inputObject, outputObject);
    }

    @ApiOperation(id = "executeStoreProductTransfer", value = "执行门店产品库存调拨", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = StoreProductTransferExecute.class)
    @RequestMapping("/post/ShopStockController/executeStoreProductTransfer")
    public void executeStoreProductTransfer(InputObject inputObject, OutputObject outputObject) {
        shopStockService.executeStoreProductTransfer(inputObject, outputObject);
    }

    @ApiOperation(id = "confirmStoreInventoryCheck", value = "门店库存盘点确认", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = StoreInventoryCheckConfirm.class)
    @RequestMapping("/post/ShopStockController/confirmStoreInventoryCheck")
    public void confirmStoreInventoryCheck(InputObject inputObject, OutputObject outputObject) {
        shopStockService.confirmStoreInventoryCheck(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreInventoryCheckHistoryList", value = "门店库存盘点历史列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStockController/queryStoreInventoryCheckHistoryList")
    public void queryStoreInventoryCheckHistoryList(InputObject inputObject, OutputObject outputObject) {
        shopStoreInventoryCheckService.queryStoreInventoryCheckHistoryList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreInventoryCheckHistoryDetail", value = "门店库存盘点历史详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "盘点记录id", required = "required")
    })
    @RequestMapping("/post/ShopStockController/queryStoreInventoryCheckHistoryDetail")
    public void queryStoreInventoryCheckHistoryDetail(InputObject inputObject, OutputObject outputObject) {
        shopStoreInventoryCheckService.queryStoreInventoryCheckHistoryDetail(inputObject, outputObject);
    }

}
