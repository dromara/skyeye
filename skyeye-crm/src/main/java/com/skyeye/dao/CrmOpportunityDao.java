/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CrmOpportunityDao
 * @Description: 客户商机管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:41
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CrmOpportunityDao {

	public List<Map<String, Object>> queryCrmOpportunityList(Map<String, Object> map) throws Exception;

	public int insertCrmOpportunityMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryOpportunityMationByName(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryOpportunityMationToDetail(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryOpportunityMationToEditById(Map<String, Object> map) throws Exception;

	public int editOpportunityMationById(Map<String, Object> map) throws Exception;

	public int deleteOpportunityMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDiscussNumsList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryOpportunityListUseToSelect(Map<String, Object> map) throws Exception;

	public int insertOpportunityDiscussMation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryResponsIdInOpportunityById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryPartIdInOpportunityById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryFollowIdInOpportunityById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyCrmOpportunityList(Map<String, Object> map) throws Exception;

	public int insertDiscussReplyMation(Map<String, Object> map) throws Exception;

	public int deleteDiscussMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDiscussMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDiscussReplyByDiscussId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCrmOpportunityStateById(@Param("opportunityId") String opportunityId) throws Exception;

	public Map<String, Object> queryOpportunityMationById(@Param("id") String id) throws Exception;

	public int updateCrmOpportunityStateISInAudit(@Param("id") String id) throws Exception;

	public int insertOpportunityProcess(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryOpportunityIdByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

	public int editOpportunityStateById(Map<String, Object> map) throws Exception;

	public int editOpportunityProcessStateById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllDiscussList(Map<String, Object> map) throws Exception;

	public int deleteCrmOpportunityProcessById(@Param("id") String id) throws Exception;

	public int deleteCrmDocumentaryByOpportunityId(Map<String, Object> map) throws Exception;

	public int deleteDiscussByOpportunityId(Map<String, Object> map) throws Exception;

	public int deleteDiscussReplyByOpportunityId(Map<String, Object> map) throws Exception;

	public int editOpportunityProcessToRevoke(@Param("opportunityId") String opportunityId) throws Exception;

	public int editOpportunityState(@Param("opportunityId") String opportunityId, @Param("state") int state) throws Exception;

}
