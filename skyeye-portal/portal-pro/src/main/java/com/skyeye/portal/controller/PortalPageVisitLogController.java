/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.annotation.operationlog.IgnoreOperationLog;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.portal.entity.PortalPageVisitLog;
import com.skyeye.portal.service.PortalPageVisitLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 官网访问统计接口：
 * - recordPortalPageVisit：官网匿名上报（allUse = 0）
 * - queryPortalVisitLogList：管理端明细分页（allUse = 1）
 * - 概览/趋势/热门页：管理端统计（allUse = 2，读日汇总表）
 */
@RestController
@Api(value = "官网访问统计", tags = "官网访问统计", modelName = "门户管理")
public class PortalPageVisitLogController {

    @Autowired
    private PortalPageVisitLogService portalPageVisitLogService;

    @IgnoreOperationLog
    @ApiOperation(id = "recordPortalPageVisit", value = "官网路由埋点上报", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = PortalPageVisitLog.class)
    @RequestMapping("/post/PortalPageVisitLogController/recordPortalPageVisit")
    public void recordPortalPageVisit(InputObject inputObject, OutputObject outputObject) {
        portalPageVisitLogService.recordPortalPageVisit(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPortalVisitLogList", value = "访问明细分页列表，供排查单条访问记录", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PortalPageVisitLogController/queryPortalVisitLogList")
    public void queryPortalVisitLogList(InputObject inputObject, OutputObject outputObject) {
        portalPageVisitLogService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPortalVisitOverviewStat", value = "概览：时间窗口内 PV 总量、今日 PV、今日 UV", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PortalPageVisitLogController/queryPortalVisitOverviewStat")
    public void queryPortalVisitOverviewStat(InputObject inputObject, OutputObject outputObject) {
        portalPageVisitLogService.queryPortalVisitOverviewStat(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPortalVisitTrendStat", value = "趋势：按天返回 PV、UV 序列（xAxisData 与 seriesData 一一对应）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PortalPageVisitLogController/queryPortalVisitTrendStat")
    public void queryPortalVisitTrendStat(InputObject inputObject, OutputObject outputObject) {
        portalPageVisitLogService.queryPortalVisitTrendStat(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPortalVisitTopPageStat", value = "热门页：按 page_path 聚合 PV 取 TopN", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PortalPageVisitLogController/queryPortalVisitTopPageStat")
    public void queryPortalVisitTopPageStat(InputObject inputObject, OutputObject outputObject) {
        portalPageVisitLogService.queryPortalVisitTopPageStat(inputObject, outputObject);
    }
}
