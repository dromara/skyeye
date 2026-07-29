/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.service.SysEveUserPcMenuFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveUserPcMenuFavoriteController
 * @Description: 用户PC菜单收藏控制层
 */
@RestController
@Api(value = "用户PC菜单收藏", tags = "用户PC菜单收藏", modelName = "用户PC菜单收藏")
public class SysEveUserPcMenuFavoriteController {

    @Autowired
    private SysEveUserPcMenuFavoriteService sysEveUserPcMenuFavoriteService;

    @ApiOperation(id = "queryUserPcFavoriteMenuList", value = "获取当前用户收藏的PC菜单列表", method = "POST", allUse = "2")
    @RequestMapping("/post/SysEveUserPcMenuFavoriteController/queryUserPcFavoriteMenuList")
    public void queryUserPcFavoriteMenuList(InputObject inputObject, OutputObject outputObject) {
        sysEveUserPcMenuFavoriteService.queryUserPcFavoriteMenuList(inputObject, outputObject);
    }

    @ApiOperation(id = "saveUserPcFavoriteMenu", value = "收藏PC菜单", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "menuId", name = "menuId", value = "菜单id", required = "required")})
    @RequestMapping("/post/SysEveUserPcMenuFavoriteController/saveUserPcFavoriteMenu")
    public void saveUserPcFavoriteMenu(InputObject inputObject, OutputObject outputObject) {
        sysEveUserPcMenuFavoriteService.saveUserPcFavoriteMenu(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteUserPcFavoriteMenuByMenuId", value = "取消收藏PC菜单", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "menuId", name = "menuId", value = "菜单id", required = "required")})
    @RequestMapping("/post/SysEveUserPcMenuFavoriteController/deleteUserPcFavoriteMenuByMenuId")
    public void deleteUserPcFavoriteMenuByMenuId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserPcMenuFavoriteService.deleteUserPcFavoriteMenuByMenuId(inputObject, outputObject);
    }

    @ApiOperation(id = "saveUserPcFavoriteMenuOrder", value = "保存收藏PC菜单排序", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "menuIds", name = "menuIds", value = "菜单id数组JSON字符串", required = "required")})
    @RequestMapping("/post/SysEveUserPcMenuFavoriteController/saveUserPcFavoriteMenuOrder")
    public void saveUserPcFavoriteMenuOrder(InputObject inputObject, OutputObject outputObject) {
        sysEveUserPcMenuFavoriteService.saveUserPcFavoriteMenuOrder(inputObject, outputObject);
    }

}
