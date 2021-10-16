/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.quartz.config;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.SysQuartzDao;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.quartz.consumer.TaskMateService;
import com.skyeye.eve.entity.quartz.SysQuartz;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @ClassName: QuartzJobFactory
 * @Description: 定时任务分发工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 22:04
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
@DisallowConcurrentExecution
public class QuartzJobFactory implements Job {

	@Autowired
	private QuartzService quartzService;

	@Autowired
	private SysQuartzDao sysQuartzDao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzJobFactory.class);
	
	/**
	 * 任务监听
	 */
	@Override
	public void execute(JobExecutionContext context){
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		Object o = jobDataMap.get("scheduleJob");
		if(o instanceof SysQuartz){
			SysQuartz sysQuartz = (SysQuartz)o;
			try {
				handleJob(sysQuartz);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	     * @Title: handleJob
	     * @Description: 任务分配
	     * @param sysQuartz
	     * @throws SchedulerException    参数
	     * @return void    返回类型
	     * @throws
	 */
	private void handleJob(SysQuartz sysQuartz) throws SchedulerException {
		try {
			LOGGER.info("start quartz, mation is: {}", JSONUtil.toJsonStr(sysQuartz));
			String taskType = sysQuartz.getGroups();
			TaskMateService taskMateService = SpringUtils.getBean(QuartzConstants.QuartzMateMationJobType.getServiceNameByTaskType(taskType));
			taskMateService.call(sysQuartz);
			LOGGER.info("end quartz, mation is: {}", JSONUtil.toJsonStr(sysQuartz));
		} catch (Exception e) {
			LOGGER.warn("execute task failed, taskId is: {}", sysQuartz.getId(), e);
			e.printStackTrace();
		} finally {
			quartzService.delete(sysQuartz);
			sysQuartzDao.deleteByPrimaryKey(sysQuartz.getId());
		}
	}

}
