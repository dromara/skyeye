/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

/**
 *
 * @ClassName: QuartzConstants
 * @Description: 定时任务常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 22:01
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class QuartzConstants {

    public static enum QuartzMateMationJobType {
        END_SURVEY_MATION("endSurveyMation", "问卷调查", "endSurveyMationService", "问卷调查 - "),
        MY_SCHEDULEDAY_MATION("myScheduleDayMation", "我的日程提醒", "myScheduleDayMationService", "我的行程 - "),
        ALL_SCHEDULE_MATION("allScheduleMation", "通知所有人日程提醒", "allScheduleMationService", ""),
        PLAN_QUARTZ_GROUPS("mySysWorkMation", "通知所有人工作计划提醒", "mySysWorkMationService", "工作计划 - "),
        QUARTZ_NOTICE_GROUP_STR("sendSysNoticeMation", "公告在定时任务中的组id", "quartzNoticeMationService", "内部公告 - ");

        private String taskType;
        private String title;
        private String serviceName;
        private String remarkPrefix;

        QuartzMateMationJobType(String taskType, String title, String serviceName, String remarkPrefix){
            this.taskType = taskType;
            this.title = title;
            this.serviceName = serviceName;
            this.remarkPrefix = remarkPrefix;
        }

        public static String getServiceNameByTaskType(String taskType){
            for (QuartzMateMationJobType bean : QuartzMateMationJobType.values()){
                if(bean.getTaskType().equals(taskType)){
                    return bean.getServiceName();
                }
            }
            return "";
        }

        public static String getRemarkPrefixByTaskType(String taskType, String title){
            for (QuartzMateMationJobType bean : QuartzMateMationJobType.values()){
                if(bean.getTaskType().equals(taskType)){
                    return String.format("%s【%s】", bean.getRemarkPrefix(), title);
                }
            }
            return "";
        }

        public String getRemarkPrefix() {
            return remarkPrefix;
        }

        public String getTaskType() {
            return taskType;
        }

        public String getTitle() {
            return title;
        }

        public String getServiceName() {
            return serviceName;
        }
    }

    public static enum SysQuartzMateMationJobType {
        CHECK_WORK_QUARTZ("定时器填充打卡信息", "00000000000000000000000000000000"),
        HOT_FORUM_QUARTZ("定时器计算每日热门贴", "11111111111111111111111111111111"),
        MONITOR_THREAD_QUARTZ("定时器获取系统的实时信息", "22222222222222222222222222222222"),
        STAFF_WAGES_QUARTZ("定时器统计上个月员工的薪资情况", "33333333333333333333333333333333"),
        ANNUAL_LEAVE_STATISTICS_QUARTZ("定时器计算员工年假", "44444444444444444444444444444444"),
        STAFF_WAGES_PAYMENT_QUARTZ("定时发放薪资功能", "55555555555555555555555555555555  "),
        TEMPORARY_FILE_DELETE_QUARTZ("定时删除临时的云压缩文件", "66666666666666666666666666666666"),
        CALC_STAFF_WAIT_PAY_WAGES_QUARTZ("定时统计员工待结算薪资的数据", "77777777777777777777777777777777");

        private String name;
        private String quartzId;


        SysQuartzMateMationJobType(String name, String quartzId) {
            this.name = name;
            this.quartzId = quartzId;
        }

        public String getName() {
            return name;
        }

        public String getQuartzId() {
            return quartzId;
        }
    }

}
