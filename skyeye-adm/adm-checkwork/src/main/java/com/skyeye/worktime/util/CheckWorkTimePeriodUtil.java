/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.util;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.util.DateAfterSpacePointTime;
import com.skyeye.common.util.DateUtil;
import com.skyeye.exception.CustomException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 考勤班次时间段工具（含跨天班次）
 */
public class CheckWorkTimePeriodUtil {

    private static final int MINUTES_PER_DAY = 24 * 60;

    private CheckWorkTimePeriodUtil() {
    }

    /**
     * 结束时间早于开始时间视为跨天班次（如 22:00-06:00）
     */
    public static boolean isCrossDay(String startTime, String endTime) {
        if (StrUtil.isBlank(startTime) || StrUtil.isBlank(endTime)) {
            return false;
        }
        String start = normalizeToHms(startTime);
        String end = normalizeToHms(endTime);
        if (start.equals(end)) {
            return false;
        }
        return !DateUtil.compareTimeHMS(start, end);
    }

    /**
     * 班次工作时长（分钟）
     */
    public static int getWorkMinutes(String startTime, String endTime) {
        int startMinute = toMinutesOfDay(startTime);
        int endMinute = toMinutesOfDay(endTime);
        if (!isCrossDay(startTime, endTime)) {
            return endMinute - startMinute;
        }
        return (MINUTES_PER_DAY - startMinute) + endMinute;
    }

    /**
     * 跨天班次凌晨段：当前时间 &lt;= 下班时间（含 endTime 临界值）
     */
    public static boolean isInCrossDayMorningSegment(String nowHms, String shiftEnd) {
        return DateUtil.compareTimeHMS(normalizeToHms(nowHms), normalizeToHms(shiftEnd));
    }

    /**
     * 跨天班次晚间段：当前时间 &gt;= 上班时间（含 startTime 临界值）
     */
    public static boolean isInCrossDayEveningSegment(String nowHms, String shiftStart) {
        return DateUtil.compareTimeHMS(normalizeToHms(shiftStart), normalizeToHms(nowHms));
    }

    /**
     * 解析考勤归属日（calendarDate 为当前自然日 yyyy-MM-dd）
     * <p>
     * 跨天班次凌晨段归属前一自然日；其余时段归属当日。
     */
    public static String resolveCheckDate(String calendarDate, String nowHms, String shiftStart, String shiftEnd, boolean crossDay) {
        if (!crossDay || StrUtil.isBlank(calendarDate)) {
            return calendarDate;
        }
        if (isInCrossDayMorningSegment(nowHms, shiftEnd)) {
            return DateAfterSpacePointTime.getSpecifiedTime(
                DateAfterSpacePointTime.ONE_DAY.getType(),
                calendarDate,
                DateUtil.YYYY_MM_DD,
                DateAfterSpacePointTime.AroundType.BEFORE);
        }
        return calendarDate;
    }

    /**
     * 将班次内时刻转为绝对日期时间（shiftStartDate 为班次开始归属日）
     */
    public static LocalDateTime resolveShiftDateTime(String shiftStartDate, String time, String shiftStart, boolean crossDay) {
        LocalDate baseDate = LocalDate.parse(shiftStartDate);
        if (!crossDay) {
            return LocalDateTime.of(baseDate, parseLocalTime(time));
        }
        int offsetMinutes = toShiftOffsetMinutes(time, shiftStart, true);
        return LocalDateTime.of(baseDate, parseLocalTime(shiftStart)).plusMinutes(offsetMinutes);
    }

    /**
     * 当前是否可打上班卡（跨天仅晚间段；同日为 now &lt;= endTime）
     */
    public static boolean canClockInNow(String nowHms, String clockIn, String clockOut, boolean crossDay) {
        if (!crossDay) {
            return DateUtil.compareTimeHMS(normalizeToHms(nowHms), normalizeToHms(clockOut));
        }
        return isInCrossDayEveningSegment(nowHms, clockIn);
    }

    /**
     * 当前是否可打下班卡（跨天：仅凌晨段至 endTime；同日不限）
     */
    public static boolean canClockOutNow(String nowHms, String clockIn, String clockOut, boolean crossDay) {
        if (!crossDay) {
            return true;
        }
        return isInCrossDayMorningSegment(nowHms, clockOut);
    }

    /**
     * 下班打卡是否早于或等于规定下班时间（早退）
     */
    public static boolean isEarlyLeave(String clockOut, String shiftEnd, String shiftStart, boolean crossDay) {
        if (!crossDay) {
            return DateUtil.compareTimeHMS(clockOut, shiftEnd);
        }
        return toShiftOffsetMinutes(clockOut, shiftStart, true) < getWorkMinutes(shiftStart, shiftEnd);
    }

    /**
     * 获取班次标准工时 HH:mm:ss 时长描述（供工时比较）
     */
    public static String getWorkDistanceHms(String startTime, String endTime) {
        int minutes = getWorkMinutes(startTime, endTime);
        int hour = minutes / 60;
        int minute = minutes % 60;
        return hour + ":" + minute + ":0";
    }

    /**
     * 校验作息是否落在工作时间范围内
     */
    public static void validateRestInWorkRange(String startTime, String endTime, String restStartTime, String restEndTime) {
        boolean crossDay = isCrossDay(startTime, endTime);
        if (!crossDay) {
            if (!DateUtil.compareTimeHMS(restStartTime + ":00", restEndTime + ":00")) {
                throw new CustomException("作息开始时间必须早于结束时间。");
            }
            if (!DateUtil.compareTimeHMS(startTime + ":00", restStartTime + ":00")
                || !DateUtil.compareTimeHMS(restEndTime + ":00", endTime + ":00")) {
                throw new CustomException("作息时间必须在工作时间范围内。");
            }
            return;
        }
        int workDuration = getWorkMinutes(startTime, endTime);
        int restStartOffset = toShiftOffsetMinutes(restStartTime, startTime, true);
        int restEndOffset = toShiftOffsetMinutes(restEndTime, startTime, true);
        if (restStartOffset < 0 || restEndOffset < 0 || restStartOffset >= workDuration || restEndOffset > workDuration) {
            throw new CustomException("作息时间必须在工作时间范围内。");
        }
        if (restStartOffset >= restEndOffset) {
            throw new CustomException("作息开始时间必须早于结束时间。");
        }
    }

    /**
     * 跨天班次是否已过结算时点（班次开始日 checkDate 的次日 endTime 之后）
     * 供定时任务在班次真正结束后再补缺卡/旷工
     */
    public static boolean isCrossDaySettleReady(String checkDate, String shiftEndTime) {
        if (StrUtil.isBlank(checkDate) || StrUtil.isBlank(shiftEndTime)) {
            return false;
        }
        String shiftEndDate = DateAfterSpacePointTime.getSpecifiedTime(
            DateAfterSpacePointTime.ONE_DAY.getType(),
            checkDate,
            DateUtil.YYYY_MM_DD,
            DateAfterSpacePointTime.AroundType.AFTER);
        String today = DateUtil.getYmdTimeAndToString();
        if (today.compareTo(shiftEndDate) > 0) {
            return true;
        }
        if (today.compareTo(shiftEndDate) < 0) {
            return false;
        }
        return !DateUtil.compareTimeHMS(DateUtil.getHmsTimeAndToString(), normalizeToHms(shiftEndTime));
    }

    private static int toShiftOffsetMinutes(String time, String shiftStart, boolean crossDay) {
        int minute = toMinutesOfDay(time);
        int startMinute = toMinutesOfDay(shiftStart);
        if (!crossDay) {
            return minute - startMinute;
        }
        if (minute >= startMinute) {
            return minute - startMinute;
        }
        return (MINUTES_PER_DAY - startMinute) + minute;
    }

    /**
     * 班次内时刻相对上班时刻的偏移分钟数（供工时计算等模块复用）
     */
    public static int toShiftOffsetMinutesPublic(String time, String shiftStart, boolean crossDay) {
        return toShiftOffsetMinutes(time, shiftStart, crossDay);
    }

    /** 将 HH:mm(:ss) 解析为 LocalTime */
    private static LocalTime parseLocalTime(String time) {
        String hms = normalizeToHms(time);
        String[] parts = hms.split(":");
        int second = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        return LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), second);
    }

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
}
