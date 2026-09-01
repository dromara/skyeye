/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.menu.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.menu.entity.SysMenu;
import com.skyeye.menu.entity.SysMenuQueryDo;
import com.skyeye.menu.service.SysEveMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveMenuController
 * @Description: 菜单管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/5/15 20:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "菜单管理", tags = "菜单管理", modelName = "菜单管理")
public class SysEveMenuController {

    @Autowired
    private SysEveMenuService sysEveMenuService;

    @ApiOperation(id = "sys006", value = "获取菜单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SysMenuQueryDo.class)
    @RequestMapping("/post/SysEveMenuController/querySysMenuList")
    public void querySysMenuList(InputObject inputObject, OutputObject outputObject) {
        sysEveMenuService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeMenu", value = "添加/编辑菜单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SysMenu.class)
    @RequestMapping("/post/SysEveMenuController/writeMenu")
    public void writeMenu(InputObject inputObject, OutputObject outputObject) {
        sysEveMenuService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "sys040", value = "系统菜单详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "菜单ID", required = "required")})
    @RequestMapping("/post/SysEveMenuController/querySysEveMenuBySysId")
    public void querySysEveMenuBySysId(InputObject inputObject, OutputObject outputObject) {
        sysEveMenuService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "sys009", value = "根据父菜单ID查看子菜单", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "父菜单ID", required = "required")})
    @RequestMapping("/post/SysEveMenuController/querySysMenuMationBySimpleLevel")
    public void querySysMenuMationBySimpleLevel(InputObject inputObject, OutputObject outputObject) {
        sysEveMenuService.querySysMenuMationBySimpleLevel(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMenuById", value = "删除菜单信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "菜单ID", required = "required")})
    @RequestMapping("/post/SysEveMenuController/deleteMenuById")
    public void deleteMenuById(InputObject inputObject, OutputObject outputObject) {
        sysEveMenuService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllSysMenuTreeList", value = "获取全部系统菜单树（AI技能打开页面等选用）", method = "GET", allUse = "2")
    @RequestMapping("/post/SysEveMenuController/queryAllSysMenuTreeList")
    public void queryAllSysMenuTreeList(InputObject inputObject, OutputObject outputObject) {
        sysEveMenuService.queryAllSysMenuTreeList(inputObject, outputObject);
    }

}
