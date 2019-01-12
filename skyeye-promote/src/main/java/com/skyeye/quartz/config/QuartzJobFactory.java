package com.skyeye.quartz.config;

import java.util.HashMap;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.DwSurveyDirectoryDao;
import com.skyeye.eve.dao.SysQuartzDao;
import com.skyeye.quartz.entity.SysQuartz;


@Component
@DisallowConcurrentExecution
public class QuartzJobFactory implements Job{

	@Autowired
	private QuartzService quartzService;

	@Autowired
	private SysQuartzDao sysQuartzDao;
	
	@Autowired
	private DwSurveyDirectoryDao dwSurveyDirectoryDao;
	
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
		if("endSurveyMation".equals(sysQuartz.getGroups())){//文件调查
			try {
				endSurveyMation(sysQuartz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 问卷调查
	 * @param sysQuartz
	 * @throws Exception 
	 */
	private void endSurveyMation(SysQuartz sysQuartz) throws Exception{
		Map<String, Object> map = new HashMap<>();
		map.put("id", sysQuartz.getName());
		Map<String, Object> surveyMation = dwSurveyDirectoryDao.querySurveyMationById(map);//获取问卷信息
		if("1".equals(surveyMation.get("surveyState").toString())){//执行中
			map.put("realEndTime", ToolUtil.getTimeAndToString());
			dwSurveyDirectoryDao.editSurveyStateToEndNumZdById(map);
		}
		quartzService.delete(sysQuartz);
		sysQuartzDao.deleteByPrimaryKey(sysQuartz.getId());
	}
	
}
