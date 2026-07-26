/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bug.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.bug.service.AutoBugStatisticsService;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoBugStatisticsController
 * @Description: bug管理统计控制层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "bug管理统计", tags = "bug管理统计", modelName = "bug管理统计")
public class AutoBugStatisticsController {

    @Autowired
    private AutoBugStatisticsService autoBugStatisticsService;

    @ApiOperation(id = "queryOverviewAutoBug", value = "bug管理统计-总览卡片", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoBugStatisticsController/queryOverviewAutoBug")
    public void queryOverviewAutoBug(InputObject inputObject, OutputObject outputObject) {
        autoBugStatisticsService.queryOverviewAutoBug(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoBugTrendStats", value = "bug管理统计-按创建时间日度趋势", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoBugStatisticsController/queryAutoBugTrendStats")
    public void queryAutoBugTrendStats(InputObject inputObject, OutputObject outputObject) {
        autoBugStatisticsService.queryAutoBugTrendStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBugStatsByState", value = "bug管理统计-按状态划分", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoBugStatisticsController/queryBugStatsByState")
    public void queryBugStatsByState(InputObject inputObject, OutputObject outputObject) {
        autoBugStatisticsService.queryBugStatsByState(inputObject, outputObject);
    }

}
