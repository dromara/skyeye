package com.skyeye.school.building.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.building.dao.RoomOccupantDao;
import com.skyeye.school.building.entity.RoomOccupant;
import com.skyeye.school.building.floorenum.RoomPersonTypeEnum;
import com.skyeye.school.building.service.RoomOccupantService;
import com.skyeye.school.student.entity.Student;
import com.skyeye.school.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 房间人员
 */
@Service
@SkyeyeService(name = "房间人员", groupName = "地点管理")
public class RoomOccupantServiceImpl extends SkyeyeBusinessServiceImpl<RoomOccupantDao, RoomOccupant> implements RoomOccupantService {

    @Autowired
    private StudentService studentService;

    @Override
    public void validatorEntity(RoomOccupant entity) {
        if (!RoomPersonTypeEnum.STAFF.getKey().equals(entity.getPersonType())
            && !RoomPersonTypeEnum.STUDENT.getKey().equals(entity.getPersonType())) {
            throw new CustomException("人员类型不正确");
        }
        if (StrUtil.isEmpty(entity.getFloorInfoId()) || StrUtil.isEmpty(entity.getPersonId())) {
            throw new CustomException("房间和人员不能为空");
        }
        if (StrUtil.isEmpty(entity.getDuty())) {
            throw new CustomException("请填写职责");
        }
        QueryWrapper<RoomOccupant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RoomOccupant::getFloorInfoId), entity.getFloorInfoId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(RoomOccupant::getPersonType), entity.getPersonType());
        queryWrapper.eq(MybatisPlusUtil.toColumns(RoomOccupant::getPersonId), entity.getPersonId());
        if (StrUtil.isNotEmpty(entity.getId())) {
            queryWrapper.ne(MybatisPlusUtil.toColumns(RoomOccupant::getId), entity.getId());
        }
        if (count(queryWrapper) > 0) {
            throw new CustomException("该人员已在此房间中");
        }
    }

    @Override
    public void queryRoomOccupantByFloorId(InputObject inputObject, OutputObject outputObject) {
        String floorInfoId = inputObject.getParams().get("floorInfoId").toString();
        List<RoomOccupant> occupants = queryGroupByFloorInfoIds(Collections.singletonList(floorInfoId))
            .getOrDefault(floorInfoId, new ArrayList<>());
        outputObject.setBeans(occupants);
        outputObject.settotal(occupants.size());
    }

    @Override
    public void deleteByFloorInfoIds(List<String> floorInfoIds) {
        if (CollectionUtil.isEmpty(floorInfoIds)) {
            return;
        }
        QueryWrapper<RoomOccupant> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(RoomOccupant::getFloorInfoId), floorInfoIds);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<RoomOccupant>> queryGroupByFloorInfoIds(List<String> floorInfoIds) {
        Map<String, List<RoomOccupant>> grouped = new LinkedHashMap<>();
        if (CollectionUtil.isEmpty(floorInfoIds)) {
            return grouped;
        }
        QueryWrapper<RoomOccupant> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(RoomOccupant::getFloorInfoId), floorInfoIds);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(RoomOccupant::getCreateTime));
        List<RoomOccupant> occupants = list(queryWrapper);
        fillPersonNames(occupants);
        for (RoomOccupant occupant : occupants) {
            grouped.computeIfAbsent(occupant.getFloorInfoId(), key -> new ArrayList<>()).add(occupant);
        }
        return grouped;
    }

    @Override
    public void fillPersonNames(List<RoomOccupant> occupants) {
        if (CollectionUtil.isEmpty(occupants)) {
            return;
        }
        List<RoomOccupant> staffList = occupants.stream()
            .filter(item -> RoomPersonTypeEnum.STAFF.getKey().equals(item.getPersonType()))
            .collect(Collectors.toList());
        iAuthUserService.setName(staffList, "personId", "personName");

        List<String> studentIds = occupants.stream()
            .filter(item -> RoomPersonTypeEnum.STUDENT.getKey().equals(item.getPersonType()))
            .map(RoomOccupant::getPersonId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(studentIds)) {
            return;
        }
        List<Student> students = studentService.selectByIds(studentIds.toArray(new String[0]));
        Map<String, String> studentNameMap = new LinkedHashMap<>();
        if (CollectionUtil.isNotEmpty(students)) {
            for (Student student : students) {
                if (student != null && StrUtil.isNotEmpty(student.getId())) {
                    studentNameMap.put(student.getId(), student.getName());
                }
            }
        }
        for (RoomOccupant occupant : occupants) {
            if (RoomPersonTypeEnum.STUDENT.getKey().equals(occupant.getPersonType())) {
                occupant.setPersonName(studentNameMap.get(occupant.getPersonId()));
            }
        }
    }
}
