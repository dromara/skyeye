/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

/**
 * @ClassName: WorkPlanConstants
 * @Description: 工作计划系统常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/15 21:42
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class WorkPlanConstants {

    // 工作计划-计划周期类型
    public static enum SysWorkPlan {
        DAY_PLAN("day", "1"),
        WEEK_PLAN("week", "2"),
        MONTH_PLAN("month", "3"),
        QUARTER_PLAN("quarter", "4"),
        HALFYEAR_PLAN("halfyear", "5"),
        YEAR_PLAN("year", "6");

        private String nameCode;
        private String num;

        SysWorkPlan(String nameCode, String num) {
            this.nameCode = nameCode;
            this.num = num;
        }

        public static String getClockInState(String nameCode) {
            for (SysWorkPlan q : SysWorkPlan.values()) {
                if (q.getNameCode().equals(nameCode)) {
                    return q.getNum();
                }
            }
            return "";
        }

        public static String getClockInName(String num) {
            for (SysWorkPlan q : SysWorkPlan.values()) {
                if (q.getNum().equals(num)) {
                    return q.getNameCode();
                }
            }
            return "";
        }

        public String getNameCode() {
            return nameCode;
        }

        public String getNum() {
            return num;
        }
    }

}
