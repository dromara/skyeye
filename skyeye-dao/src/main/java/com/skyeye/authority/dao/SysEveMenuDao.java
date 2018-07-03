package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysEveMenuDao {

	public List<Map<String, Object>> querySysMenuList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

}
