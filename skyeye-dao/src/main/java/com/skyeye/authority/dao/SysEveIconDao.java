package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysEveIconDao {

	public List<Map<String, Object>> querySysIconList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertSysIconMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysIconMationByIconClass(Map<String, Object> map) throws Exception;

	public int deleteSysIconMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysIconMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysIconMationByIconClassAndId(Map<String, Object> map) throws Exception;

	public int editSysIconMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysIconListToMenu(Map<String, Object> map, PageBounds pageBounds) throws Exception;

}
