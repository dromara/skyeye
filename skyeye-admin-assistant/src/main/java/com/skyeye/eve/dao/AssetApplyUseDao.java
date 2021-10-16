/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetApplyUseDao
 * @Description: 资产领用申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 17:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetApplyUseDao {

    /**
     * 获取我领用的资产信息
     *
     * @param map 前台入参
     * @return 申请的资产工作流信息
     * @throws Exception
     */
    public List<Map<String, Object>> queryMyUseAssetMation(Map<String, Object> map) throws Exception;

    public int insertAssetUseMation(Map<String, Object> map) throws Exception;

    public int insertAssetUseGoodsMation(List<Map<String, Object>> entitys) throws Exception;

    /**
     * 修改资产领用单为审核中
     *
     * @param id 资产领用id
     * @param processInId 流程实例id
     * @return
     * @throws Exception
     */
    public int updateAssetUseStateISInAudit(@Param("id") String id, @Param("processInId") String processInId) throws Exception;

    /**
     * 修改资产领用单关联的资产为审核中
     *
     * @param useId 资产领用id
     * @return
     * @throws Exception
     */
    public int updateAssetUseGoodStateISInAudit(@Param("useId") String useId) throws Exception;

    /**
     * 根据流程实例id获取资产领用单信息
     *
     * @param processInstanceId 流程实例id
     * @return 资产领用单信息
     * @throws Exception
     */
    public Map<String, Object> queryAssetUseByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public List<Map<String, Object>> queryAssetUseGoodsById(Map<String, Object> map) throws Exception;

    /**
     * 获取资产领用单信息
     *
     * @param id 资产领用单id
     * @return 资产领用单信息
     * @throws Exception
     */
    public Map<String, Object> queryAssetUseMationById(@Param("id") String id) throws Exception;

    /**
     * 获取资产领用单关联的资产信息
     *
     * @param useId 资产领用单id
     * @return 资产领用单关联的资产信息
     * @throws Exception
     */
    public List<Map<String, Object>> queryAssetUseGoodsMationById(@Param("useId") String useId) throws Exception;

    public Map<String, Object> queryAssetUseMationToEditById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryAssetUseGoodsMationToEditById(Map<String, Object> map) throws Exception;

    public int updateAssetUseMation(Map<String, Object> map) throws Exception;

    public int deleteAssetUseGoodsMationById(Map<String, Object> map) throws Exception;

    public int editAssetUseGoodsById(Map<String, Object> bean) throws Exception;

    public int editAssetUseById(Map<String, Object> map) throws Exception;

    public int editAssetUseGoodsState(Map<String, Object> bean) throws Exception;

    public Map<String, Object> queryAssetUseId(Map<String, Object> map) throws Exception;

    public int editAssetUseToRevoke(Map<String, Object> map) throws Exception;

    public int editAssetUseGoodsToRevoke(Map<String, Object> map) throws Exception;

}
