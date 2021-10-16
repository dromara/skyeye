/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProWorkloadDao
 * @Description: 项目工作量数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:17
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProWorkloadDao {

    public List<Map<String, Object>> queryProWorkloadList(Map<String, Object> map) throws Exception;
    
    public int insertProWorkloadMation(Map<String, Object> map) throws Exception;

	public int insertProWorkloadRelatedTasksMation(List<Map<String, Object>> entitys) throws Exception;

	public List<Map<String, Object>> queryAllProWorkloadList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProWorkloadMationById(@Param("id") String id) throws Exception;

	public List<Map<String, Object>> queryProWorkloadRelatedTasksById(@Param("workloadId") String workloadId) throws Exception;

	public int updateProWorkloadStateISInAudit(@Param("id") String id) throws Exception;

	public int deleteProWorkloadProcessById(@Param("workloadId") String workloadId) throws Exception;

	public int insertProWorkloadProcess(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryWorkloadIdByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

	public int editProWorkloadStateAndWorkloadById(Map<String, Object> map) throws Exception;

	public int editProWorkloadProcessStateById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryWorkloadTaskMationByWorkloadId(Map<String, Object> map) throws Exception;

	public int editProTaskByWorkloadId(List<Map<String, Object>> m) throws Exception;

	public Map<String, Object> queryProWorkloadId(Map<String, Object> map) throws Exception;

	public int editProWorkloadProcessToRevoke(Map<String, Object> map) throws Exception;

	public int deleteProWorkloadProcessToRevoke(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProWorkloadMationToEdit(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryRelatedTasksById(Map<String, Object> map) throws Exception;

	public int editProWorkloadMation(Map<String, Object> map) throws Exception;

	public int deleteProWorkloadRelatedTasksById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProWorkloadMationForApprovalByIdInProcess(Map<String, Object> map) throws Exception;

	public int deleteAllProWorkloadById(Map<String, Object> map) throws Exception;

	public int updateProWorkloadToCancellation(Map<String, Object> map) throws Exception;
    
}
