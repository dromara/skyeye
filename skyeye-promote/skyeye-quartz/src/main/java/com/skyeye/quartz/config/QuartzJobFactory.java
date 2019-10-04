package com.skyeye.quartz.config;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.skyeye.eve.dao.SysQuartzDao;
import com.skyeye.quartz.entity.SysQuartz;


@Component
@DisallowConcurrentExecution
public class QuartzJobFactory implements Job{

	@Autowired
	private QuartzService quartzService;

	@Autowired
	private SysQuartzDao sysQuartzDao;
	
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
	     * @param @param sysQuartz
	     * @param @throws SchedulerException    参数
	     * @return void    返回类型
	     * @throws
	 */
	private void handleJob(SysQuartz sysQuartz) throws SchedulerException {
		if("endSurveyMation".equals(sysQuartz.getGroups())){//问卷调查
			try {
//				endSurveyMation(sysQuartz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
