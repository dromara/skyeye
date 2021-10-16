/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysEveUserStaffDao
 * @Description: 员工管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 12:01
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveUserStaffDao {

	public List<Map<String, Object>> querySysUserStaffList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysUserStaffMationByIdCard(Map<String, Object> map) throws Exception;

	public int insertSysUserStaffMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysUserStaffById(Map<String, Object> map) throws Exception;

	public int editSysUserStaffById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysUserStaffByIdToDetails(@Param("staffId") String staffId) throws Exception;

	public Map<String, Object> querySysUserStaffDetailsByUserId(@Param("userId") String userId) throws Exception;

	public int editSysUserStaffState(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysUserId(Map<String, Object> map) throws Exception;

	public int editSysUserLock(Map<String, Object> bean) throws Exception;

	/**
	 * 获取所有在职的，拥有账号的员工
	 *
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryAllSysUserIsIncumbency(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryCompanyIdByUserId(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysUserStaffMationByIdCardAndId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryStaffTypeById(@Param("staffId") String staffId) throws Exception;

	public int editStaffTypeById(@Param("staffId") String staffId) throws Exception;

	public int insertSchoolStaffMation(Map<String, Object> schoolStaff) throws Exception;
	
	public List<Map<String, Object>> queryUserNameList(@Param("userIds") String userIds) throws Exception;

	public List<Map<String, Object>> queryStaffNameListByIdList(@Param("list") List<String> userIds) throws Exception;

	public List<Map<String, Object>> querySysUserStaffListToTable(Map<String, Object> map) throws Exception;

	public int insertStaffCheckWorkTimeRelation(List<Map<String, Object>> staffTimeMation) throws Exception;

	public List<Map<String, Object>> queryStaffCheckWorkTimeRelationByStaffId(@Param("staffId") String staffId) throws Exception;

	public int deleteStaffCheckWorkTimeRelationByStaffId(@Param("staffId") String staffId) throws Exception;

	public List<Map<String, Object>> queryStaffCheckWorkTimeRelationNameByStaffId(@Param("staffId") String staffId) throws Exception;

	public List<Map<String, Object>> queryCheckTimeMationByTimeIds(@Param("list") List<String> timeIds) throws Exception;

	public List<Map<String, Object>> queryCheckTimeDaysMationByTimeIds(@Param("list") List<String> timeIds) throws Exception;

	/**
	 * 根据状态获取对应状态的员工
	 *
	 * @param state 状态 1在职  2离职  3.见习  4.试用  5.退休
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryAllSysUserStaffListByState(@Param("list") List<Integer> state) throws Exception;

	/**
	 * 修改员工的年假信息
	 *
	 * @param staffId 员工id
	 * @param quarterYearHour 年假,精确到6位
	 * @param annualLeaveStatisTime 员工剩余年假数据刷新日期
	 * @throws Exception
	 */
	public void editSysUserStaffAnnualLeaveById(@Param("staffId") String staffId,
												@Param("quarterYearHour") String quarterYearHour,
												@Param("annualLeaveStatisTime") String annualLeaveStatisTime) throws Exception;

	/**
	 * 修改员工的补休池剩余补休信息
	 *
	 * @param staffId 员工id
	 * @param holidayNumber 当前员工剩余补休天数
	 * @param holidayStatisTime 刷新时间
	 * @throws Exception
	 */
	public void updateSysUserStaffHolidayNumberById(@Param("staffId") String staffId,
													@Param("holidayNumber") String holidayNumber,
													@Param("holidayStatisTime") String holidayStatisTime) throws Exception;

	/**
	 * 修改员工的补休池已休补休信息
	 *
	 * @param staffId 员工id
	 * @param retiredHolidayNumber 当前员工已休补休天数
	 * @param retiredHolidayStatisTime 刷新时间
	 * @throws Exception
	 */
	public void updateSysUserStaffRetiredHolidayNumberById(@Param("staffId") String staffId,
													@Param("retiredHolidayNumber") String retiredHolidayNumber,
													@Param("retiredHolidayStatisTime") String retiredHolidayStatisTime) throws Exception;

}
