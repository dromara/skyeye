/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

import com.skyeye.common.util.ToolUtil;
import io.netty.util.internal.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: CheckWorkConstants
 * @Description: 考勤相关的常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/24 11:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public class CheckWorkConstants {

    /**
     * 考勤状态
     */
    public static enum ClockState {
        Start("早卡", "0"),
        Normal("全勤", "1"),
        Absence("缺勤", "2"),
        Insufficient("工时不足", "3"),
        Notstart("缺早卡", "4"),
        Notend("缺晚卡", "5");

        private String name;
        private String state;

        ClockState(String name, String state) {
            this.name = name;
            this.state = state;
        }

        public static String getClockState(String str) {
            for (ClockState q : ClockState.values()) {
                if (q.getState().equals(str)) {
                    return q.getName();
                }
            }
            return "";
        }

        public String getName() {
            return name;
        }

        public String getState() {
            return state;
        }
    }

    /**
     * 上班打卡状态
     */
    public static enum ClockInTime {
        System("系统填充", "0"),
        Normal("正常", "1"),
        Late("迟到", "2"),
        Notclock("未打卡", "3");

        private String name;
        private String state;

        ClockInTime(String name, String state) {
            this.name = name;
            this.state = state;
        }

        public static String getClockInState(String str) {
            for (ClockInTime q : ClockInTime.values()) {
                if (q.getState().equals(str)) {
                    return q.getName();
                }
            }
            return "";
        }

        public String getName() {
            return name;
        }

        public String getState() {
            return state;
        }
    }

    /**
     * 下班打卡状态
     */
    public static enum ClockOutTime {
        System("系统填充", "0"),
        Normal("正常", "1"),
        Leaveearly("早退", "2"),
        Notclock("未打卡", "3");

        private String name;
        private String state;

        ClockOutTime(String name, String state) {
            this.name = name;
            this.state = state;
        }

        public static String getClockOutState(String str) {
            for (ClockOutTime q : ClockOutTime.values()) {
                if (q.getState().equals(str)) {
                    return q.getName();
                }
            }
            return "";
        }

        public String getName() {
            return name;
        }

        public String getState() {
            return state;
        }
    }

    /**
     * 日程插件上的类型(包含日程的)
     */
    public static enum CheckDayType{
        DAY_IS_PERSONAL(1, "个人", StringUtil.EMPTY_STRING),
        DAY_IS_WORK(2, "工作", StringUtil.EMPTY_STRING),
        DAY_IS_HOLIDAY(3, "节假日", "xiu"),
        DAY_IS_BIRTHDAY(4, "生日", StringUtil.EMPTY_STRING),
        DAY_IS_CUSTOM(5, "自定义", StringUtil.EMPTY_STRING),
        DAY_IS_WORKING(6, "工作日", "work-time"),
        DAY_IS_WORK_OVERTIME(7, "加班", "work-overtime"),
        DAY_IS_LEAVE(8, "请假", "leave"),
        DAY_IS_BUSINESS_TRAVEL(9, "出差", "business-travel");

        private int type;
        private String name;
        private String className;

        CheckDayType(int type, String name, String className){
            this.type = type;
            this.name = name;
            this.className = className;
        }
        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getClassName() {
            return className;
        }
    }

    /**
     * 构造上班日的对象数据
     *
     * @param day 指定日期
     * @return
     */
    public static Map<String, Object> structureWorkMation(String day){
        Map<String, Object> mation = new HashMap<>();
        mation.put("title", "班");
        mation.put("start", day + " 00:00:00");
        mation.put("end", day + " 23:59:59");
        mation.put("backgroundColor", "");
        mation.put("allDay", "1");
        mation.put("showBg", "2");
        mation.put("editable", false);
        mation.put("type", CheckDayType.DAY_IS_WORKING.getType());
        mation.put("className", CheckDayType.DAY_IS_WORKING.getClassName());
        return mation;
    }

    /**
     * 构造星期天休息日的对象数据
     *
     * @param day 指定日期
     * @param title 标题
     * @return
     */
    public static Map<String, Object> structureRestMation(String day, String title){
        Map<String, Object> mation = new HashMap<>();
        mation.put("title", ToolUtil.isBlank(title) ? "休" : title);
        mation.put("start", day + " 00:00:00");
        mation.put("end", day + " 23:59:59");
        mation.put("backgroundColor", "#54FF9F");
        mation.put("allDay", "1");
        mation.put("showBg", "2");
        mation.put("editable", false);
        mation.put("type", CheckDayType.DAY_IS_HOLIDAY.getType());
        mation.put("className", CheckDayType.DAY_IS_HOLIDAY.getClassName());
        return mation;
    }

}
