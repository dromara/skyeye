/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: StatisticsDao
 * @Description: erp统计管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 12:12
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface StatisticsDao {

	public List<Map<String, Object>> queryWarehousingDetails(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryOutgoingDetails(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryInComimgDetails(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> querySalesDetails(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryCustomerReconciliationDetails(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> querySupplierReconciliationDetails(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryInComimgAllDetails(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> querySalesAllDetails(Map<String, Object> params) throws Exception;

}
