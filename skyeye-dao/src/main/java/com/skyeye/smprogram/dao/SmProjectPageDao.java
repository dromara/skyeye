package com.skyeye.smprogram.dao;

import java.util.List;
import java.util.Map;

public interface SmProjectPageDao {

	public List<Map<String, Object>> queryProPageMationByProIdList(Map<String, Object> map) throws Exception;

}
