/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.demand.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.demand.service.AutoDemandStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoDemandStatisticsController
 * @Description: 需求管理统计控制层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "需求管理统计", tags = "需求管理统计", modelName = "需求管理统计")
public class AutoDemandStatisticsController {

    @Autowired
    private AutoDemandStatisticsService autoDemandStatisticsService;

    @ApiOperation(id = "queryOverviewAutoDemand", value = "需求管理统计-总览卡片", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoDemandStatisticsController/queryOverviewAutoDemand")
    public void queryOverviewAutoDemand(InputObject inputObject, OutputObject outputObject) {
        autoDemandStatisticsService.queryOverviewAutoDemand(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoDemandTrendStats", value = "需求管理统计-按创建时间日度趋势", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoDemandStatisticsController/queryAutoDemandTrendStats")
    public void queryAutoDemandTrendStats(InputObject inputObject, OutputObject outputObject) {
        autoDemandStatisticsService.queryAutoDemandTrendStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDemandStatsByState", value = "需求管理统计-按状态划分", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoDemandStatisticsController/queryDemandStatsByState")
    public void queryDemandStatsByState(InputObject inputObject, OutputObject outputObject) {
        autoDemandStatisticsService.queryDemandStatsByState(inputObject, outputObject);
    }

    @ApiOperation(id = "queryVersionDashboard", value = "需求管理统计-版本统计大屏", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoDemandStatisticsController/queryVersionDashboard")
    public void queryVersionDashboard(InputObject inputObject, OutputObject outputObject) {
        autoDemandStatisticsService.queryVersionDashboard(inputObject, outputObject);
    }

}
