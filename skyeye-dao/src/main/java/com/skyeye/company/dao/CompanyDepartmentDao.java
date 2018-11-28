package com.skyeye.company.dao;

import java.util.List;
import java.util.Map;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

public interface CompanyDepartmentDao {

	public List<Map<String, Object>> queryCompanyDepartmentList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertCompanyDepartmentMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartmentMationByName(Map<String, Object> map) throws Exception;

	public int deleteCompanyDepartmentMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartmentMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartmentMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editCompanyDepartmentMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartmentUserMationById(Map<String, Object> map) throws Exception;

}
