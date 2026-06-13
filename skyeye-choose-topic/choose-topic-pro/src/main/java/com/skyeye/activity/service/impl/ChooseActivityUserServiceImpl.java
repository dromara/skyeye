/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.activity.dao.ChooseActivityUserDao;
import com.skyeye.activity.entity.BatchChooseActivityUserBox;
import com.skyeye.activity.entity.ChooseActivityUser;
import com.skyeye.activity.service.ChooseActivityUserService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.chtopic.service.ChooseTopicService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.user.dao.ChooseUserDao;
import com.skyeye.user.entity.ChooseUser;
import com.skyeye.user.enumclass.ChooseUserType;
import com.skyeye.user.service.ChooseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ActivityUserServiceImpl
 * @Description: 活动可参与的用户信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "活动可参与的用户信息管理", groupName = "活动可参与的用户信息管理")
public class ChooseActivityUserServiceImpl extends SkyeyeBusinessServiceImpl<ChooseActivityUserDao, ChooseActivityUser> implements ChooseActivityUserService {

    @Autowired
    private ChooseUserDao chooseUserDao;

    @Autowired
    private ChooseUserService chooseUserService;

    @Autowired
    private ChooseTopicService chooseTopicService;

    @Override
    public void insertActivityUser(InputObject inputObject, OutputObject outputObject) {
        BatchChooseActivityUserBox inputObjectParams = inputObject.getParams(BatchChooseActivityUserBox.class);
        List<String> userNoList = inputObjectParams.getUserNoList().stream().distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(userNoList)) {
            throw new CustomException("用户编号不能为空");
        }
        List<ChooseUser> chooseUserList = chooseUserService.queryChoostUserList(userNoList);
        if (CollectionUtil.isEmpty(chooseUserList)) {
            return;
        }
        List<String> userIdList = chooseUserList.stream().map(ChooseUser::getId).distinct().collect(Collectors.toList());
        // 查询入参涉及到的活动和活动下的可参与人信息是否已经存在
        QueryWrapper<ChooseActivityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseActivityUser::getActivityId), inputObjectParams.getActivityId());
        queryWrapper.in(MybatisPlusUtil.toColumns(ChooseActivityUser::getUserId), userIdList);
        List<ChooseActivityUser> list = list(queryWrapper);
        List<String> inActivityUserIdList = list.stream().map(ChooseActivityUser::getUserId).distinct().collect(Collectors.toList());
        // 过滤掉数据库已经存在的数据
        chooseUserList = chooseUserList.stream().filter(activityUser -> {
            if (inActivityUserIdList.contains(activityUser.getId())) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(chooseUserList)) {
            return;
        }
        List<ChooseActivityUser> insertActivityList = chooseUserList.stream().map(activityUser -> {
            ChooseActivityUser chooseActivityUser = new ChooseActivityUser();
            chooseActivityUser.setActivityId(inputObjectParams.getActivityId());
            chooseActivityUser.setUserId(activityUser.getId());
            return chooseActivityUser;
        }).collect(Collectors.toList());
        super.createEntity(insertActivityList, inputObject.getLogParams().get("id").toString());
        outputObject.setBeans(insertActivityList);
        outputObject.settotal(insertActivityList.size());
    }

    @Override
    public void deleteByActivityId(String activityId) {
        if (StrUtil.isEmpty(activityId)) {
            return;
        }
        QueryWrapper<ChooseActivityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseActivityUser::getActivityId), activityId);
        remove(queryWrapper);
    }

    @Override
    @IgnoreTenant
    public void queryActivityUserList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());

        MPJLambdaWrapper<ChooseUser> mpjLambdaWrapper = JoinWrappers.lambda("b", ChooseUser.class);
        mpjLambdaWrapper.innerJoin(ChooseActivityUser.class, "a", ChooseActivityUser::getUserId, ChooseUser::getId);
        mpjLambdaWrapper.eq("a." + MybatisPlusUtil.toColumns(ChooseActivityUser::getActivityId), commonPageInfo.getObjectId());
        mpjLambdaWrapper.eq("b." + MybatisPlusUtil.toColumns(ChooseUser::getType), commonPageInfo.getType());

        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            mpjLambdaWrapper.and(wra -> {
                wra.or().like("b." + MybatisPlusUtil.toColumns(ChooseUser::getStuNo), commonPageInfo.getKeyword());
                wra.or().like("b." + MybatisPlusUtil.toColumns(ChooseUser::getName), commonPageInfo.getKeyword());
            });
        }

        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            mpjLambdaWrapper.eq("b." + MybatisPlusUtil.toColumns(ChooseUser::getActivityType), commonPageInfo.getState());
        }

        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            mpjLambdaWrapper.eq("a." + CommonConstants.TENANT_ID_FIELD, tenantId);
            mpjLambdaWrapper.eq("b." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }

        List<ChooseUser> chooseUserList = chooseUserDao.selectJoinList(ChooseUser.class, mpjLambdaWrapper);
        if (Integer.parseInt(commonPageInfo.getType()) == ChooseUserType.TEACHER.getKey()
            && CollectionUtil.isNotEmpty(chooseUserList)) {
            List<String> userIds = chooseUserList.stream().map(ChooseUser::getId).collect(Collectors.toList());
            Map<String, Integer> countByActivityId = chooseTopicService.getChooseTopicCountByActivityId(commonPageInfo.getObjectId(), userIds);
            for (ChooseUser chooseUser : chooseUserList) {
                chooseUser.setTopicCount(countByActivityId.getOrDefault(chooseUser.getId(), 0));
            }
        }
        outputObject.setBeans(chooseUserList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    public void queryTeacherActivityUserList(InputObject inputObject, OutputObject outputObject) {
        String activityId = inputObject.getParams().get("activityId").toString();

        MPJLambdaWrapper<ChooseUser> mpjLambdaWrapper = JoinWrappers.lambda("b", ChooseUser.class);
        mpjLambdaWrapper.innerJoin(ChooseActivityUser.class, "a", ChooseActivityUser::getUserId, ChooseUser::getId);
        mpjLambdaWrapper.eq("a." + MybatisPlusUtil.toColumns(ChooseActivityUser::getActivityId), activityId);
        mpjLambdaWrapper.eq("b." + MybatisPlusUtil.toColumns(ChooseUser::getType), ChooseUserType.TEACHER.getKey());

        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            mpjLambdaWrapper.eq("a." + CommonConstants.TENANT_ID_FIELD, tenantId);
            mpjLambdaWrapper.eq("b." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }

        List<ChooseUser> chooseUserList = chooseUserDao.selectJoinList(ChooseUser.class, mpjLambdaWrapper);
        outputObject.setBeans(chooseUserList);
        outputObject.settotal(chooseUserList.size());
    }

    @Override
    public void deleteActivityUserByUserId(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getParams().get("userId").toString();
        String activityId = inputObject.getParams().get("activityId").toString();
        QueryWrapper<ChooseActivityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseActivityUser::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseActivityUser::getActivityId), activityId);
        ChooseActivityUser chooseActivityUser = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(chooseActivityUser)) {
            return;
        }
        deleteById(chooseActivityUser.getId());
    }

    @Override
    @IgnoreTenant
    public boolean isActivityParticipant(String activityId, String userId) {
        if (StrUtil.isEmpty(activityId) || StrUtil.isEmpty(userId)) {
            return false;
        }
        QueryWrapper<ChooseActivityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseActivityUser::getActivityId), activityId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseActivityUser::getUserId), userId);
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            queryWrapper.eq(CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        return count(queryWrapper) > 0;
    }
}
