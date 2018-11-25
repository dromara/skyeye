package com.skyeye.company.dao;

import java.util.List;
import java.util.Map;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface CompanyMatonDao {

	public List<Map<String, Object>> queryCompanyMatonList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertCompanyMatonMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMatonMationByName(Map<String, Object> map) throws Exception;

	public int deleteCompanyMatonMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMatonMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMatonMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editCompanyMatonMationById(Map<String, Object> map) throws Exception;

}
