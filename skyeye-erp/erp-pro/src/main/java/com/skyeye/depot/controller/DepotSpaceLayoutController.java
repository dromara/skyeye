/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.service.DepotSpaceLayoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "仓库3D空间布局", tags = "仓库3D空间布局", modelName = "仓库级别管理")
public class DepotSpaceLayoutController {

    @Autowired
    private DepotSpaceLayoutService depotSpaceLayoutService;

    @ApiOperation(id = "queryDepotSpaceLayoutByDepotId", value = "按仓库查询3D布局", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "depotId", name = "depotId", value = "仓库id", required = "required")})
    @RequestMapping("/post/DepotSpaceLayoutController/queryDepotSpaceLayoutByDepotId")
    public void queryDepotSpaceLayoutByDepotId(InputObject inputObject, OutputObject outputObject) {
        depotSpaceLayoutService.queryDepotSpaceLayoutByDepotId(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDepotSpaceLayoutBatch", value = "批量保存仓库3D布局", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "depotId", name = "depotId", value = "仓库id", required = "required"),
        @ApiImplicitParam(id = "layoutList", name = "layoutList", value = "布局列表JSON", required = "required")})
    @RequestMapping("/post/DepotSpaceLayoutController/writeDepotSpaceLayoutBatch")
    public void writeDepotSpaceLayoutBatch(InputObject inputObject, OutputObject outputObject) {
        depotSpaceLayoutService.writeDepotSpaceLayoutBatch(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteDepotSpaceLayoutByLevelValId", value = "按库位值删除布局", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "levelValId", name = "levelValId", value = "库位值id", required = "required")})
    @RequestMapping("/post/DepotSpaceLayoutController/deleteDepotSpaceLayoutByLevelValId")
    public void deleteDepotSpaceLayoutByLevelValId(InputObject inputObject, OutputObject outputObject) {
        depotSpaceLayoutService.deleteDepotSpaceLayoutByLevelValId(inputObject, outputObject);
    }
}
