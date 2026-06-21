/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.util;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.worktime.entity.CheckWorkTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 考勤工时计算工具（服务端权威口径）
 * <p>
 * 请假、销假、出差均按「申请时段 ∩ 班次工作时段」计算，支持跨天班次与午休扣除。
 * 与前端 {@code util.js} 中 {@code calcLeaveMinutesInRange} / {@code calcCheckWorkTimeHour} 对齐，
 * 保存/审批时由后端重算，不依赖前端提交值。
 */
public class CheckWorkHourCalcUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private CheckWorkHourCalcUtil() {
    }

    /**
     * 计算标准班次单日工时（分钟），扣除午休。
     * 跨天班次按班次内偏移量扣休息；同日班次按时刻区间扣休息。
     * 与前端 calcCheckWorkTimeHour 对齐，供出差整班场景使用。
     */
    public static long calcStandardWorkMinutes(CheckWorkTime workTime) {
        if (workTime == null || StrUtil.isBlank(workTime.getStartTime()) || StrUtil.isBlank(workTime.getEndTime())) {
            return 0L;
        }
        boolean crossDay = CheckWorkTimePeriodUtil.isCrossDay(workTime.getStartTime(), workTime.getEndTime());
        long workMinutes = CheckWorkTimePeriodUtil.getWorkMinutes(workTime.getStartTime(), workTime.getEndTime());
        if (StrUtil.isNotEmpty(workTime.getRestStartTime()) && StrUtil.isNotEmpty(workTime.getRestEndTime())) {
            if (crossDay) {
                int workDuration = CheckWorkTimePeriodUtil.getWorkMinutes(workTime.getStartTime(), workTime.getEndTime());
                int restStartOffset = CheckWorkTimePeriodUtil.toShiftOffsetMinutesPublic(
                    workTime.getRestStartTime(), workTime.getStartTime(), true);
                int restEndOffset = CheckWorkTimePeriodUtil.toShiftOffsetMinutesPublic(
                    workTime.getRestEndTime(), workTime.getStartTime(), true);
                if (restStartOffset >= 0 && restEndOffset > restStartOffset && restEndOffset <= workDuration) {
                    workMinutes -= (restEndOffset - restStartOffset);
                }
            } else {
                String startTime = normalizeHm(workTime.getStartTime()) + ":00";
                String endTime = normalizeHm(workTime.getEndTime()) + ":00";
                String restStartTime = normalizeHm(workTime.getRestStartTime()) + ":00";
                String restEndTime = normalizeHm(workTime.getRestEndTime()) + ":00";
                if (!DateUtil.compareTimeHMS(restStartTime, endTime) && !DateUtil.compareTimeHMS(startTime, restEndTime)) {
                    long total = ChronoUnit.MINUTES.between(parseLocalTime(workTime.getStartTime()), parseLocalTime(workTime.getEndTime()));
                    long rest = ChronoUnit.MINUTES.between(parseLocalTime(workTime.getRestStartTime()), parseLocalTime(workTime.getRestEndTime()));
                    workMinutes = Math.max(0, total - rest);
                }
            }
        }
        return Math.max(0, workMinutes);
    }

    /**
     * 计算 datetime 区间与班次工作时间的交集分钟数。
     * <p>
     * 跨天班次从 leaveStart 前一天起逐日遍历；仅统计 checkWorkTimeWeekList 中的上班日；
     * 每段工作交集再扣除当班午休重叠部分。
     *
     * @param leaveStart 区间开始（含）
     * @param leaveEnd   区间结束（不含无效，须晚于开始）
     * @param workTime   班次（含上下班、作息、工作日配置）
     */
    public static long calcLeaveMinutesInRange(LocalDateTime leaveStart, LocalDateTime leaveEnd, CheckWorkTime workTime) {
        if (leaveStart == null || leaveEnd == null || workTime == null || !leaveStart.isBefore(leaveEnd)) {
            return 0L;
        }
        boolean crossDay = CheckWorkTimePeriodUtil.isCrossDay(workTime.getStartTime(), workTime.getEndTime());
        LocalTime workStart = parseLocalTime(workTime.getStartTime());
        LocalTime workEnd = parseLocalTime(workTime.getEndTime());
        LocalTime restStart = StrUtil.isNotEmpty(workTime.getRestStartTime()) ? parseLocalTime(workTime.getRestStartTime()) : null;
        LocalTime restEnd = StrUtil.isNotEmpty(workTime.getRestEndTime()) ? parseLocalTime(workTime.getRestEndTime()) : null;
        long total = 0;
        // 跨天班次：归属日可能从前一日晚间开始，循环起点需回退一天
        LocalDate loopStart = crossDay ? leaveStart.toLocalDate().minusDays(1) : leaveStart.toLocalDate();
        LocalDate loopEnd = leaveEnd.toLocalDate();
        for (LocalDate d = loopStart; !d.isAfter(loopEnd); d = d.plusDays(1)) {
            if (!CheckWorkTimeWeekUtil.isWorkDay(d.format(DATE_FMT), workTime.getCheckWorkTimeWeekList())) {
                continue;
            }
            String shiftDate = d.format(DATE_FMT);
            LocalDateTime dayWorkStart = d.atTime(workStart);
            LocalDateTime dayWorkEnd = crossDay
                ? CheckWorkTimePeriodUtil.resolveShiftDateTime(shiftDate, workTime.getEndTime(), workTime.getStartTime(), true)
                : d.atTime(workEnd);
            LocalDateTime overlapStart = leaveStart.isAfter(dayWorkStart) ? leaveStart : dayWorkStart;
            LocalDateTime overlapEnd = leaveEnd.isBefore(dayWorkEnd) ? leaveEnd : dayWorkEnd;
            if (!overlapStart.isBefore(overlapEnd)) {
                continue;
            }
            long mins = ChronoUnit.MINUTES.between(overlapStart, overlapEnd);
            if (restStart != null && restEnd != null) {
                LocalDateTime dayRestStart = crossDay
                    ? CheckWorkTimePeriodUtil.resolveShiftDateTime(shiftDate, workTime.getRestStartTime(), workTime.getStartTime(), true)
                    : d.atTime(restStart);
                LocalDateTime dayRestEnd = crossDay
                    ? CheckWorkTimePeriodUtil.resolveShiftDateTime(shiftDate, workTime.getRestEndTime(), workTime.getStartTime(), true)
                    : d.atTime(restEnd);
                LocalDateTime restOverlapStart = overlapStart.isAfter(dayRestStart) ? overlapStart : dayRestStart;
                LocalDateTime restOverlapEnd = overlapEnd.isBefore(dayRestEnd) ? overlapEnd : dayRestEnd;
                if (restOverlapStart.isBefore(restOverlapEnd)) {
                    mins -= ChronoUnit.MINUTES.between(restOverlapStart, restOverlapEnd);
                }
            }
            total += Math.max(0, mins);
        }
        return total;
    }

    /**
     * 单日 HH:mm 时段与班次的交集分钟数（销假专用）。
     * cancelDay + cancelStartTime/cancelEndTime 组合为当日 datetime 后复用请假算法。
     *
     * @param day       销假日期 yyyy-MM-dd
     * @param startTime 销假开始 HH:mm
     * @param endTime   销假结束 HH:mm（须晚于开始，且在同一自然日内）
     */
    public static long calcDayTimeRangeMinutes(String day, String startTime, String endTime, CheckWorkTime workTime) {
        if (StrUtil.hasBlank(day, startTime, endTime) || workTime == null) {
            return 0L;
        }
        LocalDateTime rangeStart = parseDayTime(day, startTime);
        LocalDateTime rangeEnd = parseDayTime(day, endTime);
        if (rangeStart == null || rangeEnd == null || !rangeStart.isBefore(rangeEnd)) {
            return 0L;
        }
        return calcLeaveMinutesInRange(rangeStart, rangeEnd, workTime);
    }

    /**
     * 出差工时（小时，保留两位小数）。
     * 若出差起止时刻与班次上下班一致，按整班标准工时；否则按 travelDay 当日时段与班次交集。
     */
    public static String calcTravelHour(String travelDay, String startTime, String endTime, CheckWorkTime workTime) {
        if (workTime == null || StrUtil.hasBlank(travelDay, startTime, endTime)) {
            return "0";
        }
        long minutes;
        if (isFullShiftRange(startTime, endTime, workTime)) {
            minutes = calcStandardWorkMinutes(workTime);
        } else {
            minutes = calcDayTimeRangeMinutes(travelDay, startTime, endTime, workTime);
        }
        return CalculationUtil.divide(String.valueOf(minutes), "60", CommonNumConstants.NUM_TWO);
    }

    /**
     * 销假工时（小时，保留两位小数），算法同 {@link #calcDayTimeRangeMinutes}。
     */
    public static String calcCancelHour(String cancelDay, String cancelStartTime, String cancelEndTime, CheckWorkTime workTime) {
        long minutes = calcDayTimeRangeMinutes(cancelDay, cancelStartTime, cancelEndTime, workTime);
        return CalculationUtil.divide(String.valueOf(minutes), "60", CommonNumConstants.NUM_TWO);
    }

    /**
     * 请假工时（小时，保留两位小数）。
     */
    public static String calcLeaveHour(LocalDateTime leaveStart, LocalDateTime leaveEnd, CheckWorkTime workTime) {
        long minutes = calcLeaveMinutesInRange(leaveStart, leaveEnd, workTime);
        return CalculationUtil.divide(String.valueOf(minutes), "60", CommonNumConstants.NUM_TWO);
    }

    /** 出差时段是否与班次上下班完全一致（整班出差） */
    private static boolean isFullShiftRange(String startTime, String endTime, CheckWorkTime workTime) {
        return normalizeHm(workTime.getStartTime()).equals(normalizeHm(startTime))
            && normalizeHm(workTime.getEndTime()).equals(normalizeHm(endTime));
    }

    private static LocalDateTime parseDayTime(String day, String time) {
        if (StrUtil.hasBlank(day, time)) {
            return null;
        }
        String dayStr = day.length() >= 10 ? day.substring(0, 10) : day;
        return DateUtil.parseLeaveDateTime(dayStr + " " + normalizeHm(time));
    }

    private static LocalTime parseLocalTime(String t) {
        if (StrUtil.isEmpty(t)) {
            return LocalTime.MIN;
        }
        String s = normalizeHm(t);
        return LocalTime.parse(s.length() == 5 ? s + ":00" : s);
    }

    private static String normalizeHm(String time) {
        if (StrUtil.isBlank(time)) {
            return time;
        }
        String value = StrUtil.trim(time);
        if (value.length() >= 8) {
            return value.substring(0, 5);
        }
        return value;
    }
}
