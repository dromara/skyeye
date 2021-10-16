/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface JobMateMationDao {

	public List<Map<String, Object>> queryJobMateMationByBigTypeList(Map<String, Object> map) throws Exception;

	public int insertJobMation(Map<String, Object> parentJob) throws Exception;

	public Map<String, Object> queryJobMationByJobId(@Param("jobId") String jobId) throws Exception;

	public int editJobMationByJobId(@Param("jobId") String jobId, @Param("status") String status,
			@Param("responseBody") String responseBody, @Param("complateTime") String complateTime) throws Exception;

	public List<Map<String, Object>> queryNoComChildJobMationByJobId(@Param("jobId") String jobId) throws Exception;

	public List<Map<String, Object>> queryFailChildJobMationByJobId(@Param("jobId") String jobId) throws Exception;

	public int editJobRequestBodyMation(Map<String, Object> parentJob) throws Exception;

}
