/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetApplyReturnDao
 * @Description: 资产归还单数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/20 22:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetApplyReturnDao {

    public List<Map<String, Object>> queryMyReturnAssetMation(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryMyUnReturnAssetListByTypeId(Map<String, Object> map) throws Exception;

    public void insertAssetReturnMation(Map<String, Object> map) throws Exception;

    public void insertAssetReturnGoodsMation(List<Map<String, Object>> entitys) throws Exception;

    /**
     * 根据资产归还单id获取资产归还单信息
     *
     * @param id 资产归还单id
     * @return 资产归还单信息
     * @throws Exception
     */
    public Map<String, Object> queryAssetReturnMationById(@Param("id") String id) throws Exception;

    /**
     * 根据资产归还单id获取关联的资产信息
     *
     * @param returnId 资产归还单id
     * @return 关联的资产信息
     * @throws Exception
     */
    public List<Map<String, Object>> queryAssetReturnGoodsMationById(@Param("returnId") String returnId) throws Exception;

    public int updateAssetReturnStateISInAudit(@Param("id") String id, @Param("processInId") String processInId) throws Exception;

    public int updateAssetReturnGoodStateISInAudit(@Param("returnId") String returnId) throws Exception;

    public Map<String, Object> queryAssetReturnByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public int editAssetReturnGoodsById(Map<String, Object> map) throws Exception;

    public int editAssetReturnById(Map<String, Object> map) throws Exception;

    public int updateAssetReturnToCancellation(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetReturnMationToEditById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryAssetReturnGoodsMationToEditById(Map<String, Object> map) throws Exception;

    public int updateAssetReturnMation(Map<String, Object> map) throws Exception;

    public int deleteAssetReturnGoodsMationById(Map<String, Object> map) throws Exception;

    public int editAssetReturnToRevoke(Map<String, Object> map) throws Exception;

    public int editAssetReturnGoodsToRevoke(Map<String, Object> map) throws Exception;

    /**
     * 根据流程实例id获取资产归还单id
     *
     * @param processInstanceId 流程实例id
     * @return 资产归还单id
     * @throws Exception
     */
    public Map<String, Object> queryAssetReturnId(@Param("processInstanceId") String processInstanceId) throws Exception;

}
