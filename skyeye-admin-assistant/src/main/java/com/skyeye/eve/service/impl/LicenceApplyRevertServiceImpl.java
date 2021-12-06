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
import com.skyeye.eve.dao.LicenceApplyRevertDao;
import com.skyeye.eve.dao.LicenceDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.LicenceApplyRevertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: LicenceApplyRevertServiceImpl
 * @Description: 证照归还申请服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 10:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class LicenceApplyRevertServiceImpl implements LicenceApplyRevertService {

    @Autowired
    private LicenceApplyRevertDao licenceApplyRevertDao;

    @Autowired
    private LicenceDao licenceDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    /**
     * 证照归还关联的工作流的key
     */
    private static final String ACTIVITI_LICENCE_REVERT_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_LICENCE_REVERT_PAGE.getKey();

    /**
     *
     * @Title: queryMyRevertLicenceList
     * @Description: 获取我归还的证照列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryMyRevertLicenceList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_REVERT_PAGE_KEY).queryWithActivitiList();
    }

    /**
     *
     * @Title: insertRevertLicenceMation
     * @Description: 证照归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertRevertLicenceMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String revertId = ToolUtil.getSurFaceId();//归还单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        List<Map<String, Object>> entitys = getLicenceListByParams(map.get("licenceStr").toString(), revertId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择证照");
            return;
        }
        map.put("id", revertId);
        map.put("oddNumber", AdminAssistantConstants.AdminAssistantType.LICENCE_REVERT_ODD_NUMBER_TO_USE.getOrderNum());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        licenceApplyRevertDao.insertLicenceRevertMation(map);
        licenceApplyRevertDao.insertLicenceRevertGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_REVERT_PAGE_KEY).submitToActivi(revertId, ActivitiConstants.APPROVAL_ID);
        }
    }

    /**
     *
     * @Title: queryRevertLicenceMationToDetails
     * @Description: 证照归还申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryRevertLicenceMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 获取归还信息
        Map<String, Object> bean = licenceApplyRevertDao.queryLicenceRevertMationById(id);
        Integer state = Integer.parseInt(bean.get("state").toString());
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        // 获取关联的证照信息
        List<Map<String, Object>> goods = licenceApplyRevertDao.queryLicenceRevertChildListById(id);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: queryRevertLicenceMationToEdit
     * @Description: 证照归还申请编辑时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryRevertLicenceMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 获取归还信息
        Map<String, Object> bean = licenceApplyRevertDao.queryLicenceRevertMationById(id);
        // 获取关联的证照信息
        List<Map<String, Object>> goods = licenceApplyRevertDao.queryLicenceRevertChildListById(id);
        bean.put("goods", goods);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: updateRevertLicenceMationById
     * @Description: 编辑证照归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateRevertLicenceMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String revertId = map.get("id").toString();//归还单主表id
        String subType = map.get("subType").toString();
        String state = ActivitiConstants.getSave2DBState(subType);
        List<Map<String, Object>> entitys = getLicenceListByParams(map.get("licenceStr").toString(), revertId, state);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择证照");
            return;
        }
        licenceApplyRevertDao.updateLicenceRevertMation(map);
        licenceApplyRevertDao.deleteLicenceRevertGoodsMationById(map);
        licenceApplyRevertDao.insertLicenceRevertGoodsMation(entitys);
        // 判断是否提交审批
        if("2".equals(subType)){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_REVERT_PAGE_KEY).submitToActivi(revertId, ActivitiConstants.APPROVAL_ID);
        }
    }

    /**
     * 根据入参选择的证照信息组装新的证照数据
     *
     * @param paramsStr 入参选择的证照信息
     * @param revertId 证照所属单id
     * @param state 单据状态(即证照状态)
     * @return 新的证照数据
     */
    private List<Map<String, Object>> getLicenceListByParams(String paramsStr, String revertId, String state) throws Exception {
        // 证照实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        List<Map<String, Object>> jArray = JSONUtil.toList(paramsStr, null);
        for(int i = 0; i < jArray.size(); i++){
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> entity = licenceDao.queryLicenceEntityById(bean.get("licenceId").toString());
            if(!entity.isEmpty()){
                entity.put("id", ToolUtil.getSurFaceId());
                // 归还单主表id
                entity.put("revertId", revertId);
                entity.put("remark", bean.get("remark"));
                entity.put("state", state);
                entitys.add(entity);
            }
        }
        return entitys;
    }

    /**
     *
     * @Title: updateRevertLicenceMationToSave
     * @Description: 编辑证照归还申请（已提交审批）
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateRevertLicenceMationToSave(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String revertId = map.get("id").toString();//归还单主表id
        List<Map<String, Object>> entitys = getLicenceListByParams(map.get("licenceStr").toString(), revertId, "1");
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择证照");
            return;
        }
        licenceApplyRevertDao.updateLicenceRevertMation(map);
        licenceApplyRevertDao.deleteLicenceRevertGoodsMationById(map);
        licenceApplyRevertDao.insertLicenceRevertGoodsMation(entitys);
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_REVERT_PAGE_KEY).editApplyMationInActiviti(revertId);
    }

    /**
     *
     * @Title: editRevertLicenceToSubApproval
     * @Description: 证照归还申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editRevertLicenceToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 查询该归还申请的信息
        Map<String, Object> bean = licenceApplyRevertDao.queryLicenceRevertMationById(id);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_REVERT_PAGE_KEY).submitToActivi(id, ActivitiConstants.APPROVAL_ID);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateRevertLicenceToCancellation
     * @Description: 作废证照归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateRevertLicenceToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 查询该归还申请的信息
        Map<String, Object> bean = licenceApplyRevertDao.queryLicenceRevertMationById(id);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            licenceApplyRevertDao.editLicenceRevertStateById(id, 4);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateRevertLicenceMationToRevoke
     * @Description: 撤销证照归还申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void updateRevertLicenceMationToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_LICENCE_REVERT_PAGE_KEY).revokeActivi();
    }

}
