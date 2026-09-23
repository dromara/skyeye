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
import com.skyeye.shop.service.ShopStoreDepotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopStoreDepotController
 * @Description: 个人门店仓储库存
 */
@RestController
@Api(value = "个人门店仓储库存", tags = "个人门店仓储库存", modelName = "门店")
public class ShopStoreDepotController {

    @Autowired
    private ShopStoreDepotService shopStoreDepotService;

    @ApiOperation(id = "queryPersonalStoreDepotList", value = "个人门店仓库列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStoreDepotController/queryPersonalStoreDepotList")
    public void queryPersonalStoreDepotList(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.queryPersonalStoreDepotList(inputObject, outputObject);
    }

    @ApiOperation(id = "createPersonalStoreDepot", value = "个人门店新增商家仓", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "depotCode", name = "depotCode", value = "仓编码", required = "required"),
        @ApiImplicitParam(id = "name", name = "name", value = "仓库名称", required = "required"),
        @ApiImplicitParam(id = "aliasName", name = "aliasName", value = "仓别名"),
        @ApiImplicitParam(id = "provinceId", name = "provinceId", value = "省", required = "required"),
        @ApiImplicitParam(id = "cityId", name = "cityId", value = "市"),
        @ApiImplicitParam(id = "areaId", name = "areaId", value = "区县"),
        @ApiImplicitParam(id = "townshipId", name = "townshipId", value = "乡镇"),
        @ApiImplicitParam(id = "absoluteAddress", name = "absoluteAddress", value = "详细地址", required = "required"),
        @ApiImplicitParam(id = "contactName", name = "contactName", value = "联系人", required = "required"),
        @ApiImplicitParam(id = "contactPhone", name = "contactPhone", value = "联系电话")})
    @RequestMapping("/post/ShopStoreDepotController/createPersonalStoreDepot")
    public void createPersonalStoreDepot(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.createPersonalStoreDepot(inputObject, outputObject);
    }

    @ApiOperation(id = "updatePersonalStoreDepot", value = "个人门店编辑商家仓", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "关联id", required = "required"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "name", name = "name", value = "仓库名称"),
        @ApiImplicitParam(id = "aliasName", name = "aliasName", value = "仓别名"),
        @ApiImplicitParam(id = "provinceId", name = "provinceId", value = "省"),
        @ApiImplicitParam(id = "cityId", name = "cityId", value = "市"),
        @ApiImplicitParam(id = "areaId", name = "areaId", value = "区县"),
        @ApiImplicitParam(id = "townshipId", name = "townshipId", value = "乡镇"),
        @ApiImplicitParam(id = "absoluteAddress", name = "absoluteAddress", value = "详细地址"),
        @ApiImplicitParam(id = "contactName", name = "contactName", value = "联系人"),
        @ApiImplicitParam(id = "contactPhone", name = "contactPhone", value = "联系电话"),
        @ApiImplicitParam(id = "enabled", name = "enabled", value = "启用状态")})
    @RequestMapping("/post/ShopStoreDepotController/updatePersonalStoreDepot")
    public void updatePersonalStoreDepot(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.updatePersonalStoreDepot(inputObject, outputObject);
    }

    @ApiOperation(id = "setDefaultPersonalStoreDepot", value = "设置默认商家仓", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "id", name = "id", value = "关联id", required = "required")})
    @RequestMapping("/post/ShopStoreDepotController/setDefaultPersonalStoreDepot")
    public void setDefaultPersonalStoreDepot(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.setDefaultPersonalStoreDepot(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePersonalStoreDepot", value = "删除商家仓", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "id", name = "id", value = "关联id", required = "required")})
    @RequestMapping("/post/ShopStoreDepotController/deletePersonalStoreDepot")
    public void deletePersonalStoreDepot(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.deletePersonalStoreDepot(inputObject, outputObject);
    }

    @ApiOperation(id = "savePersonalStoreDepotPriority", value = "保存仓库优先级", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "idOrder", name = "idOrder", value = "关联id有序数组JSON", required = "required")})
    @RequestMapping("/post/ShopStoreDepotController/savePersonalStoreDepotPriority")
    public void savePersonalStoreDepotPriority(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.savePersonalStoreDepotPriority(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPersonalStoreInventoryList", value = "个人门店库存管理列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
        @ApiImplicitParam(id = "stockMode", name = "stockMode", value = "库存模式"),
        @ApiImplicitParam(id = "isLaunchShop", name = "isLaunchShop", value = "是否上架商城")})
    @RequestMapping("/post/ShopStoreDepotController/queryPersonalStoreInventoryList")
    public void queryPersonalStoreInventoryList(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.queryPersonalStoreInventoryList(inputObject, outputObject);
    }

    @ApiOperation(id = "adjustPersonalStoreInventory", value = "个人门店调整库存", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "id", name = "id", value = "门店商品关系id", required = "required"),
        @ApiImplicitParam(id = "normsId", name = "normsId", value = "规格id", required = "required"),
        @ApiImplicitParam(id = "depotId", name = "depotId", value = "仓库id（关联仓模式必填）"),
        @ApiImplicitParam(id = "adjustType", name = "adjustType", value = "1增加 2减少", required = "required"),
        @ApiImplicitParam(id = "count", name = "count", value = "数量", required = "required")})
    @RequestMapping("/post/ShopStoreDepotController/adjustPersonalStoreInventory")
    public void adjustPersonalStoreInventory(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.adjustPersonalStoreInventory(inputObject, outputObject);
    }

    /**
     * 发货扣减门店库存。
     * <p>由商城订单发货流程调用；按门店商品 stockMode 决定扣 shop_stock 还是按仓优先级扣 ERP 规格库存。</p>
     */
    @ApiOperation(id = "deductShopStockOnShip", value = "发货扣减门店库存", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "materialStoreId", name = "materialStoreId", value = "门店商品关系id（用于识别库存模式）"),
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required"),
        @ApiImplicitParam(id = "normsId", name = "normsId", value = "规格id", required = "required"),
        @ApiImplicitParam(id = "count", name = "count", value = "扣减数量", required = "required")})
    @RequestMapping("/post/ShopStoreDepotController/deductShopStockOnShip")
    public void deductShopStockOnShip(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.deductShopStockOnShip(inputObject, outputObject);
    }

    @ApiOperation(id = "checkShopStockForSale", value = "校验门店商品可售库存", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialStoreId", name = "materialStoreId", value = "门店商品关系id", required = "required"),
        @ApiImplicitParam(id = "normsId", name = "normsId", value = "规格id", required = "required"),
        @ApiImplicitParam(id = "count", name = "count", value = "购买数量", required = "required")})
    @RequestMapping("/post/ShopStoreDepotController/checkShopStockForSale")
    public void checkShopStockForSale(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.checkShopStockForSale(inputObject, outputObject);
    }

    @ApiOperation(id = "switchPersonalStoreStockMode", value = "切换商品库存模式", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "id", name = "id", value = "门店商品关系id", required = "required"),
        @ApiImplicitParam(id = "stockMode", name = "stockMode", value = "库存模式", required = "required")})
    @RequestMapping("/post/ShopStoreDepotController/switchPersonalStoreStockMode")
    public void switchPersonalStoreStockMode(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.switchPersonalStoreStockMode(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPersonalStoreInventoryDiagnosis", value = "个人门店库存诊断", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
        @ApiImplicitParam(id = "diagnoseType", name = "diagnoseType", value = "all/oos/low")})
    @RequestMapping("/post/ShopStoreDepotController/queryPersonalStoreInventoryDiagnosis")
    public void queryPersonalStoreInventoryDiagnosis(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.queryPersonalStoreInventoryDiagnosis(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPersonalStoreMaterialStockEdit", value = "个人门店编辑库存详情", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "id", name = "id", value = "门店商品关系id", required = "required")})
    @RequestMapping("/post/ShopStoreDepotController/queryPersonalStoreMaterialStockEdit")
    public void queryPersonalStoreMaterialStockEdit(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.queryPersonalStoreMaterialStockEdit(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPersonalStoreInventoryHome", value = "个人门店库存首页汇总", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopStoreDepotController/queryPersonalStoreInventoryHome")
    public void queryPersonalStoreInventoryHome(InputObject inputObject, OutputObject outputObject) {
        shopStoreDepotService.queryPersonalStoreInventoryHome(inputObject, outputObject);
    }
}
