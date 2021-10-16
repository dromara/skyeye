/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CheckWorkTimeDao {

	public List<Map<String, Object>> queryAllCheckWorkTimeList(Map<String, Object> params) throws Exception;

	public int insertCheckWorkTimeMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCheckWorkTimeMationToEdit(@Param("id") String id) throws Exception;

	public int editCheckWorkTimeMationById(Map<String, Object> map) throws Exception;

	public int editCheckWorkTimeStateMationById(@Param("id") String id, @Param("state") int state) throws Exception;

	public List<Map<String, Object>> queryEnableCheckWorkTimeList(Map<String, Object> params) throws Exception;

	public int creatWeekDay(List<Map<String, Object>> days) throws Exception;

	public int deleteWeekDayByTimeId(@Param("timeId") String timeId) throws Exception;

	public List<Map<String, Object>> queryWeekDayByTimeId(@Param("timeId") String timeId) throws Exception;
	
	public List<Map<String, Object>> queryAllStaffCheckWorkTime() throws Exception;

}
