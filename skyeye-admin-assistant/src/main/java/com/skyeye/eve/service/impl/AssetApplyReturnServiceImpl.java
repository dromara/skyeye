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
import com.skyeye.eve.dao.AssetApplyReturnDao;
import com.skyeye.eve.dao.AssetDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.AssetApplyReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetApplyReturnServiceImpl
 * @Description: 资产归还单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/20 22:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AssetApplyReturnServiceImpl implements AssetApplyReturnService {

    @Autowired
    private AssetApplyReturnDao assetApplyReturnDao;

    @Autowired
    private AssetDao assetDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    /**
     * 资产归还关联的工作流的key
     */
    private static final String ACTIVITI_ASSET_RETURN_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_ASSET_RETURN_PAGE.getKey();

    /**
     *
     * @Title: queryMyReturnAssetMation
     * @Description: 获取我归还的资产信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryMyReturnAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_RETURN_PAGE_KEY).queryWithActivitiList();
    }

    /**
     *
     * @Title: queryMyUnReturnAssetListByTypeId
     * @Description: 根据资产类别获取我领用后未归还的资产列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryMyUnReturnAssetListByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("employeeId", user.get("id"));
        List<Map<String, Object>> beans = assetApplyReturnDao.queryMyUnReturnAssetListByTypeId(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     *
     * @Title: insertAssetListToReturn
     * @Description: 资产归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(value="transactionManager")
    public void insertAssetListToReturn(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String returnId = ToolUtil.getSurFaceId();//归还单主表id
        String subType = map.get("subType").toString();
        // 处理数据
        String state = ActivitiConstants.getSave2DBState(subType);
        List<Map<String, Object>> entitys = getAssetList(map, returnId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择资产");
            return;
        }
        map.put("id", returnId);
        map.put("oddNumber", AdminAssistantConstants.AdminAssistantType.ASSET_ODD_NUMBER_TO_RETURN.getOrderNum());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        assetApplyReturnDao.insertAssetReturnMation(map);
        assetApplyReturnDao.insertAssetReturnGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(map.get("subType").toString())){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_RETURN_PAGE_KEY).submitToActivi(returnId, ActivitiConstants.APPROVAL_ID);
        }
    }

    /**
     *
     * @Title: editAssetReturnToSubApproval
     * @Description: 资产归还申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetReturnToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String returnId = map.get("id").toString();
        // 获取资产归还单id
        Map<String, Object> bean = assetApplyReturnDao.queryAssetReturnMationById(returnId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_RETURN_PAGE_KEY).submitToActivi(returnId, ActivitiConstants.APPROVAL_ID);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: queryAssetListReturnDetailsById
     * @Description: 资产归还申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssetListReturnDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String returnId = map.get("id").toString();
        // 获取资产归还单信息
        Map<String, Object> bean = assetApplyReturnDao.queryAssetReturnMationById(returnId);
        Integer state = Integer.parseInt(bean.get("state").toString());
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        // 获取关联资产信息
        List<Map<String, Object>> goods = assetApplyReturnDao.queryAssetReturnGoodsMationById(returnId);
        bean.put("goods", goods);
        // 获取关联附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: updateAssetReturnToCancellation
     * @Description: 作废资产归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetReturnToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String returnId = map.get("id").toString();
        // 获取资产归还单id
        Map<String, Object> bean = assetApplyReturnDao.queryAssetReturnMationById(returnId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            assetApplyReturnDao.updateAssetReturnToCancellation(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: queryAssetListReturnToEditById
     * @Description: 资产归还申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssetListReturnToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 获取归还单信息
        Map<String, Object> bean = assetApplyReturnDao.queryAssetReturnMationToEditById(map);
        // 获取关联的资产信息
        List<Map<String, Object>> goods = assetApplyReturnDao.queryAssetReturnGoodsMationToEditById(map);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: updateAssetListToReturnById
     * @Description: 编辑资产归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetListToReturnById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String returnId = map.get("id").toString();//归还单主表id
        String subType = map.get("subType").toString();
        // 处理数据
        String state = ActivitiConstants.getSave2DBState(subType);
        List<Map<String, Object>> entitys = getAssetList(map, returnId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择资产");
            return;
        }
        assetApplyReturnDao.updateAssetReturnMation(map);
        assetApplyReturnDao.deleteAssetReturnGoodsMationById(map);
        assetApplyReturnDao.insertAssetReturnGoodsMation(entitys);
        //判断是否提交审批
        if("2".equals(map.get("subType").toString())){
            //提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_RETURN_PAGE_KEY).submitToActivi(returnId, ActivitiConstants.APPROVAL_ID);
        }
    }

    /**
     *
     * @Title: updateAssetListToReturnByIdInProcess
     * @Description: 在工作流中编辑资产归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateAssetListToReturnByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String returnId = map.get("id").toString();//归还单主表id
        // 处理数据
        List<Map<String, Object>> entitys = getAssetList(map, returnId, "1");
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择资产");
            return;
        }
        assetApplyReturnDao.updateAssetReturnMation(map);
        assetApplyReturnDao.deleteAssetReturnGoodsMationById(map);
        assetApplyReturnDao.insertAssetReturnGoodsMation(entitys);
        // 编辑流程表参数
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_RETURN_PAGE_KEY).editApplyMationInActiviti(returnId);
    }

    private List<Map<String, Object>> getAssetList(Map<String, Object> map, String returnId, String state) throws Exception {
        List<Map<String, Object>> jArray = JSONUtil.toList(map.get("assetListStr").toString(), null);
        // 资产实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        for(int i = 0; i < jArray.size(); i++){
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> entity = assetDao.queryAssetById(bean);
            entity.put("id", ToolUtil.getSurFaceId());
            entity.put("returnId", returnId);// 归还单主表id
            entity.put("remark", bean.get("remark"));// 备注
            entity.put("state", state);// 状态
            entitys.add(entity);
        }
        return entitys;
    }

    /**
     *
     * @Title: editAssetReturnToRevoke
     * @Description: 撤销资产归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetReturnToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_ASSET_RETURN_PAGE_KEY).revokeActivi();
    }

}
