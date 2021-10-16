/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetArticlesApplyUseDao
 * @Description: 用品领用申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetArticlesApplyUseDao {

    public List<Map<String, Object>> queryMyUseAssetArticlesMation(Map<String, Object> map) throws Exception;

    public int insertAssetArticleUseMation(Map<String, Object> bean) throws Exception;

    public int insertAssetArticleUseGoodsMation(List<Map<String, Object>> entitys) throws Exception;

    public Map<String, Object> queryAssetArticleUseMationToEditById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryAssetArticleUseGoodsMationToEditById(Map<String, Object> map) throws Exception;

    public int updateAssetArticleUseMation(Map<String, Object> map) throws Exception;

    public int deleteAssetArticleUseGoodsMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetArticleUseStateById(Map<String, Object> map) throws Exception;

    /**
     * 修改用品领用单为审核中
     *
     * @param id 用品领用申请id
     * @param processInId 流程实例id
     * @return
     * @throws Exception
     */
    public int updateAssetArticleUseStateISInAudit(@Param("id") String id, @Param("processInId") String processInId) throws Exception;

    /**
     * 根据用品领用申请id获取用品领用单详情
     *
     * @param id 用品领用申请id
     * @return 用品领用单详情
     * @throws Exception
     */
    public Map<String, Object> queryAssetArticlesUseMationById(@Param("id") String id) throws Exception;

    /**
     * 根据用品领用申请id获取关联的用品信息
     *
     * @param useId 用品领用申请id
     * @return 关联的用品信息
     * @throws Exception
     */
    public List<Map<String, Object>> queryAssetArticleUseGoodsMationById(@Param("useId") String useId) throws Exception;

    public Map<String, Object> queryAssetsArticlesUseByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public List<Map<String, Object>> queryAssetsArticlesUseGoodsById(Map<String, Object> map) throws Exception;

    public int editAssetsArticlesUseGoodsById(Map<String, Object> bean) throws Exception;

    public int editAssetsArticlesUseById(Map<String, Object> map) throws Exception;

    /**
     * 修改用品领用单关联的用品信息为审核中
     * @param useId 用品领用申请id
     * @return
     * @throws Exception
     */
    public int updateAssetArticleUseGoodStateISInAudit(@Param("useId") String useId) throws Exception;

    public int editAssetsArticlesUseGoodsByUseId(Map<String, Object> map) throws Exception;

    public int editAssetArticlesUseToRevoke(Map<String, Object> map) throws Exception;

    public int editAssetArticlesUseGoodsToRevoke(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssetArticlesUseId(Map<String, Object> map) throws Exception;

    public int updateAssetArticlesToCancellation(Map<String, Object> map) throws Exception;

}
