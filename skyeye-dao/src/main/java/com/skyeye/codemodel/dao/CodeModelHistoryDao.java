package com.skyeye.codemodel.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface CodeModelHistoryDao {

	public List<Map<String, Object>> queryCodeModelHistoryList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public List<Map<String, Object>> queryCodeModelHistoryListByFilePath(Map<String, Object> map) throws Exception;

}
