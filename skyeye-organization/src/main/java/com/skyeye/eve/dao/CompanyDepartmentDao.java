/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CompanyDepartmentDao
 * @Description: 企业部门信息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/30 19:58
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CompanyDepartmentDao {

	public List<Map<String, Object>> queryCompanyDepartmentList(Map<String, Object> map) throws Exception;

	public int insertCompanyDepartmentMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartmentMationByName(Map<String, Object> map) throws Exception;

	public int deleteCompanyDepartmentMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartmentMationById(@Param("id") String id) throws Exception;

	public Map<String, Object> queryCompanyDepartmentMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editCompanyDepartmentMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyDepartmentUserMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCompanyDepartmentListTreeByCompanyId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCompanyJobNumMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCompanyDepartmentListByCompanyIdToSelect(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCompanyDepartmentOrganization(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCompanyDepartmentListToChoose(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCompanyDepartmentListByIds(@Param("idsList") List<String> idsList) throws Exception;
}
