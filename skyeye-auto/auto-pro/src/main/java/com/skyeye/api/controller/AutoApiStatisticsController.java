/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.api.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.api.service.AutoApiStatisticsService;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoApiStatisticsController
 * @Description: 接口管理统计控制层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "接口管理统计", tags = "接口管理统计", modelName = "接口管理统计")
public class AutoApiStatisticsController {

    @Autowired
    private AutoApiStatisticsService autoApiStatisticsService;

    @ApiOperation(id = "queryOverviewAutoApi", value = "接口管理统计-总览卡片", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoApiStatisticsController/queryOverviewAutoApi")
    public void queryOverviewAutoApi(InputObject inputObject, OutputObject outputObject) {
        autoApiStatisticsService.queryOverviewAutoApi(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoApiTrendStats", value = "接口管理统计-按创建时间日度趋势", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoApiStatisticsController/queryAutoApiTrendStats")
    public void queryAutoApiTrendStats(InputObject inputObject, OutputObject outputObject) {
        autoApiStatisticsService.queryAutoApiTrendStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryApiStatsByCreator", value = "接口管理统计-按创建人统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoApiStatisticsController/queryApiStatsByCreator")
    public void queryApiStatsByCreator(InputObject inputObject, OutputObject outputObject) {
        autoApiStatisticsService.queryApiStatsByCreator(inputObject, outputObject);
    }

}
