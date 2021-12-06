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
import com.skyeye.eve.dao.SealApplyRevertDao;
import com.skyeye.eve.dao.SealDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.SealApplyRevertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealApplyRevertServiceImpl
 * @Description: 印章归还申请服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 17:40
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SealApplyRevertServiceImpl implements SealApplyRevertService {

    @Autowired
    private SealApplyRevertDao sealApplyRevertDao;

    @Autowired
    private SealDao sealDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    /**
     * 印章归还关联的工作流的key
     */
    private static final String ACTIVITI_SEAL_REVERT_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_SEAL_REVERT_PAGE.getKey();

    /**
     *
     * @Title: queryMyRevertSealList
     * @Description: 获取我归还的印章列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryMyRevertSealList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_SEAL_REVERT_PAGE_KEY).queryWithActivitiList();
    }

    /**
     *
     * @Title: insertRevertSealMation
     * @Description: 印章归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertRevertSealMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String revertId = ToolUtil.getSurFaceId();//归还单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getSealList(map.get("sealStr").toString(), revertId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择印章");
            return;
        }
        map.put("id", revertId);
        map.put("oddNumber", AdminAssistantConstants.AdminAssistantType.SEAL_REVERT_ODD_NUMBER_TO_USE.getOrderNum());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        sealApplyRevertDao.insertSealRevertMation(map);
        sealApplyRevertDao.insertSealRevertGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_SEAL_REVERT_PAGE_KEY).submitToActivi(revertId, ActivitiConstants.APPROVAL_ID);
        }
    }

    private List<Map<String, Object>> getSealList(String sealRevertStr, String revertId, String state) throws Exception {
        List<Map<String, Object>> jArray = JSONUtil.toList(sealRevertStr, null);
        // 印章实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        for (int i = 0; i < jArray.size(); i++) {
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> entity = sealDao.querySealEntityById(bean);
            if (!entity.isEmpty()) {
                entity.put("id", ToolUtil.getSurFaceId());
                entity.put("revertId", revertId);//归还单主表id
                entity.put("remark", bean.get("remark"));//备注
                entity.put("state", state);//状态
                entitys.add(entity);
            }
        }
        return entitys;
    }

    /**
     *
     * @Title: queryRevertSealMationToDetails
     * @Description: 印章归还申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryRevertSealMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String revertId = map.get("id").toString();
        // 获取归还信息
        Map<String, Object> bean = sealApplyRevertDao.querySealRevertMationById(revertId);
        Integer state = Integer.parseInt(bean.get("state").toString());
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        // 获取印章信息
        List<Map<String, Object>> goods = sealApplyRevertDao.querySealMationListByRevertId(revertId);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: queryRevertSealMationToEdit
     * @Description: 印章归还申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryRevertSealMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 获取归还单信息
        Map<String, Object> bean = sealApplyRevertDao.querySealRevertMationToEditById(map);
        // 获取印章信息
        List<Map<String, Object>> goods = sealApplyRevertDao.querySealRevertGoodsMationToEditById(map);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: updateRevertSealMationById
     * @Description: 编辑印章归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateRevertSealMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String revertId = map.get("id").toString();//归还单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getSealList(map.get("sealStr").toString(), revertId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择印章");
            return;
        }
        sealApplyRevertDao.updateSealRevertMation(map);
        sealApplyRevertDao.deleteSealRevertGoodsMationById(map);
        sealApplyRevertDao.insertSealRevertGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_SEAL_REVERT_PAGE_KEY).submitToActivi(revertId, ActivitiConstants.APPROVAL_ID);
        }
    }

    /**
     *
     * @Title: updateRevertSealMationToSave
     * @Description: 编辑印章归还申请（已提交审批）
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateRevertSealMationToSave(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String revertId = map.get("id").toString();//归还单主表id
        // 处理数据
        List<Map<String, Object>> entitys = getSealList(map.get("sealStr").toString(), revertId, "1");
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择印章");
            return;
        }
        sealApplyRevertDao.updateSealRevertMation(map);
        sealApplyRevertDao.deleteSealRevertGoodsMationById(map);
        sealApplyRevertDao.insertSealRevertGoodsMation(entitys);
        // 编辑工作流中的数据
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_SEAL_REVERT_PAGE_KEY).editApplyMationInActiviti(revertId);
    }

    /**
     *
     * @Title: editRevertSealToSubApproval
     * @Description: 印章归还申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editRevertSealToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String revertId = map.get("id").toString();
        // 查询该归还单信息
        Map<String, Object> bean = sealApplyRevertDao.querySealRevertMationById(revertId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_SEAL_REVERT_PAGE_KEY).submitToActivi(revertId, ActivitiConstants.APPROVAL_ID);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateRevertSealToCancellation
     * @Description: 作废印章归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateRevertSealToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String revertId = map.get("id").toString();
        // 查询该归还单信息
        Map<String, Object> bean = sealApplyRevertDao.querySealRevertMationById(revertId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            sealApplyRevertDao.updateSealRevertToCancellation(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }


    /**
     *
     * @Title: updateRevertSealToRevoke
     * @Description: 撤销印章归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void updateRevertSealToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_SEAL_REVERT_PAGE_KEY).revokeActivi();
    }


}
