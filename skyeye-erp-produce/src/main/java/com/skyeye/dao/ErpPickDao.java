/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ErpPickDao {

	public List<Map<String, Object>> queryErpPickOrderList(Map<String, Object> params) throws Exception;

	public int insertOrderParentMation(Map<String, Object> depothead) throws Exception;

	public int insertOrderChildMation(List<Map<String, Object>> entitys) throws Exception;

	public Map<String, Object> queryHeaderMationById(@Param("orderId") String orderId, @Param("type") String type) throws Exception;

	public List<Map<String, Object>> queryChildMationById(@Param("orderId") String orderId) throws Exception;

	public int editRequisitionMaterialOrderById(Map<String, Object> depothead) throws Exception;

	public int deleteOrderChildMationByHeaderId(@Param("orderId") String orderId) throws Exception;

	public Map<String, Object> queryPickOrderMationById(@Param("orderId") String orderId) throws Exception;

	public int deletePickOrderMationById(@Param("orderId") String orderId) throws Exception;

	public int editPickOrderMationToSubById(@Param("orderId") String orderId) throws Exception;

	public int editPickOrderMationToExamineById(Map<String, Object> mation) throws Exception;

	public List<Map<String, Object>> queryPickOrderMationToExamineById(@Param("orderId") String orderId, @Param("departMentId") String departMentId) throws Exception;

	public Map<String, Object> queryDepartMentStockByDepartIdAndNormsId(@Param("departMentId") String departMentId, @Param("normsId") String normsId) throws Exception;

	public int editDepartMentStockByDepotIdAndNormsId(@Param("departMentId") String departMentId, @Param("normsId") String normsId,
			@Param("stockNum") int stockNum) throws Exception;

	public int insertDepartMentStockByDepotIdAndNormsId(@Param("materialId") String materialId, @Param("departMentId") String departMentId,
			@Param("normsId") String normsId, @Param("stockNum") int stockNum) throws Exception;

	public int editMachinStateISPickedByMachinId(@Param("machinId") String machinId) throws Exception;

}
