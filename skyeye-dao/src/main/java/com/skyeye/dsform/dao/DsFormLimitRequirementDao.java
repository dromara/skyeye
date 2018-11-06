package com.skyeye.dsform.dao;

import java.util.List;
import java.util.Map;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface DsFormLimitRequirementDao {

	public List<Map<String, Object>> queryDsFormLimitRequirementList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertDsFormLimitRequirementMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormLimitRequirementMationByName(Map<String, Object> map) throws Exception;

	public int deleteDsFormLimitRequirementMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormLimitRequirementMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormLimitRequirementMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editDsFormLimitRequirementMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDsFormLimitRequirementMationToShow(Map<String, Object> map) throws Exception;

}
