package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysDataBaseDao {

	public List<Map<String, Object>> querySysDataBaseList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public List<Map<String, Object>> querySysDataBaseSelectList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysDataBaseDescSelectList(Map<String, Object> map) throws Exception;

}
