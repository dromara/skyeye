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
public interface DsFormContentDao {

	public List<Map<String, Object>> queryDsFormContentList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertDsFormContentMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormContentMationByName(Map<String, Object> map) throws Exception;

	public int deleteDsFormContentMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormContentMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormContentMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editDsFormContentMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDsFormContentMationToShow(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormContentDetailedMationToShow(Map<String, Object> map) throws Exception;

}
