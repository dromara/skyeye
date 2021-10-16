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
import com.skyeye.eve.dao.AssetArticlesApplyPurchaseDao;
import com.skyeye.eve.dao.AssetArticlesDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.AssetArticlesApplyPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetArticlesPurchaseServiceImpl
 * @Description: 用品采购申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 11:41
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AssetArticlesApplyPurchaseServiceImpl implements AssetArticlesApplyPurchaseService {

    @Autowired
    private AssetArticlesApplyPurchaseDao assetArticlesApplyPurchaseDao;

    @Autowired
    private AssetArticlesDao assetArticlesDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    /**
     * 用品采购关联的工作流的key
     */
    private static final String ACTIVITI_ASSETARTICLES_PURCHASE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_ASSETARTICLES_PURCHASE_PAGE.getKey();

    /**
     *
     * @Title: queryMyPurchaseAssetArticlesMation
     * @Description: 获取我采购的用品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryMyPurchaseAssetArticlesMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSETARTICLES_PURCHASE_PAGE_KEY).queryWithActivitiList();
    }

    /**
     *
     * @Title: insertAssetArticlesListToPurchase
     * @Description: 用品采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertAssetArticlesListToPurchase(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = ToolUtil.getSurFaceId();//采购单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getAssetArticlesList(map.get("assetArticlesStr").toString(), purchaseId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择用品");
            return;
        }
        map.put("id", purchaseId);
        map.put("oddNumber", AdminAssistantConstants.AdminAssistantType.ASSET_ARTICLES_ODD_NUMBER_TO_PURCHASE.getOrderNum());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        assetArticlesApplyPurchaseDao.insertAssetArticlePurchaseMation(map);
        assetArticlesApplyPurchaseDao.insertAssetArticlePurchaseGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSETARTICLES_PURCHASE_PAGE_KEY).submitToActivi(purchaseId);
        }
    }

    private List<Map<String, Object>> getAssetArticlesList(String assetArticlesStr, String purchaseId, String state) throws Exception {
        List<Map<String, Object>> jArray = JSONUtil.toList(assetArticlesStr, null);
        // 用品实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        for (int i = 0; i < jArray.size(); i++) {
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> entity = assetArticlesDao.queryAssetArticleById(bean);
            entity.put("id", ToolUtil.getSurFaceId());
            entity.put("purchaseId", purchaseId);//采购单主表id
            entity.put("applyPurchaseNum", bean.get("purchaseNum"));//采购数量
            entity.put("remark", bean.get("remark"));//备注
            entity.put("state", state);//状态
            entitys.add(entity);
        }
        return entitys;
    }

    /**
     *
     * @Title: queryAssetArticlesListPurchaseDetailsById
     * @Description: 用品采购申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssetArticlesListPurchaseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = map.get("id").toString();
        // 获取采购信息
        Map<String, Object> bean = assetArticlesApplyPurchaseDao.queryAssetArticlesPurchaseMationById(purchaseId);
        Integer state = Integer.parseInt(bean.get("state").toString());
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        // 获取用品信息
        List<Map<String, Object>> goods = assetArticlesApplyPurchaseDao.queryAssetArticlePurchaseGoodsMationById(purchaseId);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: editAssetArticlesUseToPurchaseSubApproval
     * @Description: 用品采购申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetArticlesUseToPurchaseSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = map.get("id").toString();
        // 查询该采购申请单信息
        Map<String, Object> bean = assetArticlesApplyPurchaseDao.queryAssetArticlesPurchaseMationById(purchaseId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSETARTICLES_PURCHASE_PAGE_KEY).submitToActivi(purchaseId);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: queryAssetArticlesListPurchaseToEditById
     * @Description: 用品采购申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssetArticlesListPurchaseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 获取采购信息
        Map<String, Object> bean = assetArticlesApplyPurchaseDao.queryAssetArticlePurchaseMationToEditById(map);
        // 获取用品信息
        List<Map<String, Object>> goods = assetArticlesApplyPurchaseDao.queryAssetArticlePurchaseGoodsMationToEditById(map);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: updateAssetArticlesListToPurchaseById
     * @Description: 编辑用品采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetArticlesListToPurchaseById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = map.get("id").toString();//采购单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getAssetArticlesList(map.get("assetArticlesStr").toString(), purchaseId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择用品");
            return;
        }
        assetArticlesApplyPurchaseDao.updateAssetArticlePurchaseMation(map);
        assetArticlesApplyPurchaseDao.deleteAssetArticlePurchaseGoodsMationById(map);
        assetArticlesApplyPurchaseDao.insertAssetArticlePurchaseGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSETARTICLES_PURCHASE_PAGE_KEY).submitToActivi(purchaseId);
        }
    }

    /**
     *
     * @Title: updateAssetArticlesPurchaseToCancellation
     * @Description: 作废用品采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetArticlesPurchaseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = map.get("id").toString();
        // 查询该采购申请单信息
        Map<String, Object> bean = assetArticlesApplyPurchaseDao.queryAssetArticlesPurchaseMationById(purchaseId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            assetArticlesApplyPurchaseDao.updateAssetArticlesPurchaseToCancellation(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateAssetArticlesListToPurchaseByIdInProcess
     * @Description: 在工作流中编辑用品采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetArticlesListToPurchaseByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String purchaseId = map.get("id").toString();//采购单主表id
        // 处理数据
        List<Map<String, Object>> entitys = getAssetArticlesList(map.get("assetArticlesStr").toString(), purchaseId, "1");
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择用品");
            return;
        }
        assetArticlesApplyPurchaseDao.updateAssetArticlePurchaseMation(map);
        assetArticlesApplyPurchaseDao.deleteAssetArticlePurchaseGoodsMationById(map);
        assetArticlesApplyPurchaseDao.insertAssetArticlePurchaseGoodsMation(entitys);
        // 编辑流程表参数
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSETARTICLES_PURCHASE_PAGE_KEY).editApplyMationInActiviti(purchaseId);
    }

    /**
     *
     * @Title: editAssetArticlesPurchaseToRevoke
     * @Description: 撤销用品采购申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetArticlesPurchaseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSETARTICLES_PURCHASE_PAGE_KEY).revokeActivi();
    }

}
