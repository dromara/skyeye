/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.quartz.config;

import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysQuartzDao;
import com.skyeye.eve.entity.quartz.SysQuartz;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class QuartzService {
	
    @Autowired
    private SysQuartzDao sysQuartzDao;

    @Autowired
    @Qualifier(value = "SchedulerFactory")
    private SchedulerFactoryBean schedulerFactoryBean;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzService.class);

    @Value("${quartz.ip}")
    private String quartzIp;

    @Value("${quartz.port}")
    private String quartzPort;

    /**
     * 启动定时任务
     *
     * @param name 任务唯一值
     * @param title 标题
     * @param delayedTime 启动时间
     * @param userId 创建人id
     * @param groupId 组id
     * @throws Exception
     */
    public void startUpTaskQuartz(String name, String title, String delayedTime, String userId, String groupId) throws Exception {
        SysQuartz sysQuartz = new SysQuartz();
        sysQuartz.setId(ToolUtil.getSurFaceId());
        sysQuartz.setName(name);
        sysQuartz.setRemark(QuartzConstants.QuartzMateMationJobType.getRemarkPrefixByTaskType(groupId, title));
        sysQuartz.setGroups(groupId);
        sysQuartz.setCron(ToolUtil.getCrons1(delayedTime));
        sysQuartz.setQuartzIp(quartzIp);
        sysQuartz.setQuartzPort(quartzPort);
        sysQuartz.setCreateId(userId);
        sysQuartz.setCreateTime(DateUtil.getTimeAndToString());
        sysQuartz.setQuartzType("1");
        addJob(sysQuartz);
    }

    /**
     * 停止并删除定时任务
     *
     * @param name 任务唯一值
     * @param groupId 组id
     */
    public void stopAndDeleteTaskQuartz(String name, String groupId) throws SchedulerException {
        SysQuartz sysQuartz = new SysQuartz();
        sysQuartz.setName(name);
        sysQuartz.setGroups(groupId);
        // 删除任务
        delete(sysQuartz);
        // 删除表数据
        sysQuartzDao.deleteByName(sysQuartz.getName());
    }

    /**
     * 
         * @Title: addJob
         * @Description: 增/改任务 - 名称+分组 不能重复
         * @param sysQuartz
         * @throws SchedulerException    参数
         * @return void    返回类型
         * @throws
     */
    public void addJob(SysQuartz sysQuartz) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(sysQuartz.getName(), sysQuartz.getGroups());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        // 不存在，创建一个
        if (null == trigger) {
            LOGGER.info("start quartz task, id is: {}, corn is: {}", sysQuartz.getId(), sysQuartz.getCron());
            sysQuartzDao.insert(sysQuartz);
            JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class).withIdentity(sysQuartz.getName(), sysQuartz.getGroups()).build();
            jobDetail.getJobDataMap().put("scheduleJob", sysQuartz);
            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(sysQuartz.getCron());
            //按新的cronExpression表达式构建一个新的trigger
            trigger = TriggerBuilder.newTrigger().withIdentity(sysQuartz.getName(),sysQuartz.getGroups()).withSchedule(scheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            LOGGER.info("update quartz task, id is: {}, corn is: {}", sysQuartz.getId(), sysQuartz.getCron());
            sysQuartzDao.updateByName(sysQuartz);
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
     * 启动任务
     * @param sysQuartz
     * @throws SchedulerException
     */
    public void startJob(SysQuartz sysQuartz) throws SchedulerException {
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
         * @param sysQuartz
         * @throws SchedulerException    参数
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
         * @param cron
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
