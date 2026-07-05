package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.erp.farm.service.IFarmStationService;
import com.skyeye.scheduling.dao.SchedulingShiftsDao;
import com.skyeye.scheduling.entity.SchedulingShifts;
import com.skyeye.scheduling.entity.SchedulingShiftsTime;
import com.skyeye.scheduling.entity.SchedulingShiftsTimeWork;
import com.skyeye.scheduling.service.SchedulingShiftsService;
import com.skyeye.scheduling.service.SchedulingShiftsTimeService;
import com.skyeye.scheduling.service.SchedulingShiftsTimeWorkService;
import com.skyeye.worktime.util.CheckWorkTimePeriodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班班次管理", groupName = "排班班次管理")
public class SchedulingShiftsServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingShiftsDao, SchedulingShifts> implements SchedulingShiftsService {

    @Autowired
    private SchedulingShiftsTimeService schedulingShiftsTimeService;

    @Autowired
    private SchedulingShiftsTimeWorkService schedulingShiftsTimeWorkService;

    @Autowired
    private IFarmStationService iFarmStationService;

    @Override
    protected void createPostpose(SchedulingShifts entity, String userId) {
        List<SchedulingShiftsTime> schedulingShiftsTimeMation = entity.getSchedulingShiftsTimeMation();
        if (CollectionUtil.isNotEmpty(schedulingShiftsTimeMation)) {
            for (SchedulingShiftsTime schedulingShiftsTime : schedulingShiftsTimeMation) {
                schedulingShiftsTime.setShiftId(entity.getId());
            }
            schedulingShiftsTimeService.createEntity(schedulingShiftsTimeMation, userId);
        }
    }

    @Override
    protected void validatorEntity(SchedulingShifts entity) {
        super.validatorEntity(entity);
        List<SchedulingShiftsTime> schedulingShiftsTimeMation = entity.getSchedulingShiftsTimeMation();
        if (CollectionUtil.isNotEmpty(schedulingShiftsTimeMation)) {
            for (SchedulingShiftsTime schedulingShiftsTime : schedulingShiftsTimeMation) {
                String startTime = schedulingShiftsTime.getStartTime();
                String endTime = schedulingShiftsTime.getEndTime();
                if (StrUtil.isEmpty(startTime) || StrUtil.isEmpty(endTime)) {
                    throw new CustomException("班次时间不能为空");
                }
                normalizeShiftCrossDayFlag(schedulingShiftsTime);
            }
        }
    }

    /**
     * 排班时段跨天标记：起止相同视为满24小时跨天；结束早于开始视为跨天；否则不跨天
     */
    private void normalizeShiftCrossDayFlag(SchedulingShiftsTime schedulingShiftsTime) {
        String startTime = schedulingShiftsTime.getStartTime();
        String endTime = schedulingShiftsTime.getEndTime();
        String startHm = extractShiftHm(startTime);
        String endHm = extractShiftHm(endTime);
        if (startHm.equals(endHm) || CheckWorkTimePeriodUtil.isCrossDay(startTime, endTime)) {
            schedulingShiftsTime.setIsNextDay(WhetherEnum.ENABLE_USING.getKey());
        } else {
            schedulingShiftsTime.setIsNextDay(WhetherEnum.DISABLE_USING.getKey());
        }
    }

    private String extractShiftHm(String time) {
        if (StrUtil.isEmpty(time)) {
            return "";
        }
        String t = time.trim();
        return t.length() >= 5 ? t.substring(0, 5) : t;
    }

    @Override
    protected void updatePostpose(SchedulingShifts entity, String userId) {
        // 获取传参的班次时间段信息
        List<SchedulingShiftsTime> schedulingShiftsTimeMation = entity.getSchedulingShiftsTimeMation();
        String id = entity.getId();
        // 获取数据库的班次时间段信息
        List<SchedulingShiftsTime> schedulingShiftsTimes = schedulingShiftsTimeService.queryShiftsTimeById(id);
        // 获取数据库的班次时间段ids
        List<String> ids = schedulingShiftsTimes.stream().map(SchedulingShiftsTime::getId).collect(Collectors.toList());
        // 获取传参的班次时间段ids
        List<String> schedulingShiftsTimeIds = schedulingShiftsTimeMation.stream().map(SchedulingShiftsTime::getId).distinct().collect(Collectors.toList());
        // 获取传参的班次时间段ids中不在数据库的班次时间段ids中的数据
        List<String> idsNotInSchedulingShiftsTimeIds = ids.stream().filter(Id -> !schedulingShiftsTimeIds.contains(Id))
            .collect(Collectors.toList());
        // 找出 id 为空的 SchedulingShiftsTime 对象
        List<SchedulingShiftsTime> emptyIdShifts = schedulingShiftsTimeMation.stream().filter(shift -> StrUtil.isEmpty(shift.getId()))
            .collect(Collectors.toList());
        // 找出 id 不为空的 SchedulingShiftsTime 对象
        List<SchedulingShiftsTime> notEmptyIdShifts = schedulingShiftsTimeMation.stream().filter(shift -> StrUtil.isNotEmpty(shift.getId()))
            .collect(Collectors.toList());
        // 新增编辑中新的班次时间段
        schedulingShiftsTimeService.createEntity(emptyIdShifts, userId);
        // 删除多余的班次时间信息
        schedulingShiftsTimeService.deleteSchedulingShiftsTimeByShiftIds(idsNotInSchedulingShiftsTimeIds);
        if (CollectionUtil.isNotEmpty(notEmptyIdShifts)) {
            schedulingShiftsTimeService.updateEntity(notEmptyIdShifts, userId);
        }
    }

    @Override
    public void deleteSchedulingShifts(InputObject inputObject, OutputObject outputObject) {
        String ids = inputObject.getParams().get("ids").toString();
        List<String> idList = Arrays.asList(ids);
        QueryWrapper<SchedulingShifts> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, idList);
        boolean remove = remove(queryWrapper);
        if (remove) {
            schedulingShiftsTimeService.deleteSchedulingShiftsTimeByShiftIds(idList);
        }
    }

    @Override
    public void querySchedulingShiftsList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String keyword = commonPageInfo.getKeyword();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<SchedulingShifts> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(keyword)) {
            queryWrapper.like(MybatisPlusUtil.toColumns(SchedulingShifts::getName), keyword);
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SchedulingShifts::getCreateTime));
        List<SchedulingShifts> schedulingShiftsList = list(queryWrapper);
        // 所有班次Ids
        List<String> schedulingShiftsIds = schedulingShiftsList.stream().map(SchedulingShifts::getId).collect(Collectors.toList());
        // 每个班次Id对应的班次时间段
        List<SchedulingShiftsTime> schedulingShiftsTimes = schedulingShiftsTimeService.queryTimeByIdList(schedulingShiftsIds);
        // 获取所有的班次时间id
        List<String> shiftsTimeIds = schedulingShiftsTimes.stream().map(SchedulingShiftsTime::getId).collect(Collectors.toList());
        // 获取所有的班次时间id对应的班次时间段工作
        List<SchedulingShiftsTimeWork> schedulingShiftsTimeWorks = schedulingShiftsTimeWorkService.queryShiftsTimeWorkByShiftsTimeIds(shiftsTimeIds);
        // 班次时间段id对应的班次时间段工作
        Map<String, List<SchedulingShiftsTime>> timeMapList = schedulingShiftsTimes.stream().collect(Collectors.groupingBy(SchedulingShiftsTime::getShiftId));
        // 班次时间时间段id对应的班次时间段工位需求
        Map<String, List<SchedulingShiftsTimeWork>> ShiftsTimeWorkMap = schedulingShiftsTimeWorks.stream().collect(Collectors.groupingBy(SchedulingShiftsTimeWork::getShiftsTimeId));
        // 遍历 timeMapList
        for (Map.Entry<String, List<SchedulingShiftsTime>> entry : timeMapList.entrySet()) {
            List<SchedulingShiftsTime> shiftsTimes = entry.getValue();
            // 遍历每个 SchedulingShiftsTime 对象
            for (SchedulingShiftsTime shiftsTime : shiftsTimes) {
                String shiftsTimeId = shiftsTime.getId();
                // 从 ShiftsTimeWorkMap 中查找对应的 List<SchedulingShiftsTimeWork>
                List<SchedulingShiftsTimeWork> shiftsTimeWorks = ShiftsTimeWorkMap.getOrDefault(shiftsTimeId, Collections.emptyList());
                // 将找到的 List<SchedulingShiftsTimeWork> 设置到 shiftsTime 的 shiftsTimeWorkMation
                shiftsTime.setShiftsTimeWorkMation(shiftsTimeWorks);
            }
        }
        schedulingShiftsList.forEach(k -> {
            k.setSchedulingShiftsTimeMation(timeMapList.get(k.getId()));
        });
        iAuthUserService.setName(schedulingShiftsList, "createId", "createName");
        iAuthUserService.setName(schedulingShiftsList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(schedulingShiftsList);
        outputObject.settotal(page.getTotal());

    }

    @Override
    public List<SchedulingShifts> querySchedulingShiftsByIds(List<String> shiftIds) {
        if (CollectionUtil.isEmpty(shiftIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<SchedulingShifts> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, shiftIds);
        return list(queryWrapper);
    }

    @Override
    public List<SchedulingShifts> querySchedulingShiftsByIdName(List<String> shiftIdList, String keyword) {
        if (CollectionUtil.isEmpty(shiftIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<SchedulingShifts> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, shiftIdList);
        if (StrUtil.isNotEmpty(keyword)) {
            queryWrapper.like(MybatisPlusUtil.toColumns(SchedulingShifts::getName), keyword);
        }
        return list(queryWrapper);
    }

    @Override
    public SchedulingShifts selectById(String id) {
        SchedulingShifts schedulingShifts = super.selectById(id);
        List<SchedulingShiftsTime> schedulingShiftsTimes = schedulingShiftsTimeService.queryTimeByShiftId(id);
        List<String> shiftsTimeIds = schedulingShiftsTimes.stream().map(SchedulingShiftsTime::getId).collect(Collectors.toList());
        List<SchedulingShiftsTimeWork> schedulingShiftsTimeWorks = schedulingShiftsTimeWorkService.queryShiftsTimeWorkByShiftsTimeIds(shiftsTimeIds);
        List<String> workIds = schedulingShiftsTimeWorks.stream()
            .map(SchedulingShiftsTimeWork::getWorkId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(workIds)) {
            String workIdsStr = String.join(CommonCharConstants.COMMA_MARK, workIds);
            mapList = iFarmStationService.queryFarmStationByIds(workIdsStr);
        }
        Map<String, Map<String, Object>> mapMap = mapList.stream()
            .collect(Collectors.toMap(
                k -> {
                    Object id1 = k.get("id");
                    return ObjectUtil.isEmpty(id1) ? null : id1.toString();
                },
                v -> v));
        schedulingShiftsTimeWorks.forEach(
            schedulingShift -> {
                Map<String, Object> map = mapMap.get(schedulingShift.getWorkId());
                if (CollectionUtil.isNotEmpty(map)) {
                    schedulingShift.setWorkMation(map);
                }
            }
        );
        Map<String, List<SchedulingShiftsTimeWork>> ShiftsTimeWorkMap = schedulingShiftsTimeWorks.stream().collect(Collectors.groupingBy(SchedulingShiftsTimeWork::getShiftsTimeId));
        for (SchedulingShiftsTime schedulingShiftsTime : schedulingShiftsTimes) {
            schedulingShiftsTime.setShiftsTimeWorkMation(ShiftsTimeWorkMap.get(schedulingShiftsTime.getId()));
        }
        schedulingShifts.setSchedulingShiftsTimeMation(schedulingShiftsTimes);
        return schedulingShifts;
    }

}
