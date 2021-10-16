/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ScheduleDayService {

	public void insertScheduleDayMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryScheduleDayMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryScheduleDayMationTodayByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editScheduleDayMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryScheduleDayMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void deleteScheduleDayMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryHolidayScheduleList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void downloadScheduleTemplate(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void exploreScheduleTemplate(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void deleteHolidayScheduleById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteHolidayScheduleByThisYear(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addHolidayScheduleRemind(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteHolidayScheduleRemind(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryScheduleByIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editScheduleById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addSchedule(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryHolidayScheduleListBySys(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyScheduleList(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 将其他模块同步到日程
	 * 不启动日程定时任务，如需启动，在该接口外面自行启动
	 *
	 * @param title 标题
	 * @param content 日程内容
	 * @param startTime 开始时间,格式为：yyyy-MM-dd HH:mm:ss
	 * @param endTime 结束时间,格式为：yyyy-MM-dd HH:mm:ss
	 * @param userId 执行人
	 * @param objectId 关联id
	 * @param objectType object类型：1.任务计划id，2.项目任务id
	 * @throws Exception
	 */
	public String synchronizationSchedule(String title, String content, String startTime, String endTime, String userId, String objectId, int objectType) throws Exception;
}
