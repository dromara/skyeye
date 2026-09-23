/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.shopmaterial.service.ShopMaterialStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopMaterialStoreController
 * @Description: 商城商品上线的门店控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/18 14:32
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "商城商品上线的门店", tags = "商城商品上线的门店", modelName = "商城商品上线的门店")
public class ShopMaterialStoreController {

    @Autowired
    private ShopMaterialStoreService shopMaterialStoreService;

    @ApiOperation(id = "saveShopMaterialStore", value = "新增门店时，将所有商品同步到该门店", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/saveShopMaterialStore")
    public void saveShopMaterialStore(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.saveShopMaterialStore(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialById", value = "根据id获取商城商品信息", method = "GET", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/queryShopMaterialById")
    public void queryShopMaterialById(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryShopMaterialById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialByIds", value = "根据id批量获取商城商品信息", method = "POST", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id，多个逗号隔开", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/queryShopMaterialByIds")
    public void queryShopMaterialByIds(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryShopMaterialByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialPreviewByStoreIds", value = "按门店批量预览已上架商城商品", method = "POST", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeIds", name = "storeIds", value = "门店id，多个逗号隔开", required = "required"),
        @ApiImplicitParam(id = "limit", name = "limit", value = "每个门店返回条数，默认5")})
    @RequestMapping("/post/ShopMaterialStoreController/queryShopMaterialPreviewByStoreIds")
    public void queryShopMaterialPreviewByStoreIds(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryShopMaterialPreviewByStoreIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialByMaterialIdAndStoreId", value = "根据商品id和门店id获取商城商品信息", method = "GET", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/queryShopMaterialByMaterialIdAndStoreId")
    public void queryShopMaterialByMaterialIdAndStoreId(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryShopMaterialByMaterialIdAndStoreId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialMapByMaterialIdAndStoreId", value = "根据商品id和门店id批量获取商城商品信息", method = "POST", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id，json数组格式", required = "required,json"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id，json数组格式", required = "required,json")})
    @RequestMapping("/post/ShopMaterialStoreController/queryShopMaterialMapByMaterialIdAndStoreId")
    public void queryShopMaterialMapByMaterialIdAndStoreId(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryShopMaterialMapByMaterialIdAndStoreId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialMapByMaterialIdsAndStoreIds", value = "根据商品id列表和门店id列表（IN）批量获取商城商品关系", method = "POST", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id，json数组格式", required = "required,json"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id，json数组格式", required = "required,json")})
    @RequestMapping("/post/ShopMaterialStoreController/queryShopMaterialMapByMaterialIdsAndStoreIds")
    public void queryShopMaterialMapByMaterialIdsAndStoreIds(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryShopMaterialMapByMaterialIdsAndStoreIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteShopMaterialStoreByStoreIds", value = "修改商城商品关联得指定门店为禁用，也就是这个门店下得商品都不在商城显示", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeIds", name = "storeIds", value = "门店id，多个逗号隔开", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/deleteShopMaterialStoreByStoreIds")
    public void deleteShopMaterialStoreByStoreIds(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.deleteShopMaterialStoreByStoreIds(inputObject, outputObject);
    }

    @ApiOperation(id = "addShopMaterialStore", value = "添加指定商品到指定门店--管理端使用", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/addShopMaterialStore")
    public void addShopMaterialStore(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.addShopMaterialStore(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteShopMaterialStore", value = "从指定门店删除指定商品--管理端使用", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/deleteShopMaterialStore")
    public void deleteShopMaterialStore(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.deleteShopMaterialStore(inputObject, outputObject);
    }

    @ApiOperation(id = "launchShopMaterialStore", value = "上架指定门店得指定商品到商城--管理端使用", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialIds", name = "materialIds", value = "商品id，多个逗号隔开", required = "required"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "deliveryMethod", name = "deliveryMethod", value = "配送方式", required = "required,json"),
        @ApiImplicitParam(id = "saleChannel", name = "saleChannel", value = "上架经营方式，1线上 2线下", required = "required,json")})
    @RequestMapping("/post/ShopMaterialStoreController/launchShopMaterialStore")
    public void launchShopMaterialStore(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.launchShopMaterialStore(inputObject, outputObject);
    }

    @ApiOperation(id = "unlaunchShopMaterialStore", value = "下架指定门店得指定商品到商城--管理端使用", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialIds", name = "materialIds", value = "商品id，多个逗号隔开", required = "required"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/unlaunchShopMaterialStore")
    public void unlaunchShopMaterialStore(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.unlaunchShopMaterialStore(inputObject, outputObject);
    }

    @ApiOperation(id = "getAllowedShopMaterialList", value = "获取指定门店允许得商品列表--管理端使用", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopMaterialStoreController/getAllowedShopMaterialList")
    public void getAllowedShopMaterialList(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.getAllowedShopMaterialList(inputObject, outputObject);
    }

    @ApiOperation(id = "getAddedShopMaterialList", value = "获取指定门店已经添加得商品列表--管理端使用", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopMaterialStoreController/getAddedShopMaterialList")
    public void getAddedShopMaterialList(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.getAddedShopMaterialList(inputObject, outputObject);
    }

    @ApiOperation(id = "getLaunchedShopMaterialList", value = "获取指定门店已经上架到商城的商品列表--管理端使用", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopMaterialStoreController/getLaunchedShopMaterialList")
    public void getLaunchedShopMaterialList(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.getLaunchedShopMaterialList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLaunchedStoreByMaterialId", value = "按商品查询已上架门店列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopMaterialStoreController/queryLaunchedStoreByMaterialId")
    public void queryLaunchedStoreByMaterialId(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryLaunchedStoreByMaterialId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPersonalStoreMaterialList", value = "获取当前会员个人门店已加入的商品", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopMaterialStoreController/queryPersonalStoreMaterialList")
    public void queryPersonalStoreMaterialList(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryPersonalStoreMaterialList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPlatformMaterialForPersonalStore", value = "获取可加入个人门店的平台货源（企业/加盟门店商品+门店）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopMaterialStoreController/queryPlatformMaterialForPersonalStore")
    public void queryPlatformMaterialForPersonalStore(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryPlatformMaterialForPersonalStore(inputObject, outputObject);
    }

    @ApiOperation(id = "choosePlatformMaterialForPersonalStore", value = "个人门店选入平台货源", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required"),
        @ApiImplicitParam(id = "sourceStoreId", name = "sourceStoreId", value = "供货方门店id（企业/加盟门店）", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/choosePlatformMaterialForPersonalStore")
    public void choosePlatformMaterialForPersonalStore(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.choosePlatformMaterialForPersonalStore(inputObject, outputObject);
    }

    @ApiOperation(id = "launchPersonalStoreMaterial", value = "个人门店上架商品", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "门店商品关系id，多个逗号隔开（同商品多供货门店时优先传这个）"),
        @ApiImplicitParam(id = "materialIds", name = "materialIds", value = "商品id，多个逗号隔开（无ids时使用）"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/launchPersonalStoreMaterial")
    public void launchPersonalStoreMaterial(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.launchPersonalStoreMaterial(inputObject, outputObject);
    }

    @ApiOperation(id = "unlaunchPersonalStoreMaterial", value = "个人门店下架商品", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "门店商品关系id，多个逗号隔开（同商品多供货门店时优先传这个）"),
        @ApiImplicitParam(id = "materialIds", name = "materialIds", value = "商品id，多个逗号隔开（无ids时使用）"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/unlaunchPersonalStoreMaterial")
    public void unlaunchPersonalStoreMaterial(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.unlaunchPersonalStoreMaterial(inputObject, outputObject);
    }

    @ApiOperation(id = "removePersonalStoreMaterial", value = "个人门店移除商品", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "门店商品关系id，多个逗号隔开（同商品多供货门店时优先传这个）"),
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id（无ids时使用）"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/removePersonalStoreMaterial")
    public void removePersonalStoreMaterial(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.removePersonalStoreMaterial(inputObject, outputObject);
    }

    @ApiOperation(id = "createPersonalStoreMaterial", value = "个人门店自建商品", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "name", name = "name", value = "商品名称", required = "required"),
        @ApiImplicitParam(id = "logo", name = "logo", value = "商品图片", required = "required"),
        @ApiImplicitParam(id = "salePrice", name = "salePrice", value = "售价，多规格时传默认规格销售价", required = "required"),
        @ApiImplicitParam(id = "deliveryMethod", name = "deliveryMethod", value = "配送方式", required = "required,json"),
        @ApiImplicitParam(id = "saleChannel", name = "saleChannel", value = "经营方式，1线上 2线下", required = "required,json"),
        @ApiImplicitParam(id = "model", name = "model", value = "型号", required = "required"),
        @ApiImplicitParam(id = "bigTypeId", name = "bigTypeId", value = "商城商品分类id", required = "required"),
        @ApiImplicitParam(id = "content", name = "content", value = "商品详情", required = "required"),
        @ApiImplicitParam(id = "carouselImg", name = "carouselImg", value = "轮播图，多个逗号隔开"),
        @ApiImplicitParam(id = "skuData", name = "skuData", value = "规格数据：单/多规格均含unit、unitName、materialNorms；多规格另含normsSpec。单位组由后台兜底", required = "required,json")})
    @RequestMapping("/post/ShopMaterialStoreController/createPersonalStoreMaterial")
    public void createPersonalStoreMaterial(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.createPersonalStoreMaterial(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPersonalStoreMaterialDetail", value = "个人门店商品详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/queryPersonalStoreMaterialDetail")
    public void queryPersonalStoreMaterialDetail(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryPersonalStoreMaterialDetail(inputObject, outputObject);
    }

    @ApiOperation(id = "updatePersonalStoreMaterial", value = "个人门店编辑自建商品", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required"),
        @ApiImplicitParam(id = "name", name = "name", value = "商品名称", required = "required"),
        @ApiImplicitParam(id = "logo", name = "logo", value = "商品图片", required = "required"),
        @ApiImplicitParam(id = "salePrice", name = "salePrice", value = "售价，多规格时传默认规格销售价", required = "required"),
        @ApiImplicitParam(id = "deliveryMethod", name = "deliveryMethod", value = "配送方式", required = "required,json"),
        @ApiImplicitParam(id = "saleChannel", name = "saleChannel", value = "经营方式，1线上 2线下", required = "required,json"),
        @ApiImplicitParam(id = "model", name = "model", value = "型号", required = "required"),
        @ApiImplicitParam(id = "bigTypeId", name = "bigTypeId", value = "商城商品分类id", required = "required"),
        @ApiImplicitParam(id = "content", name = "content", value = "商品详情", required = "required"),
        @ApiImplicitParam(id = "carouselImg", name = "carouselImg", value = "轮播图，多个逗号隔开"),
        @ApiImplicitParam(id = "skuData", name = "skuData", value = "规格数据，编辑时 materialNorms 需带 normsId", required = "required,json")})
    @RequestMapping("/post/ShopMaterialStoreController/updatePersonalStoreMaterial")
    public void updatePersonalStoreMaterial(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.updatePersonalStoreMaterial(inputObject, outputObject);
    }
}
