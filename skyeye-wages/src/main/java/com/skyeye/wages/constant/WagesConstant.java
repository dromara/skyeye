package com.skyeye.wages.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WagesConstant {

    /**
     * 系统默认的薪资字段
     */
    public static enum DEFAULT_WAGES_FIELD_TYPE {
        LAST_MONTH_BE_NUM("应出勤(班)", "Due attendance (shift)", "lastMonthBeNum", "上个月应出勤的班次数"),
        LAST_MONTH_BE_HOUR("应出勤(小时)", "Due attendance (hours)", "lastMonthBeHour", "上个月应出勤的小时数"),
        LAST_MONTH_REAL_NUM("实际出勤(班)", "Actual attendance (shift)", "lastMonthRealNum", "上个月实际出勤的班次数"),
        LAST_MONTH_REAL_HOUR("实际出勤(小时)", "Actual attendance (hours)", "lastMonthRealHour", "上个月实际出勤的小时数"),
        LAST_MONTH_BE_REAL_HOUR("应实际出勤(小时)", "Should actual attendance (hours)", "lastMonthBeRealHour", "上个月应该实际出勤的小时数"),
        LAST_MONTH_LATE_NUM("迟到(次)", "Late (Times)", "lastMonthLateNum", "上个月迟到的次数"),
        LAST_MONTH_EARLY_NUM("早退(次)", "Leave early (Times)", "lastMonthEarlyNum", "上个月早退的次数"),
        LAST_MONTH_DUTY_NUM("缺勤(次)", "Absence (Times)", "lastMonthDutyNum", "上个月缺勤的次数"),
        LAST_MONTH_HOLIDAY_HOUR("请假(小时)", "Leave (hours)", "lastMonthHolidayHour", "上个月请假的小时"),
        ACCUMULATED_ANNUAL_LEAVE("累积年假(小时)", "Accumulated annual leave (hours)", "accumulatedAnnualLeave", "截至到目前为止累积的年假"),
        MONTHLY_STANDARD_SALARY("月标准薪资", "Monthly standard salary", "monthlyStandardSalary", "员工的月标准薪资"),
        MONTHLY_STANDARD_REAL_MONEY("实发薪资", "Actual salary", "monthlyStandardRealMoney", "员工上月实发薪资"),
        MONTHLY_SOCIAL_SECURITY_FUND_INSURANCE("缴纳社保", "Social security payment", "monthlySocialSecurityFundInsurance", "员工上月缴纳社保"),
        MONTHLY_SOCIAL_SECURITY_FUND_ACCUMULATION("缴纳公积金", "Payment of provident fund", "monthlySocialSecurityFundAccumulation", "员工上月缴纳公积金"),
        LAST_MONTH_TAX_RATE_BY_PERSON("缴纳税额", "Tax payment", "lastMonthTaxRateByPerson", "员工上月缴纳的税额");

        private String cnName;
        private String enName;
        private String key;
        private String desc;

        /**
         *
         * @param cnName
         * @param key 薪资字段key
         * @param desc 描述
         */
        DEFAULT_WAGES_FIELD_TYPE(String cnName, String enName, String key, String desc){
            this.cnName = cnName;
            this.enName = enName;
            this.key = key;
            this.desc = desc;
        }

        public static List<Map<String, Object>> getList(){
            List<Map<String, Object>> beans = new ArrayList<>();
            Map<String, Object> bean;
            for (DEFAULT_WAGES_FIELD_TYPE q : DEFAULT_WAGES_FIELD_TYPE.values()){
                bean = new HashMap<>();
                bean.put("nameCn", q.getCnName());
                bean.put("nameEn", q.getEnName());
                bean.put("key", q.getKey());
                bean.put("desc", q.getDesc());
                beans.add(bean);
            }
            return beans;
        }

        public static String getNameByKey(String key){
            for (DEFAULT_WAGES_FIELD_TYPE q : DEFAULT_WAGES_FIELD_TYPE.values()){
                if(key.equals(q.getKey())){
                    return q.getCnName();
                }
            }
            return "";
        }

        public String getCnName() {
            return cnName;
        }

        public String getEnName() {
            return enName;
        }

        public String getKey() {
            return key;
        }

        public String getDesc() {
            return desc;
        }
    }

}
