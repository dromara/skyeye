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
public interface CodeModelDao {

	public List<Map<String, Object>> queryCodeModelList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public Map<String, Object> queryCodeModelMationByName(Map<String, Object> map) throws Exception;

	public int insertCodeModelMation(Map<String, Object> map) throws Exception;

	public int deleteCodeModelById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCodeModelMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCodeModelMationByIdAndName(Map<String, Object> map) throws Exception;

	public int editCodeModelMationById(Map<String, Object> map) throws Exception;

}
