/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CheckWorkDao {

	Map<String, Object> queryStartWorkTime(@Param("timeId") String timeId, @Param("staffId") String staffId) throws Exception;

	int insertCheckWorkStartWork(Map<String, Object> map) throws Exception;

	int insertCheckWorkEndWork(Map<String, Object> map) throws Exception;

	Map<String, Object> queryisAlreadyCheck(@Param("checkDate") String checkDate, @Param("createId") String createId,
			@Param("timeId") String timeId) throws Exception;

	int editCheckWorkEndWork(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> queryCheckWork(Map<String, Object> map) throws Exception;

	Map<String, Object> queryYesterdayIsHoliday(Map<String, Object> map) throws Exception;

	/**
	 * 获取所有昨天没有打卡的用户
	 *
	 * @param timeId 班次id
	 * @param yesterdayTime 日期信息,格式为：yyyy-MM-dd
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> queryNotCheckMember(@Param("timeId") String timeId, @Param("yesterdayTime") String yesterdayTime) throws Exception;

	int insertCheckWorkBySystem(List<Map<String, Object>> listBeans) throws Exception;

	List<Map<String, Object>> queryNotCheckEndWorkId(@Param("timeId") String timeId, @Param("yesterdayTime") String yesterdayTime) throws Exception;

	int editCheckWorkBySystem(Map<String, Object> item) throws Exception;

	List<Map<String, Object>> queryCheckWorkIdByAppealType(Map<String, Object> map) throws Exception;

	int insertCheckWorkAppeal(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> queryCheckWorkAppeallist(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> queryCheckWorkAppealMyGetlist(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> querySysEveUserStaff(Map<String, Object> map) throws Exception;

	Map<String, Object> queryStaffCheckWorkDetails(Map<String, Object> map) throws Exception;

	int editStaffCheckWork(Map<String, Object> map) throws Exception;

	Map<String, Object> queryCheckWorkDetails(Map<String, Object> map) throws Exception;

	/**
	 * 获取指定员工指定班次在指定月份的所有打卡信息
	 *
	 * @param userId 用户id
	 * @param timeId 班次id
	 * @param months 月份集合
	 * @return
	 * @throws Exception
	 */
    List<Map<String, Object>> queryCheckWorkMationByMonth(@Param("userId") String userId,
        @Param("timeId") String timeId, @Param("months") List<String> months) throws Exception;
	
	List<Map<String, Object>> queryCheckWorkReport(Map<String, Object> map) throws Exception;
	
    Map<String, Object> queryCheckWorkEcharts(Map<String, Object> beans) throws Exception;
    
    List<Map<String, Object>> downloadCheckWorkTemplate(Map<String, Object> map) throws Exception;
    
    List<Map<String, Object>> queryReportDetail(Map<String, Object> map) throws Exception;
	
    List<Map<String, Object>> queryEchartsDetail(Map<String, Object> map) throws Exception;
    
    List<Map<String, Object>> queryReportDownload(Map<String, Object> map) throws Exception;

	/**
	 * 获取指定月份的假期(type=3)
	 *
	 * @param months 指定月，格式["2020-04", "2020-05"...]
	 * @return 指定月中的所有节假日
	 * @throws Exception
	 */
	List<Map<String, Object>> queryHolidayScheduleDayMation(@Param("list") List<String> months) throws Exception;

}
