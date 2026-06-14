/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.WeekTypeEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.centerrest.user.SysEveUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.pro.rest.ISysEveUserStaffTimeRest;
import com.skyeye.worktime.classenum.CheckWorkTimeType;
import com.skyeye.worktime.classenum.CheckWorkTimeWeekType;
import com.skyeye.worktime.dao.CheckWorkTimeDao;
import com.skyeye.worktime.entity.CheckWorkTime;
import com.skyeye.worktime.entity.CheckWorkTimeWeek;
import com.skyeye.worktime.service.CheckWorkTimeService;
import com.skyeye.worktime.service.CheckWorkTimeWeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CheckWorkTimeServiceImpl
 * @Description: 考勤班次管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/3 14:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "考勤班次", groupName = "考勤班次")
public class CheckWorkTimeServiceImpl extends SkyeyeBusinessServiceImpl<CheckWorkTimeDao, CheckWorkTime> implements CheckWorkTimeService {

    @Autowired
    private CheckWorkTimeWeekService checkWorkTimeWeekService;

    @Autowired
    private SysEveUserService sysEveUserService;

    @Autowired
    private ISysEveUserStaffTimeRest iSysEveUserStaffTimeRest;

    @Override
    protected void writePostpose(CheckWorkTime entity, String userId) {
        super.writePostpose(entity, userId);
        checkWorkTimeWeekService.saveCheckWorkTimeWeekList(entity.getId(), entity.getCheckWorkTimeWeekList(), userId);
    }

    @Override
    protected void deletePreExecution(String id) {
        // 获取这个考勤班次与员工的绑定关系
        List<Map<String, Object>> beans = ExecuteFeignClient.get(() ->
            iSysEveUserStaffTimeRest.querySysEveUserStaffTimeListByTimeId(id)).getRows();

        if (CollectionUtil.isNotEmpty(beans)) {
            throw new CustomException("该考勤班次已被员工使用，无法删除。");
        }
    }

    @Override
    public CheckWorkTime getDataFromDb(String id) {
        CheckWorkTime checkWorkTime = super.getDataFromDb(id);
        checkWorkTime.setCheckWorkTimeWeekList(checkWorkTimeWeekService.selectByTimeId(checkWorkTime.getId()));
        return checkWorkTime;
    }

    @Override
    public CheckWorkTime selectById(String id) {
        CheckWorkTime checkWorkTime = super.selectById(id);
        checkWorkTime.setTypeName(CheckWorkTimeType.getShowName(checkWorkTime.getType()));
        return checkWorkTime;
    }

    @Override
    protected List<CheckWorkTime> getDataFromDb(List<String> idList) {
        List<CheckWorkTime> checkWorkTimeList = super.getDataFromDb(idList);
        Map<String, List<CheckWorkTimeWeek>> weekMap = checkWorkTimeWeekService.selectByTimeId(idList);
        checkWorkTimeList.forEach(checkWorkTime -> {
            checkWorkTime.setCheckWorkTimeWeekList(weekMap.get(checkWorkTime.getId()));
        });
        return checkWorkTimeList;
    }

    @Override
    public List<CheckWorkTime> selectByIds(String... ids) {
        List<CheckWorkTime> checkWorkTimes = super.selectByIds(ids);
        checkWorkTimes.forEach(checkWorkTime -> {
            checkWorkTime.setTypeName(CheckWorkTimeType.getShowName(checkWorkTime.getType()));
        });
        return checkWorkTimes;
    }

    @Override
    public void queryEnableCheckWorkTimeList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<CheckWorkTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CheckWorkTime::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(CheckWorkTime::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        List<CheckWorkTime> checkWorkTimeList = list(queryWrapper);
        outputObject.setBeans(checkWorkTimeList);
        outputObject.settotal(checkWorkTimeList.size());
    }

    @Override
    public void queryCheckWorkTimeListByLoginUser(InputObject inputObject, OutputObject outputObject) {
        String staffId = inputObject.getLogParams().get("staffId").toString();
        // 获取员工绑定的考勤班次信息
        List<Map<String, Object>> workTime = ExecuteFeignClient.get(() -> sysEveUserService.queryStaffCheckWorkTimeRelationNameByStaffId(staffId)).getRows();
        List<String> timeIds = workTime.stream().map(bean -> bean.get("timeId").toString()).collect(Collectors.toList());

        List<CheckWorkTime> checkWorkTimes = selectByIds(timeIds.toArray(new String[]{}));
        outputObject.setBeans(checkWorkTimes);
        outputObject.settotal(checkWorkTimes.size());
    }

    @Override
    public void getAllCheckWorkTime(InputObject inputObject, OutputObject outputObject) {
        String pointMonthDate = inputObject.getParams().get("pointMonthDate").toString();
        List<CheckWorkTime> beans = this.getAllCheckWorkTime(pointMonthDate);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 根据指定年月获取所有的考勤班次的信息以及工作日信息等
     *
     * @param pointMonthDate 指定年月，格式为yyyy-MM
     * @return
     */
    @Override
    public List<CheckWorkTime> getAllCheckWorkTime(String pointMonthDate) {
        List<CheckWorkTime> checkWorkTimes = queryAllData();
        // 获取上个月的所有日期
        List<String> lastMonthDays = DateUtil.getMonthFullDay(Integer.parseInt(pointMonthDate.split("-")[0]), Integer.parseInt(pointMonthDate.split("-")[1]));
        for (CheckWorkTime bean : checkWorkTimes) {
            List<CheckWorkTimeWeek> days = bean.getCheckWorkTimeWeekList();
            List<String> workDays = new ArrayList<>();
            for (String day : lastMonthDays) {
                // 周几
                int weekDay = DateUtil.getWeek(day);
                int weekType = DateUtil.getWeekType(day);
                CheckWorkTimeWeek simpleDay = days.stream()
                    .filter(item -> item.getWeekNumber() == weekDay && !item.getType().equals(CheckWorkTimeWeekType.DOUBLE.getKey()))
                    .findFirst().orElse(null);
                if (ObjectUtil.isEmpty(simpleDay)) {
                    continue;
                }
                // 如果今天是需要考勤的日期
                if (weekType == WeekTypeEnum.BIWEEKLY.getKey() && simpleDay.getType().equals(CheckWorkTimeWeekType.DAY.getKey())) {
                    // 如果获取到的日期是双周，但考勤班次里面是单周，则不做任何操作
                } else {
                    // 单周或者非每周的当天都上班
                    workDays.add(day);
                }
            }
            bean.setWorkDays(workDays);
        }
        return checkWorkTimes;
    }

    @Override
    public void setOnlineCheckWorkTime(InputObject inputObject, OutputObject outputObject) {
        CheckWorkTime checkWorkTime = inputObject.getParams(CheckWorkTime.class);
        UpdateWrapper<CheckWorkTime> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, checkWorkTime.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(CheckWorkTime::getLongitude), checkWorkTime.getLongitude());
        updateWrapper.set(MybatisPlusUtil.toColumns(CheckWorkTime::getLatitude), checkWorkTime.getLatitude());
        updateWrapper.set(MybatisPlusUtil.toColumns(CheckWorkTime::getProvinceId), checkWorkTime.getProvinceId());
        updateWrapper.set(MybatisPlusUtil.toColumns(CheckWorkTime::getCityId), checkWorkTime.getCityId());
        updateWrapper.set(MybatisPlusUtil.toColumns(CheckWorkTime::getAreaId), checkWorkTime.getAreaId());
        updateWrapper.set(MybatisPlusUtil.toColumns(CheckWorkTime::getTownshipId), checkWorkTime.getTownshipId());
        updateWrapper.set(MybatisPlusUtil.toColumns(CheckWorkTime::getAbsoluteAddress), checkWorkTime.getAbsoluteAddress());
        update(updateWrapper);

        refreshCache(checkWorkTime.getId());
    }

}
