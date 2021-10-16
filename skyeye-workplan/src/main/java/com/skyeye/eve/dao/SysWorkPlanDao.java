/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysWorkPlanDao
 * @Description: 任务计划管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 12:08
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysWorkPlanDao {

	public List<Map<String, Object>> querySysWorkPlanList(Map<String, Object> map) throws Exception;

	public int insertSysWorkPlanISPeople(Map<String, Object> map) throws Exception;

	public int insertSysWorkPlanExecutorISPeople(Map<String, Object> map) throws Exception;

	public int insertSysWorkPlanExecutors(List<Map<String, Object>> executors) throws Exception;

	public List<Map<String, Object>> queryUserMationByUserIds(@Param("carryPeople") String carryPeople) throws Exception;

	public Map<String, Object> queryPlanMationByUserIdAndPlanId(Map<String, Object> map) throws Exception;

	public int updateWhetherTimeById(Map<String, Object> map) throws Exception;

	public int deleteSysWorkPlanById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysWorkPlanToEditById(Map<String, Object> map) throws Exception;

	/**
	 * 删除该计划与用户的绑定信息
	 *
	 * @param planId 计划id
	 * @return
	 * @throws Exception
	 */
	public int deleteSysWorkPlanUserById(@Param("planId") String planId) throws Exception;

	public List<Map<String, Object>> querySysWorkPlanExecutorsToEditById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysWorkPlanEnclosuresToEditById(Map<String, Object> map) throws Exception;

	public int editSysWorkPlanISPeople(Map<String, Object> map) throws Exception;

	public int editSysWorkPlanTimingSend(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysWorkPlanDetailsById(@Param("id") String id, @Param("executorId") String executorId) throws Exception;

	public int insertNoticeMation(Map<String, Object> notice) throws Exception;

	public int insertNoticeListMation(List<Map<String, Object>> notices) throws Exception;

	public List<Map<String, Object>> queryMySysWorkPlanListByUserId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMySysWorkPlanMationByUserId(@Param("planId") String planId, @Param("userId") String userId) throws Exception;

	public void subEditWorkPlanStateById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyCreateSysWorkPlanList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllSysWorkPlanList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyChildJobUserListByUserId(@Param("userId") String userId) throws Exception;

	public List<Map<String, Object>> queryMyBranchSysWorkPlanList(Map<String, Object> map) throws Exception;
}
