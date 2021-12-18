/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.AdminAssistantConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.AssetArticlesApplyUseDao;
import com.skyeye.eve.dao.AssetArticlesDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.AssetArticlesApplyUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetArticlesApplyUseServiceImpl
 * @Description: 用品领用申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AssetArticlesApplyUseServiceImpl implements AssetArticlesApplyUseService {

    @Autowired
    private AssetArticlesApplyUseDao assetArticlesApplyUseDao;

    @Autowired
    private AssetArticlesDao assetArticlesDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    @Autowired
    private ActivitiUserService activitiUserService;

    /**
     * 用品领用关联的工作流的key
     */
    private static final String ACTIVITI_ASSETARTICLES_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_ASSETARTICLES_USE_PAGE.getKey();

    /**
     *
     * @Title: queryMyUseAssetArticlesMation
     * @Description: 获取我领用的用品信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryMyUseAssetArticlesMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSETARTICLES_USE_PAGE_KEY).queryWithActivitiList();
    }

    /**
     *
     * @Title: insertAssetArticlesListToUse
     * @Description: 用品领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void insertAssetArticlesListToUse(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = ToolUtil.getSurFaceId();//领用单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getAssetArticlesList(outputObject, map.get("assetArticlesStr").toString(), useId, state);
        if (entitys == null) return;
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择用品");
            return;
        }
        map.put("id", useId);
        map.put("oddNumber", AdminAssistantConstants.AdminAssistantType.ASSET_ARTICLES_ODD_NUMBER_TO_USE.getOrderNum());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        assetArticlesApplyUseDao.insertAssetArticleUseMation(map);
        assetArticlesApplyUseDao.insertAssetArticleUseGoodsMation(entitys);
        // 操作工作流数据
        activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
            ACTIVITI_ASSETARTICLES_USE_PAGE_KEY, useId, map.get("approvalId").toString());
    }

    private List<Map<String, Object>> getAssetArticlesList(OutputObject outputObject, String assetArticlesStr, String useId, String state) throws Exception {
        List<Map<String, Object>> jArray = JSONUtil.toList(assetArticlesStr, null);
        // 用品实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        for (int i = 0; i < jArray.size(); i++) {
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> entity = assetArticlesDao.queryAssetArticleById(bean);
            // 库存余量是否大于领用数量
            if (Integer.parseInt(entity.get("residualNum").toString()) >= Integer.parseInt(bean.get("useNum").toString())) {
                // 库存余量充足
                entity.put("id", ToolUtil.getSurFaceId());
                entity.put("useId", useId);//领用单主表id
                entity.put("applyUseNum", bean.get("useNum"));//领用数量
                entity.put("remark", bean.get("remark"));//备注
                entity.put("state", state);//状态
                entitys.add(entity);
            } else {
                // 库存余量不足
                outputObject.setreturnMessage("用户【" + entity.get("articlesName").toString() + "】库存余量不足。");
                return null;
            }
        }
        return entitys;
    }

    /**
     *
     * @Title: queryAssetArticlesListUseDetailsById
     * @Description: 用品领用申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssetArticlesListUseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = map.get("id").toString();
        // 查询该领用申请信息
        Map<String, Object> bean = assetArticlesApplyUseDao.queryAssetArticlesUseMationById(useId);
        Integer state = Integer.parseInt(bean.get("state").toString());
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        // 获取关联的用品信息
        List<Map<String, Object>> goods = assetArticlesApplyUseDao.queryAssetArticleUseGoodsMationById(useId);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: queryAssetArticlesListUseToEditById
     * @Description: 用品领用申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssetArticlesListUseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        //获取领用信息
        Map<String, Object> bean = assetArticlesApplyUseDao.queryAssetArticleUseMationToEditById(map);
        //获取用品信息
        List<Map<String, Object>> goods = assetArticlesApplyUseDao.queryAssetArticleUseGoodsMationToEditById(map);
        bean.put("goods", goods);
        //获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: updateAssetArticlesListToUseById
     * @Description: 编辑用品领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void updateAssetArticlesListToUseById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = map.get("id").toString();//领用单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getAssetArticlesList(outputObject, map.get("assetArticlesStr").toString(), useId, state);
        if (entitys == null) return;
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择用品");
            return;
        }
        assetArticlesApplyUseDao.updateAssetArticleUseMation(map);
        assetArticlesApplyUseDao.deleteAssetArticleUseGoodsMationById(map);
        assetArticlesApplyUseDao.insertAssetArticleUseGoodsMation(entitys);
        // 操作工作流数据
        activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
            ACTIVITI_ASSETARTICLES_USE_PAGE_KEY, useId, map.get("approvalId").toString());
    }

    /**
     *
     * @Title: editAssetArticlesUseToSubApproval
     * @Description: 用品领用申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void editAssetArticlesUseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = map.get("id").toString();
        // 查询该领用申请信息
        Map<String, Object> bean = assetArticlesApplyUseDao.queryAssetArticlesUseMationById(useId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            activitiUserService.addOrEditToSubmit(inputObject, outputObject, 2,
                ACTIVITI_ASSETARTICLES_USE_PAGE_KEY, useId, map.get("approvalId").toString());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateAssetArticlesToCancellation
     * @Description: 作废用品领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetArticlesToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = map.get("id").toString();
        // 查询该领用申请信息
        Map<String, Object> bean = assetArticlesApplyUseDao.queryAssetArticlesUseMationById(useId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            assetArticlesApplyUseDao.updateAssetArticlesToCancellation(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: editAssetArticlesUseToRevoke
     * @Description: 撤销用品领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void editAssetArticlesUseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSETARTICLES_USE_PAGE_KEY).revokeActivi();
    }

}
