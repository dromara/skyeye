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
import com.skyeye.shopmaterial.entity.ShopMaterial;
import com.skyeye.shopmaterial.service.ShopMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopMaterialController
 * @Description: 商城商品控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/4 17:57
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "商城商品", tags = "商城商品", modelName = "商城商品")
public class ShopMaterialController {

    @Autowired
    private ShopMaterialService shopMaterialService;

    @ApiOperation(id = "queryTransMaterialById", value = "根据id获取商品信息，用于上架商城", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "商品id", required = "required")})
    @RequestMapping("/post/ShopMaterialController/queryTransMaterialById")
    public void queryTransMaterialById(InputObject inputObject, OutputObject outputObject) {
        shopMaterialService.queryTransMaterialById(inputObject, outputObject);
    }

    @ApiOperation(id = "saveShopMaterial", value = "ERP商品上架商城", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ShopMaterial.class)
    @RequestMapping("/post/ShopMaterialController/saveShopMaterial")
    public void saveShopMaterial(InputObject inputObject, OutputObject outputObject) {
        shopMaterialService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialList", value = "获取商城商品信息列表(已经被门店加入到门店下的 && 已经上架的)--商城使用", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopMaterialController/queryShopMaterialList")
    public void queryShopMaterialList(InputObject inputObject, OutputObject outputObject) {
        shopMaterialService.queryShopMaterialList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialListForStore", value = "以门店为主获取商城商品信息列表--商城使用", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopMaterialController/queryShopMaterialListForStore")
    public void queryShopMaterialListForStore(InputObject inputObject, OutputObject outputObject) {
        shopMaterialService.queryShopMaterialListForStore(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialByNormsIdList", value = "根据规格id获取商城商品信息", method = "POST", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "normsIds", name = "normsIds", value = "规格id，多个逗号隔开", required = "required")})
    @RequestMapping("/post/ShopMaterialController/queryShopMaterialByNormsIdList")
    public void queryShopMaterialByNormsIdList(InputObject inputObject, OutputObject outputObject) {
        shopMaterialService.queryShopMaterialByNormsIdList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialByMaterialIdList", value = "根据商品id获取商城商品信息", method = "POST", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialIds", name = "materialIds", value = "商品id，多个逗号隔开", required = "required")})
    @RequestMapping("/post/ShopMaterialController/queryShopMaterialByMaterialIdList")
    public void queryShopMaterialByMaterialIdList(InputObject inputObject, OutputObject outputObject) {
        shopMaterialService.queryShopMaterialByMaterialIdList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBrandShopMaterialList", value = "获取商城商品信息列表根据品牌进行分组，只取前8条--商城PC端使用", method = "GET", allUse = "0")
    @RequestMapping("/post/ShopMaterialController/queryBrandShopMaterialList")
    public void queryBrandShopMaterialList(InputObject inputObject, OutputObject outputObject) {
        shopMaterialService.queryBrandShopMaterialList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllShopMaterialListForChoose", value = "获取当前租户下的商品信息列表供选择--管理端", method = "GET", allUse = "2")
    @RequestMapping("/post/ShopMaterialController/queryAllShopMaterialListForChoose")
    public void queryAllShopMaterialListForChoose(InputObject inputObject, OutputObject outputObject) {
        shopMaterialService.queryAllShopMaterialListForChoose(inputObject, outputObject);
    }

}
