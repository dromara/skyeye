/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.shop.entity.StoreProductTransferExecute;
import com.skyeye.shop.service.ShopStockService;
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

    @ApiOperation(id = "queryShopStockList", value = "获取门店物料库存信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStockController/queryShopStockList")
    public void queryShopStockList(InputObject inputObject, OutputObject outputObject) {
        shopStockService.queryShopStockList(inputObject, outputObject);
    }

    @ApiOperation(id = "executeStoreProductTransfer", value = "执行门店产品库存调拨", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = StoreProductTransferExecute.class)
    @RequestMapping("/post/ShopStockController/executeStoreProductTransfer")
    public void executeStoreProductTransfer(InputObject inputObject, OutputObject outputObject) {
        shopStockService.executeStoreProductTransfer(inputObject, outputObject);
    }

}
