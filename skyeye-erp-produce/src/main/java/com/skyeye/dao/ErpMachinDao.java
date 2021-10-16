/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ErpMachinDao
 * @Description: 加工单管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 20:52
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ErpMachinDao {

	public List<Map<String, Object>> queryMachinOrderToList(Map<String, Object> params) throws Exception;

	public int insertMachinOrderMation(Map<String, Object> header) throws Exception;

	public int insertMachinOrderProcedureMation(List<Map<String, Object>> procedure) throws Exception;

	public int insertMachinOrderMaterialMation(List<Map<String, Object>> materiel) throws Exception;

	public Map<String, Object> queryMachinOrderMationById(@Param("orderId") String orderId) throws Exception;

	public List<Map<String, Object>> queryMachinOrderProcedureMationByOrdeId(@Param("orderId") String orderId) throws Exception;

	public List<Map<String, Object>> queryMachinOrderMaterialMationByOrdeId(@Param("orderId") String orderId) throws Exception;

	public int editMachinOrderMationById(Map<String, Object> header) throws Exception;

	public int deleteMachinOrderProcedureMationByOrderId(@Param("orderId") String orderId) throws Exception;

	public int deleteMachinOrderMaterialMationByOrderId(@Param("orderId") String orderId) throws Exception;

	public int deleteMachinOrderMationById(@Param("orderId") String orderId) throws Exception;

	public int editMachinOrderStateMationById(@Param("orderId") String orderId, @Param("state") String state) throws Exception;

	public int editProductionProcedureBindMationByOrdeIdAndProceId(@Param("orderId") String orderId, @Param("procedureId") String procedureId) throws Exception;

	public int editMachinOrderStateMationToExamineById(@Param("orderId") String orderId, @Param("state") String state,
			@Param("userId") String userId, @Param("content") String content, @Param("time") String time);

	public List<Map<String, Object>> queryMachinStateIsPassOrderListByDepartmentId(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryMachinStateIsPassNoComplateOrderListByDepartmentId(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryMachinStateIsPassOrderMationById(@Param("orderId") String orderId) throws Exception;

	public Map<String, Object> queryMachinChildMationByChildId(@Param("childId") String childId) throws Exception;

	public int editMachinStateMationByChildId(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryMachinOrderFinishedMaterialMationByOrdeId(@Param("orderId") String orderId) throws Exception;

	public int queryMachinOrderToComplateByOrdeId(@Param("orderId") String orderId) throws Exception;

	public List<Map<String, Object>> queryMachinOrderMaterialListByOrdeId(@Param("orderId") String orderId) throws Exception;

	public List<Map<String, Object>> queryNoComProcedureByProductionId(@Param("productionId") String productionId) throws Exception;

	public int editProductionStateIsComByProductionId(@Param("productionId") String productionId) throws Exception;

}
