package com.skyeye.school.route.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.route.entity.Routes;
import com.skyeye.school.route.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: RouteController
 * @Description: 路线管理控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/18 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "路线管理", tags = "路线管理", modelName = "路线管理")
public class RouteController {

    @Autowired
    private RoutesService routeService;

    @ApiOperation(id = "writeRoute", value = "新增/编辑路线", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Routes.class)
    @RequestMapping("/post/RouteController/writeRoute")
    public void writeRoute(InputObject inputObject, OutputObject outputObject) {
        routeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRouteList", value = "除分页参数还需要holderId(schoolId)查询路线列表", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/RouteController/queryRouteList")
    public void queryRouteList(InputObject inputObject, OutputObject outputObject) {
        routeService.queryRouteList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteRouteById", value = "根据id删除路线", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "路线id", required = "required")})
    @RequestMapping("/post/RouteController/deleteRouteById")
    public void deleteRouteById(InputObject inputObject, OutputObject outputObject) {
        routeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRouteById", value = "根据id查询路线信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "路线id", required = "required")})
    @RequestMapping("/post/RouteController/queryRouteById")
    public void queryRouteById(InputObject inputObject, OutputObject outputObject) {
        routeService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "writeRouteStopList", value = "编辑路线停靠点列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "路线id", required = "required"),
        @ApiImplicitParam(id = "routeStopList", name = "routeStopList", value = "路线停靠点列表", required = "json"),
        @ApiImplicitParam(id = "routeLength", name = "routeLength", value = "路线长度（米，前端计算）", required = "required")})
    @RequestMapping("/post/RouteController/writeRouteStopList")
    public void writeRouteStopList(InputObject inputObject, OutputObject outputObject) {
        routeService.writeRouteStopList(inputObject, outputObject);
    }

}
