/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.activity.dao.ChooseActivityDao;
import com.skyeye.activity.entity.ChooseActivity;
import com.skyeye.activity.entity.ChooseActivityUser;
import com.skyeye.activity.service.ChooseActivityService;
import com.skyeye.activity.service.ChooseActivityUserService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.chtopic.service.ChooseTopicService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.user.enumclass.ChooseUserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ActivityServiceImpl
 * @Description: 选题活动管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "选题活动管理", groupName = "选题活动管理")
public class ChooseActivityServiceImpl extends SkyeyeBusinessServiceImpl<ChooseActivityDao, ChooseActivity> implements ChooseActivityService {

    @Autowired
    private ChooseActivityUserService chooseActivityUserService;

    @Autowired
    private ChooseTopicService chooseTopicService;

    public void validatorEntity(ChooseActivity entity) {
        super.validatorEntity(entity);
        Map<String, Object> currentUserInfo = InputObject.getLogParamsStatic();
        if (!Integer.valueOf(currentUserInfo.get("type").toString()).equals(ChooseUserType.ADMIN.getKey())
            && !Integer.valueOf(currentUserInfo.get("type").toString()).equals(ChooseUserType.TEACHER.getKey())) {
            throw new CustomException("管理员用户，和老师用户才能创建/编辑选题活动");
        }
        if (DateUtil.compare(entity.getEndTime(), entity.getStartTime())) {
            throw new CustomException("结束时间不能早于开始时间");
        }
        if (entity.getTopicEnable() == null) {
            entity.setTopicEnable(WhetherEnum.ENABLE_USING.getKey());
        }
        if (entity.getTeacherEnable() == null) {
            entity.setTeacherEnable(WhetherEnum.ENABLE_USING.getKey());
        }
    }

    @Override
    public void deletePreExecution(ChooseActivity entity) {
        super.deletePreExecution(entity);
        Map<String, Object> currentUserInfo = InputObject.getLogParamsStatic();
        if (Integer.valueOf(currentUserInfo.get("type").toString()).equals(ChooseUserType.ADMIN.getKey())) {
            // 管理账号直接越过创建人校验逻辑
            return;
        }
        String currentUserId = currentUserInfo.get("id").toString();
        if (!currentUserId.equals(entity.getCreateId())) {
            throw new CustomException("该活动不是你创建的，你没有权限删除它");
        }
    }

    @Override
    public void deletePostpose(String id) {
        super.deletePostpose(id);
        chooseActivityUserService.deleteByActivityId(id);
        chooseTopicService.deleteByActivityId(id);
    }

    @Override
    public ChooseActivity selectById(String id) {
        ChooseActivity chooseActivity = super.selectById(id);
        Map<String, Object> user = InputObject.getLogParamsStatic();
        Integer type = Integer.valueOf(user.get("type").toString());
        if (ChooseUserType.ADMIN.getKey().equals(type)) {
            chooseActivity.setInActivity(true);
            return chooseActivity;
        }
        String userId = user.get("id").toString();
        chooseActivity.setInActivity(isActivityParticipant(chooseActivity.getId(), userId));
        return chooseActivity;
    }

    @Override
    public boolean isActivityParticipant(String activityId, String userId) {
        return chooseActivityUserService.isActivityParticipant(activityId, userId);
    }

    @Override
    public void checkActivityParticipant(String activityId, String userId) {
        if (!isActivityParticipant(activityId, userId)) {
            throw new CustomException("您不是本次活动的参选人员，无法操作");
        }
    }

    @Override
    public void queryActivityList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        Map<String, Object> user = InputObject.getLogParamsStatic();
        String currentUserId = user.get("id").toString();
        Integer type = Integer.valueOf(user.get("type").toString());

        MPJLambdaWrapper<ChooseActivity> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        if (type == ChooseUserType.STUDENT.getKey() || type == ChooseUserType.TEACHER.getKey()) {
            // 学生和教师只能查看自己参加的活动
            mpjLambdaWrapper.innerJoin(ChooseActivityUser.class, ChooseActivityUser::getActivityId, ChooseActivity::getId);
            mpjLambdaWrapper.eq(ChooseActivityUser::getUserId, currentUserId);
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            mpjLambdaWrapper.like(MybatisPlusUtil.toColumns(ChooseActivity::getName), commonPageInfo.getKeyword());
        }
        mpjLambdaWrapper.orderByDesc(MybatisPlusUtil.toColumns(ChooseActivity::getCreateTime));
        List<ChooseActivity> chooseActivityList = skyeyeBaseMapper.selectJoinList(ChooseActivity.class, mpjLambdaWrapper);
        outputObject.setBeans(chooseActivityList);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 校验活动是否在运行中
     *
     * @param activity
     * @return
     */
    @Override
    public boolean checkActivityIsRun(ChooseActivity activity) {
        String currentTime = DateUtil.getTimeAndToString();
        return DateUtil.compare(activity.getStartTime(), currentTime) && DateUtil.compare(currentTime, activity.getEndTime());
    }

    /**
     * 校验活动是否开始过
     *
     * @param activity
     * @return
     */
    @Override
    public boolean checkActivityIsStart(ChooseActivity activity) {
        String currentTime = DateUtil.getTimeAndToString();
        return DateUtil.compare(activity.getStartTime(), currentTime);
    }

    @Override
    public boolean isTopicSelectionEnabled(ChooseActivity activity) {
        return activity == null || activity.getTopicEnable() == null
            || WhetherEnum.ENABLE_USING.getKey().equals(activity.getTopicEnable());
    }

    @Override
    public boolean isTeacherSelectionEnabled(ChooseActivity activity) {
        return activity == null || activity.getTeacherEnable() == null
            || WhetherEnum.ENABLE_USING.getKey().equals(activity.getTeacherEnable());
    }

    @Override
    public void checkTopicSelectionEnabled(ChooseActivity activity) {
        if (!isTopicSelectionEnabled(activity)) {
            throw new CustomException("当前活动已关闭选题功能");
        }
    }

    @Override
    public void checkTeacherSelectionEnabled(ChooseActivity activity) {
        if (!isTeacherSelectionEnabled(activity)) {
            throw new CustomException("当前活动已关闭导师选择功能");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void updateActivityFeatureEnable(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> currentUserInfo = InputObject.getLogParamsStatic();
        if (!Integer.valueOf(currentUserInfo.get("type").toString()).equals(ChooseUserType.ADMIN.getKey())) {
            throw new CustomException("仅管理员可以修改活动功能开关");
        }
        ChooseActivity activity = selectById(id);
        if (ObjectUtil.isEmpty(activity)) {
            throw new CustomException("活动不存在");
        }
        if (StrUtil.isNotEmpty(params.get("topicEnable").toString())) {
            activity.setTopicEnable(Integer.valueOf(params.get("topicEnable").toString()));
        }
        if (StrUtil.isNotEmpty(params.get("teacherEnable").toString())) {
            activity.setTeacherEnable(Integer.valueOf(params.get("teacherEnable").toString()));
        }
        String currentUserId = currentUserInfo.get("id").toString();
        updateEntity(activity, currentUserId);
        outputObject.setBean(selectById(id));
    }

}
