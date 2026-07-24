/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.schedule.service.AutoScheduleTaskHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoScheduleTaskHistoryController
 * @Description: 定时任务执行记录控制层
 */
@RestController
@Api(value = "定时任务执行记录", tags = "定时任务执行记录", modelName = "定时任务执行记录")
public class AutoScheduleTaskHistoryController {

    @Autowired
    private AutoScheduleTaskHistoryService autoScheduleTaskHistoryService;

    /**
     * 分页查询定时任务执行记录
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAutoScheduleTaskHistoryList", value = "分页查询定时任务执行记录", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AutoScheduleTaskHistoryController/queryAutoScheduleTaskHistoryList")
    public void queryAutoScheduleTaskHistoryList(InputObject inputObject, OutputObject outputObject) {
        autoScheduleTaskHistoryService.queryPageList(inputObject, outputObject);
    }

}
