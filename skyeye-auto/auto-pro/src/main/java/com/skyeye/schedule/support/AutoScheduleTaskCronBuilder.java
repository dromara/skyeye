/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.support;

import com.skyeye.common.util.QuartzCronUtil;
import com.skyeye.schedule.entity.AutoScheduleTask;

/**
 * 将自动化定时任务频次转换为 XXL-JOB / Quartz 风格 CRON。
 * 具体转换见 {@link QuartzCronUtil}。
 */
public final class AutoScheduleTaskCronBuilder {

    private AutoScheduleTaskCronBuilder() {
    }

    /**
     * @return 可注册的 CRON；无法构建时返回 null
     */
    public static String buildScheduleConf(AutoScheduleTask task) {
        if (task == null) {
            return null;
        }
        return QuartzCronUtil.buildScheduleConf(
            task.getFrequency(), task.getExecuteTime(),
            task.getWeekDays(), task.getMonthDays(), task.getCustomCron());
    }
}
