package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.scheduling.dao.SchedulingTimeWorkDao;
import com.skyeye.scheduling.entity.SchedulingTimeWork;
import com.skyeye.scheduling.entity.SchedulingTimeWorkPeople;
import com.skyeye.scheduling.service.SchedulingTimeWorkPeopleService;
import com.skyeye.scheduling.service.SchedulingTimeWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班时间段下工位管理", groupName = "排班时间段下工位管理")
public class SchedulingTimeWorkServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingTimeWorkDao, SchedulingTimeWork> implements SchedulingTimeWorkService {

    @Autowired
    private SchedulingTimeWorkPeopleService schedulingTimeWorkPeopleService;

    @Override
    protected void createPrepose(List<SchedulingTimeWork> entity) {
        for (SchedulingTimeWork schedulingTimeWork : entity) {
            Integer minStaff = schedulingTimeWork.getMinStaff();
            Integer maxStaff = schedulingTimeWork.getMaxStaff();
            if (minStaff > maxStaff) {
                throw new RuntimeException("最小需求人数不能大于最大需求人数");
            }
        }
    }

    @Override
    protected void createPostpose(List<SchedulingTimeWork> entity, String userId) {
        for (SchedulingTimeWork schedulingTimeWork : entity) {
            List<SchedulingTimeWorkPeople> schedulingTimeWorkPeopleMation = schedulingTimeWork.getSchedulingTimeWorkPeopleMation();
            if (CollectionUtil.isEmpty(schedulingTimeWorkPeopleMation)) {
                continue;
            }
            for (SchedulingTimeWorkPeople schedulingTimeWorkPeople : schedulingTimeWorkPeopleMation) {
                schedulingTimeWorkPeople.setSchedulingTimeWorkId(schedulingTimeWork.getId());
                schedulingTimeWorkPeople.setSchedulingId(schedulingTimeWork.getSchedulingId());
                schedulingTimeWorkPeople.setSchedulingTimeId(schedulingTimeWork.getSchedulingTimeId());
            }
        }
        List<List<SchedulingTimeWorkPeople>> TimeWorkPeopleMations = entity.stream().map(SchedulingTimeWork::getSchedulingTimeWorkPeopleMation).collect(Collectors.toList());
        List<SchedulingTimeWorkPeople> schedulingTimeWorkPeople = TimeWorkPeopleMations.stream().flatMap(List::stream).collect(Collectors.toList());
        schedulingTimeWorkPeopleService.createEntity(schedulingTimeWorkPeople, userId);
    }

    @Override
    protected void updatePostpose(List<SchedulingTimeWork> entity, String userId) {
        List<List<SchedulingTimeWorkPeople>> collect = entity.stream().map(SchedulingTimeWork::getSchedulingTimeWorkPeopleMation).collect(Collectors.toList());
        List<SchedulingTimeWorkPeople> schedulingTimeWorkPeople = collect.stream().flatMap(List::stream).collect(Collectors.toList());
        List<String> deleteIdList = new ArrayList<>();
        for (SchedulingTimeWork schedulingTimeWork : entity) {
            String id = schedulingTimeWork.getId();
            String schedulingId = schedulingTimeWork.getSchedulingId();
            String schedulingTimeId = schedulingTimeWork.getSchedulingTimeId();
            // 入参员工记录
            List<SchedulingTimeWorkPeople> schedulingTimeWorkPeopleMation = schedulingTimeWork.getSchedulingTimeWorkPeopleMation();
            List<String> schedulingTimeWorkPeopleIds = schedulingTimeWorkPeopleMation.stream().map(SchedulingTimeWorkPeople::getId).collect(Collectors.toList());
            // 数据库员工记录
            List<SchedulingTimeWorkPeople> timeWorkPeople = schedulingTimeWorkPeopleService.queryPeopleByThreeId(id, schedulingId, schedulingTimeId);
            String employIds = String.join(CommonCharConstants.COMMA_MARK, timeWorkPeople.stream()
                .map(SchedulingTimeWorkPeople::getEmployeeId).collect(Collectors.toList()));
            List<Map<String, Object>> allStaffList = iAuthUserService.queryDataMationByIds(employIds);
            timeWorkPeople.forEach(
                staff -> {
                    Map<String, Object> staffMap = allStaffList.stream().filter(map -> ObjectUtil.equal(map.get("id"), staff.getEmployeeId())).findFirst().orElse(null);
                    if (ObjectUtil.isNotEmpty(staffMap)) {
                        staff.setStaffMation(staffMap);
                    }
                }
            );
            iAuthUserService.setName(timeWorkPeople, "createId", "createName");
            iAuthUserService.setName(timeWorkPeople, "lastUpdateId", "lastUpdateName");
            List<String> timeWorkPeopleList = timeWorkPeople.stream().map(SchedulingTimeWorkPeople::getId).collect(Collectors.toList());
            List<String> deleteIds = timeWorkPeopleList.stream().filter(
                time -> !schedulingTimeWorkPeopleIds.contains(time)
            ).collect(Collectors.toList());
            deleteIdList.addAll(deleteIds);
        }
        if (CollectionUtil.isNotEmpty(deleteIdList)) {
            schedulingTimeWorkPeopleService.deleteBySchedulingTimeIds(deleteIdList);
        }
        // 筛选出 id 不为空的数据
        List<SchedulingTimeWorkPeople> nonEmptyIdPeople = schedulingTimeWorkPeople.stream()
            .filter(people -> people.getId() != null && !people.getId().isEmpty())
            .collect(Collectors.toList());
        // 筛选出 id 为空的数据
        List<SchedulingTimeWorkPeople> emptyIdPeople = schedulingTimeWorkPeople.stream()
            .filter(people -> people.getId() == null || people.getId().isEmpty())
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(emptyIdPeople)) {
            schedulingTimeWorkPeopleService.createEntity(emptyIdPeople, userId);
        }
        if (CollectionUtil.isNotEmpty(nonEmptyIdPeople)) {
            schedulingTimeWorkPeopleService.updateEntity(nonEmptyIdPeople, userId);
        }
    }

    @Override
    public List<SchedulingTimeWork> querySchedulingTimeByTimeIdAndId(String id, List<String> timeIds) {
        QueryWrapper<SchedulingTimeWork> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWork::getSchedulingId), id);
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWork::getSchedulingTimeId), timeIds);
        List<SchedulingTimeWork> schedulingTimeWorkList = list(queryWrapper);
        if (CollectionUtil.isEmpty(schedulingTimeWorkList)) {
            return Collections.emptyList();
        }
        List<String> timeWorkIds = schedulingTimeWorkList.stream().map(SchedulingTimeWork::getId).collect(Collectors.toList());
        List<SchedulingTimeWorkPeople> timeWorkPeople = schedulingTimeWorkPeopleService.queryTimeWorkByThreeId(id, timeIds, timeWorkIds);
        Map<String, Map<String, List<SchedulingTimeWorkPeople>>> collect = timeWorkPeople.stream().collect(Collectors.groupingBy(SchedulingTimeWorkPeople::getSchedulingTimeId, Collectors.groupingBy(SchedulingTimeWorkPeople::getSchedulingTimeWorkId)));
        for (SchedulingTimeWork schedulingTimeWork : schedulingTimeWorkList) {
            String timeWorkIdId = schedulingTimeWork.getId();
            String schedulingTimeId = schedulingTimeWork.getSchedulingTimeId();
            // 获取对应时间ID的映射
            Map<String, List<SchedulingTimeWorkPeople>> timeWorkMap = collect.get(schedulingTimeId);
            if (!CollectionUtils.isEmpty(timeWorkMap)) {
                // 获取对应时间工作ID的列表
                List<SchedulingTimeWorkPeople> timeWorkPeople1 = timeWorkMap.get(timeWorkIdId);
                if (timeWorkPeople1 != null) {
                    schedulingTimeWork.setSchedulingTimeWorkPeopleMation(timeWorkPeople1);
                }
            }
        }
        return schedulingTimeWorkList;
    }

    @Override
    public void deleteBySchedulingTimeIds(List<String> schedulingTimeIds) {
        schedulingTimeWorkPeopleService.deleteBySchedulingTimeIdsAndOthorId(schedulingTimeIds);
        QueryWrapper<SchedulingTimeWork> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWork::getSchedulingTimeId), schedulingTimeIds);
        List<SchedulingTimeWork> schedulingTimeWorkList = list(queryWrapper);
        remove(queryWrapper);
    }

    @Override
    public List<SchedulingTimeWork> querySchedulingTimeWorkBySchedulingIdAndId(String schedulingId, String id) {
        QueryWrapper<SchedulingTimeWork> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWork::getSchedulingId), schedulingId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWork::getSchedulingTimeId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySchedulingTimeWorkIds(List<String> allDeleteIds) {
        schedulingTimeWorkPeopleService.deleteSchedulingTimeWorkPeopleByTimeIds(allDeleteIds);
        QueryWrapper<SchedulingTimeWork> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, allDeleteIds);
        remove(queryWrapper);
    }

    @Override
    public List<SchedulingTimeWork> querySchedulingTimeByIds(List<String> workIds) {
        if (CollectionUtil.isEmpty(workIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<SchedulingTimeWork> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, workIds);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySchedulingIds(List<String> ids) {
        schedulingTimeWorkPeopleService.deleteBySchedulingIds(ids);
        QueryWrapper<SchedulingTimeWork> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWork::getSchedulingId), ids);
        remove(queryWrapper);
    }


}
