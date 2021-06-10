/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */

package com.skyeye.quartz.config;

import lombok.extern.slf4j.Slf4j;

import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.skyeye.eve.dao.SysQuartzDao;
import com.skyeye.quartz.entity.SysQuartz;

import java.util.Date;

/**
 *
 * @ClassName: QuartzService
 * @Description: 定时任务操作类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/10 20:36
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
public class QuartzService {
	
    @Autowired
    private SysQuartzDao sysQuartzDao;

    @Autowired @Qualifier(value = "SchedulerFactory") //红线运行无错,貌似是idea的问题 ,可忽略
    private SchedulerFactoryBean schedulerFactoryBean;

    /**
     * 
         * @Title: addJob
         * @Description: 增/改任务 - 名称+分组 不能重复
         * @param @param sysQuartz
         * @param @throws SchedulerException    参数
         * @return void    返回类型
         * @throws
     */
    public void addJob(SysQuartz sysQuartz) throws SchedulerException {
    	sysQuartzDao.insert(sysQuartz);
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(sysQuartz.getName(), sysQuartz.getGroups());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        //不存在，创建一个
        if (null == trigger) {
            JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class).withIdentity(sysQuartz.getName(), sysQuartz.getGroups()).build();
            jobDetail.getJobDataMap().put("scheduleJob", sysQuartz);
            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(sysQuartz.getCron());
            //按新的cronExpression表达式构建一个新的trigger
            trigger = TriggerBuilder.newTrigger().withIdentity(sysQuartz.getName(),sysQuartz.getGroups()).withSchedule(scheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            // Trigger已存在，那么更新相应的定时设置
            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(sysQuartz.getCron());
            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            //按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        }
    }

    /**
     * 
         * @Title: delete
         * @Description: 删除任务
         * @param @param sysQuartz
         * @param @throws SchedulerException    参数
         * @return void    返回类型
         * @throws
     */
    public void delete(SysQuartz sysQuartz) throws SchedulerException{
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(sysQuartz.getName(), sysQuartz.getGroups());
        scheduler.deleteJob(jobKey);
    }

    /**
     * 
         * @Title: checkCron
         * @Description: 检查cron表达式是否是可执行的。
         * @param @param cron
         * @param @return    参数
         * @return boolean    返回类型
         * @throws
     */
    public boolean checkCron(String cron){
        CronTriggerImpl trigger = new CronTriggerImpl();
        try {
            trigger.setCronExpression(cron);
            Date date = trigger.computeFirstFireTime(null);
            return date != null && date.after(new Date());
        } catch (Exception e) {
            log.error("[TaskUtils.isValidExpression]:failed. throw ex:" , e);
        }
        return false;
    }
}
