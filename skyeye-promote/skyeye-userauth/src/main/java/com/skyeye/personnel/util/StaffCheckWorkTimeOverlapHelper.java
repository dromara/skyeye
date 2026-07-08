/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.util.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 员工多班次绑定时的时段冲突校验（支持跨天班次）。
 * <p>
 * 原 {@link DateUtil#checkOverlap} 将跨天班（如 23:00-06:00）按同日时段比较，
 * 无法识别次日凌晨段与其它班次的重叠，本工具按「星期几 + 当天分钟区间」逐日比对。
 */
public final class StaffCheckWorkTimeOverlapHelper {

    /** 一天总分钟数，区间上界为 1440（不含） */
    private static final int MINUTES_PER_DAY = 24 * 60;

    private StaffCheckWorkTimeOverlapHelper() {
    }

    /**
     * 判断多个考勤班次是否存在时段冲突。
     *
     * @param timeMationList 班次详情（含 startTime、endTime、checkWorkTimeWeekList）
     * @return true 表示至少有一对班次在某一工作日的时间上有重叠
     */
    public static boolean hasConflict(List<Map<String, Object>> timeMationList) {
        if (CollectionUtil.isEmpty(timeMationList) || timeMationList.size() < 2) {
            return false;
        }
        for (int i = 0; i < timeMationList.size(); i++) {
            for (int j = i + 1; j < timeMationList.size(); j++) {
                if (hasConflictBetween(timeMationList.get(i), timeMationList.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断两个班次是否在任意一个星期几存在时间重叠。
     */
    private static boolean hasConflictBetween(Map<String, Object> left, Map<String, Object> right) {
        String leftStart = stringVal(left.get("startTime"));
        String leftEnd = stringVal(left.get("endTime"));
        String rightStart = stringVal(right.get("startTime"));
        String rightEnd = stringVal(right.get("endTime"));
        if (StrUtil.hasBlank(leftStart, leftEnd, rightStart, rightEnd)) {
            return false;
        }

        boolean leftCrossDay = isCrossDay(leftStart, leftEnd);
        boolean rightCrossDay = isCrossDay(rightStart, rightEnd);
        List<Map<String, Object>> leftWeekList = getWeekList(left);
        List<Map<String, Object>> rightWeekList = getWeekList(right);

        // weekNumber：1=周一 … 7=周日，与考勤班次配置一致
        for (int weekNumber = 1; weekNumber <= 7; weekNumber++) {
            List<int[]> leftIntervals = getOccupiedIntervals(weekNumber, leftWeekList, leftStart, leftEnd, leftCrossDay);
            List<int[]> rightIntervals = getOccupiedIntervals(weekNumber, rightWeekList, rightStart, rightEnd, rightCrossDay);
            if (leftIntervals.isEmpty() || rightIntervals.isEmpty()) {
                continue;
            }
            if (intervalsOverlap(leftIntervals, rightIntervals)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算某班次在指定星期几当天占用的分钟区间（左闭右开，范围 0~1440）。
     * <ul>
     *   <li>非跨天：仅在该日为工作日时返回 [start, end)</li>
     *   <li>跨天：当日晚段 [start, 24:00) + 前一日班次延续的凌晨段 [0, end)</li>
     * </ul>
     * 例：周一 23:00-06:00 → 周一 [1380,1440)，周二 [0,360)（周二凌晨来自周一晚班）
     */
    private static List<int[]> getOccupiedIntervals(int weekNumber, List<Map<String, Object>> weekList,
                                                    String startTime, String endTime, boolean crossDay) {
        Set<Integer> workWeekNumbers = getWorkWeekNumbers(weekList);
        if (workWeekNumbers.isEmpty()) {
            return Collections.emptyList();
        }

        int startMinute = toMinutesOfDay(startTime);
        int endMinute = toMinutesOfDay(endTime);
        List<int[]> intervals = new ArrayList<>();

        if (!crossDay) {
            if (workWeekNumbers.contains(weekNumber)) {
                intervals.add(new int[]{startMinute, endMinute});
            }
            return intervals;
        }

        // 跨天班：当日晚班开始段（仅当日本身为工作日）
        if (workWeekNumbers.contains(weekNumber)) {
            intervals.add(new int[]{startMinute, MINUTES_PER_DAY});
        }
        // 跨天班：凌晨下班段（归属前一工作日开始的班次）
        int prevWeekNumber = prevWeekNumber(weekNumber);
        if (workWeekNumbers.contains(prevWeekNumber)) {
            intervals.add(new int[]{0, endMinute});
        }
        return intervals;
    }

    /**
     * 判断两组分钟区间是否存在重叠（左闭右开）。
     */
    private static boolean intervalsOverlap(List<int[]> left, List<int[]> right) {
        for (int[] l : left) {
            for (int[] r : right) {
                if (l[0] < r[1] && r[0] < l[1]) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 从班次工作日配置中提取需要上班的星期几（排除 type=3 休假） */
    private static Set<Integer> getWorkWeekNumbers(List<Map<String, Object>> weekList) {
        return weekList.stream()
            .filter(StaffCheckWorkTimeOverlapHelper::isWorkWeekEntry)
            .map(entry -> Integer.parseInt(stringVal(entry.get("weekNumber"))))
            .collect(Collectors.toSet());
    }

    /**
     * 是否为有效工作日配置。type=3 表示「不上班/休假」。
     */
    private static boolean isWorkWeekEntry(Map<String, Object> entry) {
        if (entry == null) {
            return false;
        }
        String type = stringVal(entry.get("type"));
        return StrUtil.isNotBlank(type) && !"3".equals(type);
    }

    /**
     * 是否跨天班次：结束时间早于或等于开始时间（如 23:00-06:00）。
     */
    private static boolean isCrossDay(String startTime, String endTime) {
        String start = normalizeToHms(startTime);
        String end = normalizeToHms(endTime);
        if (start.equals(end)) {
            return false;
        }
        return !DateUtil.compareTimeHMS(start, end);
    }

    /** 上一工作日（周一的前一日为周日） */
    private static int prevWeekNumber(int weekNumber) {
        return weekNumber == 1 ? 7 : weekNumber - 1;
    }

    /** 将 HH:mm(:ss) 转为当天 0 点起的分钟数 */
    private static int toMinutesOfDay(String time) {
        String hms = normalizeToHms(time);
        String[] parts = hms.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private static String normalizeToHms(String time) {
        String value = StrUtil.trim(time);
        if (value.length() == 5) {
            return value + ":00";
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> getWeekList(Map<String, Object> timeMation) {
        Object list = timeMation.get("checkWorkTimeWeekList");
        if (list instanceof List) {
            return (List<Map<String, Object>>) list;
        }
        return Collections.emptyList();
    }

    private static String stringVal(Object value) {
        return value == null ? StrUtil.EMPTY : value.toString();
    }
}
