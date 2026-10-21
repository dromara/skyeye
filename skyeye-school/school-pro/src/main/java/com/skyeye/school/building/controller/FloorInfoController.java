package com.skyeye.school.building.controller;


import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.building.entity.FloorInfo;
import com.skyeye.school.building.service.FloorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FloorInfoController
 * @Description: 楼层教室服务管理控制层
 * @author: skyeye云系列--lqy
 * @date: 2023/9/5 17:12
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "楼层教室服务管理", tags = "楼层教室服务管理", modelName = "楼层教室服务管理")
public class FloorInfoController {

    @Autowired
    private FloorInfoService floorInfoService;

    @ApiOperation(id = "writeOrUpdateFloorInfo", value = "新增/编辑楼层、教室、服务", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = FloorInfo.class)
    @RequestMapping("/post/FloorInfoController/writeOrUpdateFloorInfo")
    public void writeOrUpdateFloorInfo(InputObject inputObject, OutputObject outputObject) {
        floorInfoService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteFloorInfoById", value = "根据id删除楼层、教室、服务", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FloorInfoController/deleteFloorInfoById")
    public void deleteFloorInfoById(InputObject inputObject, OutputObject outputObject) {
        floorInfoService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFloorInfosByLocationId", value = "根据地点id(holderId)获取楼层所有信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/FloorInfoController/queryFloorInfosByLocationId")
    public void queryFloorInfosByLocationId(InputObject inputObject, OutputObject outputObject) {
        floorInfoService.queryFloorInfosByLocationId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFloorInfoById", value = "根据id查询楼层、教室、服务", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FloorInfoController/queryFloorInfoById")
    public void queryFloorInfoById(InputObject inputObject, OutputObject outputObject) {
        floorInfoService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFloorClassList", value = "根据学校id和教室关键字获取教室信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "keyword", name = "keyword", value = "关键字"),
        @ApiImplicitParam(id = "schoolId", name = "schoolId", value = "学校id", required = "required")})
    @RequestMapping("/post/FloorInfoController/queryFloorClassList")
    public void queryFloorClassList(InputObject inputObject, OutputObject outputObject) {
        floorInfoService.queryFloorClassList(inputObject, outputObject);
    }

    @ApiOperation(id = "syncVerticalSpaceToFloors", value = "楼梯/电梯一键同步到其他楼层", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "源楼梯/电梯房间id", required = "required")})
    @RequestMapping("/post/FloorInfoController/syncVerticalSpaceToFloors")
    public void syncVerticalSpaceToFloors(InputObject inputObject, OutputObject outputObject) {
        floorInfoService.syncVerticalSpaceToFloors(inputObject, outputObject);
    }

}
