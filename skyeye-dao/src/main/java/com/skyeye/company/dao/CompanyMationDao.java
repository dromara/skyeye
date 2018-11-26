package com.skyeye.company.dao;

import java.util.List;
import java.util.Map;

public interface CompanyMationDao {

	public List<Map<String, Object>> queryCompanyMationList(Map<String, Object> map) throws Exception;

	public int insertCompanyMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMationByName(Map<String, Object> map) throws Exception;

	public int deleteCompanyMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editCompanyMationById(Map<String, Object> map) throws Exception;

}
