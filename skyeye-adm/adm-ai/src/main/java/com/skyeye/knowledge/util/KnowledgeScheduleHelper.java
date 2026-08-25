/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.pattern.CronPattern;
import com.skyeye.common.enumeration.ScheduleFrequency;
import com.skyeye.knowledge.entity.Knowledge;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 知识库定时规则判断，对齐巡检单的每天/每周/每月/自定义。
 */
public final class KnowledgeScheduleHelper {

    private KnowledgeScheduleHelper() {
    }

    public static boolean isDue(Knowledge knowledge, Date now) {
        if (knowledge == null || knowledge.getFrequency() == null) {
            return false;
        }
        if (alreadyRanThisMinute(knowledge.getLastSyncTime(), now)) {
            return false;
        }
        ScheduleFrequency frequency = ScheduleFrequency.getByKey(knowledge.getFrequency());
        if (frequency == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        switch (frequency) {
            case DAILY:
                return matchExecuteTime(knowledge.getExecuteTime(), calendar);
            case WEEKLY:
                return matchDay(knowledge.getWeekDays(), bizDayOfWeek(calendar))
                    && matchExecuteTime(knowledge.getExecuteTime(), calendar);
            case MONTHLY:
                return matchDay(knowledge.getMonthDays(), calendar.get(Calendar.DAY_OF_MONTH))
                    && matchExecuteTime(knowledge.getExecuteTime(), calendar);
            case CUSTOM:
                return matchCron(knowledge.getCustomCron(), now);
            default:
                return false;
        }
    }

    private static boolean alreadyRanThisMinute(String lastSyncTime, Date now) {
        if (StrUtil.isBlank(lastSyncTime) || lastSyncTime.length() < 16) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        String slot = String.format("%04d-%02d-%02d %02d:%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE));
        return lastSyncTime.substring(0, 16).equals(slot);
    }

    private static boolean matchExecuteTime(String executeTime, Calendar calendar) {
        String time = StrUtil.blankToDefault(executeTime, "00:00");
        String[] parts = time.split(":");
        if (parts.length < 2) {
            return false;
        }
        int hour;
        int minute;
        try {
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        return calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE) == minute;
    }

    private static boolean matchDay(String days, int current) {
        if (StrUtil.isBlank(days)) {
            return false;
        }
        Set<String> set = new HashSet<>(Arrays.asList(days.split(",")));
        return set.contains(String.valueOf(current));
    }

    /**
     * 业务周：1=周一 ... 7=周日
     */
    private static int bizDayOfWeek(Calendar calendar) {
        int javaDow = calendar.get(Calendar.DAY_OF_WEEK);
        return javaDow == Calendar.SUNDAY ? 7 : javaDow - 1;
    }

    private static boolean matchCron(String cron, Date now) {
        if (StrUtil.isBlank(cron)) {
            return false;
        }
        try {
            CronPattern pattern = new CronPattern(cron);
            return pattern.match(now.getTime(), true);
        } catch (Exception e) {
            return false;
        }
    }

}
