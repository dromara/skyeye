package com.skyeye.codemodel.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface CodeModelGroupDao {

	public List<Map<String, Object>> queryCodeModelGroupList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public Map<String, Object> queryCodeModelGroupMationByName(Map<String, Object> map) throws Exception;

	public int insertCodeModelGroupMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCodeModelNumById(Map<String, Object> map) throws Exception;

	public int deleteCodeModelGroupById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCodeModelGroupMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCodeModelGroupMationByIdAndName(Map<String, Object> map) throws Exception;

	public int editCodeModelGroupMationById(Map<String, Object> map) throws Exception;

}
