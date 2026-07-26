/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.module.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.module.service.AutoModuleStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoModuleStatisticsController
 * @Description: 项目模块统计控制层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "项目模块统计", tags = "项目模块统计", modelName = "项目模块统计")
public class AutoModuleStatisticsController {

    @Autowired
    private AutoModuleStatisticsService autoModuleStatisticsService;

    @ApiOperation(id = "queryOverviewAutoModule", value = "总览卡片", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoModuleStatisticsController/queryOverviewAutoModule")
    public void queryOverviewAutoModule(InputObject inputObject, OutputObject outputObject) {
        autoModuleStatisticsService.queryOverviewAutoModule(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoModuleTrendStats", value = "按创建模块时间日度趋势", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoModuleStatisticsController/queryAutoModuleTrendStats")
    public void queryAutoModuleTrendStats(InputObject inputObject, OutputObject outputObject) {
        autoModuleStatisticsService.queryAutoModuleTrendStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCaseStatsByModule", value = "按模块统计用例数", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoModuleStatisticsController/queryCaseStatsByModule")
    public void queryCaseStatsByModule(InputObject inputObject, OutputObject outputObject) {
        autoModuleStatisticsService.queryCaseStatsByModule(inputObject, outputObject);
    }

}
