package com.skyeye.smprogram.dao;

import java.util.List;
import java.util.Map;

public interface SmProjectPageModeDao {

	public List<Map<String, Object>> queryProPageModeMationByPageIdList(Map<String, Object> map) throws Exception;

	public int deletePageModelMationListByPageId(Map<String, Object> map) throws Exception;

	public int editProPageModeMationByPageIdList(List<Map<String, Object>> beans) throws Exception;

}
