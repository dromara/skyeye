/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

@Repository
@Mapper
public interface CompanyDepartmentDao {

	public List<Map<String, Object>> queryCompanyDepartmentList(Map<String, Object> map, PageBounds pageBounds) throws Exception;

	public int insertCompanyDepartmentMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartmentMationByName(Map<String, Object> map) throws Exception;

	public int deleteCompanyDepartmentMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartmentMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartmentMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editCompanyDepartmentMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartmentUserMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCompanyDepartmentListTreeByCompanyId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyJobNumMationById(Map<String, Object> map) throws Exception;

}
