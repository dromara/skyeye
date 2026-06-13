/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chtopic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.chtopic.classenum.StudentChooseActionType;
import com.skyeye.chtopic.dao.ChooseStudentHistoryDao;
import com.skyeye.chtopic.entity.ChooseStudentHistory;
import com.skyeye.chtopic.entity.ChooseTopic;
import com.skyeye.activity.service.ChooseActivityService;
import com.skyeye.chtopic.service.ChooseStudentHistoryService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.user.entity.ChooseUser;
import com.skyeye.user.service.ChooseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@SkyeyeService(name = "学生选择历史", groupName = "课题管理")
public class ChooseStudentHistoryServiceImpl extends SkyeyeBusinessServiceImpl<ChooseStudentHistoryDao, ChooseStudentHistory>
    implements ChooseStudentHistoryService {

    @Autowired
    private ChooseUserService chooseUserService;

    @Autowired
    private ChooseActivityService chooseActivityService;

    @Override
    public void saveStudentHistory(String activityId, String studentId, StudentChooseActionType actionType,
                                   ChooseTopic chooseTopic, String teacherId, String remark, String operatorId) {
        if (StrUtil.isEmpty(activityId) || StrUtil.isEmpty(studentId) || actionType == null) {
            return;
        }
        ChooseStudentHistory history = new ChooseStudentHistory();
        history.setActivityId(activityId);
        history.setStudentId(studentId);
        history.setActionType(actionType.getKey());
        if (ObjectUtil.isNotEmpty(chooseTopic)) {
            history.setTopicId(chooseTopic.getId());
            history.setTopicTitle(resolveTopicTitle(chooseTopic.getTitle()));
        }
        if (StrUtil.isNotEmpty(teacherId)) {
            history.setTeacherId(teacherId);
            ChooseUser teacher = chooseUserService.selectById(teacherId);
            if (ObjectUtil.isNotEmpty(teacher)) {
                history.setTeacherName(teacher.getName());
            }
        }
        history.setRemark(remark);
        createEntity(history, operatorId);
    }

    @Override
    public void queryStudentChooseHistoryByActivity(InputObject inputObject, OutputObject outputObject) {
        String activityId = inputObject.getParams().get("activityId").toString();
        String studentId = inputObject.getLogParams().get("id").toString();
        if (inputObject.getParams().containsKey("studentId")
            && StrUtil.isNotEmpty(inputObject.getParams().get("studentId").toString())) {
            studentId = inputObject.getParams().get("studentId").toString();
        }
        chooseActivityService.checkActivityParticipant(activityId, studentId);

        QueryWrapper<ChooseStudentHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseStudentHistory::getActivityId), activityId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseStudentHistory::getStudentId), studentId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ChooseStudentHistory::getCreateTime));
        List<ChooseStudentHistory> list = list(queryWrapper);
        list.forEach(item -> item.setActionTypeName(StudentChooseActionType.getNameByKey(item.getActionType())));
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void queryTeacherReviewHistoryByActivity(InputObject inputObject, OutputObject outputObject) {
        String activityId = inputObject.getParams().get("activityId").toString();
        String teacherId = inputObject.getLogParams().get("id").toString();
        chooseActivityService.checkActivityParticipant(activityId, teacherId);

        QueryWrapper<ChooseStudentHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseStudentHistory::getActivityId), activityId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseStudentHistory::getTeacherId), teacherId);
        queryWrapper.in(MybatisPlusUtil.toColumns(ChooseStudentHistory::getActionType), Arrays.asList(
            StudentChooseActionType.TEACHER_AGREE.getKey(),
            StudentChooseActionType.TEACHER_REJECT.getKey(),
            StudentChooseActionType.AUTO_REJECT.getKey()
        ));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ChooseStudentHistory::getCreateTime));
        List<ChooseStudentHistory> list = list(queryWrapper);
        list.forEach(item -> {
            item.setActionTypeName(StudentChooseActionType.getTeacherReviewNameByKey(item.getActionType()));
            if (StrUtil.isNotEmpty(item.getStudentId())) {
                ChooseUser student = chooseUserService.selectById(item.getStudentId());
                if (ObjectUtil.isNotEmpty(student)) {
                    item.setStudentName(student.getName());
                    item.setStudentStuNo(student.getStuNo());
                }
            }
        });
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    private String resolveTopicTitle(String title) {
        if (StrUtil.isEmpty(title)) {
            return "--";
        }
        if (StrUtil.startWith(title, "仅选导师")) {
            return "直接选导";
        }
        return title;
    }
}
