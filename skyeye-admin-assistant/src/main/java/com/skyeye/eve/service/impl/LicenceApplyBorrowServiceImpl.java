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
import com.skyeye.eve.dao.LicenceApplyBorrowDao;
import com.skyeye.eve.dao.LicenceDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.LicenceApplyBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: LicenceApplyBorrowServiceImpl
 * @Description: 证照借用服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 22:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class LicenceApplyBorrowServiceImpl implements LicenceApplyBorrowService {

    @Autowired
    private LicenceApplyBorrowDao licenceApplyBorrowDao;

    @Autowired
    private LicenceDao licenceDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    /**
     * 证照借用关联的工作流的key
     */
    private static final String ACTIVITI_LICENCE_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_LICENCE_USE_PAGE.getKey();

    /**
     *
     * @Title: queryMyBorrowLicenceList
     * @Description: 获取我借用的证照列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    @Override
    public void queryMyBorrowLicenceList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_USE_PAGE_KEY).queryWithActivitiList();
    }

    /**
     *
     * @Title: insertBorrowLicenceMation
     * @Description: 证照借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertBorrowLicenceMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = ToolUtil.getSurFaceId();//借用单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        List<Map<String, Object>> entitys = getLicenceList(map.get("licenceStr").toString(), useId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择证照");
            return;
        }
        map.put("id", useId);
        map.put("oddNumber", AdminAssistantConstants.AdminAssistantType.LICENCE_BORROW_ODD_NUMBER_TO_USE.getOrderNum());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        licenceApplyBorrowDao.insertLicenceBorrowMation(map);
        licenceApplyBorrowDao.insertLicenceBorrowGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_USE_PAGE_KEY).submitToActivi(useId, ActivitiConstants.APPROVAL_ID);
        }
    }

    private List<Map<String, Object>> getLicenceList(String paramsStr, String useId, String state) throws Exception {
        // 证照实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        List<Map<String, Object>> jArray = JSONUtil.toList(paramsStr, null);
        for(int i = 0; i < jArray.size(); i++){
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> entity = licenceDao.queryLicenceEntityById(bean.get("licenceId").toString());
            if(!entity.isEmpty()){
                entity.put("id", ToolUtil.getSurFaceId());
                // 借用单主表id
                entity.put("useId", useId);
                // 备注
                entity.put("remark", bean.get("remark"));
                // 状态
                entity.put("state", state);
                entitys.add(entity);
            }
        }
        return entitys;
    }

    /**
     *
     * @Title: queryBorrowLicenceMationToDetails
     * @Description: 证照借用申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryBorrowLicenceMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 获取借用单信息
        Map<String, Object> bean = licenceApplyBorrowDao.queryLicenceBorrowMationById(id);
        Integer state = Integer.parseInt(bean.get("state").toString());
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        // 获取证照信息
        List<Map<String, Object>> goods = licenceApplyBorrowDao.queryLicenceBorrowGoodsByUseId(id);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: queryBorrowLicenceMationToEdit
     * @Description: 证照借用申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    @Override
    public void queryBorrowLicenceMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 获取借用信息
        Map<String, Object> bean = licenceApplyBorrowDao.queryLicenceBorrowMationToEditById(map);
        // 获取证照信息
        List<Map<String, Object>> goods = licenceApplyBorrowDao.queryLicenceBorrowGoodsMationToEditById(map);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: updateBorrowLicenceMationById
     * @Description: 编辑证照借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(value="transactionManager")
    public void updateBorrowLicenceMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = map.get("id").toString();//借用单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        List<Map<String, Object>> entitys = getLicenceList(map.get("licenceStr").toString(), useId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择证照");
            return;
        }
        licenceApplyBorrowDao.updateLicenceBorrowMation(map);
        licenceApplyBorrowDao.deleteLicenceBorrowGoodsMationById(map);
        licenceApplyBorrowDao.insertLicenceBorrowGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_USE_PAGE_KEY).submitToActivi(useId, ActivitiConstants.APPROVAL_ID);
        }
    }

    /**
     *
     * @Title: updateBorrowLicenceMationToSave
     * @Description: 编辑证照借用申请（已提交审批）
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateBorrowLicenceMationToSave(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String useId = map.get("id").toString();//借用单主表id
        List<Map<String, Object>> entitys = getLicenceList(map.get("licenceStr").toString(), useId, "1");
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择证照");
            return;
        }
        licenceApplyBorrowDao.updateLicenceBorrowMation(map);
        licenceApplyBorrowDao.deleteLicenceBorrowGoodsMationById(map);
        licenceApplyBorrowDao.insertLicenceBorrowGoodsMation(entitys);
        // 更新工作流数据
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_USE_PAGE_KEY).editApplyMationInActiviti(useId);
    }

    /**
     *
     * @Title: editBorrowLicenceToSubApproval
     * @Description: 证照借用申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editBorrowLicenceToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 查询该借用申请单的信息
        Map<String, Object> bean = licenceApplyBorrowDao.queryLicenceBorrowMationById(id);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_USE_PAGE_KEY).submitToActivi(id, ActivitiConstants.APPROVAL_ID);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateBorrowLicenceToCancellation
     * @Description: 作废证照借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateBorrowLicenceToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 查询该借用申请单的信息
        Map<String, Object> bean = licenceApplyBorrowDao.queryLicenceBorrowMationById(id);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            licenceApplyBorrowDao.updateLicenceBorrowToCancellation(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateBorrowLicenceMationToRevoke
     * @Description: 撤销证照借用申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void updateBorrowLicenceMationToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_USE_PAGE_KEY).revokeActivi();
    }

}
