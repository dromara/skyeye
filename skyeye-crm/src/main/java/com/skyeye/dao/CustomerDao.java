/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CustomerDao
 * @Description: 客户信息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 18:41
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CustomerDao {

    public List<Map<String, Object>> queryCustomerList(Map<String, Object> map) throws Exception;

    public int insertCustomerMation(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryCustomerMationById(Map<String, Object> map) throws Exception;
    
    public int editCustomerMationById(Map<String, Object> map) throws Exception;
    
    public int deleteCustomerMationById(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryContractListByCustomerId(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryOpportunityListByCustomerId(Map<String, Object> map) throws Exception;
    
    public int deleteContractMationByCustomerId(Map<String, Object> map) throws Exception;
    
    public int deleteOpportunityMationByCustomerId(Map<String, Object> map) throws Exception;
    
    public int deleteServiceMationByCustomerId(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryCustomerMationToDetail(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryCustomerMationByName(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryCustomerMationByNameAndId(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryCustomerListToChoose(Map<String, Object> bean) throws Exception;
    
    public List<Map<String, Object>> queryOpportunityNumsDetail(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryContractNumsDetail(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryServiceNumsDetail(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCustomerListTableToChoose(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyConscientiousList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyCreateList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDocumentaryNumsDetail(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryContactsNumsDetail(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDiscussNumsDetail(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryInternationalCustomerList(Map<String, Object> map) throws Exception;
}
