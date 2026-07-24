/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.support;

import com.skyeye.common.util.QuartzCronUtil;
import com.skyeye.maintenance.entity.MaintenancePlan;

/**
 * 将保养计划频次转换为 XXL-JOB / Quartz 风格 CRON。
 * 具体转换见 {@link QuartzCronUtil}。
 */
public final class MaintenancePlanCronBuilder {

    private MaintenancePlanCronBuilder() {
    }

    /**
     * @return 可注册的 CRON；无法构建时返回 null
     */
    public static String buildScheduleConf(MaintenancePlan plan) {
        if (plan == null) {
            return null;
        }
        return QuartzCronUtil.buildScheduleConf(
            plan.getFrequency(), plan.getMaintainTime(),
            plan.getWeekDays(), plan.getMonthDays(), plan.getCustomCron());
    }
}
