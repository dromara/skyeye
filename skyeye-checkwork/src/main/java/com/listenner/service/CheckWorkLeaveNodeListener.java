/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import cn.hutool.json.JSONUtil;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.CheckWorkLeaveDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.service.SystemFoundationSettingsService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: CheckWorkLeaveNodeListener
 * @Description: 请假申请监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/4 23:06
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public class CheckWorkLeaveNodeListener implements JavaDelegate {

    // 值为pass，则通过，为nopass，则不通过
    private Expression state;

    /**
     * 请假申请关联的工作流的key
     */
    private static final String CHECK_WORK_LEAVE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CHECK_WORK_LEAVE_PAGE.getKey();

    private CheckWorkLeaveDao checkWorkLeaveDao;

    private SysEveUserStaffDao sysEveUserStaffDao;

    private SystemFoundationSettingsService systemFoundationSettingsService;

    public CheckWorkLeaveNodeListener(){
        checkWorkLeaveDao = SpringUtils.getBean(CheckWorkLeaveDao.class);
        sysEveUserStaffDao = SpringUtils.getBean(SysEveUserStaffDao.class);
        systemFoundationSettingsService = SpringUtils.getBean(SystemFoundationSettingsService.class);
    }

    /**
     *
     * @param execution
     * @throws Exception
     */
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // 流程实例id
        String processInstanceId = execution.getProcessInstanceId();
        Map<String, Object> mation = checkWorkLeaveDao.queryCheckWorkLeaveByProcessInstanceId(processInstanceId);
        String leaveId = mation.get("id").toString();
        String createId = mation.get("createId").toString();
        // 服务任务状态值
        String value1 = (String) state.getValue(execution);
        // 修改状态
        if("pass".equalsIgnoreCase(value1)){
            // 通过
            checkWorkLeaveDao.updateCheckWorkLeaveStateById(leaveId, 2);
            calcUserStaffYearMation(leaveId, createId);
        }else{
            checkWorkLeaveDao.updateCheckWorkLeaveStateById(leaveId, 3);
            checkWorkLeaveDao.updateCheckWorkLeaveDaysStateByLeaveId(leaveId, 3);
        }
        // 编辑流程表参数
        ActivitiRunFactory.run(CHECK_WORK_LEAVE_PAGE_KEY).editApplyMationInActiviti(leaveId);
    }

    /**
     * 计算请假中关联的年假信息，更新员工年假
     *
     * @param leaveId 请假信息id
     * @param createId 创建人id
     */
    private void calcUserStaffYearMation(String leaveId, String createId) throws Exception {
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
        // 获取请假扣薪制度
        List<Map<String, Object>> holidaysTypeMation = getSystemHolidaysTypeJsonMation();
        // 获取请假天数信息
        List<Map<String, Object>> leaveDay = checkWorkLeaveDao.queryCheckWorkLeaveDaysDetailByLeaveId(leaveId);
        for(Map<String, Object> day: leaveDay){
            String leaveSoltId = day.get("id").toString();
            // 请假时长
            String leaveHour = day.get("leaveHour").toString();
            // 假期类型
            String leaveType = day.get("leaveType").toString();
            List<Map<String, Object>> holidays = holidaysTypeMation.stream()
                    .filter(bean -> leaveType.equals(bean.get("holidayNo").toString())).collect(Collectors.toList());
            if(holidays != null && !holidays.isEmpty()){
                // 是否使用年假，1.是   2.否
                Integer whetherYearHour = Integer.parseInt(holidays.get(0).get("whetherYearHour").toString());
                // 是否使用补休，1.是   2.否
                Integer whetherComLeave = Integer.parseInt(holidays.get(0).get("whetherComLeave").toString());
                if(whetherYearHour == 1){
                    if (annualLeave.equals(CalculationUtil.getMax(annualLeave, leaveHour, 2))) {
                        // 剩余年假够用
                        annualLeave = CalculationUtil.subtract(annualLeave, leaveHour, 2);
                        checkWorkLeaveDao.updateCheckWorkLeaveSoltStateByLeaveId(leaveSoltId, 2, 1);
                    } else {
                        // 剩余年假不够用，则默认审核不通过
                        checkWorkLeaveDao.updateCheckWorkLeaveSoltStateByLeaveId(leaveSoltId, 3, null);
                    }
                    continue;
                }
                if(whetherComLeave == 1){
                    if (holidayNumber.equals(CalculationUtil.getMax(holidayNumber, leaveHour, 2))) {
                        // 剩余补休够用
                        holidayNumber = CalculationUtil.subtract(holidayNumber, leaveHour, 2);
                        retiredHolidayNumber = CalculationUtil.add(retiredHolidayNumber, leaveHour, 2);
                        checkWorkLeaveDao.updateCheckWorkLeaveSoltStateByLeaveId(leaveSoltId, 2, 3);
                    } else {
                        // 剩余补休不够用，则默认审核不通过
                        checkWorkLeaveDao.updateCheckWorkLeaveSoltStateByLeaveId(leaveSoltId, 3, null);
                    }
                }
            }
            checkWorkLeaveDao.updateCheckWorkLeaveSoltStateByLeaveId(leaveSoltId, 2, null);
        }
        // 修改员工剩余年假信息
        sysEveUserStaffDao.editSysUserStaffAnnualLeaveById(staffId, annualLeave, DateUtil.getTimeAndToString());
        // 修改员工剩余补休信息
        sysEveUserStaffDao.updateSysUserStaffHolidayNumberById(staffId, holidayNumber, DateUtil.getTimeAndToString());
        // 修改员工已休补休信息
        sysEveUserStaffDao.updateSysUserStaffRetiredHolidayNumberById(staffId, retiredHolidayNumber, DateUtil.getTimeAndToString());
    }

    /**
     * 获取请假扣薪制度
     *
     * @return 请假扣薪制度
     * @throws Exception
     */
    private List<Map<String, Object>> getSystemHolidaysTypeJsonMation() throws Exception {
        Map<String, Object> sysSetting = getSystemFoundationSettings();
        String holidaysTypeJson = sysSetting.get("holidaysTypeJson").toString();
        return JSONUtil.toList(holidaysTypeJson, null);
    }


    /**
     * 获取系统基础配置信息
     *
     * @return 系统基础配置信息
     * @throws Exception
     */
    private Map<String, Object> getSystemFoundationSettings() throws Exception {
        return systemFoundationSettingsService.getSystemFoundationSettings();
    }
}
