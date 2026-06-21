/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.skyeye.common.enumeration.WeekTypeEnum;
import com.skyeye.common.util.DateUtil;
import com.skyeye.worktime.classenum.CheckWorkTimeWeekType;
import com.skyeye.worktime.entity.CheckWorkTimeWeek;

import java.util.List;

/**
 * 考勤班次工作日判断工具（打卡、报表、定时任务、请假等统一口径）
 */
public class CheckWorkTimeWeekUtil {

    private CheckWorkTimeWeekUtil() {
    }

    /**
     * 判断指定日期在该班次配置下是否为工作日
     *
     * @param date     日期，格式 yyyy-MM-dd
     * @param weekList 班次工作日配置
     * @return true 工作日，false 休息日或未配置
     */
    public static boolean isWorkDay(String date, List<CheckWorkTimeWeek> weekList) {
        if (CollectionUtil.isEmpty(weekList)) {
            return false;
        }
        return isWorkDay(DateUtil.getWeek(date), DateUtil.getWeekType(date), weekList);
    }

    /**
     * 判断指定星期在该班次配置下是否为工作日
     *
     * @param weekDay  周几（1-7）
     * @param weekType 单双周（0 单周，1 双周）
     * @param weekList 班次工作日配置
     */
    public static boolean isWorkDay(int weekDay, int weekType, List<CheckWorkTimeWeek> weekList) {
        if (CollectionUtil.isEmpty(weekList)) {
            return false;
        }
        CheckWorkTimeWeek simpleDay = weekList.stream()
            .filter(item -> item.getWeekNumber() == weekDay && !CheckWorkTimeWeekType.DOUBLE.getKey().equals(item.getType()))
            .findFirst().orElse(null);
        if (ObjectUtil.isEmpty(simpleDay)) {
            return false;
        }
        if (weekType == WeekTypeEnum.ODD_WEEKS.getKey() && CheckWorkTimeWeekType.SINGLE_DAY.getKey().equals(simpleDay.getType())) {
            return true;
        }
        if (weekType == WeekTypeEnum.BIWEEKLY.getKey() && CheckWorkTimeWeekType.SINGLE_DAY.getKey().equals(simpleDay.getType())) {
            return false;
        }
        return true;
    }
}
