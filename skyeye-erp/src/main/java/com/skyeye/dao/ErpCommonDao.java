/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ErpCommonDao {

	public Map<String, Object> queryDepotHeadDetailsMationById(@Param("orderId") String orderId) throws Exception;

	public List<Map<String, Object>> queryDepotItemDetailsMationById(Map<String, Object> bean) throws Exception;

	public Map<String, Object> queryInoutitemMationById(Map<String, Object> map) throws Exception;

	/**
	 * 根据订单id获取该订单下的所有商品规格信息
	 *
	 * @param orderId 订单id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryOrderNormsToEditByOrderId(@Param("orderId") String orderId) throws Exception;

	/**
	 * 插入主单据信息
	 *
	 * @param depothead
	 * @throws Exception
	 */
	public int insertOrderParentMation(Map<String, Object> depothead) throws Exception;

	/**
	 * 插入子单据信息
	 *
	 * @param entitys
	 * @throws Exception
	 */
	public int insertOrderChildMation(List<Map<String, Object>> entitys) throws Exception;

	/**
	 * 编辑主单据信息
	 *
	 * @param depothead
	 * @throws Exception
	 */
	public int editOrderParentMationById(Map<String, Object> depothead) throws Exception;

	/**
	 * 获取主单据信息用于编辑回显
	 *
	 * @param orderId 订单id
	 * @param orderType 订单类型
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryOrderMationToEditById(@Param("orderId") String orderId, @Param("orderType") String orderType) throws Exception;

	/**
	 * 删除主单据信息
	 *
	 * @param orderId 订单id
	 * @param orderType 订单类型
	 * @return
	 * @throws Exception
	 */
	public int deleteOrderParentMationById(@Param("orderId") String orderId, @Param("orderType") String orderType) throws Exception;

	/**
	 * 删除子单据信息
	 *
	 * @param orderId 订单id
	 * @return
	 * @throws Exception
	 */
	public int deleteOrderChildMationById(@Param("orderId") String orderId) throws Exception;

	/**
	 * 获取主单据状态
	 *
	 * @param orderId 订单id
	 * @param orderType 订单类型
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryOrderParentStateById(@Param("orderId") String orderId, @Param("orderType") String orderType) throws Exception;

	/**
	 * 
	    * @Title: insertProductionOrderRelationMation
	    * @Description: 新增生产单与采购订单的关系信息erp_production_purchase
	    * @param production
	    * @param @return
	    * @throws Exception    参数
	    * @return int    返回类型
	    * @throws
	 */
	public int insertProductionOrderRelationMation(Map<String, Object> production) throws Exception;

	/**
	 * 
	    * @Title: deleteProductionOrderRelationMationByPurchaseOrderId
	    * @Description: 根据采购订单id删除生产单与采购订单的关系信息
	    * @param useId
	    * @param @return
	    * @throws Exception    参数
	    * @return int    返回类型
	    * @throws
	 */
	public int deleteProductionOrderRelationMationByPurchaseOrderId(@Param("purchaseOrderId") String useId) throws Exception;

	/**
	 * 
	    * @Title: editProductionOrderStateById
	    * @Description: 修改生产计划单状态
	    * @param orderId
	    * @param state
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	public void editProductionOrderStateById(@Param("productionId") String orderId, @Param("state") String state) throws Exception;

	/**
	 * 
	    * @Title: queryOrderNormsNumMationByOrderId
	    * @Description: 根据单据id获取当前单据所有商品规格数量
	    * @param orderId
	    * @param @return
	    * @throws Exception    参数
	    * @return List<Map<String,Object>>    返回类型
	    * @throws
	 */
	public List<Map<String, Object>> queryOrderNormsNumMationByOrderId(@Param("orderId") String orderId) throws Exception;

	/**
	 * 
	    * @Title: queryOrderNormsOverNumMationByOrderId
	    * @Description: 根据单据编号获取该单据下的商品规格还有多少数量没有入库/出库
	    * @param linkNumber
	    * @param @return
	    * @throws Exception    参数
	    * @return List<Map<String,Object>>    返回类型
	    * @throws
	 */
	public List<Map<String, Object>> queryOrderNormsOverNumMationByOrderNumber(@Param("orderNumber") String linkNumber) throws Exception;

	/**
	 * 
	    * @Title: editOrderStatusToComByOrderNumber
	    * @Description: 根据单据编号修改单据状态为已完成
	    * @param linkNumber
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	public int editOrderStatusToComByOrderNumber(@Param("orderNumber") String linkNumber) throws Exception;

	/**
	 * 
	    * @Title: queryDepotStockByDepotIdAndNormsId
	    * @Description: 根据仓库id和规格id获取当前规格的库存
	    * @param depotId
	    * @param normsId
	    * @param @return
	    * @throws Exception    参数
	    * @return Map<String,Object>    返回类型
	    * @throws
	 */
	public Map<String, Object> queryDepotStockByDepotIdAndNormsId(@Param("depotId") String depotId, @Param("normsId") String normsId) throws Exception;

	/**
	 * 
	    * @Title: editDepotStockByDepotIdAndNormsId
	    * @Description: 根据仓库id和规格id修改当前规格的库存
	    * @param depotId
	    * @param normsId
	    * @param stockNum
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	public int editDepotStockByDepotIdAndNormsId(@Param("depotId") String depotId, @Param("normsId") String normsId, 
			@Param("stockNum") int stockNum) throws Exception;

	/**
	 * 
	    * @Title: insertDepotStockByDepotIdAndNormsId
	    * @Description: 新增当前规格的库存信息
	    * @param materialId
	    * @param depotId
	    * @param normsId
	    * @param stockNum
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	public int insertDepotStockByDepotIdAndNormsId(@Param("materialId") String materialId, @Param("depotId") String depotId, 
			@Param("normsId") String normsId, @Param("stockNum") int stockNum) throws Exception;

	/**
	 * 
	    * @Title: editOrderStateToSubExamineById
	    * @Description: 修改订单状态为审核中
	    * @param orderId
	    * @param orderType
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	public int editOrderStateToSubExamineById(@Param("orderId") String orderId, @Param("orderType") String orderType) throws Exception;

	/**
	 * 编辑订单状态
	 *
	 * @param orderId 订单id
	 * @param orderType 订单类型
	 * @param state 要修改的状态
	 * @return
	 * @throws Exception
	 */
	int editOrderStateById(@Param("orderId") String orderId, @Param("orderType") String orderType, @Param("state") String state) throws Exception;

	/**
	 * 根据销售单id获取生产计划单
	 *
	 * @param orderId 销售单id
	 * @param type 这里传的是生产计划单的类型
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryErpProductionHeadByPId(@Param("orderId") String orderId, @Param("type") Integer type) throws Exception;

	/**
	 * 根据生产计划单id获取加工单
	 *
	 * @param orderId 生产计划单id
	 * @param type 这里传的是加工单的类型
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryErpMachinHead(@Param("idsList") List<String> orderId, @Param("type") Integer type) throws Exception;

	/**
	 * 根据生产计划单id获取采购单
	 *
	 * @param orderId 生产计划单id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryErpPurchaseHead(@Param("idsList") List<String> orderId) throws Exception;

	/**
	 * 根据加工单id获取子单据(工序验收单)
	 *
	 * @param orderId 加工单id
	 * @param type 这里传的是工序验收单的类型
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryErpMachinChild(@Param("idsList") List<String> orderId, @Param("type") Integer type) throws Exception;

	/**
	 * 根据加工单id获取验收入库单
	 *
	 * @param orderId 加工单id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryErpMachinPut(@Param("idsList") List<String> orderId) throws Exception;

	/**
	 * 根据采购单id获取采购入库单
	 *
	 * @param orderId 采购单id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryErpPurchasePutHead(@Param("idsList") List<String> orderId) throws Exception;

	/**
	 * 根据销售单id获取销售出库单
	 *
	 * @param orderId 销售单id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryErpSalesOutHead(@Param("orderId") String orderId) throws Exception;

	/**
	 * 根据订单id获取生产计划单信息
	 *
	 * @param orderId 订单id
	 * @param type 这里传的是生产计划单的类型
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryProductionHeadDetailsMationById(@Param("orderId") String orderId, @Param("type") Integer type) throws Exception;

	/**
	 * 根据订单id获取加工单信息
	 *
	 * @param orderId 订单id
	 * @param type 这里传的是加工单的类型
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryMachinDetailsMationById(@Param("orderId") String orderId, @Param("type") Integer type) throws Exception;

	/**
	 * 根据订单id获取领料单/退料单/补料单
	 *
	 * @param orderId 订单id
	 * @param type 单据类型  19.领料单  20.补料单  21.退料单
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryErpPickHead(@Param("idsList") List<String> orderId, @Param("type") Integer type) throws Exception;

	/**
	 * 编辑订单状态提交到工作流
	 *
	 * @param id 订单id
	 * @param state 要修改的状态
	 * @param processInId 流程实例id
	 * @return
	 * @throws Exception
	 */
    void editOrderStateToSubActiviti(@Param("orderId") String id, @Param("state") String state, @Param("processInId") String processInId) throws Exception;

	/**
	 * 根据流程实例id获取单据信息
	 *
	 * @param processInstanceId 流程实例id
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> queryOrderMationByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;
}
