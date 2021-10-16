/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.activitifactory;

import com.skyeye.activiti.factory.ActivitiFactory;
import com.skyeye.activiti.factory.ActivitiFactoryResult;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.AssetApplyPurchaseDao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetPurchaseFactory
 * @Description: 资产采购单工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 23:26
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AssetPurchaseFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private AssetApplyPurchaseDao assetApplyPurchaseDao;

    public AssetPurchaseFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public AssetPurchaseFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        this.assetApplyPurchaseDao = SpringUtils.getBean(AssetApplyPurchaseDao.class);
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
        return assetApplyPurchaseDao.queryMyAssetPurchaseList(inputParams);
    }

    /**
     * 提交数据到工作流时获取数据
     *
     * @param id 需要提交到工作流的主单据id--资产采购单id
     * @return
     * @throws Exception
     */
    @Override
    protected Map<String, Object> submitToActiviGetDate(String id) throws Exception {
        Map<String, Object> purchaseMation = assetApplyPurchaseDao.queryAssetPurchaseMationById(id);
        // 获取资产信息
        List<Map<String, Object>> goods = assetApplyPurchaseDao.queryAssetPurchaseGoodsMationById(id);
        for(Map<String, Object> m : goods){
            m.put("managementImg", "<img src=" + m.get("managementImg").toString() + " class='photo-img'>");
        }
        purchaseMation.put("goods", goods);
        // 获取附件信息
        purchaseMation.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(purchaseMation.get("enclosureInfo").toString()));
        return purchaseMation;
    }

    /**
     * 提交数据到工作流后成功的回调函数
     *
     * @param id          需要提交到工作流的主单据id--资产采购单id
     * @param processInId 提交成功后的流程实例id
     * @throws Exception
     */
    @Override
    protected void submitToActiviSuccess(String id, String processInId) throws Exception {
        // 资产采购单修改为审核中
        assetApplyPurchaseDao.updateAssetPurchaseStateISInAudit(id, processInId);
        // 资产采购单关联的资产状态修改为等待审批
        assetApplyPurchaseDao.updateAssetPurchaseGoodStateISInAudit(id);
    }

    /**
     * 撤销工作流成功后会执行的回调
     *
     * @param map 入参
     * @throws Exception
     */
    @Override
    protected void revokeActiviSuccess(Map<String, Object> map) throws Exception {
        // 查询资产采购主表的Id
        map = assetApplyPurchaseDao.queryAssetPurchaseId(map);
        // 撤销资产采购为撤销状态
        assetApplyPurchaseDao.editAssetPurchaseToRevoke(map);
        // 撤销资产采购的资产为未提交审批状态
        assetApplyPurchaseDao.editAssetPurchaseGoodsToRevoke(map);
    }
}
