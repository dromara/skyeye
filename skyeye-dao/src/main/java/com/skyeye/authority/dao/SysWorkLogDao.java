package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface SysWorkLogDao {

	public List<Map<String, Object>> querySysWorkLogList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

}
