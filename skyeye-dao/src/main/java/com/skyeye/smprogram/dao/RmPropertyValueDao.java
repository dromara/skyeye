package com.skyeye.smprogram.dao;

import java.util.List;
import java.util.Map;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface RmPropertyValueDao {

	public List<Map<String, Object>> queryRmPropertyValueList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertRmPropertyValueMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyValueMationByName(Map<String, Object> map) throws Exception;

	public int deleteRmPropertyValueMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyValueMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyValueMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editRmPropertyValueMationById(Map<String, Object> map) throws Exception;

}
