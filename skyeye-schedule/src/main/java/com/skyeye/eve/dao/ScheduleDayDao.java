/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ScheduleDayDao
 * @Description: 日程相关数据交互层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/1 11:42
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ScheduleDayDao {

	/**
	 * 新增日程
	 *
	 * @param map 日程对象
	 * @return
	 * @throws Exception
	 */
	public int insertScheduleDayMation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryScheduleDayMationByUserId(@Param("userId") String userId, @Param("list") List<String> months) throws Exception;

	public List<Map<String, Object>> queryScheduleDayMationTodayByUserId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryScheduleDayMationById(Map<String, Object> map) throws Exception;

	public int editScheduleDayMationById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryScheduleDayDetailsMationById(Map<String, Object> map) throws Exception;
	
	public int deleteScheduleDayMationById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryHolidayScheduleList(Map<String, Object> map) throws Exception;
	
	public int exploreScheduleTemplate(List<Map<String, Object>> beans) throws Exception;
	
	public int deleteHolidayScheduleById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryHolidayScheduleByThisYear(Map<String, Object> map) throws Exception;

	public int deleteHolidayScheduleByThisYear(Map<String, Object> map) throws Exception;

	public int addHolidayScheduleRemind(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryHolidayScheduleDayMationById(Map<String, Object> map) throws Exception;

	public int deleteHolidayScheduleRemind(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryScheduleByIdToEdit(Map<String, Object> map) throws Exception;

	public int editScheduleById(Map<String, Object> map) throws Exception;

	public int addSchedule(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryIsnullThistime(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryHolidayScheduleListBySys(Map<String, Object> map) throws Exception;

	/**
	 * 根据关联id删除日程
	 *
	 * @param objectId 关联id
	 * @return
	 * @throws Exception
	 */
	public int deleteScheduleDayMationByPlanId(@Param("objectId") String objectId) throws Exception;

	/**
	 * 分页获取我的日程信息
	 *
	 * @param map 查询条件
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryMyScheduleList(Map<String, Object> map) throws Exception;

}
