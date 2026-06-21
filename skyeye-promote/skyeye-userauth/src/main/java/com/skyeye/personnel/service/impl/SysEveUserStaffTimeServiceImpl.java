/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.personnel.dao.SysEveUserStaffTimeDao;
import com.skyeye.personnel.entity.SysEveUserStaffTime;
import com.skyeye.personnel.service.SysEveUserStaffTimeService;
import com.skyeye.rest.checkwork.service.ICheckWorkTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SysEveUserStaffTimeServiceImpl
 * @Description: 员工工作时间服务实现类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/12 22:16
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "员工绑定的考勤班次", groupName = "员工绑定的考勤班次")
public class SysEveUserStaffTimeServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserStaffTimeDao, SysEveUserStaffTime> implements SysEveUserStaffTimeService {

    @Autowired
    private ICheckWorkTimeService iCheckWorkTimeService;

    @Override
    public void querySysEveUserStaffTimeListByTimeId(InputObject inputObject, OutputObject outputObject) {
        String timeId = inputObject.getParams().get("timeId").toString();
        QueryWrapper<SysEveUserStaffTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaffTime::getCheckWorkTimeId), timeId);
        List<SysEveUserStaffTime> beans = list(queryWrapper);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void countSysEveUserStaffTimeByTimeIds(InputObject inputObject, OutputObject outputObject) {
        String timeIds = inputObject.getParams().get("timeIds").toString();
        List<String> timeIdList = Arrays.stream(timeIds.split(",")).map(String::trim)
            .filter(StrUtil::isNotBlank).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(timeIdList)) {
            return;
        }
        List<Map<String, Object>> result = countStaffByTimeIds(timeIdList);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    private List<Map<String, Object>> countStaffByTimeIds(List<String> timeIdList) {
        QueryWrapper<SysEveUserStaffTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("check_work_time_id timeId", "COUNT(1) staffCount");
        queryWrapper.in(MybatisPlusUtil.toColumns(SysEveUserStaffTime::getCheckWorkTimeId), timeIdList);
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(SysEveUserStaffTime::getCheckWorkTimeId));
        return listMaps(queryWrapper);
    }

    @Override
    public void deleteByStaffId(String staffId) {
        QueryWrapper<SysEveUserStaffTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaffTime::getStaffId), staffId);
        remove(queryWrapper);
    }

    @Override
    public void saveUserStaffCheckWorkTime(List<String> timeIdList, String staffId) {
        // 删除员工考勤时间段信息再重新添加
        deleteByStaffId(staffId);
        // 逗号隔开的多班次考勤
        if (CollectionUtil.isNotEmpty(timeIdList)) {
            // 校验多班次考勤是否有重复时间段
            boolean repeat = judgeRepeatShift(timeIdList);
            if (repeat) {
                // 存在冲突的工作时间段
                throw new CustomException("Conflicting working hours.");
            }
            List<SysEveUserStaffTime> staffTimeMation = new ArrayList<>();
            timeIdList.stream().forEach(timeId -> {
                if (!ToolUtil.isBlank(timeId)) {
                    SysEveUserStaffTime bean = new SysEveUserStaffTime();
                    bean.setStaffId(staffId);
                    bean.setCheckWorkTimeId(timeId);
                    staffTimeMation.add(bean);
                }
            });
            if (!staffTimeMation.isEmpty()) {
                createEntity(staffTimeMation, StrUtil.EMPTY);
            }
        }
    }

    @Override
    public void getStaffCheckWorkTimeByStaffId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        List<Map<String, Object>> timeList = getStaffCheckWorkTimeByStaffId(staffId);

        outputObject.setBeans(timeList);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public List<Map<String, Object>> getStaffCheckWorkTimeByStaffId(String staffId) {
        QueryWrapper<SysEveUserStaffTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaffTime::getStaffId), staffId);
        List<SysEveUserStaffTime> sysEveUserStaffTimeList = list(queryWrapper);
        if (CollectionUtil.isEmpty(sysEveUserStaffTimeList)) {
            return new ArrayList<>();
        }

        // 获取考勤班次信息
        List<String> timeIds = sysEveUserStaffTimeList.stream().map(SysEveUserStaffTime::getCheckWorkTimeId).collect(Collectors.toList());
        List<Map<String, Object>> timeList = iCheckWorkTimeService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(timeIds));

        if (CollectionUtil.isEmpty(timeList)) {
            return new ArrayList<>();
        }

        timeList.forEach(t -> {
            t.put("staffId", staffId);
            t.put("timeId", t.get("id"));
        });
        return timeList;
    }

    private boolean judgeRepeatShift(List<String> timeIds) {
        // 1.获取班次的上下班打卡时间
        List<Map<String, Object>> timeMation = iCheckWorkTimeService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(timeIds));
        // 2.校验工作日是否冲突
        return judgeRepeatWorking(timeMation);
    }

    private boolean judgeRepeatWorking(List<Map<String, Object>> timeMation) {
        if (timeMation.size() > 1) {
            for (int i = 0; i < timeMation.size(); i++) {
                for (int j = (i + 1); j < timeMation.size(); j++) {
                    List<String> times = new ArrayList<>();
                    times.add(timeMation.get(i).get("startTime").toString() + "-"
                        + timeMation.get(i).get("endTime").toString());
                    times.add(timeMation.get(j).get("startTime").toString() + "-"
                        + timeMation.get(j).get("endTime").toString());
                    // 1.首先判断每天的工作日的开始时间和结束时间是否有重复
                    boolean flag = DateUtil.checkOverlap(times);
                    if (flag) {
                        // 开始时间和结束时间是否有重复
                        List<Map<String, Object>> iDayMation = (List<Map<String, Object>>) timeMation.get(i)
                            .get("checkWorkTimeWeekList");
                        List<Map<String, Object>> jDayMation = (List<Map<String, Object>>) timeMation.get(j)
                            .get("checkWorkTimeWeekList");
                        // 求这两个班次的工作日冲突的天数，根据类型和工作日(周几)判断
                        int size = iDayMation.stream()
                            .map(t -> jDayMation.stream()
                                .filter(s -> (Objects.equals(t.get("type").toString(), s.get("type").toString())
                                    || Objects.equals(t.get("type").toString(), "1")
                                    || Objects.equals(s.get("type").toString(), "1"))
                                    && Objects.equals(t.get("weekNumber").toString(), s.get("weekNumber").toString()))
                                .findAny().orElse(null)).filter(Objects::nonNull).collect(Collectors.toList()).size();
                        if (size > 0) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }
}
