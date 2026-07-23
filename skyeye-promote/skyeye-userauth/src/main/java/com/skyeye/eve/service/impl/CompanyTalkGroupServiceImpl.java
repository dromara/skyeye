/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.chat.enums.TalkChatType;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.CompanyTalkGroupDao;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroup;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupInvite;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupUser;
import com.skyeye.eve.enumclass.CompanyTalkGroupInviteInGroupType;
import com.skyeye.eve.enumclass.CompanyTalkGroupInviteState;
import com.skyeye.eve.enumclass.CompanyTalkGroupState;
import com.skyeye.eve.service.CompanyTalkGroupInviteService;
import com.skyeye.eve.service.CompanyTalkGroupService;
import com.skyeye.eve.service.CompanyTalkGroupUserService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CompanyTalkGroupServiceImpl
 * @Description: 群组信息管理服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:51
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "群组管理", groupName = "聊天模块")
public class CompanyTalkGroupServiceImpl extends SkyeyeBusinessServiceImpl<CompanyTalkGroupDao, CompanyTalkGroup> implements CompanyTalkGroupService {

    @Autowired
    private CompanyTalkGroupDao companyTalkGroupDao;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private CompanyTalkGroupInviteService companyTalkGroupInviteService;

    @Autowired
    private CompanyTalkGroupUserService companyTalkGroupUserService;

    @Override
    public void validatorEntity(CompanyTalkGroup entity) {
        super.validatorEntity(entity);
        String[] invites = entity.getUserIds().split(",");
        if (invites.length < 1) {
            throw new IllegalArgumentException("群组中最少拥有两名成员。");
        }
    }

    @Override
    public void createPrepose(CompanyTalkGroup entity) {
        super.createPrepose(entity);
        entity.setGroupUserNum(200);
        entity.setGroupNum(ToolUtil.getTalkGroupNum());
        entity.setGroupHistroyImg(entity.getGroupImg() + ",");
        entity.setState(CompanyTalkGroupState.NORMAL.getKey());
    }

    @Override
    public void createPostpose(CompanyTalkGroup entity, String userId) {
        // 保存群组信息
        String[] invites = entity.getUserIds().split(",");
        companyTalkGroupInviteService.saveList(entity.getId(), Arrays.asList(invites), userId);

        // 将当前用户添加到群组中
        CompanyTalkGroupUser groupUser = new CompanyTalkGroupUser();
        groupUser.setUserId(userId);
        groupUser.setGroupId(entity.getId());
        groupUser.setCreateTime(DateUtil.getTimeAndToString());
        companyTalkGroupUserService.createEntity(groupUser, userId);
        companyTalkGroupUserService.evictGroupMemberCache(entity.getId());
    }

    @Override
    public QueryWrapper<CompanyTalkGroup> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<CompanyTalkGroup> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroup::getState), CompanyTalkGroupState.NORMAL.getKey());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        String userId = inputObject.getLogParams().get("id").toString();
        List<String> groupIds = beans.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        Map<String, String> groupUserIsExit = companyTalkGroupUserService.batchCheckGroupUserIsExit(groupIds, userId);
        beans.forEach(bean -> {
            String groupId = bean.get("id").toString();
            bean.put("inId", groupUserIsExit.get(groupId));
        });
        return beans;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertGroupMationToTalk(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String groupId = map.get("groupId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        // 判断用户是否在该群聊
        boolean userIsExit = companyTalkGroupUserService.checkGroupUserIsExit(groupId, userId);
        if (userIsExit) {
            throw new CustomException("您已在该群聊。");
        }
        // 判断是否有该用户的未审批的群聊申请信息
        boolean invitation = companyTalkGroupInviteService.checkGroupInvitationMationByUserId(userId, groupId);
        if (invitation) {
            return;
        }

        CompanyTalkGroup companyTalkGroup = selectById(groupId);
        // 判断群组人数是否已达上限
        long userCount = companyTalkGroupUserService.countByGroupId(companyTalkGroup.getId());
        if (companyTalkGroup.getGroupUserNum() <= userCount) {
            throw new CustomException("群组人数已达上限！");
        }
        CompanyTalkGroupInvite companyTalkGroupInvite = new CompanyTalkGroupInvite();
        companyTalkGroupInvite.setGroupId(groupId);
        companyTalkGroupInvite.setInviteUserId(companyTalkGroup.getCreateId());
        companyTalkGroupInvite.setState(CompanyTalkGroupInviteState.WAITING_CHECK.getKey());
        companyTalkGroupInvite.setInGroupType(CompanyTalkGroupInviteInGroupType.SEARCH_ACCOUNT.getKey());
        companyTalkGroupInvite.setWhetherRead(WhetherEnum.DISABLE_USING.getKey());
        companyTalkGroupInviteService.createEntity(companyTalkGroupInvite, userId);
        outputObject.setBean(companyTalkGroupInvite);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryGroupMemberByGroupId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String groupId = map.get("id").toString();
        String cacheKey = Constants.checkSysEveTalkGroupUserListByGroupId(groupId);
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = redisCache.getList(cacheKey, key -> {
            return companyTalkGroupDao.queryGroupMemberByGroupId(groupId, tenantId);
        }, RedisConstants.ALL_USE_TIME);
        map.clear();
        map.put("members", beans.size());
        map.put("list", beans);
        outputObject.setBean(map);
    }

    @Override
    @IgnoreTenant
    public void queryChatLogByType(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String chatType = map.get("chatType").toString();
        if (StrUtil.equals(TalkChatType.PERSONAL_TO_PERSONAL.getChType(), chatType)) {//个人对个人
            Map<String, Object> user = inputObject.getLogParams();
            map.put("userId", user.get("id"));
            Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
            List<Map<String, Object>> beans = companyTalkGroupDao.queryChatLogByPerToPer(map);
            outputObject.setBeans(beans);
            outputObject.settotal(pages.getTotal());
        } else if (StrUtil.equals(TalkChatType.GROUP_CHAT.getChType(), chatType)) {//个人对群组
            Map<String, Object> user = inputObject.getLogParams();
            map.put("userId", user.get("id"));
            Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
            List<Map<String, Object>> beans = companyTalkGroupDao.queryChatLogByPerToGroup(map);
            outputObject.setBeans(beans);
            outputObject.settotal(pages.getTotal());
        } else {
            outputObject.setreturnMessage("参数错误");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editUserToExitGroup(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String groupId = map.get("groupId").toString();
        CompanyTalkGroup companyTalkGroup = selectById(groupId);
        if (companyTalkGroup == null || StrUtil.isEmpty(companyTalkGroup.getId())) {
            throw new CustomException("群信息不存在，请核实后进行操作。");
        }
        String userId = inputObject.getLogParams().get("id").toString();
        if (StrUtil.equals(companyTalkGroup.getCreateId(), userId)) {
            outputObject.setreturnMessage("您是该群聊的创建人，无法退群，请进行解散群聊操作。");
        }

        companyTalkGroupUserService.deleteByGroupIdAndUserId(groupId, userId);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editCreateToExitGroup(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String groupId = map.get("groupId").toString();
        CompanyTalkGroup companyTalkGroup = selectById(groupId);
        if (companyTalkGroup == null || StrUtil.isEmpty(companyTalkGroup.getId())) {
            throw new CustomException("群信息不存在，请核实后进行操作。");
        }
        String userId = inputObject.getLogParams().get("id").toString();
        if (!StrUtil.equals(companyTalkGroup.getCreateId(), userId)) {
            outputObject.setreturnMessage("您不是该群聊的创建人，无法退群，请进行退出群聊操作。");
        }
        // 删除群组成员缓存
        companyTalkGroupUserService.evictGroupMemberCache(groupId);
        dissolvedGroup(groupId);
    }

    private void dissolvedGroup(String groupId) {
        // 解散群聊
        UpdateWrapper<CompanyTalkGroup> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, groupId);
        updateWrapper.set(MybatisPlusUtil.toColumns(CompanyTalkGroup::getState), CompanyTalkGroupState.DISSOLVED.getKey());
        update(updateWrapper);
        refreshCache(groupId);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editGroupMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String groupName = params.get("groupName").toString();
        String groupImg = params.get("groupImg").toString();
        String userId = inputObject.getLogParams().get("id").toString();

        CompanyTalkGroup companyTalkGroup = selectById(id);
        if (companyTalkGroup == null || StrUtil.isEmpty(companyTalkGroup.getId())) {
            throw new CustomException("群信息不存在，请核实后进行操作。");
        }
        if (!StrUtil.equals(companyTalkGroup.getCreateId(), userId)) {
            throw new CustomException("只有群主可以修改群信息。");
        }

        UpdateWrapper<CompanyTalkGroup> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(CompanyTalkGroup::getGroupName), groupName);
        if (StrUtil.isNotEmpty(groupImg)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(CompanyTalkGroup::getGroupImg), groupImg);
            String historyImg = companyTalkGroup.getGroupHistroyImg();
            if (StrUtil.isNotEmpty(historyImg) && !historyImg.contains(groupImg)) {
                updateWrapper.set(MybatisPlusUtil.toColumns(CompanyTalkGroup::getGroupHistroyImg), historyImg + groupImg + ",");
            }
        }
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertGroupMemberInvite(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String groupId = params.get("groupId").toString();
        String userIds = params.get("userIds").toString();
        String userId = inputObject.getLogParams().get("id").toString();

        CompanyTalkGroup companyTalkGroup = selectById(groupId);
        if (companyTalkGroup == null || StrUtil.isEmpty(companyTalkGroup.getId())) {
            throw new CustomException("群信息不存在，请核实后进行操作。");
        }
        boolean isMember = companyTalkGroupUserService.checkGroupUserIsExit(groupId, userId);
        if (!isMember) {
            throw new CustomException("您不在该群聊中，无法邀请成员。");
        }

        List<String> inviteUserIds = Arrays.stream(userIds.split(","))
            .map(String::trim)
            .filter(str -> !ToolUtil.isBlank(str))
            .filter(str -> !companyTalkGroupUserService.checkGroupUserIsExit(groupId, str))
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(inviteUserIds)) {
            outputObject.setreturnMessage("没有可邀请的成员。");
            return;
        }
        companyTalkGroupInviteService.addInviteList(groupId, inviteUserIds, userId);
    }

    /**
     * 员工离职时批量处理群聊：普通成员退群，群主转让或解散。
     *
     * @param userId         离职用户id
     * @param transferUserId 离职单指定的交接人用户id，可为空
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void handleUserQuitGroup(String userId, String transferUserId) {
        List<CompanyTalkGroupUser> userGroups = companyTalkGroupUserService.selectByUserId(userId);
        if (CollectionUtil.isEmpty(userGroups)) {
            return;
        }
        // 批量查询用户所在群信息，过滤已解散/关闭的群
        List<String> allGroupIds = userGroups.stream()
            .map(CompanyTalkGroupUser::getGroupId)
            .distinct()
            .collect(Collectors.toList());
        List<CompanyTalkGroup> groups = selectByIds(allGroupIds.toArray(new String[0]));
        Map<String, CompanyTalkGroup> groupMap = groups.stream()
            .filter(group -> group != null
                && StrUtil.isNotEmpty(group.getId())
                && CompanyTalkGroupState.NORMAL.getKey().equals(group.getState()))
            .collect(Collectors.toMap(CompanyTalkGroup::getId, group -> group, (a, b) -> a));
        if (groupMap.isEmpty()) {
            return;
        }

        // 按群主/普通成员分组
        List<String> creatorGroupIds = new ArrayList<>();
        List<String> memberGroupIds = new ArrayList<>();
        groupMap.forEach((groupId, group) -> {
            if (StrUtil.equals(group.getCreateId(), userId)) {
                creatorGroupIds.add(groupId);
            } else {
                memberGroupIds.add(groupId);
            }
        });

        // 计算群主群的处理结果：解散 or 转让给新群主
        List<String> dissolveGroupIds = new ArrayList<>();
        Map<String, String> transferCreatorMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(creatorGroupIds)) {
            List<CompanyTalkGroupUser> creatorGroupMembers = companyTalkGroupUserService.selectByGroupIds(creatorGroupIds);
            Map<String, List<CompanyTalkGroupUser>> membersByGroup = creatorGroupMembers.stream()
                .collect(Collectors.groupingBy(CompanyTalkGroupUser::getGroupId));
            for (String groupId : creatorGroupIds) {
                List<CompanyTalkGroupUser> otherMembers = membersByGroup.getOrDefault(groupId, new ArrayList<>()).stream()
                    .filter(member -> !StrUtil.equals(member.getUserId(), userId))
                    .collect(Collectors.toList());
                if (CollectionUtil.isEmpty(otherMembers)) {
                    dissolveGroupIds.add(groupId);
                } else {
                    transferCreatorMap.put(groupId, resolveNewCreator(otherMembers, transferUserId, userId));
                }
            }
        }

        // 批量执行数据库操作
        batchDissolveGroups(dissolveGroupIds);
        batchTransferGroupCreator(transferCreatorMap);

        // 批量删除成员记录（解散群不删成员记录，与原逻辑一致）
        List<String> quitMemberGroupIds = new ArrayList<>(memberGroupIds);
        quitMemberGroupIds.addAll(transferCreatorMap.keySet());
        companyTalkGroupUserService.deleteByUserIdAndGroupIds(userId, quitMemberGroupIds);

        // 清理缓存
        groupMap.keySet().forEach(companyTalkGroupUserService::evictGroupMemberCache);
        dissolveGroupIds.forEach(this::refreshCache);
        transferCreatorMap.keySet().forEach(this::refreshCache);
    }

    /**
     * 批量解散群聊。
     *
     * @param groupIds 待解散的群id列表
     */
    private void batchDissolveGroups(List<String> groupIds) {
        if (CollectionUtil.isEmpty(groupIds)) {
            return;
        }
        UpdateWrapper<CompanyTalkGroup> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(CommonConstants.ID, groupIds);
        updateWrapper.set(MybatisPlusUtil.toColumns(CompanyTalkGroup::getState), CompanyTalkGroupState.DISSOLVED.getKey());
        update(updateWrapper);
    }

    /**
     * 批量转让群主：按新群主分组，相同新群主的群合并为一条 UPDATE。
     *
     * @param transferCreatorMap 群id -> 新群主用户id
     */
    private void batchTransferGroupCreator(Map<String, String> transferCreatorMap) {
        if (CollectionUtil.isEmpty(transferCreatorMap)) {
            return;
        }
        Map<String, List<String>> groupsByNewCreator = transferCreatorMap.entrySet().stream()
            .collect(Collectors.groupingBy(
                Map.Entry::getValue,
                Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
        for (Map.Entry<String, List<String>> entry : groupsByNewCreator.entrySet()) {
            UpdateWrapper<CompanyTalkGroup> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in(CommonConstants.ID, entry.getValue());
            updateWrapper.set(MybatisPlusUtil.toColumns(CompanyTalkGroup::getCreateId), entry.getKey());
            update(updateWrapper);
        }
    }

    /**
     * 确定群主离职后的新群主：优先转给交接人（需在群内），否则取第一个其他成员。
     *
     * @param otherMembers   群内除离职人外的成员
     * @param transferUserId 离职单指定的交接人用户id
     * @param quitUserId     离职用户id
     * @return 新群主用户id
     */
    private String resolveNewCreator(List<CompanyTalkGroupUser> otherMembers, String transferUserId, String quitUserId) {
        if (StrUtil.isNotEmpty(transferUserId) && !StrUtil.equals(transferUserId, quitUserId)) {
            boolean transferUserInGroup = otherMembers.stream()
                .anyMatch(member -> StrUtil.equals(member.getUserId(), transferUserId));
            if (transferUserInGroup) {
                return transferUserId;
            }
        }
        return otherMembers.get(0).getUserId();
    }

    /**
     * 组织解散时，将该租户下正常状态的群聊全部解散。
     * <p>开启多租户时，调用方需先设置 TenantContext；未开启多租户时按单租户数据直接处理。</p>
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void handleTenantDissolve() {
        QueryWrapper<CompanyTalkGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroup::getState), CompanyTalkGroupState.NORMAL.getKey());
        List<CompanyTalkGroup> groups = list(queryWrapper);
        if (CollectionUtil.isEmpty(groups)) {
            return;
        }
        List<String> groupIds = groups.stream().map(CompanyTalkGroup::getId).collect(Collectors.toList());
        // 批量标记为已解散，并刷新群聊缓存
        batchDissolveGroups(groupIds);
        groupIds.forEach(this::refreshCache);
    }

}
