package com.skyeye.smprogram.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface RmGroupMemberDao {

	public List<Map<String, Object>> queryRmGroupMemberList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

}
