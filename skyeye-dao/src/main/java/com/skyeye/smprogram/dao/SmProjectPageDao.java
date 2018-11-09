package com.skyeye.smprogram.dao;

import java.util.List;
import java.util.Map;

public interface SmProjectPageDao {

	public List<Map<String, Object>> queryProPageMationByProIdList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryFilePathNumMaxMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryFileNameNumMaxMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySortMaxMationByProjectId(Map<String, Object> map) throws Exception;

	public int insertProPageMationByProId(Map<String, Object> map) throws Exception;

}
