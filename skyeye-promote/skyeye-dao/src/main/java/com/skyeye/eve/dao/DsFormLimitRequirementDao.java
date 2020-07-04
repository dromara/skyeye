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
public interface DsFormLimitRequirementDao {

	public List<Map<String, Object>> queryDsFormLimitRequirementList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertDsFormLimitRequirementMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormLimitRequirementMationByName(Map<String, Object> map) throws Exception;

	public int deleteDsFormLimitRequirementMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormLimitRequirementMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormLimitRequirementMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editDsFormLimitRequirementMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDsFormLimitRequirementMationToShow(Map<String, Object> map) throws Exception;

}
