/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chtopic.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.activity.classenum.ActivityType;
import com.skyeye.activity.entity.ChooseActivity;
import com.skyeye.activity.service.ChooseActivityService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.chtopic.classenum.StudentChooseActionType;
import com.skyeye.chtopic.classenum.TeacherResultState;
import com.skyeye.chtopic.dao.ChooseTopicDao;
import com.skyeye.chtopic.entity.ChooseTopic;
import com.skyeye.chtopic.service.ChooseStudentHistoryService;
import com.skyeye.chtopic.service.ChooseTopicService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.util.RedisLock;
import com.skyeye.user.entity.ChooseUser;
import com.skyeye.user.enumclass.ChooseUserType;
import com.skyeye.user.service.ChooseUserService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ChooseTopicServiceImpl
 * @Description: 课题服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/8 10:21
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "课题管理", groupName = "课题管理")
public class ChooseTopicServiceImpl extends SkyeyeBusinessServiceImpl<ChooseTopicDao, ChooseTopic> implements ChooseTopicService {

    @Autowired
    private ChooseUserService chooseUserService;

    @Autowired
    private ChooseActivityService chooseActivityService;

    @Autowired
    private ChooseStudentHistoryService chooseStudentHistoryService;

    private void logStudentHistory(String activityId, String studentId, StudentChooseActionType actionType,
                                   ChooseTopic topic, String teacherId, String remark, String operatorId) {
        logStudentHistory(activityId, studentId, actionType, topic, teacherId, null, remark, operatorId);
    }

    private void logStudentHistory(String activityId, String studentId, StudentChooseActionType actionType,
                                   ChooseTopic topic, String teacherId, String teacherName, String remark, String operatorId) {
        chooseStudentHistoryService.saveStudentHistory(activityId, studentId, actionType, topic, teacherId, teacherName, remark, operatorId);
    }

    private void checkActivityParticipant(String activityId, String userId) {
        chooseActivityService.checkActivityParticipant(activityId, userId);
    }

    @Override
    protected QueryWrapper<ChooseTopic> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ChooseTopic> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getType())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getChoose), commonPageInfo.getType());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Map<String, Object> user = InputObject.getLogParamsStatic();
        if (user != null && !user.isEmpty() && StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            Integer type = Integer.valueOf(user.get("type").toString());
            if (!ChooseUserType.ADMIN.getKey().equals(type)) {
                checkActivityParticipant(commonPageInfo.getObjectId(), user.get("id").toString());
            }
        }
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        chooseUserService.setMationForMap(beans, "chooseUserId", "chooseUserMation");
        chooseUserService.setMationForMap(beans, "teacherId", "teacherMation");
        chooseActivityService.setMationForMap(beans, "activityId", "activityMation");
        return beans;
    }

    @Override
    public void importChooseTopic(InputObject inputObject, OutputObject outputObject) {
        // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (multipartResolver.isMultipart(PutObject.getRequest())) {
            // 将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
            // 获取multiRequest 中所有的文件名
            Iterator iter = multiRequest.getFileNames();
            String activityId = inputObject.getParams().get("activityId").toString();
            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                ImportParams reportModelAttrParams = new ImportParams();
                reportModelAttrParams.setStartSheetIndex(0);
                List<ChooseTopic> chooseTopicList;
                try {
                    chooseTopicList = ExcelImportUtil.importExcel(file.getInputStream(), ChooseTopic.class, reportModelAttrParams);
                } catch (Exception ee) {
                    throw new CustomException(ee);
                }
                List<ChooseTopic> insertList = checkChooseTopicListExit(chooseTopicList);
                if (CollectionUtil.isEmpty(insertList)) {
                    return;
                }
                insertList.forEach(bean -> {
                    Map<String, Object> business = BeanUtil.beanToMap(bean);
                    bean.setChoose(1);
                    bean.setActivityId(activityId);
                    bean.setOddNumber(iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business));
                });
                createEntity(insertList, StrUtil.EMPTY);
            }
        }
    }

    /**
     * 上传课题列表时检查数据
     *
     * @param chooseTopicList
     * @return 数据库不存在的数据
     */
    private List<ChooseTopic> checkChooseTopicListExit(List<ChooseTopic> chooseTopicList) {
        if (CollectionUtil.isEmpty(chooseTopicList)) {
            return Collections.emptyList();
        }
        // 过滤标题为空的数据
        List<ChooseTopic> insertList = chooseTopicList.stream()
            .filter(chooseTopic -> StrUtil.isNotEmpty(chooseTopic.getTitle())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(insertList)) {
            return Collections.emptyList();
        }
        // 获取标题集合
        List<String> titilList = insertList.stream().map(ChooseTopic::getTitle).collect(Collectors.toList());
        String titleColumn = MybatisPlusUtil.toColumns(ChooseTopic::getTitle);
        // 查询数据库已经存在的标题
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(titleColumn);
        queryWrapper.in(titleColumn, titilList);
        List<ChooseTopic> exitChooseTopicList = list(queryWrapper);
        if (CollectionUtil.isEmpty(exitChooseTopicList)) {
            return insertList;
        }
        List<String> exitsTitle = exitChooseTopicList.stream().map(ChooseTopic::getTitle).collect(Collectors.toList());
        // 过滤掉已存在的数据
        List<ChooseTopic> resultList = insertList.stream().filter(chooseTopic -> !exitsTitle.contains(chooseTopic.getTitle())).collect(Collectors.toList());
        return resultList;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void chooseTopicById(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String id = inputObject.getParams().get("id").toString();
        Object teacherIdParam = inputObject.getParams().get("teacherId");
        String teacherId = teacherIdParam == null ? StrUtil.EMPTY : teacherIdParam.toString();

        ChooseTopic preliminary = selectById(id);
        if (ObjectUtil.isEmpty(preliminary)) {
            throw new CustomException("该数据不存在");
        }
        if (StrUtil.isEmpty(preliminary.getActivityId())) {
            throw new CustomException("该课题未关联活动,课题未发布");
        }
        String activityId = preliminary.getActivityId();
        Runnable action = () -> chooseTopicByIdInternal(userId, id, teacherId, outputObject);
        if (StrUtil.isNotEmpty(teacherId)) {
            executeWithTopicLock(id, () -> executeWithStudentChooseLock(activityId, userId,
                () -> executeWithTeacherChooseLock(activityId, teacherId, action)));
        } else {
            executeWithTopicLock(id, () -> executeWithStudentChooseLock(activityId, userId, action));
        }
    }

    private void chooseTopicByIdInternal(String userId, String id, String teacherId, OutputObject outputObject) {
        ChooseTopic chooseTopic = selectById(id);
        if (ObjectUtil.isEmpty(chooseTopic)) {
            throw new CustomException("该数据不存在");
        }
        checkActivityParticipant(chooseTopic.getActivityId(), userId);
        String oldTeacherId = chooseTopic.getTeacherId();
        boolean chooseTopicAction = false;
        boolean chooseTeacherAction = false;
        ChooseUser chooseUserTeacher = null;
        UpdateWrapper<ChooseTopic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        ChooseActivity chooseActivity = chooseActivityService.selectById(chooseTopic.getActivityId());
        boolean activityIsRun = chooseActivityService.checkActivityIsRun(chooseActivity);
        if (activityIsRun) {
            chooseActivityService.checkTopicSelectionEnabled(chooseActivity);
            if (chooseTopic.getChoose() == CommonNumConstants.NUM_TWO && !StrUtil.equals(chooseTopic.getChooseUserId(), userId)) {
                throw new CustomException("该课题已被选择");
            }
            QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getChooseUserId), userId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), chooseTopic.getActivityId());
            queryWrapper.ne(CommonConstants.ID, id);
            ChooseTopic one = getOne(queryWrapper, false);
            if (ObjectUtil.isNotEmpty(one)) {
                throw new CustomException("您已选题，请勿重复选题");
            }
            chooseTopicAction = chooseTopic.getChoose() != CommonNumConstants.NUM_TWO
                || !StrUtil.equals(chooseTopic.getChooseUserId(), userId);
            if (chooseTopicAction) {
                updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getChoose), CommonNumConstants.NUM_TWO);
                updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getChooseUserId), userId);
            }
        } else if (StrUtil.isEmpty(teacherId)) {
            throw new CustomException("该活动未开始或已结束");
        }
        if (StrUtil.isNotEmpty(teacherId) && chooseActivityService.checkActivityIsStart(chooseActivity)) {
            chooseActivityService.checkTeacherSelectionEnabled(chooseActivity);
            chooseUserTeacher = getTeacherUser(teacherId);
            if (Objects.equals(chooseUserTeacher.getActivityType(), ActivityType.UN_SINGLE.getKey())
                && StrUtil.equals(chooseTopic.getTeacherId(), teacherId)
                && chooseTopic.getTeacherResult() == TeacherResultState.AGREE.getKey()) {
                throw new CustomException("双选类型选题活动，导师同意后不可选择导师");
            }
            Integer teacherResult = resolveTeacherResult(chooseUserTeacher);
            if (!(StrUtil.equals(chooseTopic.getTeacherId(), teacherId)
                && Objects.equals(chooseTopic.getTeacherResult(), teacherResult))) {
                if (checkTeacherOverLimit(teacherId, chooseTopic.getActivityId(), chooseUserTeacher)) {
                    throw new CustomException("已超过该导师的最大选择次数，请选择其他导师");
                }
                updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), teacherId);
                updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult), teacherResult);
                chooseTeacherAction = true;
            }
        }
        if (chooseTopicAction || chooseTeacherAction) {
            update(updateWrapper);
            refreshCache(id);
        }
        if (chooseTopicAction) {
            logStudentHistory(chooseTopic.getActivityId(), userId, StudentChooseActionType.CHOOSE_TOPIC,
                chooseTopic, null, null, userId);
        }
        if (chooseTeacherAction) {
            StudentChooseActionType actionType = StrUtil.isNotEmpty(oldTeacherId)
                && !StrUtil.equals(oldTeacherId, teacherId) ? StudentChooseActionType.CHANGE_TEACHER
                : StudentChooseActionType.CHOOSE_TEACHER;
            logStudentHistory(chooseTopic.getActivityId(), userId, actionType, chooseTopic, teacherId,
                chooseUserTeacher.getName(), null, userId);
        }
        outputObject.setBean(selectById(id));
    }

    /**
     * 检查导师选择次数是否大于等于8次(不包含8)
     *
     * @param teacherId  教师id
     * @param activityId 活动id
     * @return ture:大于等于8次  false:小于8次
     */
    private boolean checkTeacherOverLimit(String teacherId, String activityId) {
        ChooseUser chooseUser = chooseUserService.selectById(teacherId);
        return checkTeacherOverLimit(teacherId, activityId, chooseUser);
    }

    private boolean checkTeacherOverLimit(String teacherId, String activityId, ChooseUser chooseUser) {
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID)
            .eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), teacherId)
            .eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId)
            .eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult), TeacherResultState.AGREE.getKey());
        long count = count(queryWrapper);
        int maxCount = chooseUser == null || chooseUser.getGuideCapacity() == null
            ? CommonNumConstants.NUM_EIGHT : chooseUser.getGuideCapacity();
        return count >= maxCount;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cnacleChooseTopicById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        ChooseTopic chooseTopic = selectById(id);
        if (ObjectUtil.isEmpty(chooseTopic)) {
            throw new CustomException("该数据不存在");
        }
        if (StrUtil.isEmpty(chooseTopic.getActivityId())) {
            throw new CustomException("该课题未关联活动,课题未发布");
        }
        checkActivityParticipant(chooseTopic.getActivityId(), userId);
        ChooseActivity chooseActivity = chooseActivityService.selectById(chooseTopic.getActivityId());
        chooseActivityService.checkTopicSelectionEnabled(chooseActivity);
        boolean activityIsRun = chooseActivityService.checkActivityIsRun(chooseActivity);
        if (!activityIsRun) {
            throw new CustomException("该活动未开始或已结束,不可取消选课");
        }
        if (chooseTopic.getChoose() == CommonNumConstants.NUM_ONE) {
            throw new CustomException("该课题未被选择");
        }
        if (!StrUtil.equals(userId, chooseTopic.getChooseUserId())) {
            throw new CustomException("您无法取消他人选择的课题");
        }
        String oldTeacherId = chooseTopic.getTeacherId();
        UpdateWrapper<ChooseTopic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getChoose), CommonNumConstants.NUM_ONE);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getChooseUserId), StrUtil.EMPTY);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult), null);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), StrUtil.EMPTY);
        update(updateWrapper);
        refreshCache(id);
        logStudentHistory(chooseTopic.getActivityId(), userId, StudentChooseActionType.CANCEL_TOPIC,
            chooseTopic, null, null, userId);
        if (StrUtil.isNotEmpty(oldTeacherId)) {
            logStudentHistory(chooseTopic.getActivityId(), userId, StudentChooseActionType.CANCEL_TEACHER,
                chooseTopic, oldTeacherId, null, userId);
        }
    }

    @Override
    public void exportChooseTopic(InputObject inputObject, OutputObject outputObject) {
        String activityId = inputObject.getParams().get("activityId").toString();
        exportExcel(activityId);
    }

    private void exportExcel(String activityId) {
        HttpServletResponse response = InputObject.getResponse();
        ChooseActivity activity = chooseActivityService.selectById(activityId);
        boolean topicEnabled = chooseActivityService.isTopicSelectionEnabled(activity);
        boolean teacherOnly = !topicEnabled && chooseActivityService.isTeacherSelectionEnabled(activity);
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        if (teacherOnly) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getChoose), CommonNumConstants.NUM_TWO);
        }
        // 导出数据
        List<ChooseTopic> list = list(queryWrapper);
        chooseUserService.setDataMation(list, ChooseTopic::getChooseUserId);
        chooseUserService.setDataMation(list, ChooseTopic::getTeacherId);
        chooseActivityService.setDataMation(list, ChooseTopic::getActivityId);
        list.forEach(bean -> {
            if (ObjectUtil.isNotEmpty(bean.getChooseUserMation())) {
                bean.setChooseUserName(bean.getChooseUserMation().getName());
            }
            if (ObjectUtil.isNotEmpty(bean.getActivityMation())) {
                bean.setActivityName(bean.getActivityMation().getName());
            }
            if (ObjectUtil.isNotEmpty(bean.getTeacherMation())) {
                bean.setMentorName(bean.getTeacherMation().getName());
                if (ObjectUtil.isNotEmpty(bean.getTeacherMation().getActivityType())) {
                    bean.setActivityType(bean.getTeacherMation().getActivityType());
                }
            }
            if (teacherOnly && StrUtil.isNotEmpty(bean.getTitle()) && StrUtil.startWith(bean.getTitle(), "仅选导师")) {
                bean.setTitle("直接选导");
            }
        });
        //.xls格式
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        //前端存在跨域不成功，设置可访问
        response.setHeader("Access-Control-Allow-Origin", "*");
        //设置不要缓存
        response.setHeader("Pragma", "No-cache");
        try {
            response.setHeader("Content-disposition", "attachment;filename=chooseTopicResult.xls");
            //设置sheet名
            ExportParams params = new ExportParams();
            params.setSheetName(teacherOnly ? "学生选导结果表" : "学生选题结果表");
            // 这里需要设置不关闭流
            Workbook workbook = ExcelExportUtil.exportExcel(params, ChooseTopic.class, list);
            //输出流
            OutputStream outStream = response.getOutputStream();
            //浏览器下载
            workbook.write(outStream);
            //关闭流
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<ChooseTopic> queryListByActivityId(String activityId) {
        if (StrUtil.isEmpty(activityId)) {
            return new ArrayList<>();
        }
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        List<ChooseTopic> chooseTopicList = list(queryWrapper);
        chooseUserService.setDataMation(chooseTopicList, ChooseTopic::getChooseUserId);
        chooseUserService.setDataMation(chooseTopicList, ChooseTopic::getTeacherId);
        return chooseTopicList;
    }

    @Override
    public void queryChooseMeTopicList(InputObject inputObject, OutputObject outputObject) {
        String currentUserId = inputObject.getLogParams().get("id").toString();
        String activityId = inputObject.getParams().get("activityId").toString();
        if (StrUtil.isNotEmpty(activityId)) {
            checkActivityParticipant(activityId, currentUserId);
        }
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(activityId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), currentUserId);
        List<ChooseTopic> beans = list(queryWrapper);
        chooseUserService.setDataMation(beans, ChooseTopic::getChooseUserId);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void changeResultForTeacher(InputObject inputObject, OutputObject outputObject) {
        Integer teacherResult = Integer.valueOf(inputObject.getParams().get("teacherResult").toString());
        String currentUserId = inputObject.getLogParams().get("id").toString();
        ChooseTopic chooseTopic = selectById(inputObject.getParams().get("id").toString());
        if (ObjectUtil.isEmpty(chooseTopic)) {
            throw new CustomException("该课题不存在");
        }
        ChooseActivity chooseActivity = chooseActivityService.selectById(chooseTopic.getActivityId());
        chooseActivityService.checkTeacherSelectionEnabled(chooseActivity);
        checkActivityParticipant(chooseActivity.getId(), currentUserId);
        if (!StrUtil.equals(currentUserId, chooseTopic.getTeacherId())) {
            throw new CustomException("该课题信息你不是指导老师，不可修改");
        }
        // 判断入参是否合法，合法则修改，非法则报错
        if (teacherResult.equals(TeacherResultState.AGREE.getKey())) {
            chooseTopic.setTeacherResult(TeacherResultState.AGREE.getKey());
            super.updateEntity(chooseTopic, currentUserId);
            refreshCache(chooseTopic.getId());
            logStudentHistory(chooseActivity.getId(), chooseTopic.getChooseUserId(), StudentChooseActionType.TEACHER_AGREE,
                chooseTopic, currentUserId, null, currentUserId);
        } else if (teacherResult.equals(TeacherResultState.NOT_AGREE.getKey())) {
            String rejectedTeacherId = chooseTopic.getTeacherId();
            String studentId = chooseTopic.getChooseUserId();
            // 双选拒绝后解除导师与学生关系，释放已选人数占用
            chooseTopic.setTeacherId(StrUtil.EMPTY);
            chooseTopic.setTeacherResult(null);
            super.updateEntity(chooseTopic, currentUserId);
            refreshCache(chooseTopic.getId());
            logStudentHistory(chooseActivity.getId(), studentId, StudentChooseActionType.TEACHER_REJECT,
                chooseTopic, rejectedTeacherId, null, currentUserId);
        } else {
            throw new CustomException("非法状态");
        }
        boolean overLimit = checkTeacherOverLimit(currentUserId, chooseActivity.getId());
        if (overLimit) {
            // 超过数量(8个)限制，其他同学自动拒绝并解除关系
            QueryWrapper<ChooseTopic> pendingQuery = new QueryWrapper<>();
            pendingQuery.eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), currentUserId)
                .eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), chooseActivity.getId())
                .eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult), TeacherResultState.WAITE.getKey());
            List<ChooseTopic> pendingList = list(pendingQuery);
            if (CollectionUtil.isNotEmpty(pendingList)) {
                UpdateWrapper<ChooseTopic> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), currentUserId)
                    .eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), chooseActivity.getId())
                    .eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult), TeacherResultState.WAITE.getKey());
                updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), StrUtil.EMPTY);
                updateWrapper.set(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult), null);
                List<String> chooseTopicIdList = pendingList.stream().map(ChooseTopic::getId).collect(Collectors.toList());
                update(updateWrapper);
                refreshCache(chooseTopicIdList);
                pendingList.forEach(item -> logStudentHistory(chooseActivity.getId(), item.getChooseUserId(),
                    StudentChooseActionType.AUTO_REJECT, item, currentUserId, "导师指导容量已满", currentUserId));
            }
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cancelTeacherResult(InputObject inputObject, OutputObject outputObject) {
        String currentUserId = inputObject.getLogParams().get("id").toString();
        String topicId = inputObject.getParams().get("id").toString();
        ChooseTopic preliminary = selectById(topicId);
        if (ObjectUtil.isEmpty(preliminary)) {
            throw new CustomException("该课题不存在");
        }
        if (StrUtil.isEmpty(preliminary.getActivityId())) {
            throw new CustomException("该课题未关联活动，不可取消");
        }

        executeWithStudentChooseLock(preliminary.getActivityId(), currentUserId, () -> {
            ChooseTopic chooseTopic = selectById(topicId);
            if (ObjectUtil.isEmpty(chooseTopic)) {
                throw new CustomException("该课题不存在");
            }
            ChooseActivity chooseActivity = chooseActivityService.selectById(chooseTopic.getActivityId());
            chooseActivityService.checkTeacherSelectionEnabled(chooseActivity);
            checkActivityParticipant(chooseTopic.getActivityId(), currentUserId);
            if (StrUtil.isEmpty(chooseTopic.getChooseUserId())) {
                throw new CustomException("该课题未关联学生，不可取消");
            }
            if (!StrUtil.equals(currentUserId, chooseTopic.getChooseUserId())) {
                throw new CustomException("该课题信息你不是选择的学生，不可取消");
            }
            if (StrUtil.isEmpty(chooseTopic.getTeacherId())) {
                outputObject.setBean(chooseTopic);
                return;
            }

            ChooseUser chooseUserTeacher = chooseUserService.selectById(chooseTopic.getTeacherId());
            assertCanCancelTeacher(chooseTopic, chooseUserTeacher);
            String oldTeacherId = chooseTopic.getTeacherId();
            String teacherName = ObjectUtil.isNotEmpty(chooseUserTeacher) ? chooseUserTeacher.getName() : null;
            chooseTopic.setTeacherId(StrUtil.EMPTY);
            chooseTopic.setTeacherResult(null);
            super.updateEntity(chooseTopic, currentUserId);
            refreshCache(chooseTopic.getId());
            logStudentHistory(chooseTopic.getActivityId(), currentUserId, StudentChooseActionType.CANCEL_TEACHER,
                chooseTopic, oldTeacherId, teacherName, null, currentUserId);
            outputObject.setBean(selectById(topicId));
        });
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void chooseTeacher(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String currentUserId = inputObject.getLogParams().get("id").toString();
        String topicId = params.get("id").toString();
        String teacherId = params.get("teacherId").toString();

        ChooseTopic preliminary = selectById(topicId);
        if (ObjectUtil.isEmpty(preliminary)) {
            throw new CustomException("该课题不存在");
        }
        if (StrUtil.isEmpty(preliminary.getActivityId())) {
            throw new CustomException("该课题未关联活动，不可选择导师");
        }

        executeWithTopicLock(topicId, () -> executeWithStudentChooseLock(preliminary.getActivityId(), currentUserId,
            () -> executeWithTeacherChooseLock(preliminary.getActivityId(), teacherId, () -> {
                ChooseTopic chooseTopic = selectById(topicId);
                if (ObjectUtil.isEmpty(chooseTopic)) {
                    throw new CustomException("该课题不存在");
                }
                checkActivityParticipant(chooseTopic.getActivityId(), currentUserId);
                ChooseActivity chooseActivity = chooseActivityService.selectById(chooseTopic.getActivityId());
                chooseActivityService.checkTeacherSelectionEnabled(chooseActivity);
                if (!chooseActivityService.checkActivityIsStart(chooseActivity)) {
                    throw new CustomException("该活动未开始，不可选择导师");
                }
                if (StrUtil.isEmpty(chooseTopic.getChooseUserId())) {
                    throw new CustomException("该课题未关联学生，不可选择导师");
                }
                if (!StrUtil.equals(currentUserId, chooseTopic.getChooseUserId())) {
                    throw new CustomException("你未选择该课题，不可选择导师");
                }

                ChooseUser chooseUserTeacher = getTeacherUser(teacherId);
                if (Objects.equals(chooseUserTeacher.getActivityType(), ActivityType.UN_SINGLE.getKey())
                    && StrUtil.equals(chooseTopic.getTeacherId(), teacherId)
                    && chooseTopic.getTeacherResult() == TeacherResultState.AGREE.getKey()) {
                    throw new CustomException("双选类型选题活动，导师同意后不可选择导师");
                }
                Integer teacherResult = resolveTeacherResult(chooseUserTeacher);
                if (StrUtil.equals(chooseTopic.getTeacherId(), teacherId)
                    && Objects.equals(chooseTopic.getTeacherResult(), teacherResult)) {
                    outputObject.setBean(chooseTopic);
                    return;
                }
                if (checkTeacherOverLimit(teacherId, chooseActivity.getId(), chooseUserTeacher)) {
                    throw new CustomException("超出导师选择数量限制，请选择其他导师");
                }

                String oldTeacherId = chooseTopic.getTeacherId();
                chooseTopic.setTeacherId(teacherId);
                chooseTopic.setTeacherResult(teacherResult);
                super.updateEntity(chooseTopic, currentUserId);
                refreshCache(chooseTopic.getId());
                StudentChooseActionType actionType = StrUtil.isNotEmpty(oldTeacherId)
                    && !StrUtil.equals(oldTeacherId, teacherId) ? StudentChooseActionType.CHANGE_TEACHER
                    : StudentChooseActionType.CHOOSE_TEACHER;
                logStudentHistory(chooseTopic.getActivityId(), currentUserId, actionType, chooseTopic, teacherId,
                    chooseUserTeacher.getName(), null, currentUserId);
                outputObject.setBean(selectById(topicId));
            })));
    }

    private ChooseUser getTeacherUser(String teacherId) {
        if (StrUtil.isEmpty(teacherId)) {
            throw new CustomException("导师不存在或该角色不是教师");
        }
        ChooseUser chooseUser = chooseUserService.selectById(teacherId);
        if (ObjectUtil.isEmpty(chooseUser) || !Objects.equals(chooseUser.getType(), ChooseUserType.TEACHER.getKey())) {
            throw new CustomException("导师不存在或该角色不是教师");
        }
        return chooseUser;
    }

    private ChooseTopic getStudentChooseRecord(String activityId, String userId) {
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getChooseUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getChoose), CommonNumConstants.NUM_TWO);
        return getOne(queryWrapper, false);
    }

    private ChooseTopic getStudentTopicRecord(String activityId, String userId) {
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getChooseUserId), userId);
        return getOne(queryWrapper, false);
    }

    private Integer resolveTeacherResult(ChooseUser teacher) {
        return Objects.equals(teacher.getActivityType(), ActivityType.SINGLE.getKey())
            ? TeacherResultState.AGREE.getKey() : TeacherResultState.WAITE.getKey();
    }

    private void validateTeacherSelectionActivity(ChooseActivity chooseActivity) {
        if (ObjectUtil.isEmpty(chooseActivity)) {
            throw new CustomException("活动不存在");
        }
        chooseActivityService.checkTeacherSelectionEnabled(chooseActivity);
        if (!chooseActivityService.checkActivityIsRun(chooseActivity)) {
            throw new CustomException("该活动未开始或已结束,不可选择导师");
        }
    }

    private void assertCanCancelTeacher(ChooseTopic chooseTopic, ChooseUser teacher) {
        if (ObjectUtil.isEmpty(teacher)) {
            return;
        }
        if (Objects.equals(teacher.getActivityType(), ActivityType.UN_SINGLE.getKey())
            && chooseTopic.getTeacherResult() == TeacherResultState.AGREE.getKey()) {
            throw new CustomException("双选类型选题活动，导师同意后不可取消选择导师");
        }
    }

    private void removeChooseTopicRecord(ChooseTopic chooseTopic) {
        clearCache(chooseTopic.getId());
        removeById(chooseTopic.getId());
    }

    private void executeWithTopicLock(String topicId, Runnable action) {
        String lockKey = String.format("choose:topic:%s", topicId);
        RedisLock lock = new RedisLock(lockKey, 3000, 10000);
        try {
            if (!lock.lock()) {
                throw new CustomException("当前选择人数较多，请稍后重试");
            }
            action.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException("操作失败，请稍后重试");
        } finally {
            lock.unlock();
        }
    }

    private void executeWithStudentChooseLock(String activityId, String userId, Runnable action) {
        String lockKey = String.format("choose:activity:%s:student:%s", activityId, userId);
        RedisLock lock = new RedisLock(lockKey, 3000, 10000);
        try {
            if (!lock.lock()) {
                throw new CustomException("操作过于频繁，请稍后重试");
            }
            action.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException("操作失败，请稍后重试");
        } finally {
            lock.unlock();
        }
    }

    private void executeWithTeacherChooseLock(String activityId, String teacherId, Runnable action) {
        String lockKey = String.format("choose:activity:%s:teacher:%s", activityId, teacherId);
        RedisLock lock = new RedisLock(lockKey, 3000, 10000);
        try {
            if (!lock.lock()) {
                throw new CustomException("当前选择人数较多，请稍后重试");
            }
            action.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException("操作失败，请稍后重试");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 判断导师id是否存在
     *
     * @param teacherId
     * @return ture  存在 false 不存在
     */
    private boolean checkTeacherId(String teacherId) {
        if (StrUtil.isEmpty(teacherId)) {
            return false;
        }
        ChooseUser chooseUser = chooseUserService.selectById(teacherId);
        return ObjectUtil.isNotEmpty(chooseUser) && Objects.equals(chooseUser.getType(), ChooseUserType.TEACHER.getKey());
    }

    @Override
    public void deleteByActivityId(String activityId) {
        if (StrUtil.isEmpty(activityId)) {
            return;
        }
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, Integer> getChooseTopicCountByActivityId(String activityId, List<String> userIds) {
        if (StrUtil.isEmpty(activityId) || CollectionUtil.isEmpty(userIds)) {
            return new HashMap<>();
        }
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        queryWrapper.in(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), userIds);
        queryWrapper.ne(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), StrUtil.EMPTY);
        queryWrapper.in(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult),
            TeacherResultState.AGREE.getKey(), TeacherResultState.WAITE.getKey());
        List<ChooseTopic> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) { // 没有数据
            return new HashMap<>();
        }
        Map<String, Integer> resultMap = new HashMap<>();
        list.forEach(bean -> {
            String teacherId = bean.getTeacherId();
            if (resultMap.containsKey(teacherId)) {
                resultMap.put(teacherId, resultMap.get(teacherId) + 1);
            } else {
                resultMap.put(teacherId, 1);
            }
        });
        return resultMap;
    }

    @Override
    public void queryTeacherTopicNum(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String activityId = params.get("activityId").toString();
        String teacherId = params.get("teacherId").toString();
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherId), teacherId);
        queryWrapper.in(MybatisPlusUtil.toColumns(ChooseTopic::getTeacherResult),
            TeacherResultState.AGREE.getKey(), TeacherResultState.WAITE.getKey());
        List<ChooseTopic> list = list(queryWrapper);
        ChooseUser chooseUser = chooseUserService.selectById(teacherId);
        chooseUser.setTopicCount(list.size());
        outputObject.setBean(chooseUser);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryStudentChooseByActivity(InputObject inputObject, OutputObject outputObject) {
        String activityId = inputObject.getParams().get("activityId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        checkActivityParticipant(activityId, userId);
        QueryWrapper<ChooseTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getActivityId), activityId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getChooseUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseTopic::getChoose), CommonNumConstants.NUM_TWO);
        ChooseTopic chooseTopic = getOne(queryWrapper, false);
        outputObject.setBean(chooseTopic);
        outputObject.settotal(ObjectUtil.isEmpty(chooseTopic) ? CommonNumConstants.NUM_ZERO : CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void chooseActivityTeacher(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String activityId = params.get("activityId").toString();
        String teacherId = params.get("teacherId").toString();
        String userId = inputObject.getLogParams().get("id").toString();

        executeWithStudentChooseLock(activityId, userId, () -> executeWithTeacherChooseLock(activityId, teacherId, () -> {
            ChooseActivity chooseActivity = chooseActivityService.selectById(activityId);
            validateTeacherSelectionActivity(chooseActivity);
            checkActivityParticipant(activityId, userId);
            ChooseUser chooseUserTeacher = getTeacherUser(teacherId);
            Integer teacherResult = resolveTeacherResult(chooseUserTeacher);

            ChooseTopic chooseTopic = getStudentTopicRecord(activityId, userId);
            if (ObjectUtil.isNotEmpty(chooseTopic)) {
                if (StrUtil.equals(chooseTopic.getTeacherId(), teacherId)
                    && Objects.equals(chooseTopic.getTeacherResult(), teacherResult)) {
                    outputObject.setBean(chooseTopic);
                    return;
                }
                if (Objects.equals(chooseUserTeacher.getActivityType(), ActivityType.UN_SINGLE.getKey())
                    && StrUtil.equals(chooseTopic.getTeacherId(), teacherId)
                    && chooseTopic.getTeacherResult() == TeacherResultState.AGREE.getKey()) {
                    throw new CustomException("双选类型选题活动，导师同意后不可选择导师");
                }
                if (checkTeacherOverLimit(teacherId, activityId, chooseUserTeacher)) {
                    throw new CustomException("已超过该导师的最大选择次数，请选择其他导师");
                }
                String oldTeacherId = chooseTopic.getTeacherId();
                chooseTopic.setTeacherId(teacherId);
                chooseTopic.setTeacherResult(teacherResult);
                super.updateEntity(chooseTopic, userId);
                refreshCache(chooseTopic.getId());
                StudentChooseActionType actionType = StrUtil.isNotEmpty(oldTeacherId)
                    && !StrUtil.equals(oldTeacherId, teacherId) ? StudentChooseActionType.CHANGE_TEACHER
                    : StudentChooseActionType.CHOOSE_TEACHER;
                logStudentHistory(activityId, userId, actionType, chooseTopic, teacherId,
                    chooseUserTeacher.getName(), null, userId);
                outputObject.setBean(chooseTopic);
                return;
            }

            if (chooseActivityService.isTopicSelectionEnabled(chooseActivity)) {
                throw new CustomException("请先选择课题后再选择导师");
            }
            if (checkTeacherOverLimit(teacherId, activityId, chooseUserTeacher)) {
                throw new CustomException("已超过该导师的最大选择次数，请选择其他导师");
            }

            ChooseTopic insertTopic = new ChooseTopic();
            insertTopic.setActivityId(activityId);
            insertTopic.setChooseUserId(userId);
            insertTopic.setChoose(CommonNumConstants.NUM_TWO);
            insertTopic.setTitle("仅选导师-" + userId);
            insertTopic.setTeacherId(teacherId);
            insertTopic.setTeacherResult(teacherResult);
            Map<String, Object> business = BeanUtil.beanToMap(insertTopic);
            insertTopic.setOddNumber(iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business));
            createEntity(insertTopic, userId);
            logStudentHistory(activityId, userId, StudentChooseActionType.CHOOSE_TEACHER, insertTopic, teacherId,
                chooseUserTeacher.getName(), null, userId);
            outputObject.setBean(insertTopic);
        }));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cancelActivityTeacher(InputObject inputObject, OutputObject outputObject) {
        String activityId = inputObject.getParams().get("activityId").toString();
        String userId = inputObject.getLogParams().get("id").toString();

        executeWithStudentChooseLock(activityId, userId, () -> {
            ChooseActivity chooseActivity = chooseActivityService.selectById(activityId);
            if (ObjectUtil.isEmpty(chooseActivity)) {
                throw new CustomException("活动不存在");
            }
            chooseActivityService.checkTeacherSelectionEnabled(chooseActivity);
            checkActivityParticipant(activityId, userId);

            ChooseTopic chooseTopic = getStudentChooseRecord(activityId, userId);
            if (ObjectUtil.isEmpty(chooseTopic) || StrUtil.isEmpty(chooseTopic.getTeacherId())) {
                outputObject.setBean(null);
                return;
            }

            ChooseUser chooseUserTeacher = StrUtil.isNotEmpty(chooseTopic.getTeacherId())
                ? chooseUserService.selectById(chooseTopic.getTeacherId()) : null;
            assertCanCancelTeacher(chooseTopic, chooseUserTeacher);
            String oldTeacherId = chooseTopic.getTeacherId();
            String teacherName = ObjectUtil.isNotEmpty(chooseUserTeacher) ? chooseUserTeacher.getName() : null;

            if (chooseActivityService.isTopicSelectionEnabled(chooseActivity) && StrUtil.isNotEmpty(chooseTopic.getTitle())
                && !StrUtil.startWith(chooseTopic.getTitle(), "仅选导师")) {
                chooseTopic.setTeacherId(StrUtil.EMPTY);
                chooseTopic.setTeacherResult(null);
                super.updateEntity(chooseTopic, userId);
                refreshCache(chooseTopic.getId());
                logStudentHistory(activityId, userId, StudentChooseActionType.CANCEL_TEACHER,
                    chooseTopic, oldTeacherId, teacherName, null, userId);
                outputObject.setBean(chooseTopic);
                return;
            }

            logStudentHistory(activityId, userId, StudentChooseActionType.CANCEL_TEACHER,
                chooseTopic, oldTeacherId, teacherName, null, userId);
            removeChooseTopicRecord(chooseTopic);
            outputObject.setBean(null);
        });
    }
}
