package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

public interface SysTAreaDao {

	public List<Map<String, Object>> querySysTAreaList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaProvinceList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaCityList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaChildAreaList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaTownShipList(Map<String, Object> map) throws Exception;

}
