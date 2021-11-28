/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.eve.dao.WagesModelDao;
import com.skyeye.eve.dao.WagesModelFieldDao;
import com.skyeye.eve.dao.WagesStaffMationDao;
import com.skyeye.eve.service.SysScheduleCommonService;
import com.skyeye.eve.service.WagesStaffMationService;
import com.skyeye.wages.constant.WagesConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: WagesStaffMationServiceImpl
 * @Description: 员工薪资设定管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:18
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class WagesStaffMationServiceImpl implements WagesStaffMationService {

    private static Logger log = LoggerFactory.getLogger(WagesStaffMationServiceImpl.class);

    @Autowired
    private WagesStaffMationDao wagesStaffMationDao;

    @Autowired
    private WagesModelDao wagesModelDao;

    @Autowired
    private WagesModelFieldDao wagesModelFieldDao;

    @Autowired
    private SysScheduleCommonService sysScheduleCommonService;

    /**
     * 计薪资字段状态
     */
    public static enum STATE{
        WAIT_DESIGN_WAGES(1, "待设定"),
        TOO_DESIGN_WAGES(2, "已设定");
        private int state;
        private String name;
        STATE(int state, String name){
            this.state = state;
            this.name = name;
        }
        public int getState() {
            return state;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 获取待设定薪资员工列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesStaffWaitAllocatedMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = wagesStaffMationDao.queryWagesStaffWaitAllocatedMationList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 根据员工id获取该员工拥有的薪资字段
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryStaffWagesModelFieldMationListByStaffId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        Map<String, Object> staffMation = wagesStaffMationDao.querySysUserStaffMationById(staffId);
        List<String> wagesApplicableObject = Arrays.asList(new String[]{
                staffMation.get("companyId").toString(),
                staffMation.get("departmentId").toString(),
                staffId});
        List<Map<String, Object>> modelField = new ArrayList<>();
        // 1.获取薪资模板
        List<Map<String, Object>> model = wagesModelDao.queryWagesModelListByApplicableObjectIds(wagesApplicableObject);
        if(model != null && !model.isEmpty()){
            // 2.获取模板参数
            List<String> modelIds = model.stream().map(p -> p.get("id").toString()).collect(Collectors.toList());
            modelField = wagesModelFieldDao.queryWagesModelFieldByModelIdsAndStaffId(modelIds, staffId, staffMation.get("jobScoreId").toString());
        }
        modelField.stream().forEach(bean -> {
            if("1".equals(bean.get("monthlyClearing").toString())){
                // 如果是自动清零，则不可进行薪资编辑
                bean.put("disabled", "disabled");
            }
            if("1".equals(bean.get("wagesType").toString())){
                bean.put("wagesTypeStr", "薪资增加");
            }else{
                bean.put("wagesTypeStr", "薪资减少");
            }
        });
        outputObject.setBeans(modelField);
        outputObject.settotal(modelField.size());
    }

    /**
     * 保存员工薪资设定
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void saveStaffWagesModelFieldMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        String fieldStr = map.get("fieldStr").toString();
        List<Map<String, Object>> wagesModelFieldMation = JSONUtil.toList(JSONUtil.parseArray(fieldStr), null);
        // 保存薪资要素字段信息
        wagesStaffMationDao.saveStaffWagesModelFieldMation(wagesModelFieldMation);
        // 保存员工月标准薪资信息以及设定状态
        wagesStaffMationDao.editStaffDesignWagesByStaffId(staffId, STATE.TOO_DESIGN_WAGES.getState(), map.get("actMoney").toString());
    }

    /**
     * 获取已设定薪资员工列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesStaffDesignMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = wagesStaffMationDao.queryWagesStaffDesignMationList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取员工薪资条薪资
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesStaffPaymentDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        String payMonth = map.get("payMonth").toString();
        Map<String, Object> bean = wagesStaffMationDao.queryWagesStaffPaymentDetail(staffId, payMonth);
        bean.put("wagesJson", JSONUtil.parseArray(bean.get("wagesJson").toString()));
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 设置应出勤的班次以及小时
     *
     * @param staffWorkTime      员工对应的考勤班次
     * @param staffModelFieldMap 员工拥有的所有薪资要素字段以及对应的值
     * @param lastMonthDate      指定年月，格式为yyyy-MM
     * @throws Exception
     */
    @Override
    public void setLastMonthBe(List<Map<String, Object>> staffWorkTime, Map<String, String> staffModelFieldMap, String lastMonthDate) throws Exception {
        List<String> lastMonthDays = DateUtil.getMonthFullDay(Integer.parseInt(lastMonthDate.split("-")[0]), Integer.parseInt(lastMonthDate.split("-")[1]));
        int lastMonthBeNum = 0;
        String lastMonthBeHour = "0";
        for (Map<String, Object> bean : staffWorkTime) {
            List<Map<String, Object>> days = (List<Map<String, Object>>) bean.get("days");
            for (String day : lastMonthDays) {// 周几
                if(sysScheduleCommonService.judgeISHoliday(day)){
                    // 如果是节假日，则不计算
                    continue;
                }
                int weekDay = DateUtil.getWeek(day);
                int weekType = DateUtil.getWeekType(day);
                List<Map<String, Object>> simpleDay = days.stream().filter(item -> Integer.parseInt(item.get("day").toString()) == weekDay)
                        .collect(Collectors.toList());
                if (simpleDay != null && !simpleDay.isEmpty()) {
                    // 如果今天是需要考勤的日期
                    int dayType = Integer.parseInt(simpleDay.get(0).get("type").toString());
                    if (weekType == 1 && dayType == 2) {
                        // 如果获取到的日期是双周，但考勤班次里面是单周，则不做任何操作
                    } else {
                        // 单周或者每周的当天都上班
                        lastMonthBeNum++;
                        try {
                            String time = DateUtil.getDistanceMinuteByHMS(bean.get("startTime").toString(), bean.get("endTime").toString());
                            lastMonthBeHour = CalculationUtil.add(lastMonthBeHour, time, 2);
                        } catch (Exception e) {
                            log.warn("get differ time failed, startTime is: {}, endTime is: {}", bean.get("startTime").toString(),
                                    bean.get("endTime").toString(), e);
                        }
                    }
                }
            }
        }
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_NUM.getKey(), String.valueOf(lastMonthBeNum));
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_HOUR.getKey(), CalculationUtil.divide(lastMonthBeHour, "60", 2));
    }

}
