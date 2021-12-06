/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.AdminAssistantConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.AssetApplyPurchaseDao;
import com.skyeye.eve.dao.AssetDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.AssetApplyPurchaseService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetPurchaseServiceImpl
 * @Description: 资产采购单服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 23:29
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AssetApplyPurchaseServiceImpl implements AssetApplyPurchaseService {

    @Autowired
    private AssetDao assetDao;

    @Autowired
    private AssetApplyPurchaseDao assetApplyPurchaseDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    /**
     * 资产采购关联的工作流的key
     */
    private static final String ACTIVITI_ASSET_PURCHAES_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_ASSET_PURCHAES_PAGE.getKey();

    /**
     * 获取我采购的资产信息列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryMyPurchaseAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_PURCHAES_PAGE_KEY).queryWithActivitiList();
    }

    /**
     * 新增资产采购申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
    public void insertAssetListToPurchase(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = ToolUtil.getSurFaceId();// 采购单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getAssetMationList(outputObject, map.get("assetListStr").toString(), purchaseId, state);
        if (entitys == null) return;
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择资产");
            return;
        }
        map.put("id", purchaseId);
        map.put("oddNumber", AdminAssistantConstants.AdminAssistantType.ASSET_ODD_NUMBER_TO_PURCHASE.getOrderNum());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        assetApplyPurchaseDao.insertAssetPurchaseMation(map);
        assetApplyPurchaseDao.insertAssetPurchaseGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_PURCHAES_PAGE_KEY).submitToActivi(purchaseId, ActivitiConstants.APPROVAL_ID);
        }
    }

    private List<Map<String, Object>> getAssetMationList(OutputObject outputObject, String assetArticlesStr, String purchaseId, String state) throws Exception {
        List<Map<String, Object>> jArray = JSONUtil.toList(assetArticlesStr, null);
        // 资产实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        for(int i = 0; i < jArray.size(); i++){
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> m = assetDao.queryAnythingFromAsset(bean);
            if(m != null){
                outputObject.setreturnMessage("采购单中某些资产已经存在！");
                return null;
            }
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("purchaseId", purchaseId);
            bean.put("state", state);
            entitys.add(bean);
        }
        return entitys;
    }

    /**
     * 资产采购申请提交审批
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetPurchaseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = map.get("id").toString();
        // 查询资产采购单信息
        Map<String, Object> bean = assetApplyPurchaseDao.queryAssetPurchaseMationById(purchaseId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_PURCHAES_PAGE_KEY).submitToActivi(purchaseId, ActivitiConstants.APPROVAL_ID);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 资产采购申请详情
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAssetListPurchaseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = map.get("id").toString();
        // 获取采购信息
        Map<String, Object> bean = assetApplyPurchaseDao.queryAssetPurchaseMationById(purchaseId);
        Integer state = Integer.parseInt(bean.get("state").toString());
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        // 获取资产信息
        List<Map<String, Object>> goods = assetApplyPurchaseDao.queryAssetPurchaseGoodsMationById(purchaseId);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     * 作废资产采购申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetPurchaseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = map.get("id").toString();
        // 查询资产采购单信息
        Map<String, Object> bean = assetApplyPurchaseDao.queryAssetPurchaseMationById(purchaseId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            assetApplyPurchaseDao.updateAssetPurchaseToCancellation(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 资产采购申请编辑时进行回显
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAssetListPurchaseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 获取采购信息
        Map<String, Object> bean = assetApplyPurchaseDao.queryAssetPurchaseMationToEditById(map);
        // 获取资产信息
        List<Map<String, Object>> goods = assetApplyPurchaseDao.queryAssetPurchaseGoodsMationToEditById(map);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     * 编辑资产采购申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetListToPurchaseById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = map.get("id").toString();//采购单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getAssetMationList(outputObject, map.get("assetListStr").toString(), purchaseId, state);
        if (entitys == null) return;
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择资产");
            return;
        }
        assetApplyPurchaseDao.updateAssetPurchaseMation(map);
        assetApplyPurchaseDao.deleteAssetPurchaseGoodsMationById(map);
        assetApplyPurchaseDao.insertAssetPurchaseGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_PURCHAES_PAGE_KEY).submitToActivi(purchaseId, ActivitiConstants.APPROVAL_ID);
        }
    }

    /**
     * 在工作流中编辑资产采购申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetListToPurchaseByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = map.get("id").toString();//采购单主表id
        // 处理数据
        List<Map<String, Object>> entitys = getAssetMationList(outputObject, map.get("assetListStr").toString(), purchaseId, "1");
        if (entitys == null) return;
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择资产");
            return;
        }
        assetApplyPurchaseDao.updateAssetPurchaseMation(map);
        assetApplyPurchaseDao.deleteAssetPurchaseGoodsMationById(map);
        assetApplyPurchaseDao.insertAssetPurchaseGoodsMation(entitys);
        // 编辑流程表参数
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_PURCHAES_PAGE_KEY).editApplyMationInActiviti(purchaseId);
    }

    /**
     * 撤销资产采购申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetPurchaseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_PURCHAES_PAGE_KEY).revokeActivi();
    }

}
