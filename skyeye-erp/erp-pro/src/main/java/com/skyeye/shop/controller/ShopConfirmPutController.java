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
import com.skyeye.shop.entity.ShopConfirmPut;
import com.skyeye.shop.service.ShopConfirmPutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopConfirmPutController
 * @Description: 物料接收单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/27 10:05
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "物料接收单", tags = "物料接收单", modelName = "门店")
public class ShopConfirmPutController {

    @Autowired
    private ShopConfirmPutService shopConfirmPutService;

    @ApiOperation(id = "queryShopConfirmPutList", value = "获取物料接收单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "门店id")})
    @RequestMapping("/post/ShopConfirmPutController/queryShopConfirmPutList")
    public void queryShopConfirmPutList(InputObject inputObject, OutputObject outputObject) {
        shopConfirmPutService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeShopConfirmPut", value = "新增/编辑物料接收单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ShopConfirmPut.class)
    @RequestMapping("/post/ShopConfirmPutController/writeShopConfirmPut")
    public void writeShopConfirmPut(InputObject inputObject, OutputObject outputObject) {
        shopConfirmPutService.saveOrUpdateEntity(inputObject, outputObject);
    }

}
