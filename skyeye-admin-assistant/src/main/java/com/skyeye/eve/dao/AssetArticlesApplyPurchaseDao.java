/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetArticlesPurchaseDao
 * @Description: 用品采购申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 11:40
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetArticlesApplyPurchaseDao {

    public int insertAssetArticlePurchaseMation(Map<String, Object> map) throws Exception;

    public int insertAssetArticlePurchaseGoodsMation(List<Map<String, Object>> entitys) throws Exception;

    public List<Map<String, Object>> queryMyPurchaseAssetArticlesMation(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetArticlePurchaseStateById(Map<String, Object> map) throws Exception;

    /**
     * 根据用品采购单id获取关联的用品信息
     *
     * @param purchaseId 用品采购单id
     * @return 关联的用品信息
     * @throws Exception
     */
    public List<Map<String, Object>> queryAssetArticlePurchaseGoodsMationById(@Param("purchaseId") String purchaseId) throws Exception;

    /**
     * 根据用品采购单id获取用品采购单信息
     *
     * @param id 用品采购单id
     * @return 用品采购单信息
     * @throws Exception
     */
    public Map<String, Object> queryAssetArticlesPurchaseMationById(@Param("id") String id) throws Exception;

    /**
     * 修改用品采购单为审核中
     *
     * @param id 用品采购申请id
     * @param processInId 流程实例id
     * @return
     * @throws Exception
     */
    public int updateAssetArticlePurchaseStateISInAudit(@Param("id") String id, @Param("processInId") String processInId) throws Exception;

    public Map<String, Object> queryAssetArticlePurchaseMationToEditById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryAssetArticlePurchaseGoodsMationToEditById(Map<String, Object> map) throws Exception;

    public int updateAssetArticlePurchaseMation(Map<String, Object> map) throws Exception;

    public int deleteAssetArticlePurchaseGoodsMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetsArticlesPurchaseByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public List<Map<String, Object>> queryAssetsArticlesPurchaseGoodsById(Map<String, Object> map) throws Exception;

    public int editAssetsArticlesPurchaseGoodsById(Map<String, Object> bean) throws Exception;

    public int editAssetsArticlesPurchaseById(Map<String, Object> map) throws Exception;

    public int updateAssetArticlesPurchaseToCancellation(Map<String, Object> map) throws Exception;

    /**
     * 修改用品采购单关联的用品信息为审核中
     *
     * @param purchaseId 用品采购申请id
     * @return
     * @throws Exception
     */
    public int updateAssetArticlePurchaseGoodStateISInAudit(@Param("purchaseId") String purchaseId) throws Exception;

    public int editAssetsArticlesPurchaseGoodsByPurchaseId(Map<String, Object> map) throws Exception;

    public int editAssetArticlesPurchaseToRevoke(Map<String, Object> map) throws Exception;

    public int editAssetArticlesPurchaseGoodsToRevoke(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetArticlesPurchaseId(Map<String, Object> map) throws Exception;

}
