/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.CheckWorkOvertimeDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.service.CompanyDepartmentService;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CheckWorkOvertimeNodeListener
 * @Description: 加班申请监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/4 23:06
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public class CheckWorkOvertimeNodeListener implements JavaDelegate {

    // 值为pass，则通过，为nopass，则不通过
    private Expression state;

    /**
     * 加班申请关联的工作流的key
     */
    private static final String CHECK_WORK_OVERTIME_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CHECK_WORK_OVERTIME_PAGE.getKey();

    private CheckWorkOvertimeDao checkWorkOvertimeDao;

    private SysEveUserStaffDao sysEveUserStaffDao;

    private CompanyDepartmentService companyDepartmentService;

    public CheckWorkOvertimeNodeListener(){
        checkWorkOvertimeDao = SpringUtils.getBean(CheckWorkOvertimeDao.class);
        sysEveUserStaffDao = SpringUtils.getBean(SysEveUserStaffDao.class);
        companyDepartmentService = SpringUtils.getBean(CompanyDepartmentService.class);
    }

    @SneakyThrows
    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();//流程实例id
        Map<String, Object> mation = checkWorkOvertimeDao.queryCheckWorkOvertimeByProcessInstanceId(processInstanceId);
        String overtimeId = mation.get("id").toString();
        String createId = mation.get("createId").toString();
        String value1 = (String) state.getValue(execution);
        // 修改状态
        if("pass".equalsIgnoreCase(value1)){
            // 通过
            checkWorkOvertimeDao.updateCheckWorkOvertimeStateById(overtimeId, 2);
            calcUserStaffOvertimeMation(overtimeId, createId);
        }else{
            checkWorkOvertimeDao.updateCheckWorkOvertimeStateById(overtimeId, 3);
            checkWorkOvertimeDao.updateCheckWorkOvertimeDaysStateByOvertimeId(overtimeId, 3);
        }
        // 编辑流程表参数
        ActivitiRunFactory.run(CHECK_WORK_OVERTIME_PAGE_KEY).editApplyMationInActiviti(overtimeId);
    }

    /**
     * 校验该单据中的天数是否符合规则
     *
     * @param overtimeId 加班信息id
     * @param createId 创建人id
     */
    private void calcUserStaffOvertimeMation(String overtimeId, String createId) throws Exception {
        // 用户信息
        Map<String, Object> staff = sysEveUserStaffDao.querySysUserStaffDetailsByUserId(createId);
        // 用户所在部门的加班结算方式
        int overtimeSettlementType = companyDepartmentService.getDepartmentOvertimeSettlementType(staff.get("departmentId").toString());
        // 员工id
        String staffId = staff.get("id").toString();
        // 员工当前剩余补休
        String holidayNumber = staff.get("holidayNumber").toString();
        // 获取加班天数信息
        List<Map<String, Object>> businessTripDay = checkWorkOvertimeDao.queryCheckWorkOvertimeDaysDetailByOvertimeId(overtimeId);
        for(Map<String, Object> day: businessTripDay){
            String overtimeSoltId = day.get("id").toString();
            String overtimeDay = day.get("overtimeDay").toString();
            String overtimeHour = day.get("overtimeHour").toString();
            List<Map<String, Object>> inData = checkWorkOvertimeDao.queryPassThisDayAndCreateId(createId, overtimeDay);
            if(inData == null || inData.isEmpty()){
                // 如果指定天还没有审批通过的记录，则审批通过
                checkWorkOvertimeDao.updateCheckWorkOvertimeSoltStateByOvertimeId(overtimeSoltId, 2);
                if(overtimeSettlementType == 6){
                    // 补休结算
                    holidayNumber = CalculationUtil.add(holidayNumber, overtimeHour, 2);
                    // 修改加班电子流的结算状态
                    checkWorkOvertimeDao.updateOvertimeSettleState(overtimeSoltId, 2);
                }
                // 修改加班电子流的结算类型
                checkWorkOvertimeDao.updateOvertimeSettlementType(overtimeSoltId, overtimeSettlementType);
            }else{
                checkWorkOvertimeDao.updateCheckWorkOvertimeSoltStateByOvertimeId(overtimeSoltId, 3);
            }
        }
        // 修改员工剩余补休信息
        sysEveUserStaffDao.updateSysUserStaffHolidayNumberById(staffId, holidayNumber, DateUtil.getTimeAndToString());
    }

}
