/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CrmContractDao
 * @Description: 客户合同管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/14 14:03
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CrmContractDao {

    public List<Map<String, Object>> queryCrmContractList(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryMyCrmContractList(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryDepartmentListToChoose(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryCrmContractMationByNameAndNum(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryCrmContractMationByNameAndId(Map<String, Object> map) throws Exception;
    
    int insertCrmContractMation(Map<String, Object> map) throws Exception;
    
    int editCrmContractMationById(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryCrmContractMationToDetail(@Param("id") String id) throws Exception;
    
    public Map<String, Object> queryCrmContractMationById(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryCrmContractListToChoose(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryCrmContractStateById(Map<String, Object> map) throws Exception;
    
    public int updateCrmContractStateISInAudit(@Param("id") String id) throws Exception;
    
    public int updateCrmContractState(Map<String, Object> map) throws Exception;
    
    public int updateCrmContractRelationState(Map<String, Object> map) throws Exception;
    
    public int insertCrmContractRelationMation(Map<String, Object> map) throws Exception;
    
    public Map<String, Object> queryCrmContractRelationByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;
    
    public int deleteCrmContractRelationByContractId(@Param("id") String id) throws Exception;
    
    public int deleteCrmContractById(Map<String, Object> map) throws Exception;
    
    public int deleteCrmServiceByContractId(Map<String, Object> map) throws Exception;
    
    public int editCrmContractStateToRevokeById(@Param("id") String id) throws Exception;

}
