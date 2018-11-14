package com.skyeye.exexplain.dao;

import java.util.Map;

public interface ExExplainToRmPropertyDao {

	public int insertExExplainToRmPropertyMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryExExplainToRmPropertyMation(Map<String, Object> map) throws Exception;

	public int editExExplainToRmPropertyMationById(Map<String, Object> map) throws Exception;

}
