/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.schedule.entity.AutoScheduleTask;

/**
 * @Description: 自动化定时任务服务
 */
public interface AutoScheduleTaskService extends SkyeyeBusinessService<AutoScheduleTask> {

    /**
     * 执行定时任务（解析用例范围并逐个投递执行）
     */
    void executeScheduleTask(InputObject inputObject, OutputObject outputObject);

    /**
     * 按任务id执行
     */
    void executeScheduleTask(String id);

}
