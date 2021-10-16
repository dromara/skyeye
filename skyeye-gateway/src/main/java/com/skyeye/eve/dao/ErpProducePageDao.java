/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ErpProducePageDao {

	public List<Map<String, Object>> queryPickMaterialYearByDepartmentId(@Param("departmentId") String departmentId, @Param("year") String year) throws Exception;

	public List<Map<String, Object>> queryPickMaterialNumYearByDepartmentId(@Param("departmentId") String departmentId, @Param("year") String year,
			@Param("id") String id) throws Exception;

	public List<Map<String, Object>> queryPatchMaterialYearByDepartmentId(@Param("departmentId") String departmentId, @Param("year") String year) throws Exception;

	public List<Map<String, Object>> queryPatchMaterialNumYearByDepartmentId(@Param("departmentId") String departmentId, @Param("year") String year,
			@Param("id") String id) throws Exception;

	public List<Map<String, Object>> queryReturnMaterialYearByDepartmentId(@Param("departmentId") String departmentId, @Param("year") String year) throws Exception;

	public List<Map<String, Object>> queryReturnMaterialNumYearByDepartmentId(@Param("departmentId") String departmentId, @Param("year") String year,
			@Param("id") String id) throws Exception;

	public List<Map<String, Object>> queryDepartmentMachin(@Param("departmentId") String departmentId, @Param("year") String year) throws Exception;

}
