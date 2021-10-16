/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetPurchaseDao
 * @Description: 资产采购单数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 23:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetApplyPurchaseDao {

    public int updateAssetPurchaseToCancellation(Map<String, Object> map) throws Exception;

    /**
     * 获取我的采购单列表数据
     *
     * @param map 前端入参
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryMyAssetPurchaseList(Map<String, Object> map) throws Exception;

    public int insertAssetPurchaseGoodsMation(List<Map<String, Object>> entitys) throws Exception;

    public int insertAssetPurchaseMation(Map<String, Object> map) throws Exception;

    /**
     * 根据资产采购单id获取资产采购单信息
     *
     * @param id 资产采购单id
     * @return 资产采购单信息
     * @throws Exception
     */
    public Map<String, Object> queryAssetPurchaseMationById(@Param("id") String id) throws Exception;

    public List<Map<String, Object>> queryAssetPurchaseGoodsMationById(@Param("purchaseId") String purchaseId) throws Exception;

    public int updateAssetPurchaseStateISInAudit(@Param("id") String id, @Param("processInId") String processInId) throws Exception;

    public int updateAssetPurchaseGoodStateISInAudit(@Param("purchaseId") String purchaseId) throws Exception;

    public Map<String, Object> queryAssetPurchaseByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public int editAssetPurchaseGoodsById(Map<String, Object> map) throws Exception;

    public int editAssetPurchaseById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryAssetGoodsListByPurchaseId(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetPurchaseMationToEditById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryAssetPurchaseGoodsMationToEditById(Map<String, Object> map) throws Exception;

    public int updateAssetPurchaseMation(Map<String, Object> map) throws Exception;

    public int deleteAssetPurchaseGoodsMationById(Map<String, Object> map) throws Exception;

    public int editAssetPurchaseToRevoke(Map<String, Object> map) throws Exception;

    public int editAssetPurchaseGoodsToRevoke(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryAssetPurchaseGoodsById(Map<String, Object> map) throws Exception;

    public int editAssetPurchaseGoodsState(Map<String, Object> bean) throws Exception;

    public Map<String, Object> queryAssetPurchaseId(Map<String, Object> map) throws Exception;

}
