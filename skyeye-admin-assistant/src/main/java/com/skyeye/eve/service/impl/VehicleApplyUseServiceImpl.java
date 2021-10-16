/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.AdminAssistantConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.VehicleApplyUseDao;
import com.skyeye.eve.dao.VehicleDao;
import com.skyeye.eve.service.VehicleApplyUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @ClassName: VehicleApplyUseServiceImpl
 * @Description: 用车申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 17:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class VehicleApplyUseServiceImpl implements VehicleApplyUseService {

    @Autowired
    private VehicleDao vehicleDao;

    @Autowired
    private VehicleApplyUseDao vehicleApplyUseDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    /**
     * 用车申请关联的工作流的key
     */
    private static final String ACTIVITI_VEHICLE_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_VEHICLE_USE_PAGE.getKey();

    /**
     *
     * @Title: queryMyUseVehicleMation
     * @Description: 获取我的用车申请信息列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryMyUseVehicleMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_VEHICLE_USE_PAGE_KEY).queryWithActivitiList();
    }

    /**
     *
     * @Title: insertVehicleListToUse
     * @Description: 用车申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertVehicleMationToUse(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> m = vehicleDao.queryVehicleMationByVehicleId(map);
        if(m != null && !m.isEmpty()){
            map.put("designatedVehicleInfo", m.get("designatedVehicleInfo").toString());
        }
        String rowId = ToolUtil.getSurFaceId();
        map.put("id", rowId);
        map.put("oddNumber", AdminAssistantConstants.AdminAssistantType.VEHICLE_ODD_NUMBER_TO_USE.getOrderNum());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        vehicleApplyUseDao.insertVehicleMationToUse(map);
        // 判断是否提交审批
        if("2".equals(map.get("subType").toString())){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_VEHICLE_USE_PAGE_KEY).submitToActivi(rowId);
        }
    }

    /**
     *
     * @Title: queryVehicleUseDetails
     * @Description: 用车申请详情
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryVehicleUseDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = vehicleApplyUseDao.queryVehicleUseDetails(id);
        Integer state = Integer.parseInt(bean.get("state").toString());
        bean.put("stateName", ActivitiConstants.ActivitiState.getStateNameByState(state));
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: editVehicleUseToSubApproval
     * @Description: 用车申请提交审批
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editVehicleUseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 查询该用车申请的信息
        Map<String, Object> bean = vehicleApplyUseDao.queryVehicleUseDetails(id);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state) {
            // 草稿、审核不通过或者撤销状态下可以提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_VEHICLE_USE_PAGE_KEY).submitToActivi(id);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateVehicleUseToCancellation
     * @Description: 作废用车申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateVehicleUseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 查询该用车申请的信息
        Map<String, Object> bean = vehicleApplyUseDao.queryVehicleUseDetails(id);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state) {
            // 草稿、审核不通过或者撤销状态下可以作废
            vehicleApplyUseDao.updateVehicleUseToCancellation(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: queryVehicleUseMationToEdit
     * @Description: 获取用车申请信息用于编辑回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryVehicleUseMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = vehicleApplyUseDao.queryVehicleUseMationToEdit(map);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: updateVehicleUseMationToEdit
     * @Description: 用车申请编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateVehicleUseMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> m = vehicleDao.queryVehicleMationByVehicleId(map);
        if(m != null && !m.isEmpty()){
            map.put("designatedVehicleInfo", m.get("designatedVehicleInfo").toString());
        }
        vehicleApplyUseDao.updateVehicleUseMationToEdit(map);
        // 判断是否提交审批
        if("2".equals(map.get("subType").toString())){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_VEHICLE_USE_PAGE_KEY).submitToActivi(id);
        }
    }

    /**
     *
     * @Title: updateVehicleUseMationByIdInProcess
     * @Description: 在工作流中编辑用车申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateVehicleUseMationByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> m = vehicleDao.queryVehicleMationByVehicleId(map);
        if(m != null && !m.isEmpty()){
            map.put("designatedVehicleInfo", m.get("designatedVehicleInfo").toString());
        }
        vehicleApplyUseDao.updateVehicleUseMationToEdit(map);
        // 编辑流程表参数
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_VEHICLE_USE_PAGE_KEY).editApplyMationInActiviti(id);
    }

    /**
     *
     * @Title: updateVehicleUseToRevoke
     * @Description: 撤销用车申请
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateVehicleUseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, ACTIVITI_VEHICLE_USE_PAGE_KEY).revokeActivi();
    }

}
