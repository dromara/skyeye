/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.CompanyTalkGroupInviteDao;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroup;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupInvite;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupUser;
import com.skyeye.eve.enumclass.CompanyTalkGroupInviteInGroupType;
import com.skyeye.eve.enumclass.CompanyTalkGroupInviteState;
import com.skyeye.eve.service.CompanyTalkGroupInviteService;
import com.skyeye.eve.service.CompanyTalkGroupService;
import com.skyeye.eve.service.CompanyTalkGroupUserService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: CompanyTalkGroupInviteServiceImpl
 * @Description: 群组邀请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 16:59
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "群组邀请管理", groupName = "聊天模块")
public class CompanyTalkGroupInviteServiceImpl extends SkyeyeBusinessServiceImpl<CompanyTalkGroupInviteDao, CompanyTalkGroupInvite> implements CompanyTalkGroupInviteService {

    @Autowired
    private CompanyTalkGroupService companyTalkGroupService;

    @Autowired
    private CompanyTalkGroupUserService companyTalkGroupUserService;

    @Override
    public void saveList(String groupId, List<String> userIds, String createId) {
        deleteByGroupId(groupId);
        if (CollectionUtil.isNotEmpty(userIds)) {
            List<CompanyTalkGroupInvite> inviteBeans = new ArrayList<>();
            for (String str : userIds) {
                if (!ToolUtil.isBlank(str)) {
                    CompanyTalkGroupInvite inviteBean = new CompanyTalkGroupInvite();
                    inviteBean.setInviteUserId(str);
                    inviteBean.setGroupId(groupId);
                    inviteBean.setState(CompanyTalkGroupInviteState.WAITING_CHECK.getKey());
                    inviteBean.setInGroupType(CompanyTalkGroupInviteInGroupType.INVITE.getKey());
                    inviteBeans.add(inviteBean);
                }
            }
            if (CollectionUtil.isNotEmpty(inviteBeans)) {
                createEntity(inviteBeans, createId);
            }
        }
    }

    @Override
    public void addInviteList(String groupId, List<String> userIds, String createId) {
        if (CollectionUtil.isEmpty(userIds)) {
            return;
        }
        List<CompanyTalkGroupInvite> inviteBeans = new ArrayList<>();
        for (String str : userIds) {
            if (ToolUtil.isBlank(str)) {
                continue;
            }
            if (checkGroupInvitationMationByUserId(str, groupId)) {
                continue;
            }
            CompanyTalkGroupInvite inviteBean = new CompanyTalkGroupInvite();
            inviteBean.setInviteUserId(str);
            inviteBean.setGroupId(groupId);
            inviteBean.setState(CompanyTalkGroupInviteState.WAITING_CHECK.getKey());
            inviteBean.setInGroupType(CompanyTalkGroupInviteInGroupType.INVITE.getKey());
            inviteBeans.add(inviteBean);
        }
        if (CollectionUtil.isNotEmpty(inviteBeans)) {
            createEntity(inviteBeans, createId);
        }
    }

    @Override
    public void deleteByGroupId(String groupId) {
        QueryWrapper<CompanyTalkGroupInvite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getGroupId), groupId);
        remove(queryWrapper);
    }

    @Override
    public List<CompanyTalkGroupInvite> selectByGroupId(String groupId) {
        QueryWrapper<CompanyTalkGroupInvite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getGroupId), groupId);
        List<CompanyTalkGroupInvite> list = list(queryWrapper);
        return list;
    }

    @Override
    public void queryGroupInvitationMation(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        // 当前用户「待处理且未读」邀请总数（不走分页，在 PageHelper 之前统计）
        QueryWrapper<CompanyTalkGroupInvite> unreadQw = new QueryWrapper<>();
        unreadQw.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getInviteUserId), userId);
        unreadQw.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getState), CompanyTalkGroupInviteState.WAITING_CHECK.getKey());
        unreadQw.and(w -> w.isNull(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getWhetherRead))
            .or()
            .eq(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getWhetherRead), WhetherEnum.DISABLE_USING.getKey()));
        long unreadInviteCount = count(unreadQw);

        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        QueryWrapper<CompanyTalkGroupInvite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getInviteUserId), userId);

        List<CompanyTalkGroupInvite> list = list(queryWrapper);
        companyTalkGroupService.setDataMation(list, CompanyTalkGroupInvite::getGroupId);
        iAuthUserService.setDataMation(list, CompanyTalkGroupInvite::getCreateId);
        list.forEach(item -> {
            item.setGroupName(item.getGroupMation().getGroupName());
            item.setUserName(item.getCreateMation().get("userName").toString());
            item.setUserPhoto(item.getCreateMation().get("userPhoto").toString());
        });
        outputObject.setBeans(list);
        outputObject.settotal(pages.getTotal());
        outputObject.getObject().put("unreadInviteCount", unreadInviteCount);
    }

    @Override
    public void editAgreeInGroupInvitationMation(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        CompanyTalkGroupInvite companyTalkGroupInvite = selectById(id);
        if (companyTalkGroupInvite == null) {
            throw new CustomException("邀请不存在！");
        }
        if (!companyTalkGroupInvite.getInviteUserId().equals(userId)
            || companyTalkGroupInvite.getState() != CompanyTalkGroupInviteState.WAITING_CHECK.getKey()) {
            throw new CustomException("无权操作！");
        }
        CompanyTalkGroup companyTalkGroup = companyTalkGroupService.selectById(companyTalkGroupInvite.getGroupId());
        long userCount = companyTalkGroupUserService.countByGroupId(companyTalkGroup.getId());
        if (companyTalkGroup.getGroupUserNum() <= userCount) {
            throw new CustomException("群组人数已达上限！");
        }

        // 更新邀请状态与已读
        UpdateWrapper<CompanyTalkGroupInvite> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getState), CompanyTalkGroupInviteState.AGREED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getWhetherRead), WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);

        CompanyTalkGroupUser companyTalkGroupUser = new CompanyTalkGroupUser();
        companyTalkGroupUser.setGroupId(companyTalkGroup.getId());
        if (companyTalkGroupInvite.getInGroupType() == CompanyTalkGroupInviteInGroupType.INVITE.getKey()) {
            companyTalkGroupUser.setUserId(userId);
        } else if (companyTalkGroupInvite.getInGroupType() == CompanyTalkGroupInviteInGroupType.SEARCH_ACCOUNT.getKey()) {
            companyTalkGroupUser.setUserId(companyTalkGroupInvite.getCreateId());
        }
        companyTalkGroupUser.setCreateTime(DateUtil.getTimeAndToString());
        companyTalkGroupUserService.createEntity(companyTalkGroupUser, StrUtil.EMPTY);
        companyTalkGroupUserService.evictGroupMemberCache(companyTalkGroupInvite.getGroupId());

        Map<String, Object> result = JSONUtil.toBean(JSONUtil.toJsonStr(companyTalkGroup), null);
        result.put("inGroupType", companyTalkGroupInvite.getInGroupType());
        result.put("userId", companyTalkGroupUser.getUserId());
        outputObject.setBean(result);
    }

    @Override
    public void editRefuseInGroupInvitationMation(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        CompanyTalkGroupInvite companyTalkGroupInvite = selectById(id);
        if (companyTalkGroupInvite == null) {
            outputObject.setreturnMessage("邀请不存在！");
            return;
        }
        if (companyTalkGroupInvite.getState() != CompanyTalkGroupInviteState.WAITING_CHECK.getKey()) {
            outputObject.setreturnMessage("状态不正确！");
            return;
        }
        // 更新邀请状态与已读
        UpdateWrapper<CompanyTalkGroupInvite> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getState), CompanyTalkGroupInviteState.REJECTED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getWhetherRead), WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public boolean checkGroupInvitationMationByUserId(String userId, String groupId) {
        QueryWrapper<CompanyTalkGroupInvite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getInviteUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getGroupId), groupId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupInvite::getState), CompanyTalkGroupInviteState.WAITING_CHECK.getKey());
        CompanyTalkGroupInvite companyTalkGroupInvite = getOne(queryWrapper, false);
        if (companyTalkGroupInvite != null) {
            return true;
        }
        return false;
    }
}
