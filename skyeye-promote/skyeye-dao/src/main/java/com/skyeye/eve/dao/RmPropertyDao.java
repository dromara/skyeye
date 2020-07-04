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
public interface RmPropertyDao {

	public List<Map<String, Object>> queryRmPropertyList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertRmPropertyMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyMationByName(Map<String, Object> map) throws Exception;

	public int deleteRmPropertyMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editRmPropertyMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryRmPropertyListToShow(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyValueNumById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryUseRmPropertyNumById(Map<String, Object> map) throws Exception;

}
