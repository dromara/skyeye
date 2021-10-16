/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CompanyMationDao {

	public List<Map<String, Object>> queryCompanyMationList(Map<String, Object> map) throws Exception;

	public int insertCompanyMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMationByName(Map<String, Object> map) throws Exception;

	public int deleteCompanyMationById(@Param("companyId") String companyId) throws Exception;

	public Map<String, Object> queryCompanyMationToEditById(@Param("companyId") String companyId) throws Exception;

	public Map<String, Object> queryCompanyMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editCompanyMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryOverAllCompanyMationList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCompanyMationListTree(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyUserNumMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartMentNumMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCompanyListToSelect(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCompanyMationListToChoose(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCompanyMationListByIds(@Param("idsList") List<String> idsList) throws Exception;
}
