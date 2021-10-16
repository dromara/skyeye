/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.activitifactory;

import com.skyeye.activiti.factory.ActivitiFactory;
import com.skyeye.activiti.factory.ActivitiFactoryResult;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.AssetApplyReturnDao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetReturnFactory
 * @Description: 资产归还单工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/20 22:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AssetReturnFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private AssetApplyReturnDao assetApplyReturnDao;

    public AssetReturnFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public AssetReturnFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        this.assetApplyReturnDao = SpringUtils.getBean(AssetApplyReturnDao.class);
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
        return assetApplyReturnDao.queryMyReturnAssetMation(inputParams);
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
        Map<String, Object> returnMation = assetApplyReturnDao.queryAssetReturnMationById(id);
        // 获取资产信息
        List<Map<String, Object>> goods = assetApplyReturnDao.queryAssetReturnGoodsMationById(id);
        returnMation.put("goods", goods);
        // 获取附件信息
        returnMation.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(returnMation.get("enclosureInfo").toString()));
        return returnMation;
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
        // 归还主表单状态修改为审核中
        assetApplyReturnDao.updateAssetReturnStateISInAudit(id, processInId);
        // 归还商品单状态修改为等待审批
        assetApplyReturnDao.updateAssetReturnGoodStateISInAudit(id);
    }

    /**
     * 撤销工作流成功后会执行的回调
     *
     * @param map 入参
     * @throws Exception
     */
    @Override
    protected void revokeActiviSuccess(Map<String, Object> map) throws Exception {
        String processInstanceId = map.get("processInstanceId").toString();
        // 查询资产归还主表的Id
        map = assetApplyReturnDao.queryAssetReturnId(processInstanceId);
        // 撤销资产归还为撤销状态
        assetApplyReturnDao.editAssetReturnToRevoke(map);
        // 撤销资产归还的资产为未提交审批状态
        assetApplyReturnDao.editAssetReturnGoodsToRevoke(map);
    }
}
