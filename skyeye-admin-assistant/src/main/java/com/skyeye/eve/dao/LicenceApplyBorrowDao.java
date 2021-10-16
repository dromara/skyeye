/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LicenceApplyBorrowDao
 * @Description: 证照借用数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 22:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface LicenceApplyBorrowDao {

    public List<Map<String, Object>> selectMyLicenceBorrowList(Map<String, Object> map) throws Exception;

    public int insertLicenceBorrowMation(Map<String, Object> bean) throws Exception;

    public int insertLicenceBorrowGoodsMation(List<Map<String, Object>> entitys) throws Exception;

    public Map<String, Object> queryLicenceBorrowMationById(@Param("id") String id) throws Exception;

    /**
     * 根据流程实例id获取借用id及审批状态
     *
     * @param processInstanceId 流程实例id
     * @return 借用id及审批状态
     * @throws Exception
     */
    public Map<String, Object> queryLicenceBorrowByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public int updateLicenceBorrowStateISInAudit(@Param("id") String id, @Param("processInId") String processInId) throws Exception;

    public int updateLicenceBorrowGoodsStateISInAudit(@Param("useId") String useId) throws Exception;

    public int editLicenceBorrowGoodsByUseId(@Param("useId") String useId, @Param("state") Integer state) throws Exception;

    public int editLicenceBorrowGoodsById(@Param("id") String id, @Param("state") Integer state) throws Exception;

    public int editLicenceBorrowById(Map<String, Object> map) throws Exception;

    public int editLicenceBorrowToRevokeById(@Param("id") String id) throws Exception;

    public Map<String, Object> queryBorrowNameLicenceBorrow(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryLicenceBorrowMationToEditById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryLicenceBorrowGoodsByUseId(@Param("useId") String useId) throws Exception;

    public List<Map<String, Object>> queryLicenceBorrowGoodsMationToEditById(Map<String, Object> map) throws Exception;

    public int updateLicenceBorrowMation(Map<String, Object> map) throws Exception;

    public int deleteLicenceBorrowGoodsMationById(Map<String, Object> map) throws Exception;

    public int updateLicenceBorrowToCancellation(Map<String, Object> map) throws Exception;

}
