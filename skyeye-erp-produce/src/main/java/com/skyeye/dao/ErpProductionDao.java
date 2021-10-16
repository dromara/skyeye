/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ErpProductionDao {

	public List<Map<String, Object>> queryErpProductionList(Map<String, Object> params) throws Exception;

	public int insertErpProductionMation(Map<String, Object> production) throws Exception;

	public int insertErpProductionChildMation(List<Map<String, Object>> childProList) throws Exception;

	public int insertErpProductionProcedureMation(List<Map<String, Object>> procedureList) throws Exception;

	public Map<String, Object> queryErpProductionMationToEditById(@Param("orderId") String orderId) throws Exception;

	public List<Map<String, Object>> queryErpProductionChildMationToEditByOrderId(@Param("orderId") String orderId) throws Exception;

	public List<Map<String, Object>> queryErpProductionProcedureMationDetailsById(@Param("orderId") String orderId) throws Exception;

	public int editErpProductionMationById(Map<String, Object> production) throws Exception;

	public int deleteErpProductionChildMation(@Param("orderId") String orderId) throws Exception;

	public int deleteErpProductionProcedureMation(@Param("orderId") String orderId) throws Exception;

	public Map<String, Object> queryErpPruductionStateById(@Param("orderId") String orderId) throws Exception;

	public int deleteErpProductionMationById(@Param("orderId") String orderId) throws Exception;

	public Map<String, Object> queryErpProductionMationToDetailById(@Param("orderId") String orderId) throws Exception;

	public List<Map<String, Object>> queryErpProductionChildMationToDetailByOrderId(@Param("orderId") String orderId) throws Exception;

	public int editErpPruductionStateById(@Param("orderId") String orderId, @Param("state") String state) throws Exception;

	public int editErpProductionStateToExamineById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryErpProductionListToTable(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryErpProductionOutsideProListByOrderId(@Param("orderId") String orderId, @Param("type") String type) throws Exception;

}
