/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ActUserProcessInstanceIdDao {

	int insertActUserProInsIdMation(Map<String, Object> actUserProInsId) throws Exception;

	List<Map<String, Object>> queryStartProcessNotSubByUserId(Map<String, Object> map) throws Exception;

	Map<String, Object> queryProcessInstanceMationByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

	Map<String, Object> queryProcessMationByProcessInstanceId(Map<String, Object> map) throws Exception;

	int editDsFormStateIsDraftByProcessInstanceId(Map<String, Object> map) throws Exception;

	void deleteProcessMationByProcessInstanceId(Map<String, Object> map) throws Exception;

	/**
	 * 根据流程实例id获取该流程的创建人信息
	 *
	 * @param processInstanceId 流程实例id
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> queryActUserProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

}
