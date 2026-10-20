package com.skyeye.school.building.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.building.entity.RoomOccupant;
import com.skyeye.school.building.service.RoomOccupantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 房间人员
 */
@RestController
@Api(value = "房间人员", tags = "房间人员", modelName = "地点管理")
public class RoomOccupantController {

    @Autowired
    private RoomOccupantService roomOccupantService;

    @ApiOperation(id = "writeRoomOccupant", value = "新增或编辑房间人员", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = RoomOccupant.class)
    @RequestMapping("/post/RoomOccupantController/writeRoomOccupant")
    public void writeRoomOccupant(InputObject inputObject, OutputObject outputObject) {
        roomOccupantService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteRoomOccupantById", value = "删除房间人员", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/RoomOccupantController/deleteRoomOccupantById")
    public void deleteRoomOccupantById(InputObject inputObject, OutputObject outputObject) {
        roomOccupantService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRoomOccupantByFloorId", value = "按房间查询人员", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "floorInfoId", name = "floorInfoId", value = "房间id", required = "required")
    })
    @RequestMapping("/post/RoomOccupantController/queryRoomOccupantByFloorId")
    public void queryRoomOccupantByFloorId(InputObject inputObject, OutputObject outputObject) {
        roomOccupantService.queryRoomOccupantByFloorId(inputObject, outputObject);
    }
}
