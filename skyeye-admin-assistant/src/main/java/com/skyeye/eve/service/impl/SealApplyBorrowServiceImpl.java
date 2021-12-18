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
import com.skyeye.eve.dao.SealApplyBorrowDao;
import com.skyeye.eve.dao.SealDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.SealApplyBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealApplyBorrowServiceImpl
 * @Description: 印章借用服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 15:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SealApplyBorrowServiceImpl implements SealApplyBorrowService {

    @Autowired
    private SealApplyBorrowDao sealApplyBorrowDao;

    @Autowired
    private SealDao sealDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    @Autowired
    private ActivitiUserService activitiUserService;

    /**
     * 印章借用关联的工作流的key
     */
    private static final String ACTIVITI_SEAL_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_SEAL_USE_PAGE.getKey();

    /**
     *
     * @Title: queryMyBorrowSealList
     * @Description: 获取我借用的印章列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryMyBorrowSealList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_SEAL_USE_PAGE_KEY).queryWithActivitiList();
    }

    /**
     *
     * @Title: insertBorrowSealMation
     * @Description: 印章借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void insertBorrowSealMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String borrowId = ToolUtil.getSurFaceId();//借用单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getSealList(map.get("sealStr").toString(), borrowId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择印章");
            return;
        }
        map.put("id", borrowId);
        map.put("oddNumber", AdminAssistantConstants.AdminAssistantType.SEAL_BORROW_ODD_NUMBER_TO_USE.getOrderNum());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        sealApplyBorrowDao.insertSealBorrowMation(map);
        sealApplyBorrowDao.insertSealBorrowGoodsMation(entitys);
        // 操作工作流数据
        activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
            ACTIVITI_SEAL_USE_PAGE_KEY, borrowId, map.get("approvalId").toString());
    }

    private List<Map<String, Object>> getSealList(String sealBorrowStr, String borrowId, String state) throws Exception {
        List<Map<String, Object>> jArray = JSONUtil.toList(sealBorrowStr, null);
        // 印章实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        for (int i = 0; i < jArray.size(); i++) {
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> entity = sealDao.querySealEntityById(bean);
            if (!entity.isEmpty()) {
                entity.put("id", ToolUtil.getSurFaceId());
                entity.put("useId", borrowId);//借用单主表id
                entity.put("remark", bean.get("remark"));//备注
                entity.put("state", state);//状态
                entitys.add(entity);
            }
        }
        return entitys;
    }

    /**
     *
     * @Title: queryBorrowSealMationToDetails
     * @Description: 印章借用申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryBorrowSealMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String borrowId = map.get("id").toString();
        // 获取借用单信息
        Map<String, Object> bean = sealApplyBorrowDao.querySealBorrowMationById(borrowId);
        Integer state = Integer.parseInt(bean.get("state").toString());
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        // 获取印章信息
        List<Map<String, Object>> goods = sealApplyBorrowDao.queryBorrowSealById(borrowId);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: queryBorrowSealMationToEdit
     * @Description: 印章借用申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryBorrowSealMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String borrowId = map.get("id").toString();
        // 获取借用单信息
        Map<String, Object> bean = sealApplyBorrowDao.querySealBorrowMationById(borrowId);
        // 获取印章信息
        List<Map<String, Object>> goods = sealApplyBorrowDao.querySealBorrowGoodsMationToEditById(map);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: updateBorrowSealMationById
     * @Description: 编辑印章借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void updateBorrowSealMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String borrowId = map.get("id").toString();//借用单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        // 处理数据
        List<Map<String, Object>> entitys = getSealList(map.get("sealStr").toString(), borrowId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择印章");
            return;
        }
        sealApplyBorrowDao.updateSealBorrowMation(map);
        sealApplyBorrowDao.deleteSealBorrowGoodsMationById(map);
        sealApplyBorrowDao.insertSealBorrowGoodsMation(entitys);
        // 操作工作流数据
        activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
            ACTIVITI_SEAL_USE_PAGE_KEY, borrowId, map.get("approvalId").toString());
    }

    /**
     *
     * @Title: editBorrowSealToSubApproval
     * @Description: 印章借用申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void editBorrowSealToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String borrowId = map.get("id").toString();
        // 查询该借用申请单信息
        Map<String, Object> bean = sealApplyBorrowDao.querySealBorrowMationById(borrowId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            activitiUserService.addOrEditToSubmit(inputObject, outputObject, 2,
                ACTIVITI_SEAL_USE_PAGE_KEY, borrowId, map.get("approvalId").toString());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateBorrowSealToCancellation
     * @Description: 作废印章借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateBorrowSealToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String borrowId = map.get("id").toString();
        // 查询该借用申请单信息
        Map<String, Object> bean = sealApplyBorrowDao.querySealBorrowMationById(borrowId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            sealApplyBorrowDao.updateSealBorrowToCancellation(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateBorrowSealToRevoke
     * @Description: 撤销印章借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void updateBorrowSealToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_SEAL_USE_PAGE_KEY).revokeActivi();
    }

}
