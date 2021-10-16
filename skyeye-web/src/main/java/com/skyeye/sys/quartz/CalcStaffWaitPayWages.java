/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sys.quartz;

import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.eve.dao.CheckWorkOvertimeDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.entity.quartz.SysQuartzRunHistory;
import com.skyeye.eve.service.CheckWorkTimeService;
import com.skyeye.eve.service.SysEveUserStaffCapitalService;
import com.skyeye.eve.service.SysQuartzRunHistoryService;
import com.skyeye.eve.service.WagesStaffMationService;
import com.skyeye.wages.constant.WagesConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CalcStaffWaitPayWages
 * @Description: 定时统计员工待结算其他奖金的数据
 *                  1. 加班结算
 * @author: skyeye云系列--卫志强
 * @date: 2021/9/2 15:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class CalcStaffWaitPayWages {

    private static Logger log = LoggerFactory.getLogger(CalcStaffWaitPayWages.class);

    private static final String QUARTZ_ID = QuartzConstants.SysQuartzMateMationJobType.CALC_STAFF_WAIT_PAY_WAGES_QUARTZ.getQuartzId();

    @Autowired
    private SysQuartzRunHistoryService sysQuartzRunHistoryService;

    @Autowired
    private CheckWorkOvertimeDao checkWorkOvertimeDao;

    @Autowired
    private CheckWorkTimeService checkWorkTimeService;

    @Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;

    @Autowired
    private WagesStaffMationService wagesStaffMationService;

    @Autowired
    private SysEveUserStaffCapitalService sysEveUserStaffCapitalService;

    /**
     * 定时统计员工待结算其他奖金的数据 凌晨一点半执行
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 30 1 * * ?")
    public void handler() throws Exception {
        String historyId = sysQuartzRunHistoryService.startSysQuartzRun(QUARTZ_ID);
        log.info("定时统计员工待结算薪资的数据定时任务开始执行");
        try{
            calcWaitWages();
        } catch (Exception e){
            sysQuartzRunHistoryService.endSysQuartzRun(historyId, SysQuartzRunHistory.State.START_ERROR.getState());
            log.warn("CalcStaffWaitPayWages error.", e);
        }
        log.info("定时统计员工待结算薪资的数据定时任务执行完成");
        sysQuartzRunHistoryService.endSysQuartzRun(historyId, SysQuartzRunHistory.State.START_SUCCESS.getState());
    }

    private void calcWaitWages() throws Exception {
        // 指定年月的考勤信息的缓存
        Map<String, List<Map<String, Object>>> pointMonthCheckWorkTimeCache = new HashMap<>();
        // 获取所有待结算的加班信息
        List<Map<String, Object>> overTimeWaitSettlementList = checkWorkOvertimeDao.queryCheckWorkOvertimeWaitSettlement();
        log.info("overTimeWaitSettlementList size is: {}", overTimeWaitSettlementList.size());
        Map<String, List<Map<String, Object>>> overTimeWaitSettlementByStaffId = overTimeWaitSettlementList.stream()
                .collect(Collectors.groupingBy(map -> map.get("staffId").toString() + map.get("overtimeMonth").toString()));
        for (Map.Entry<String, List<Map<String, Object>>> entry : overTimeWaitSettlementByStaffId.entrySet()) {
            List<Map<String, Object>> overTimeList = entry.getValue();
            // 员工薪资
            String actWages = overTimeList.get(0).get("actWages").toString();
            String overtimeMonth = overTimeList.get(0).get("overtimeMonth").toString();
            String staffId = overTimeList.get(0).get("staffId").toString();
            String companyId = overTimeList.get(0).get("companyId").toString();
            String departmentId = overTimeList.get(0).get("departmentId").toString();
            String hourWages = getStaffHourWages(pointMonthCheckWorkTimeCache, actWages, overtimeMonth, staffId);
            String resultMoney = getAllOverTimeMoneyThisMonth(overTimeList, hourWages);
            sysEveUserStaffCapitalService.addMonthMoney2StaffCapital(staffId, companyId, departmentId, overtimeMonth, 1, resultMoney);
        }
    }

    private String getAllOverTimeMoneyThisMonth(List<Map<String, Object>> overTimeList, String hourWages) throws Exception {
        String allOverTimeHourThisMonth = "0";
        for(Map<String, Object> bean: overTimeList) {
            // 加班工时
            String overtimeHour = bean.get("overtimeHour").toString();
            // 结算类型
            int overtimeSettlementType = Integer.parseInt(bean.get("overtimeSettlementType").toString());
            String money = "0";
            if(overtimeSettlementType == 1){
                // 单倍薪资结算
                money = CalculationUtil.multiply(2, overtimeHour, hourWages, "1");
            } else if(overtimeSettlementType == 2){
                // 1.5倍薪资结算
                money = CalculationUtil.multiply(2, overtimeHour, hourWages, "1.5");
            } else if(overtimeSettlementType == 3){
                // 双倍薪资结算
                money = CalculationUtil.multiply(2, overtimeHour, hourWages, "2");
            }
            allOverTimeHourThisMonth = CalculationUtil.add(allOverTimeHourThisMonth, money, 2);
            // 修改加班电子流的结算状态为已计入统计
            checkWorkOvertimeDao.updateOvertimeSettleState(bean.get("id").toString(), 2);
        }
        return allOverTimeHourThisMonth;
    }

    /**
     * 获取员工每小时的工资
     *
     * @param pointMonthCheckWorkTimeCache 指定年月的考勤信息的缓存
     * @param actWages 员工信息
     * @param overtimeMonth 加班年月，格式为：yyyy-MM
     * @param staffId 员工id
     * @return hourWages
     * @throws Exception
     */
    private String getStaffHourWages(Map<String, List<Map<String, Object>>> pointMonthCheckWorkTimeCache, String actWages,
                                   String overtimeMonth, String staffId) throws Exception {
        // 考勤日期
        List<Map<String, Object>> workTime = getPointMonthCheckWorkTime(pointMonthCheckWorkTimeCache, overtimeMonth);
        // 1.获取该员工拥有的考勤班次id集合
        List<Map<String, Object>> staffTimeIdMation = sysEveUserStaffDao
                .queryStaffCheckWorkTimeRelationByStaffId(staffId);
        List<String> userTimeIds = staffTimeIdMation.stream()
                .map(p -> p.get("timeId").toString()).collect(Collectors.toList());
        List<Map<String, Object>> staffWorkTime = workTime.stream()
                .filter(bean -> userTimeIds.contains(bean.get("timeId").toString()))
                .collect(Collectors.toList());
        Map<String, String> staffModelFieldMap = new HashMap<>();
        // 2.获取应出勤的班次以及小时
        wagesStaffMationService.setLastMonthBe(staffWorkTime, staffModelFieldMap, overtimeMonth);
        // 获取每小时的工资
        String hourWages = CalculationUtil.divide(actWages,
                staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_HOUR.getKey()), 2);
        return hourWages;
    }

    private List<Map<String, Object>> getPointMonthCheckWorkTime(Map<String, List<Map<String, Object>>> cache, String pointMonthDate) throws Exception {
        if(cache.containsKey(pointMonthDate)){
            return cache.get(pointMonthDate);
        }
        // 所有的考勤班次信息
        List<Map<String, Object>> workTime = checkWorkTimeService.getAllCheckWorkTime(pointMonthDate);
        cache.put(pointMonthDate, workTime);
        return workTime;
    }

}
