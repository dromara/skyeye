/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: DateUtil
 * @Description: 日期工具类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class DateUtil {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYYMMDD = "yyyyMMdd";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String HH_MM_SS = "HH:mm:ss";

    public static final String HH_MM = "HH:mm";

    public static String getTimeAndToString() {
        return getPointTime(YYYY_MM_DD_HH_MM_SS);
    }

    public static String getTimeIsYMD() {
        return getPointTime(YYYYMMDD);
    }

    public static String getTimeStrAndToString() {
        return getPointTime(YYYYMMDDHHMMSSSSS);
    }

    public static String getYmdTimeAndToString() {
        return getPointTime(YYYY_MM_DD);
    }

    public static String getHmsTimeAndToString() {
        return getPointTime(HH_MM_SS);
    }

    /**
     * 获取两个日期之间的所有日期(包含最后一天)
     *
     * @param startTime 开始日期
     * @param endTime 结束日期
     * @return 所有日期(包含最后一天)
     */
    public static List<String> getDays(String startTime, String endTime) {
        // 返回的日期集合
        List<String> days = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(getPointTime(startTime, YYYY_MM_DD));

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(getPointTime(endTime, YYYY_MM_DD));
            // 日期加1(包含结束)
            tempEnd.add(Calendar.DATE, +1);
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 将日期转化为正常的年月日时分秒
     *
     * @param timeStmp
     * @return
     */
    public static String getDateStr(long timeStmp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return dateFormat.format(new Date(timeStmp));
    }

    /**
     * 转化cron
     *
     * @param date date
     * @param foramt foramt
     * @return
     */
    public static int getTime(Date date, String foramt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if ("y".equals(foramt)) {
            return cal.get(Calendar.YEAR);// 获取年份
        } else if ("M".equals(foramt)) {
            return cal.get(Calendar.MONTH) + 1;// 获取月
        } else if ("d".equals(foramt)) {
            return cal.get(Calendar.DAY_OF_MONTH);// 获取日
        } else if ("H".equals(foramt)) {
            return cal.get(Calendar.HOUR_OF_DAY);// 获取时
        } else if ("m".equals(foramt)) {
            return cal.get(Calendar.MINUTE);// 获取分
        } else if ("s".equals(foramt)) {
            return cal.get(Calendar.SECOND);// 获取秒
        } else {
            return -1;
        }
    }

    /**
     * 获取指定日期 前/后n天|分钟的日期
     *
     * @param date 日期
     * @param n N天/分钟
     * @param format 标识：d代表天，m代表分钟
     * @return
     */
    public static Date getAfDate(Date date, int n, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if ("d".equals(format)) {
            calendar.add(Calendar.DAY_OF_MONTH, n);// 天
        } else if ("m".equals(format)) {
            calendar.add(Calendar.MINUTE, n);// 分钟
        }
        date = calendar.getTime();
        return date;
    }

    /**
     * 计算两个日期相差多少天
     *
     * @param startDate 开始日期 yyyy-MM-dd/yyyy-MM-dd HH:mm:ss
     * @param endDate 结束日期 yyyy-MM-dd/yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static int getDistanceDay(String startDate, String endDate) throws ParseException{
        Calendar cal = Calendar.getInstance();
        // 开始日期
        cal.setTime(getPointTime(startDate, YYYY_MM_DD));
        long time1 = cal.getTimeInMillis();
        // 结束日期
        cal.setTime(getPointTime(endDate, YYYY_MM_DD));
        long time2 = cal.getTimeInMillis();
        // 计算
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 两个时间之间相差距离多少分
     *
     * @param str1 时间参数 1，格式：yyyy-MM-dd HH:mm:ss：
     * @param str2 时间参数 2，格式：yyyy-MM-dd HH:mm:ss：
     * @return 相差天数
     */
    public static long getDistanceMinute(String str1, String str2) throws Exception {
        long min = 0;
        try {
            Date one = getPointTime(str1, YYYY_MM_DD_HH_MM_SS);
            Date two = getPointTime(str2, YYYY_MM_DD_HH_MM_SS);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            min = diff / (1000 * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return min;
    }

    /**
     * 两个时间之间相差距离多少时分秒
     *
     * @param str1 时间参数 1：
     * @param str2 时间参数 2：
     * @return 相差时分秒数
     */
    public static String getDistanceHMS(String str1, String str2) throws Exception {
        long diff = getDistanceMillisecondHMS(str1, str2);
        long hour = diff / (1000 * 60 * 60);
        long minute = (diff - hour * 60 * 60 * 1000) / (1000 * 60);
        long second = (diff - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;
        return hour + ":" + minute + ":" + second;
    }

    /**
     * 两个时间之间相差距离多少分钟
     *
     * @param str1 时间参数 1，格式：HH:mm:ss
     * @param str2 时间参数 2，格式：HH:mm:ss
     * @return 相差分钟
     * @throws Exception
     */
    public static String getDistanceMinuteByHMS(String str1, String str2) throws Exception {
        long diff = getDistanceMillisecondHMS(str1, str2);
        return CalculationUtil.divide(
                String.valueOf(diff),
                CalculationUtil.multiply("1000", "60", 2), 2);
    }

    /**
     * 两个时间之间相差距离多少毫秒
     *
     * @param str1 时间参数 1：
     * @param str2 时间参数 2：
     * @return 相差毫秒
     * @throws Exception
     */
    public static long getDistanceMillisecondHMS(String str1, String str2) throws Exception {
        try {
            Date one = getPointTime(str1, HH_MM_SS);
            Date two = getPointTime(str2, HH_MM_SS);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            return diff;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 时间字符串比较大小，字符串格式为：yyyy-MM-dd hh:mm:ss
     *
     * @param time1 日期1
     * @param time2 日期2
     * @return 日期1早于日期2，返回true,反之返回false
     * @throws ParseException
     */
    public static boolean compare(String time1, String time2) throws ParseException {
        return compareTime(time1, time2, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 时间字符串比较大小，字符串格式为：HH:mm
     *
     * @param time1 日期1
     * @param time2 日期2
     * @return 日期1早于日期2，返回true,反之返回false
     * @throws ParseException
     */
    public static boolean compareTime(String time1, String time2) throws ParseException {
        return compareTime(time1, time2, HH_MM);
    }

    /**
     * 时分秒字符串比较大小，字符串格式为：HH:mm:ss
     *
     * @param time1 日期1
     * @param time2 日期2
     * @return 日期1早于日期2，返回true,反之返回false
     * @throws ParseException
     */
    public static boolean compareTimeHMS(String time1, String time2) throws ParseException {
        return compareTime(time1, time2, HH_MM_SS);
    }

    public static boolean compareTime(String time1, String time2, String pattern) throws ParseException {
        Date t1 = getPointTime(time1, pattern);
        Date t2 = getPointTime(time2, pattern);
        // Date类的一个方法，如果t1早于t2返回true，否则返回false
        if (!t1.after(t2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 比较时分时间段是否有重复
     *
     * @param list ->[08:00-09:00,13:00-16:30]
     * @return true:有重复；false:没有重复
     * @throws ParseException
     */
    public static boolean checkOverlap(List<String> list) throws ParseException {
        // 排序ASC
        Collections.sort(list);
        // 是否重叠标识
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            // 跳过第一个时间段不做判断
            String[] itime = list.get(i).split("-");
            for (int j = (i + 1); j < list.size(); j++) {
                // 如果当前遍历的i开始时间小于j中某个时间段的结束时间那么则有重叠，反之没有重叠
                String[] jtime = list.get(j).split("-");
                // 此处compare为日期比较(返回true:date1小/相等、返回false:date1大)
                boolean compare = compare((DateUtil.getYmdTimeAndToString() + " " + itime[1] + ":00"),
                        (DateUtil.getYmdTimeAndToString() + " " + jtime[0] + ":00"));
                if (!compare) {
                    flag = true;
                    // 只要存在一个重叠则可退出内循环
                    break;
                }
            }
            // 当标识已经认为重叠了则可退出外循环
            if (flag) {
                break;
            }
        }
        return flag;
    }

    /**
     * 获取上个月的年月，格式yyyy-MM
     *
     * @return
     */
    public static String getLastMonthDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        return sdf.format(cal.getTime());
    }

    /**
     * 获取某年某月的所有日期（yyyy-mm-dd格式字符串）
     *
     * @param year 年
     * @param month 月
     * @return 所有日期
     */
    public static List<String> getMonthFullDay(int year, int month) {
        SimpleDateFormat dateFormatYYYYMMDD = new SimpleDateFormat(YYYY_MM_DD);
        List<String> fullDayList = new ArrayList<>(32);
        // 获得当前日期对象
        Calendar cal = Calendar.getInstance();
        // 清除信息
        cal.clear();
        cal.set(Calendar.YEAR, year);
        // 1月从0开始
        cal.set(Calendar.MONTH, month - 1);
        // 当月1号
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int count = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int j = 1; j <= count; j++) {
            fullDayList.add(dateFormatYYYYMMDD.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return fullDayList;
    }

    /**
     * 获取指定月后N个月的月份日期(包含指定月)
     *
     * @param yearMonth 指定月，格式为yyyy-MM
     * @return List<String>
     * @throws Exception
     */
    public static List<String> getPointMonthAfterMonthList(String yearMonth, int n) throws Exception {
        List<String> list = new ArrayList<>();
        list.add(yearMonth);
        for(int i = 1; i <= n; i++){
            // 获取往后的日期
            list.add(getSpecifiedDayMation(yearMonth, YYYY_MM, 1, i, 10));
        }
        return list;
    }

    /**
     * 判断指定日期（yyyy-MM-dd）是周几
     *
     * @param dates 指定日期
     * @return 周几
     * @throws ParseException
     */
    public static int getWeek(String dates) throws ParseException {
        Calendar cal = Calendar.getInstance();
        Date d = getPointTime(dates, YYYY_MM_DD);
        cal.setTime(d);
        int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekDay == 0){
            weekDay = 7;
        }
        return weekDay;
    }

    /**
     * 判断指定日期（yyyy-MM-dd）是单周还是双周
     *
     * @param dates 指定日期
     * @return 1是双周，0是单周
     */
    public static int getWeekType(String dates) throws ParseException {
        Calendar cal = Calendar.getInstance();
        Date d = getPointTime(dates, YYYY_MM_DD);
        cal.setTime(d);
        int weekNum = cal.get(Calendar.WEEK_OF_YEAR);
        if(weekNum % 2 == 0){
            // 双周
            return 1;
        }else{
            // 单周
            return 0;
        }
    }

    /**
     * 获取指定月份的所有日期
     *
     * @param months 月份集合，格式为yyyy-MM
     * @return
     */
    public static List<String> getDaysByMonths(List<String> months){
        List<String> monthDays = new ArrayList<>();
        for (String month: months){
            monthDays.addAll(DateUtil.getMonthFullDay(Integer.parseInt(month.split("-")[0]), Integer.parseInt(month.split("-")[1])));
        }
        return monthDays;
    }

    /**
     * 获取当前时间戳
     * @return
     */
    public static long getTimeStampAndToString() {
        return System.currentTimeMillis();
    }

    /**
     * 根据指定时间获取各种时间之后的时间
     *
     * @param dateStr 指定时间
     * @param dateType 时间格式：yyyy-MM-dd hh:mm:ss
     * @param beforeOrAfter 往前计算还是往后计算 0:时间往前推;1：时间往后推
     * @param num 时间长度
     * @param remindType 往前计算或者往后计算是按照天还是小时还是分钟计算[1,2,3,4]:分钟;[5,6]:小时;[7,8,9]:天;[10]:月
     * @return 计算后的日期
     * @throws Exception
     */
    public static String getSpecifiedDayMation(String dateStr, String dateType, int beforeOrAfter, int num, int remindType) throws Exception{
        Calendar c = Calendar.getInstance();
        c.setTime(getPointTime(dateStr, dateType));
        if(beforeOrAfter == 0)
            num = -num;
        if(remindType == 1 || remindType == 2 || remindType == 3 || remindType == 4){
            // 分钟
            c.add(Calendar.MINUTE, num);
        }else if(remindType == 5 || remindType == 6){
            // 小时
            c.add(Calendar.HOUR_OF_DAY, num);
        }else if(remindType == 7 || remindType == 8 || remindType == 9){
            // 天
            c.add(Calendar.DAY_OF_MONTH, num);
        }else if(remindType == 10){
            // 月
            c.add(Calendar.MONTH, num);
        }else{
            return null;
        }
        String dayAfter = new SimpleDateFormat(dateType).format(c.getTime());
        return dayAfter;
    }

    /**
     * 获取指定格式的日期字符串
     *
     * @param pattern 格式
     * @return 日期字符串
     */
    public static String getPointTime(String pattern){
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat(pattern);
        String nowTime = df.format(dt);
        return nowTime;
    }

    /**
     * 获取指定格式以及指定日期对象
     * @param time 指定日期
     * @param pattern 指定格式
     * @return 日期对象
     * @throws ParseException
     */
    public static Date getPointTime(String time, String pattern) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.parse(time);
    }

}
