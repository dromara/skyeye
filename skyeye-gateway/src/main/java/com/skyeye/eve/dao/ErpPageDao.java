/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface ErpPageDao {

	public String queryThisMonthSales() throws Exception;

	public String queryThisMonthRetail() throws Exception;

	public String queryThisMonthPurchase() throws Exception;

	public String queryThisMonthProfit() throws Exception;

	public List<Map<String, Object>> querySixMonthPurchaseMoneyList() throws Exception;

	public List<Map<String, Object>> querySixMonthSealsMoneyList() throws Exception;

	public List<Map<String, Object>> queryTwelveMonthProfitMoneyList() throws Exception;

}
