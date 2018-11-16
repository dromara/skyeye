package com.skyeye.exexplain.dao;

import java.util.Map;

public interface ExExplainToDsFormDisplayTemplateDao {

	public int insertExExplainToDsFormDisplayTemplateMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryExExplainToDsFormDisplayTemplateMation(Map<String, Object> map) throws Exception;

	public int editExExplainToDsFormDisplayTemplateMationById(Map<String, Object> map) throws Exception;

}
