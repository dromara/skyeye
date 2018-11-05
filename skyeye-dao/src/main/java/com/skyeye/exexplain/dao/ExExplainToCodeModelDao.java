package com.skyeye.exexplain.dao;

import java.util.Map;

public interface ExExplainToCodeModelDao {

	public int insertExExplainToCodeModelMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryExExplainToCodeModelMation(Map<String, Object> map) throws Exception;

	public int editExExplainToCodeModelMationById(Map<String, Object> map) throws Exception;

}
