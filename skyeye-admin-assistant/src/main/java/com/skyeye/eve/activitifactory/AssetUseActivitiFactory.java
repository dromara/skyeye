/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.activitifactory;

import com.skyeye.activiti.factory.ActivitiFactory;
import com.skyeye.activiti.factory.ActivitiFactoryResult;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.AssetApplyUseDao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetUseActivitiFactory
 * @Description: 资产领用工作流工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/16 23:24
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AssetUseActivitiFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private AssetApplyUseDao assetApplyUseDao;

    public AssetUseActivitiFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public AssetUseActivitiFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        this.assetApplyUseDao = SpringUtils.getBean(AssetApplyUseDao.class);
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
        return assetApplyUseDao.queryMyUseAssetMation(inputParams);
    }

    /**
     * 提交数据到工作流时获取数据
     *
     * @param id 需要提交到工作流的主单据id---资产领用单id
     * @return
     * @throws Exception
     */
    @Override
    protected Map<String, Object> submitToActiviGetDate(String id) throws Exception {
        Map<String, Object> assetUseMation = assetApplyUseDao.queryAssetUseMationById(id);
        // 获取资产信息
        List<Map<String, Object>> goods = assetApplyUseDao.queryAssetUseGoodsMationById(id);
        assetUseMation.put("goods", goods);
        // 获取附件信息
        assetUseMation.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(assetUseMation.get("enclosureInfo").toString()));
        return assetUseMation;
    }

    /**
     * 提交数据到工作流后成功的回调函数
     *
     * @param id 需要提交到工作流的主单据id---资产领用单id
     * @param processInId 提交成功后的流程实例id
     * @throws Exception
     */
    @Override
    protected void submitToActiviSuccess(String id, String processInId) throws Exception {
        // 领用主表单状态修改为审核中
        assetApplyUseDao.updateAssetUseStateISInAudit(id, processInId);
        // 领用商品单状态修改为等待发放
        assetApplyUseDao.updateAssetUseGoodStateISInAudit(id);
    }

    /**
     * 撤销工作流成功后会执行的回调
     *
     * @param map 入参
     * @throws Exception
     */
    @Override
    protected void revokeActiviSuccess(Map<String, Object> map) throws Exception {
        // 查询资产领用主表的Id
        map = assetApplyUseDao.queryAssetUseId(map);
        // 撤销资产领用为撤销状态
        assetApplyUseDao.editAssetUseToRevoke(map);
        // 撤销资产领用的资产为未提交审批状态
        assetApplyUseDao.editAssetUseGoodsToRevoke(map);
    }

}
