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
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CheckWorkLeaveDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.CheckWorkLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CheckWorkLeaveServiceImpl
 * @Description: 请假申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/14 13:04
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CheckWorkLeaveServiceImpl implements CheckWorkLeaveService {

    @Autowired
    private CheckWorkLeaveDao checkWorkLeaveDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    @Autowired
    private ActivitiUserService activitiUserService;

    /**
     * 请假申请关联的工作流的key
     */
    private static final String CHECK_WORK_LEAVE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CHECK_WORK_LEAVE_PAGE.getKey();

    /**
     * 获取我的请假申请列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryMyCheckWorkLeaveList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_LEAVE_PAGE_KEY).queryWithActivitiList();
    }

    /**
     * 新增请假申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
    public void insertCheckWorkLeaveMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 请假申请id
        String leaveId = ToolUtil.getSurFaceId();
        // 处理请假天数
        List<Map<String, Object>> beans = getLeaveDays(map.get("leaveDayStr").toString(), leaveId);
        if(beans.size() == 0){
            outputObject.setreturnMessage("请填写请假日期.");
            return;
        }
        map.put("id", leaveId);
        map.put("oddNumber", Constants.getCheckWorkLeaveOddNumber());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        checkWorkLeaveDao.insertCheckWorkLeaveMation(map);
        checkWorkLeaveDao.insertCheckWorkLeaveDaysMation(beans);
        // 操作工作流数据
        activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
            CHECK_WORK_LEAVE_PAGE_KEY, leaveId, map.get("approvalId").toString());
    }

    private List<Map<String, Object>> getLeaveDays(String leaveDayStr, String leaveId) {
        List<Map<String, Object>> jArray = JSONUtil.toList(leaveDayStr, null);
        List<Map<String, Object>> beans = new ArrayList<>();
        for (int i = 0; i < jArray.size(); i++) {
            Map<String, Object> bean = jArray.get(i);
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("leaveId", leaveId);
            beans.add(bean);
        }
        return beans;
    }

    /**
     * 请假申请编辑时进行回显
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCheckWorkLeaveToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = checkWorkLeaveDao.queryCheckWorkLeaveToEditById(id);
        // 获取请假日期信息
        List<Map<String, Object>> leaveDay = checkWorkLeaveDao.queryCheckWorkLeaveDaysMationToEditByLeaveId(id);
        bean.put("leaveDay", leaveDay);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     * 编辑请假申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
    public void updateCheckWorkLeaveById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 请假申请id
        String leaveId = map.get("id").toString();
        // 处理请假天数
        List<Map<String, Object>> beans = getLeaveDays(map.get("leaveDayStr").toString(), leaveId);
        if(beans.size() == 0){
            outputObject.setreturnMessage("请填写请假日期.");
            return;
        }
        checkWorkLeaveDao.updateCheckWorkLeaveMation(map);
        checkWorkLeaveDao.deleteCheckWorkLeaveDaysMationByLeaveId(leaveId);
        checkWorkLeaveDao.insertCheckWorkLeaveDaysMation(beans);
        // 操作工作流数据
        activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
            CHECK_WORK_LEAVE_PAGE_KEY, leaveId, map.get("approvalId").toString());
    }

    /**
     * 请假申请详情
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCheckWorkLeaveDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = checkWorkLeaveDao.queryCheckWorkLeaveDetailById(id);
        // 获取请假日期信息
        List<Map<String, Object>> leaveDay = checkWorkLeaveDao.queryCheckWorkLeaveDaysDetailByLeaveId(id);
        bean.put("leaveDay", leaveDay);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     * 提交审批请假申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
    public void editCheckWorkLeaveToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String leaveId = map.get("id").toString();
        Map<String, Object> bean = checkWorkLeaveDao.queryCheckWorkLeaveDetailById(leaveId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            activitiUserService.addOrEditToSubmit(inputObject, outputObject, 2,
                CHECK_WORK_LEAVE_PAGE_KEY, leaveId, map.get("approvalId").toString());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 作废请假申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateCheckWorkLeaveToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String leaveId = map.get("id").toString();
        Map<String, Object> bean = checkWorkLeaveDao.queryCheckWorkLeaveDetailById(leaveId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            checkWorkLeaveDao.updateCheckWorkLeaveStateById(leaveId, 4);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 撤销请假申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
    public void editCheckWorkLeaveToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_LEAVE_PAGE_KEY).revokeActivi();
    }

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的请假申请数据
     *
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> queryStateIsSuccessLeaveDayByUserIdAndMonths(String userId, String timeId, List<String> months) throws Exception {
        List<Map<String, Object>> beans = checkWorkLeaveDao.queryStateIsSuccessLeaveDayByUserIdAndMonths(userId, timeId, months);
        beans.forEach(bean -> {
            bean.put("type", CheckWorkConstants.CheckDayType.DAY_IS_LEAVE.getType());
            bean.put("className", CheckWorkConstants.CheckDayType.DAY_IS_LEAVE.getClassName());
            bean.put("allDay", "1");
            bean.put("showBg", "2");
            bean.put("editable", false);
        });
        return beans;
    }
}
