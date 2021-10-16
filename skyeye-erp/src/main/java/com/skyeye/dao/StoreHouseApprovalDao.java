/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: StoreHouseApprovalDao
 * @Description: 出入库单据审核管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:37
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface StoreHouseApprovalDao {

	public Map<String, Object> queryOrderMationByOrderId(@Param("orderId") String orderId) throws Exception;

	public int editWarehouseOrderStateToExamineById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryNoStockListMationByOrderId(@Param("orderId") String orderId) throws Exception;

	public List<Map<String, Object>> queryNotApprovedOtherOrderList(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryNoStockSplitListMationByOrderId(@Param("orderId") String orderId) throws Exception;

	public List<Map<String, Object>> queryNoStockAssemblyListMationByOrderId(@Param("orderId") String orderId) throws Exception;

	public List<Map<String, Object>> queryPassApprovedOtherOrderList(Map<String, Object> params) throws Exception;

}
