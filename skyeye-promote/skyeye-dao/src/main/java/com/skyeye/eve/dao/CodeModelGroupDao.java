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
public interface CodeModelGroupDao {

	public List<Map<String, Object>> queryCodeModelGroupList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public Map<String, Object> queryCodeModelGroupMationByName(Map<String, Object> map) throws Exception;

	public int insertCodeModelGroupMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCodeModelNumById(Map<String, Object> map) throws Exception;

	public int deleteCodeModelGroupById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCodeModelGroupMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCodeModelGroupMationByIdAndName(Map<String, Object> map) throws Exception;

	public int editCodeModelGroupMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryTableParameterByTableName(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCodeModelListByGroupId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryTableBzByTableName(Map<String, Object> map) throws Exception;

}
