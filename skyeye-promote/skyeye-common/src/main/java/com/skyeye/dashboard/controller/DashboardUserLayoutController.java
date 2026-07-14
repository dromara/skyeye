/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dashboard.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.dashboard.entity.DashboardUserLayout;
import com.skyeye.dashboard.service.DashboardUserLayoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DashboardUserLayoutController
 * @Description: 用户仪表盘布局控制类
 */
@RestController
@Api(value = "用户仪表盘布局", tags = "用户仪表盘布局", modelName = "仪表盘设计器")
public class DashboardUserLayoutController {

    @Autowired
    private DashboardUserLayoutService dashboardUserLayoutService;

    @ApiOperation(id = "queryDashboardLayoutList", value = "获取当前用户仪表盘布局列表", method = "GET", allUse = "2")
    @RequestMapping("/post/DashboardUserLayoutController/queryDashboardLayoutList")
    public void queryDashboardLayoutList(InputObject inputObject, OutputObject outputObject) {
        dashboardUserLayoutService.queryDashboardLayoutList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDefaultDashboardLayout", value = "获取当前用户默认仪表盘布局", method = "GET", allUse = "2")
    @RequestMapping("/post/DashboardUserLayoutController/queryDefaultDashboardLayout")
    public void queryDefaultDashboardLayout(InputObject inputObject, OutputObject outputObject) {
        dashboardUserLayoutService.queryDefaultDashboardLayout(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDashboardLayout", value = "新增/编辑仪表盘布局", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DashboardUserLayout.class)
    @RequestMapping("/post/DashboardUserLayoutController/writeDashboardLayout")
    public void writeDashboardLayout(InputObject inputObject, OutputObject outputObject) {
        dashboardUserLayoutService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "setDefaultDashboardLayoutById", value = "设置默认仪表盘布局", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DashboardUserLayoutController/setDefaultDashboardLayoutById")
    public void setDefaultDashboardLayoutById(InputObject inputObject, OutputObject outputObject) {
        dashboardUserLayoutService.setDefaultDashboardLayoutById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteDashboardLayoutById", value = "删除仪表盘布局", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DashboardUserLayoutController/deleteDashboardLayoutById")
    public void deleteDashboardLayoutById(InputObject inputObject, OutputObject outputObject) {
        dashboardUserLayoutService.deleteDashboardLayoutById(inputObject, outputObject);
    }

}
