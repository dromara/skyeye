package com.skyeye.exexplain.dao;

import java.util.Map;

public interface ExExplainToDsFormContentDao {

	public int insertExExplainToDsFormContentMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryExExplainToDsFormContentMation(Map<String, Object> map) throws Exception;

	public int editExExplainToDsFormContentMationById(Map<String, Object> map) throws Exception;

}
