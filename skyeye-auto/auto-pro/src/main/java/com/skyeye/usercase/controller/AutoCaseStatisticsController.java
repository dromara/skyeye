/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.usercase.service.AutoCaseStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoCaseStatisticsController
 * @Description: 用例管理统计控制层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用例管理统计", tags = "用例管理统计", modelName = "用例管理统计")
public class AutoCaseStatisticsController {

    @Autowired
    private AutoCaseStatisticsService autoCaseStatisticsService;

    @ApiOperation(id = "queryOverviewAutoCase", value = "用例管理统计-总览卡片", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoCaseStatisticsController/queryOverviewAutoCase")
    public void queryOverviewAutoCase(InputObject inputObject, OutputObject outputObject) {
        autoCaseStatisticsService.queryOverviewAutoCase(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoCaseTrendStats", value = "用例管理统计-按创建时间日度趋势", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoCaseStatisticsController/queryAutoCaseTrendStats")
    public void queryAutoCaseTrendStats(InputObject inputObject, OutputObject outputObject) {
        autoCaseStatisticsService.queryAutoCaseTrendStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoCaseStatsByModule", value = "用例管理统计-按模块统计用例数", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AutoCaseStatisticsController/queryAutoCaseStatsByModule")
    public void queryAutoCaseStatsByModule(InputObject inputObject, OutputObject outputObject) {
        autoCaseStatisticsService.queryAutoCaseStatsByModule(inputObject, outputObject);
    }

}
