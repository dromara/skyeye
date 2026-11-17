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
import com.skyeye.depot.service.DepotLevelValOccupancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "仓库库位占用", tags = "仓库库位占用", modelName = "仓库级别管理")
public class DepotLevelValOccupancyController {

    @Autowired
    private DepotLevelValOccupancyService depotLevelValOccupancyService;

    @ApiOperation(id = "queryDepotLevelValOccupancyByDepotId", value = "按仓库查询库位占用（3D占用色）", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "depotId", name = "depotId", value = "仓库id", required = "required")})
    @RequestMapping("/post/DepotLevelValOccupancyController/queryDepotLevelValOccupancyByDepotId")
    public void queryDepotLevelValOccupancyByDepotId(InputObject inputObject, OutputObject outputObject) {
        depotLevelValOccupancyService.queryDepotLevelValOccupancyByDepotId(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDepotLevelValOccupancyBatch", value = "批量保存库位占用", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "depotId", name = "depotId", value = "仓库id", required = "required"),
        @ApiImplicitParam(id = "occupancyList", name = "occupancyList", value = "占用列表JSON", required = "required")})
    @RequestMapping("/post/DepotLevelValOccupancyController/writeDepotLevelValOccupancyBatch")
    public void writeDepotLevelValOccupancyBatch(InputObject inputObject, OutputObject outputObject) {
        depotLevelValOccupancyService.writeDepotLevelValOccupancyBatch(inputObject, outputObject);
    }
}
