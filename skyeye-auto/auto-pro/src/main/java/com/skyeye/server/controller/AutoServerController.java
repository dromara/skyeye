/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.server.entity.AutoServer;
import com.skyeye.server.service.AutoServerMetricHistoryService;
import com.skyeye.server.service.AutoServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoServerController
 * @Description: 服务器管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:59
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "服务器管理", tags = "服务器管理", modelName = "服务器管理")
public class AutoServerController {

    @Autowired
    private AutoServerService autoServerService;

    @Autowired
    private AutoServerMetricHistoryService autoServerMetricHistoryService;

    /**
     * 获取服务器信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAutoServerList", value = "获取服务器信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AutoServerController/queryAutoServerList")
    public void queryAutoServerList(InputObject inputObject, OutputObject outputObject) {
        autoServerService.queryPageList(inputObject, outputObject);
    }

    /**
     * 添加或修改服务器信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeAutoServer", value = "新增/编辑服务器信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoServer.class)
    @RequestMapping("/post/AutoServerController/writeAutoServer")
    public void writeAutoServer(InputObject inputObject, OutputObject outputObject) {
        autoServerService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除服务器信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteAutoServerById", value = "根据ID删除服务器信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoServerController/deleteAutoServerById")
    public void deleteAutoServerById(InputObject inputObject, OutputObject outputObject) {
        autoServerService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据环境id获取服务器信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAutoServerListByEnvironmentId", value = "根据环境id获取服务器信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "environmentId", name = "environmentId", value = "环境id")})
    @RequestMapping("/post/AutoServerController/queryAutoServerListByEnvironmentId")
    public void queryAutoServerListByEnvironmentId(InputObject inputObject, OutputObject outputObject) {
        autoServerService.queryAutoServerListByEnvironmentId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryServerMonitorDashboard", value = "查询服务器监控大屏", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目key")})
    @RequestMapping("/post/AutoServerController/queryServerMonitorDashboard")
    public void queryServerMonitorDashboard(InputObject inputObject, OutputObject outputObject) {
        autoServerService.queryServerMonitorDashboard(inputObject, outputObject);
    }

    @ApiOperation(id = "probeAutoServersByObjectId", value = "中心探测当前项目全部服务器", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目key")})
    @RequestMapping("/post/AutoServerController/probeAutoServersByObjectId")
    public void probeAutoServersByObjectId(InputObject inputObject, OutputObject outputObject) {
        autoServerService.probeAutoServersByObjectId(inputObject, outputObject);
    }

    @ApiOperation(id = "probeAutoServerById", value = "中心探测单台服务器", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "服务器id", required = "required")})
    @RequestMapping("/post/AutoServerController/probeAutoServerById")
    public void probeAutoServerById(InputObject inputObject, OutputObject outputObject) {
        autoServerService.probeAutoServerById(inputObject, outputObject);
    }

    @ApiOperation(id = "collectServerMetricsByObjectId", value = "SSH采集当前项目服务器资源指标", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "项目key")})
    @RequestMapping("/post/AutoServerController/collectServerMetricsByObjectId")
    public void collectServerMetricsByObjectId(InputObject inputObject, OutputObject outputObject) {
        autoServerService.collectServerMetricsByObjectId(inputObject, outputObject);
    }

    @ApiOperation(id = "collectServerMetricsById", value = "SSH采集单台服务器资源指标", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "服务器id", required = "required")})
    @RequestMapping("/post/AutoServerController/collectServerMetricsById")
    public void collectServerMetricsById(InputObject inputObject, OutputObject outputObject) {
        autoServerService.collectServerMetricsById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryServerMetricHistory", value = "查询服务器资源采集历史（折线图）", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "serverId", name = "serverId", value = "服务器id", required = "required"),
        @ApiImplicitParam(id = "hours", name = "hours", value = "最近小时数，默认24，最大168")})
    @RequestMapping("/post/AutoServerController/queryServerMetricHistory")
    public void queryServerMetricHistory(InputObject inputObject, OutputObject outputObject) {
        autoServerMetricHistoryService.queryServerMetricHistory(inputObject, outputObject);
    }

}
