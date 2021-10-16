/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealApplyBorrowDao
 * @Description: 印章借用数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 15:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealApplyBorrowDao {

    public List<Map<String, Object>> selectMySealBorrowList(Map<String, Object> map) throws Exception;

    public int insertSealBorrowMation(Map<String, Object> bean) throws Exception;

    public int insertSealBorrowGoodsMation(List<Map<String, Object>> entitys) throws Exception;

    /**
     * 根据id获取印章借用单信息
     *
     * @param id 印章借用单id
     * @return 印章借用单信息
     * @throws Exception
     */
    public Map<String, Object> querySealBorrowMationById(@Param("id") String id) throws Exception;

    /**
     * 根据流程实例id获取借用id及审批状态
     *
     * @param processInstanceId 流程实例id
     * @return 借用id及审批状态
     * @throws Exception
     */
    public Map<String, Object> querySealBorrowByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public int updateSealBorrowStateISInAudit(@Param("id") String id, @Param("processInId") String processInId) throws Exception;

    public int updateSealBorrowGoodsStateISInAudit(@Param("id") String id) throws Exception;

    public int editSealBorrowGoodsByBorrowId(@Param("borrowId") String borrowId, @Param("state") Integer state) throws Exception;

    public int editSealBorrowGoodsById(@Param("id") String id, @Param("state") Integer state) throws Exception;

    public int editSealBorrowById(@Param("id") String id, @Param("state") Integer state) throws Exception;

    public int editSealBorrowToRevokeById(@Param("id") String id) throws Exception;

    /**
     * 根据借用单id获取关联的印章信息
     *
     * @param borrowId 借用单id
     * @return 关联的印章信息
     * @throws Exception
     */
    public List<Map<String, Object>> queryBorrowSealById(@Param("borrowId") String borrowId) throws Exception;

    public List<Map<String, Object>> querySealBorrowGoodsMationToEditById(Map<String, Object> map) throws Exception;

    public int updateSealBorrowMation(Map<String, Object> map) throws Exception;

    public int deleteSealBorrowGoodsMationById(Map<String, Object> map) throws Exception;

    public int updateSealBorrowToCancellation(Map<String, Object> map) throws Exception;

}
