/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SalesOrderDao
 * @Description: 销售订单管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/14 23:08
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SalesOrderDao {

	public List<Map<String, Object>> querySalesOrderToList(Map<String, Object> params) throws Exception;

	public int editSalesOrderStateToExamineById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySalesOrderToTurnPutById(@Param("orderId") String orderId, @Param("orderType")  String orderType) throws Exception;
	
	public List<Map<String, Object>> querySalesOrderNormsToTurnPutById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryOrderIsStandardById(Map<String, Object> entity) throws Exception;

	public List<Map<String, Object>> queryMationToExcel(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> querySalesOrderListToTree() throws Exception;

}
