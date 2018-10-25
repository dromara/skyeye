package com.skyeye.smprogram.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SmProjectDao {

	public List<Map<String, Object>> querySmProjectList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public Map<String, Object> querySmProjectByNameAndUserId(Map<String, Object> map) throws Exception;

	public int insertSmProjectMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySmProjectPageNumById(Map<String, Object> map) throws Exception;

	public int deleteSmProjectById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySmProjectMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySmProjectMationByIdAndName(Map<String, Object> map) throws Exception;

	public int editSmProjectMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySmProjectPageModelNumById(Map<String, Object> map) throws Exception;

}
