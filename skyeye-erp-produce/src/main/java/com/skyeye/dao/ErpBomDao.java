/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ErpBomDao {

	public List<Map<String, Object>> queryErpBomList(Map<String, Object> params) throws Exception;

	public int insertErpBomParentMation(Map<String, Object> bomMation) throws Exception;

	public int insertErpBomChildMation(List<Map<String, Object>> beans) throws Exception;

	public int insertErpBomChildProcedureMation(List<Map<String, Object>> childProcedure) throws Exception;

	public Map<String, Object> queryBomParentById(@Param("bomId") String bomId) throws Exception;

	public Map<String, Object> queryBomTitleInSQLByTitle(@Param("title") String title) throws Exception;

	public int deleteErpBomParentMationById(@Param("bomId") String bomId) throws Exception;

	public int deleteErpBomChildMationById(@Param("bomId") String bomId) throws Exception;

	public Map<String, Object> queryErpBomMationToEditById(String bomId) throws Exception;

	public int editErpBomParentMation(Map<String, Object> bomMation) throws Exception;

	public Map<String, Object> queryBomTitleInSQLByIdAndTitle(@Param("bomId") String bomId, @Param("title") String title) throws Exception;

	public int deleteErpBomChildMationByBomId(@Param("bomId") String bomId) throws Exception;

	public List<Map<String, Object>> queryErpBomListByNormsId(@Param("normsId") String normsId) throws Exception;

	/**
	 * 获取bom子件清单中子件的工序信息
	 *
	 * @param childId
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> queryBomChildProcedureListByChildId(@Param("childId") String childId) throws Exception;
}
