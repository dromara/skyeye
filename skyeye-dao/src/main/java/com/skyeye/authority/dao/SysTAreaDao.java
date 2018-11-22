package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysTAreaDao {

	public List<Map<String, Object>> querySysTAreaList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public List<Map<String, Object>> querySysTAreaSecondList(Map<String, Object> bean) throws Exception;

}
