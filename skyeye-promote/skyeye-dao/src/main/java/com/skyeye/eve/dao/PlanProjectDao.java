/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

@Repository
@Mapper
public interface PlanProjectDao {

	public List<Map<String, Object>> queryPlanProjectList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertPlanProjectMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryPlanProjectMationByName(Map<String, Object> map) throws Exception;

	public int deletePlanProjectMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryPlanProjectMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryPlanProjectMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editPlanProjectMationById(Map<String, Object> map) throws Exception;

}
