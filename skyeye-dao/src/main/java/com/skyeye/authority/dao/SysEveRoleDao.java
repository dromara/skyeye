package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysEveRoleDao {

	public List<Map<String, Object>> querySysRoleList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public List<Map<String, Object>> querySysRoleBandMenuList(Map<String, Object> map) throws Exception;

}
