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
import com.skyeye.eve.dao.AssetApplyUseDao;
import com.skyeye.eve.dao.AssetDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.AssetApplyUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetApplyUseServiceImpl
 * @Description: 资产领用服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 17:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AssetApplyUseServiceImpl implements AssetApplyUseService {

    @Autowired
    private AssetDao assetDao;

    @Autowired
    private AssetApplyUseDao assetApplyUseDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    /**
     * 资产领用关联的工作流的key
     */
    private static final String ACTIVITI_ASSET_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_ASSET_USE_PAGE.getKey();

    /**
     *
     * @Title: queryMyUseAssetMation
     * @Description: 获取我领用的资产信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    @Override
    public void queryMyUseAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_USE_PAGE_KEY).queryWithActivitiList();
    }

    /**
     *
     * @Title: insertAssetListToUse
     * @Description: 资产领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertAssetListToUse(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = ToolUtil.getSurFaceId();//领用单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getAssetMationList(map.get("assetListStr").toString(), useId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择资产");
            return;
        }
        map.put("id", useId);
        map.put("oddNumber", AdminAssistantConstants.AdminAssistantType.ASSET_ODD_NUMBER_TO_USE.getOrderNum());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        assetApplyUseDao.insertAssetUseMation(map);
        assetApplyUseDao.insertAssetUseGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_USE_PAGE_KEY).submitToActivi(useId);
        }
    }

    /**
     * 获取资产领用单关联的资产信息
     *
     * @param assetArticlesStr 前台选中的资产
     * @param useId 领用单id
     * @param state 资产状态
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> getAssetMationList(String assetArticlesStr, String useId, String state) throws Exception {
        List<Map<String, Object>> jArray = JSONUtil.toList(assetArticlesStr, null);
        // 领用单关联的资产集合信息
        List<Map<String, Object>> assetList = new ArrayList<>();
        for (int i = 0; i < jArray.size(); i++) {
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> assetMation = assetDao.queryAssetById(bean);
            assetMation.put("id", ToolUtil.getSurFaceId());
            assetMation.put("useId", useId);//领用单主表id
            assetMation.put("remark", bean.get("remark"));//备注
            assetMation.put("state", state);//状态
            assetList.add(assetMation);
        }
        return assetList;
    }

    /**
     *
     * @Title: queryAssetListUseDetailsById
     * @Description: 资产领用申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssetListUseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        //获取领用信息
        Map<String, Object> bean = assetApplyUseDao.queryAssetUseMationById(id);
        Integer state = Integer.parseInt(bean.get("state").toString());
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        //获取资产信息
        List<Map<String, Object>> goods = assetApplyUseDao.queryAssetUseGoodsMationById(id);
        bean.put("goods", goods);
        //获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: queryAssetListUseToEditById
     * @Description: 资产领用申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssetListUseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        //获取领用信息
        Map<String, Object> bean = assetApplyUseDao.queryAssetUseMationToEditById(map);
        //获取资产信息
        List<Map<String, Object>> goods = assetApplyUseDao.queryAssetUseGoodsMationToEditById(map);
        bean.put("goods", goods);
        //获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: updateAssetArticlesListToUseById
     * @Description: 编辑资产领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetListToUseById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = map.get("id").toString();//领用单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getAssetMationList(map.get("assetListStr").toString(), useId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择资产");
            return;
        }
        assetApplyUseDao.updateAssetUseMation(map);
        assetApplyUseDao.deleteAssetUseGoodsMationById(map);
        assetApplyUseDao.insertAssetUseGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            //提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_USE_PAGE_KEY).submitToActivi(useId);
        }
    }

    /**
     *
     * @Title: updateAssetToCancellation
     * @Description: 作废资产领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = map.get("id").toString();
        // 查询该领用申请的状态
        Map<String, Object> bean = assetApplyUseDao.queryAssetUseMationById(useId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            assetDao.updateAssetToCancellation(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: editAssetUseToSubApproval
     * @Description: 资产领用申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetUseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = map.get("id").toString();
        // 查询该领用申请的状态
        Map<String, Object> bean = assetApplyUseDao.queryAssetUseMationById(useId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
            || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
            || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_USE_PAGE_KEY).submitToActivi(useId);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateAssetListToUseByIdInProcess
     * @Description: 在工作流中编辑资产领用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetListToUseByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = map.get("id").toString();//领用单主表id
        //处理数据
        List<Map<String, Object>> entitys = getAssetMationList(map.get("assetListStr").toString(), useId, "1");
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择资产");
            return;
        }
        assetApplyUseDao.updateAssetUseMation(map);
        assetApplyUseDao.deleteAssetUseGoodsMationById(map);
        assetApplyUseDao.insertAssetUseGoodsMation(entitys);
        // 编辑流程表参数
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_USE_PAGE_KEY).editApplyMationInActiviti(useId);
    }

    /**
     * 撤销资产领用申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetUseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_USE_PAGE_KEY).revokeActivi();
    }

}
