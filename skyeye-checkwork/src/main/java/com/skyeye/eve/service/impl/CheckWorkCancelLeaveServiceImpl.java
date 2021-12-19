/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.CheckWorkConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CheckWorkCancelLeaveDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.CheckWorkCancelLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CheckWorkCancelLeaveServiceImpl
 * @Description: 销假申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/11 9:49
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class CheckWorkCancelLeaveServiceImpl implements CheckWorkCancelLeaveService {

    @Autowired
    private CheckWorkCancelLeaveDao checkWorkCancelLeaveDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    @Autowired
    private ActivitiUserService activitiUserService;

    /**
     * 销假申请关联的工作流的key
     */
    private static final String CHECK_WORK_CANCEL_LEAVE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CHECK_WORK_CANCEL_LEAVE_PAGE.getKey();

    /**
     * 获取我的销假申请列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryMyCheckWorkCancelLeaveList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_CANCEL_LEAVE_PAGE_KEY).queryWithActivitiList();
    }

    /**
     * 新增销假申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void insertCheckWorkCancelLeaveMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 销假申请id
        String cancelLeaveId = ToolUtil.getSurFaceId();
        // 处理销假天数
        List<Map<String, Object>> beans = getCancelLeaveDays(map.get("cancelLeaveDayStr").toString(), cancelLeaveId);
        if(beans.size() == 0){
            outputObject.setreturnMessage("请填写销假日期.");
            return;
        }
        map.put("id", cancelLeaveId);
        map.put("oddNumber", CheckWorkConstants.getCheckWorkCancelLeaveOddNumber());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        checkWorkCancelLeaveDao.insertCheckWorkCancelLeaveMation(map);
        checkWorkCancelLeaveDao.insertCheckWorkCancelLeaveDaysMation(beans);
        // 操作工作流数据
        activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
            CHECK_WORK_CANCEL_LEAVE_PAGE_KEY, cancelLeaveId, map.get("approvalId").toString());
    }

    private List<Map<String, Object>> getCancelLeaveDays(String cancelLeaveDayStr, String cancelLeaveId) {
        List<Map<String, Object>> jArray = JSONUtil.toList(cancelLeaveDayStr, null);
        List<Map<String, Object>> beans = new ArrayList<>();
        for (int i = 0; i < jArray.size(); i++) {
            Map<String, Object> bean = jArray.get(i);
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("cancelLeaveId", cancelLeaveId);
            beans.add(bean);
        }
        return beans;
    }

    /**
     * 销假申请编辑时进行回显
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCheckWorkCancelLeaveToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = checkWorkCancelLeaveDao.queryCheckWorkCancelLeaveToEditById(id);
        // 获取销假日期信息
        List<Map<String, Object>> cancelLeaveDay = checkWorkCancelLeaveDao.queryCheckWorkCancelLeaveDaysMationToEditByCancelLeaveId(id);
        bean.put("cancelLeaveDay", cancelLeaveDay);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     * 编辑销假申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void updateCheckWorkCancelLeaveById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 销假申请id
        String cancelLeaveId = map.get("id").toString();
        // 处理销假天数
        List<Map<String, Object>> beans = getCancelLeaveDays(map.get("cancelLeaveDayStr").toString(), cancelLeaveId);
        if(beans.size() == 0){
            outputObject.setreturnMessage("请填写销假日期.");
            return;
        }
        checkWorkCancelLeaveDao.updateCheckWorkCancelLeaveMation(map);
        checkWorkCancelLeaveDao.deleteCheckWorkCancelLeaveDaysMationByCancelLeaveId(cancelLeaveId);
        checkWorkCancelLeaveDao.insertCheckWorkCancelLeaveDaysMation(beans);
        // 操作工作流数据
        activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
            CHECK_WORK_CANCEL_LEAVE_PAGE_KEY, cancelLeaveId, map.get("approvalId").toString());
    }

    /**
     * 销假申请详情
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCheckWorkCancelLeaveDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = checkWorkCancelLeaveDao.queryCheckWorkCancelLeaveDetailById(id);
        // 获取销假日期信息
        List<Map<String, Object>> cancelLeaveDay = checkWorkCancelLeaveDao.queryCheckWorkCancelLeaveDaysDetailByCancelLeaveId(id);
        bean.put("cancelLeaveDay", cancelLeaveDay);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     * 提交审批销假申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void editCheckWorkCancelLeaveToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String cancelLeaveId = map.get("id").toString();
        Map<String, Object> bean = checkWorkCancelLeaveDao.queryCheckWorkCancelLeaveDetailById(cancelLeaveId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            activitiUserService.addOrEditToSubmit(inputObject, outputObject, 2,
                CHECK_WORK_CANCEL_LEAVE_PAGE_KEY, cancelLeaveId, map.get("approvalId").toString());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 作废销假申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateCheckWorkCancelLeaveToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String cancelLeaveId = map.get("id").toString();
        Map<String, Object> bean = checkWorkCancelLeaveDao.queryCheckWorkCancelLeaveDetailById(cancelLeaveId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            checkWorkCancelLeaveDao.updateCheckWorkCancelLeaveStateById(cancelLeaveId, 4);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 撤销销假申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void editCheckWorkCancelLeaveToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_CANCEL_LEAVE_PAGE_KEY).revokeActivi();
    }
}
