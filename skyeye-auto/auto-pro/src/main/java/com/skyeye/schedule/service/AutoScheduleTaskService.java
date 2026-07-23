/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.schedule.entity.AutoScheduleTask;

/**
 * @ClassName: AutoScheduleTaskService
 * @Description: 自动化定时任务服务
 * @author: skyeye云系列--卫志强
 * @date: 2026/7/22
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoScheduleTaskService extends SkyeyeBusinessService<AutoScheduleTask> {

    /**
     * 执行定时任务
     */
    void executeScheduleTask(InputObject inputObject, OutputObject outputObject);

    /**
     * 按任务id执行
     */
    void executeScheduleTask(String id);

}
