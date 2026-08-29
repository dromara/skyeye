/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.groups.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.QRCodeLinkType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.qrcode.QRCodeLogoUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.rest.wall.certification.service.ICertificationService;
import com.skyeye.school.groups.dao.GroupsDao;
import com.skyeye.school.groups.entity.Groups;
import com.skyeye.school.groups.entity.GroupsInformation;
import com.skyeye.school.groups.service.GroupsInformationService;
import com.skyeye.school.groups.service.GroupsService;
import com.skyeye.school.groups.service.GroupsStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "学生分组管理", groupName = "分组管理")
public class GroupServiceImpl extends SkyeyeBusinessServiceImpl<GroupsDao, Groups> implements GroupsService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Autowired
    private GroupsInformationService groupsInformationService;

    @Autowired
    private GroupsStudentService groupsStudentService;

    @Autowired
    private ICertificationService iCertificationService;

    @Override
    public QueryWrapper<Groups> getQueryWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<Groups> queryWrapper = super.getQueryWrapper(tableSelectInfo);
        // 学生分组信息下的所有分组
        queryWrapper.eq(MybatisPlusUtil.toColumns(Groups::getGroupsInformationId), tableSelectInfo.getObjectId());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Groups::getCreateTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        List<Map<String, Object>> sortedBeans = beans.stream()
            .sorted(Comparator.comparingInt(map -> {
                String groupName = (String) map.get("groupName");
                // 提取数字部分
                String numberStr = groupName.replaceAll("[^\\d]", "");
                return Integer.parseInt(numberStr);
            }))
            .collect(Collectors.toList());
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份采取判断是否已经加入分组
            List<String> groupsIds = sortedBeans.stream().map(m -> m.get("id").toString()).collect(Collectors.toList());
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            Map<String, Object> certification = iCertificationService.queryCertificationById(userId);
            String studentNumber = certification.getOrDefault("studentNumber", StrUtil.EMPTY).toString();
            if (StrUtil.isNotBlank(studentNumber)) {
                Map<String, Boolean> isJoined = groupsStudentService.checkStudentIsJoined(groupsIds, studentNumber);
                sortedBeans.forEach(item -> {
                    String groupId = item.get("id").toString();
                    item.put("isJoined", isJoined.getOrDefault(groupId, false));
                });
            }
        }
        List<String> groupsIds = sortedBeans.stream().map(m -> m.get("id").toString()).collect(Collectors.toList());
        Map<String, Integer> studentCount = groupsStudentService.getStudentCountByGroupId(groupsIds);
        sortedBeans.forEach(item -> {
            String groupId = item.get("id").toString();
            item.put("studentCount", studentCount.getOrDefault(groupId, CommonNumConstants.NUM_ZERO));
        });
        return sortedBeans;
    }

    @Override
    public void insertList(GroupsInformation groupsInformation) {
        //构造数据
        Integer status = groupsInformation.getStatus();
        List<Groups> groupsList = new ArrayList<>();
        if (status.equals(CommonNumConstants.NUM_ZERO)) {
            Integer groNumber = groupsInformation.getGroNumber();
            getGroupList(groupsInformation, groNumber, groupsList);
            super.createEntity(groupsList, groupsInformation.getCreateId());
            List<String> groupsIds = groupsList.stream().map(Groups::getId).collect(Collectors.toList());
            refreshCache(groupsIds);
        }
        if (status.equals(CommonNumConstants.NUM_ONE)) {
            Integer groupsNumber = groupsInformation.getGroupsNumber();
            getGroupList(groupsInformation, groupsNumber, groupsList);
            super.createEntity(groupsList, groupsInformation.getCreateId());
            List<String> groupsIds = groupsList.stream().map(Groups::getId).collect(Collectors.toList());
            refreshCache(groupsIds);
        }
    }

    private void getGroupList(GroupsInformation groupsInformation, Integer groupsNumber, List<Groups> groupsList) {
        for (int i = 1; i <= groupsNumber; i++) {
            Groups entity = getGroups(groupsInformation, i);
            entity.setGroupsInformationId(groupsInformation.getId());
            groupsList.add(entity);
        }
    }

    private Groups getGroups(GroupsInformation groupsInformation, int i) {
        Groups entity = new Groups();
        entity.setGroupName("第" + i + "组");
        entity.setGroupsInformationId(groupsInformation.getId());
        String imgPath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY + entity.getGroupBarcode());
        // 生成分组编码
        String code = ToolUtil.getFourWord();
        entity.setGroupBarcode(code);
        // 生成分组二维码
        String content = QRCodeLinkType.getJsonStrByType(QRCodeLinkType.STUDENT_CHECKWORK.getKey(), code);
        String sourCodeLogo = QRCodeLogoUtil.encode(content, imgPath, tPath, true, FileConstants.FileUploadPath.SCHOOL_SUBJECT.getType()[0]);
        entity.setGrCodeUrl(sourCodeLogo);
        return entity;
    }

    @Override
    public void deleteGroups(String groupsInformationId) {
        QueryWrapper<Groups> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Groups::getGroupsInformationId), groupsInformationId);
        List<Groups> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        list.forEach(item -> {
            // 删除分组的二维码
            FileUtil.deleteFile(tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + item.getGrCodeUrl());
        });
        List<String> ids = list.stream().map(Groups::getId).collect(Collectors.toList());
        // 删除分组下的学生信息
        groupsStudentService.deleteByGroupsIds(ids);
        // 删除分组信息
        remove(queryWrapper);
    }

    @Override
    public void deleteGroupsById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<Groups> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        remove(updateWrapper);
    }

    @Override
    public void changeState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Integer state = (Integer) map.get("state");
        UpdateWrapper<Groups> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Groups::getState), state);
        update(updateWrapper);
    }

    @Override
    public Groups selectById(String id) {
        Groups groups = super.selectById(id);
        GroupsInformation groupsInformation = groupsInformationService.selectById(groups.getGroupsInformationId());
        groups.setGroupsInformationMation(groupsInformation);

        List<Map<String, Object>> students = groupsStudentService.queryGroupsStudentsByGroupId(id);
        groups.setStudents(students);

        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份采取判断是否已经加入分组
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            Map<String, Object> certification = iCertificationService.queryCertificationById(userId);
            String studentNumber = certification.getOrDefault("studentNumber", StrUtil.EMPTY).toString();
            if (StrUtil.isNotBlank(studentNumber)) {
                Map<String, Boolean> isJoined = groupsStudentService.checkStudentIsJoined(Arrays.asList(id), studentNumber);
                groups.setIsJoined(isJoined.getOrDefault(id, false));
            }
        }

        Map<String, Integer> studentCount = groupsStudentService.getStudentCountByGroupId(Arrays.asList(id));
        groups.setStudentCount(studentCount.getOrDefault(id, CommonNumConstants.NUM_ZERO));
        return groups;
    }
}