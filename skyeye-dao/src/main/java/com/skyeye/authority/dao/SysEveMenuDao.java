package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysEveMenuDao {

	public List<Map<String, Object>> querySysMenuList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public void insertSysMenuMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysMenuMationToEditById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysMenuMationBySimpleLevel(Map<String, Object> map) throws Exception;

	public void editSysMenuMationById(Map<String, Object> map) throws Exception;

	public void deleteSysMenuChildMationById(Map<String, Object> map) throws Exception;

	public void deleteSysMenuMationById(Map<String, Object> map) throws Exception;

}
