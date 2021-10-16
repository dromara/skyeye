/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface PlanProjectDao {

	public List<Map<String, Object>> queryPlanProjectList(Map<String, Object> map) throws Exception;

	public int insertPlanProjectMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryPlanProjectMationByName(Map<String, Object> map) throws Exception;

	public int deletePlanProjectMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryPlanProjectMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryPlanProjectMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editPlanProjectMationById(Map<String, Object> map) throws Exception;

}
