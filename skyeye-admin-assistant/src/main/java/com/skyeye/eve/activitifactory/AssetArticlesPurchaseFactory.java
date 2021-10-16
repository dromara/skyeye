/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.activitifactory;

import com.skyeye.activiti.factory.ActivitiFactory;
import com.skyeye.activiti.factory.ActivitiFactoryResult;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.AssetArticlesApplyPurchaseDao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetArticlesPurchaseFactory
 * @Description: 用品采购申请工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 11:53
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AssetArticlesPurchaseFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private AssetArticlesApplyPurchaseDao assetArticlesApplyPurchaseDao;

    public AssetArticlesPurchaseFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public AssetArticlesPurchaseFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        this.assetArticlesApplyPurchaseDao = SpringUtils.getBean(AssetArticlesApplyPurchaseDao.class);
    }

    /**
     * 获取和工作流相关操作的列表时获取数据
     *
     * @param inputParams 前台传递的入参
     * @return 结果
     * @throws Exception
     */
    @Override
    protected List<Map<String, Object>> queryWithActivitiListRunSql(Map<String, Object> inputParams) throws Exception {
        return assetArticlesApplyPurchaseDao.queryMyPurchaseAssetArticlesMation(inputParams);
    }

    /**
     * 提交数据到工作流时获取数据
     *
     * @param id 需要提交到工作流的主单据id
     * @return
     * @throws Exception
     */
    @Override
    protected Map<String, Object> submitToActiviGetDate(String id) throws Exception {
        Map<String, Object> assetArticlesPurchaseMation = assetArticlesApplyPurchaseDao.queryAssetArticlesPurchaseMationById(id);
        // 获取关联的用品信息
        List<Map<String, Object>> goods = assetArticlesApplyPurchaseDao.queryAssetArticlePurchaseGoodsMationById(id);
        assetArticlesPurchaseMation.put("goods", goods);
        // 获取附件信息
        assetArticlesPurchaseMation.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(assetArticlesPurchaseMation.get("enclosureInfo").toString()));
        return assetArticlesPurchaseMation;
    }

    /**
     * 提交数据到工作流后成功的回调函数
     *
     * @param id          需要提交到工作流的主单据id
     * @param processInId 提交成功后的流程实例id
     * @throws Exception
     */
    @Override
    protected void submitToActiviSuccess(String id, String processInId) throws Exception {
        // 修改为审核中
        assetArticlesApplyPurchaseDao.updateAssetArticlePurchaseStateISInAudit(id, processInId);
        // 采购商品单状态修改为等待发放
        assetArticlesApplyPurchaseDao.updateAssetArticlePurchaseGoodStateISInAudit(id);
    }

    /**
     * 撤销工作流成功后会执行的回调
     *
     * @param map 入参
     * @throws Exception
     */
    @Override
    protected void revokeActiviSuccess(Map<String, Object> map) throws Exception {
        // 查询用品采购主表的Id
        map = assetArticlesApplyPurchaseDao.queryAssetArticlesPurchaseId(map);
        // 撤销用品采购为撤销状态
        assetArticlesApplyPurchaseDao.editAssetArticlesPurchaseToRevoke(map);
        // 撤销用品采购单关联的用品为未提交审批状态
        assetArticlesApplyPurchaseDao.editAssetArticlesPurchaseGoodsToRevoke(map);
    }
}
