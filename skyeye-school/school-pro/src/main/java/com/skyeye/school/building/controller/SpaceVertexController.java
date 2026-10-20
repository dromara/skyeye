package com.skyeye.school.building.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.building.service.SpaceVertexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 楼栋、房间轮廓
 */
@RestController
@Api(value = "空间轮廓", tags = "空间轮廓", modelName = "地点管理")
public class SpaceVertexController {

    @Autowired
    private SpaceVertexService spaceVertexService;

    @ApiOperation(id = "writeSpaceOutline", value = "保存楼栋或房间轮廓", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ownerType", name = "ownerType", value = "归属类型，1楼栋 2房间", required = "required"),
        @ApiImplicitParam(id = "ownerId", name = "ownerId", value = "归属id", required = "required"),
        @ApiImplicitParam(id = "vertexList", name = "vertexList", value = "顶点JSON，空数组表示清除", required = "required")
    })
    @RequestMapping("/post/SpaceVertexController/writeSpaceOutline")
    public void writeSpaceOutline(InputObject inputObject, OutputObject outputObject) {
        spaceVertexService.writeSpaceOutline(inputObject, outputObject);
    }

    @ApiOperation(id = "querySpaceVertexByOwner", value = "查询楼栋或房间轮廓", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ownerType", name = "ownerType", value = "归属类型，1楼栋 2房间", required = "required"),
        @ApiImplicitParam(id = "ownerId", name = "ownerId", value = "归属id", required = "required")
    })
    @RequestMapping("/post/SpaceVertexController/querySpaceVertexByOwner")
    public void querySpaceVertexByOwner(InputObject inputObject, OutputObject outputObject) {
        spaceVertexService.querySpaceVertexByOwner(inputObject, outputObject);
    }
}
