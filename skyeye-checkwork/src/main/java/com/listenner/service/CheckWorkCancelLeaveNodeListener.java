/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.CheckWorkCancelLeaveDao;
import com.skyeye.eve.dao.CheckWorkLeaveDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CheckWorkLeaveNodeListener
 * @Description: 销假申请监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/4 23:06
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public class CheckWorkCancelLeaveNodeListener implements JavaDelegate {

    /**
     * 值为pass，则通过，为nopass，则不通过
     */
    private Expression state;

    /**
     * 销假申请关联的工作流的key
     */
    private static final String CHECK_WORK_LEAVE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CHECK_WORK_CANCEL_LEAVE_PAGE.getKey();

    private CheckWorkCancelLeaveDao checkWorkCancelLeaveDao;

    private CheckWorkLeaveDao checkWorkLeaveDao;

    private SysEveUserStaffDao sysEveUserStaffDao;

    public CheckWorkCancelLeaveNodeListener(){
        checkWorkCancelLeaveDao = SpringUtils.getBean(CheckWorkCancelLeaveDao.class);
        checkWorkLeaveDao = SpringUtils.getBean(CheckWorkLeaveDao.class);
        sysEveUserStaffDao = SpringUtils.getBean(SysEveUserStaffDao.class);
    }

    /**
     *
     * @param execution
     * @throws Exception
     */
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();//流程实例id
        Map<String, Object> mation = checkWorkCancelLeaveDao.queryCheckWorkCancelLeaveByProcessInstanceId(processInstanceId);
        String cancelLeaveId = mation.get("id").toString();
        String createId = mation.get("createId").toString();
        //服务任务状态值
        String value1 = (String) state.getValue(execution);
        // 修改状态
        if("pass".equals(value1.toLowerCase())){
            // 通过
            checkWorkCancelLeaveDao.updateCheckWorkCancelLeaveStateById(cancelLeaveId, 2);
            calcUserStaffCancelLeaveMation(cancelLeaveId, createId);
        }else{
            checkWorkCancelLeaveDao.updateCheckWorkCancelLeaveStateById(cancelLeaveId, 3);
            checkWorkCancelLeaveDao.updateCheckWorkCancelLeaveDaysStateByCancelLeaveId(cancelLeaveId, 3);
        }
        // 编辑流程表参数
        ActivitiRunFactory.run(CHECK_WORK_LEAVE_PAGE_KEY).editApplyMationInActiviti(cancelLeaveId);
    }

    /**
     * 计算销假中关联的年假信息，更新员工年假
     *
     * @param cancelLeaveId 销假信息id
     * @param createId 创建人id
     */
    private void calcUserStaffCancelLeaveMation(String cancelLeaveId, String createId) throws Exception {
        // 用户信息
        Map<String, Object> staff = sysEveUserStaffDao.querySysUserStaffDetailsByUserId(createId);
        // 员工id
        String staffId = staff.get("id").toString();
        // 员工当前剩余年假
        String annualLeave = staff.get("annualLeave").toString();
        // 员工当前剩余补休
        String holidayNumber = staff.get("holidayNumber").toString();
        // 员工当前已休补休
        String retiredHolidayNumber = staff.get("retiredHolidayNumber").toString();
        // 获取销假天数信息
        List<Map<String, Object>> cancelLeaveDay = checkWorkCancelLeaveDao.queryCheckWorkCancelLeaveDaysDetailByCancelLeaveId(cancelLeaveId);
        for(Map<String, Object> day: cancelLeaveDay){
            String cancelLeaveTimeId = day.get("id").toString();
            String cancelDay = day.get("cancelDay").toString();
            String timeId = day.get("timeId").toString();
            String cancelHour = day.get("cancelHour").toString();
            // 判断该员工在这一天是否有销假成功的记录，如果有，则审核失败，如果没有，则继续操作
            Map<String, Object> mation = checkWorkCancelLeaveDao.queryCheckWorkCancelLeaveByMation(createId, cancelDay);
            if(mation == null || mation.isEmpty()){
                // 判断该员工在这一天是否有请假记录，如果没有，则审核失败，如果有，则继续操作
                Map<String, Object> leaveDayMation = checkWorkLeaveDao.queryCheckWorkLeaveByMation(createId, timeId, cancelDay);
                if(leaveDayMation != null && !leaveDayMation.isEmpty()){
                    Integer useYearHoliday = Integer.parseInt(leaveDayMation.get("useYearHoliday").toString());
                    if(useYearHoliday == 1){
                        // 如果之前请假使用的是年假，则恢复年假
                        annualLeave = CalculationUtil.add(annualLeave, cancelHour, 2);
                    }else if(useYearHoliday == 3){
                        // 如果之前请假使用的是补休，则恢复补休
                        retiredHolidayNumber = CalculationUtil.subtract(retiredHolidayNumber, cancelHour, 2);
                        holidayNumber = CalculationUtil.add(holidayNumber, cancelHour, 2);
                    }
                    // 修改销假记录为成功
                    checkWorkCancelLeaveDao.updateCheckWorkCancelLeaveSoltStateById(cancelLeaveTimeId, 2);
                    continue;
                }
            }
            checkWorkCancelLeaveDao.updateCheckWorkCancelLeaveSoltStateById(cancelLeaveTimeId, 3);
        }
        // 修改员工剩余年假信息
        sysEveUserStaffDao.editSysUserStaffAnnualLeaveById(staffId, annualLeave, DateUtil.getTimeAndToString());
        // 修改员工剩余补休信息
        sysEveUserStaffDao.updateSysUserStaffHolidayNumberById(staffId, holidayNumber, DateUtil.getTimeAndToString());
        // 修改员工已休补休信息
        sysEveUserStaffDao.updateSysUserStaffRetiredHolidayNumberById(staffId, retiredHolidayNumber, DateUtil.getTimeAndToString());
    }

}
