/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CrmOpportunityChangeHistoryDao {

	public int insertOpportunityChangeHistory(Map<String, Object> changeHistory) throws Exception;

	public List<Map<String, Object>> queryOpportunityChangeHistoryByOpportunityId(@Param("opportunityId") String opportunityId) throws Exception;
	
}
