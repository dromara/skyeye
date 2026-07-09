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
import com.skyeye.personnel.service.SysEveUserMenuFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveUserMenuFavoriteController
 * @Description: 用户APP菜单收藏控制层
 */
@RestController
@Api(value = "用户APP菜单收藏", tags = "用户APP菜单收藏", modelName = "用户APP菜单收藏")
public class SysEveUserMenuFavoriteController {

    @Autowired
    private SysEveUserMenuFavoriteService sysEveUserMenuFavoriteService;

    @ApiOperation(id = "queryUserFavoriteMenuList", value = "获取当前用户收藏的APP菜单列表", method = "POST", allUse = "2")
    @RequestMapping("/post/SysEveUserMenuFavoriteController/queryUserFavoriteMenuList")
    public void queryUserFavoriteMenuList(InputObject inputObject, OutputObject outputObject) {
        sysEveUserMenuFavoriteService.queryUserFavoriteMenuList(inputObject, outputObject);
    }

    @ApiOperation(id = "saveUserFavoriteMenu", value = "收藏APP菜单", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "menuId", name = "menuId", value = "菜单id", required = "required")})
    @RequestMapping("/post/SysEveUserMenuFavoriteController/saveUserFavoriteMenu")
    public void saveUserFavoriteMenu(InputObject inputObject, OutputObject outputObject) {
        sysEveUserMenuFavoriteService.saveUserFavoriteMenu(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteUserFavoriteMenuByMenuId", value = "取消收藏APP菜单", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "menuId", name = "menuId", value = "菜单id", required = "required")})
    @RequestMapping("/post/SysEveUserMenuFavoriteController/deleteUserFavoriteMenuByMenuId")
    public void deleteUserFavoriteMenuByMenuId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserMenuFavoriteService.deleteUserFavoriteMenuByMenuId(inputObject, outputObject);
    }

    @ApiOperation(id = "saveUserFavoriteMenuOrder", value = "保存收藏APP菜单排序", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "menuIds", name = "menuIds", value = "菜单id数组JSON字符串", required = "required")})
    @RequestMapping("/post/SysEveUserMenuFavoriteController/saveUserFavoriteMenuOrder")
    public void saveUserFavoriteMenuOrder(InputObject inputObject, OutputObject outputObject) {
        sysEveUserMenuFavoriteService.saveUserFavoriteMenuOrder(inputObject, outputObject);
    }

}
