/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProTaskDao
 * @Description: 项目任务相关数据交互层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/1 12:32
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProTaskDao {

	public List<Map<String, Object>> queryProTaskList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProTaskByTaskName(Map<String, Object> map) throws Exception;

	public int insertProTaskMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProTaskMationToDetails(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProTaskMationToEdit(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryPerformIdByProTaskId(Map<String, Object> map) throws Exception;

	public int editProTaskMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProTaskStateAndPidById(Map<String, Object> map) throws Exception;

	public int deleteProTaskMationById(Map<String, Object> map) throws Exception;

	public int deleteProTaskProcessMationById(Map<String, Object> map) throws Exception;

	public int deleteAllProTaskMationByPid(Map<String, Object> map) throws Exception;

	public int deleteAllProTaskProcessMationByPid(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProTaskMationForApprovalById(@Param("id") String id) throws Exception;

	public int updateProTaskStateISInAudit(@Param("id") String id) throws Exception;

	public int deleteProTaskProcessById(@Param("taskId") String taskId) throws Exception;

	/**
	 * 根据流程实例id查询项目任务信息
	 *
	 * @param processInstanceId 流程实例id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryTaskIdByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

	public int editProTaskStateById(Map<String, Object> map) throws Exception;

	public int editProTaskProcessStateById(Map<String, Object> map) throws Exception;

	public int insertProTaskProcess(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProTaskId(Map<String, Object> map) throws Exception;

	public int editProTaskProcessToRevoke(Map<String, Object> map) throws Exception;

	public int deleteProTaskProcessToRevoke(Map<String, Object> map) throws Exception;

	public int updateProTaskToCancellation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyProTaskList(Map<String, Object> map) throws Exception;

	public int updateProTaskToExecutionBegin(Map<String, Object> map) throws Exception;

	/**
	 * 根据项目任务id获取部分信息
	 *
	 * @param taskId 项目任务id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryRestWorkloadById(@Param("taskId") String taskId) throws Exception;

	public int editProTaskStateAndWorkloadById(Map<String, Object> map) throws Exception;

	/**
	 * 根据项目任务id获取任务详情信息
	 *
	 * @param taskId 项目任务id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryProTaskMationByTaskId(@Param("taskId") String taskId) throws Exception;

	public int updateProTaskToExecutionOver(Map<String, Object> map) throws Exception;

	public int updateProTaskToExecutionClose(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryProTaskInExecution(Map<String, Object> map) throws Exception;

}
