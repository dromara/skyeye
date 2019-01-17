package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface SysTAreaDao {

	public List<Map<String, Object>> querySysTAreaList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaProvinceList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaCityList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaChildAreaList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaTownShipList(Map<String, Object> map) throws Exception;

}
