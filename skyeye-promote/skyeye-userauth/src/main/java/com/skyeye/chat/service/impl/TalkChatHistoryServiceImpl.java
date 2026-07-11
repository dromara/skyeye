/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.chat.dao.TalkChatHistoryDao;
import com.skyeye.chat.entity.TalkChatHistory;
import com.skyeye.chat.enums.TalkChatType;
import com.skyeye.chat.service.TalkChatHistoryService;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroup;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupUser;
import com.skyeye.eve.enumclass.CompanyTalkGroupState;
import com.skyeye.eve.service.CompanyTalkGroupService;
import com.skyeye.eve.service.CompanyTalkGroupUserService;
import com.skyeye.personnel.service.SysEveUserStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TalkChatHistoryServiceImpl
 * @Description: 聊天记录服务层实现类--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/12 14:25
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "聊天历史记录", groupName = "聊天历史记录", tenant = TenantEnum.NO_ISOLATION)
public class TalkChatHistoryServiceImpl extends SkyeyeBusinessServiceImpl<TalkChatHistoryDao, TalkChatHistory> implements TalkChatHistoryService {

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Autowired
    private CompanyTalkGroupService companyTalkGroupService;

    @Autowired
    private CompanyTalkGroupUserService companyTalkGroupUserService;

    private long countPersonalUnread(String userId, String talkUserId) {
        QueryWrapper<TalkChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReceiveId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getSendId), talkUserId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getChatType), TalkChatType.PERSONAL_TO_PERSONAL.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReadType), WhetherEnum.DISABLE_USING.getKey());
        return count(queryWrapper);
    }

    private long countGroupUnread(String userId, String groupId, String joinTime) {
        QueryWrapper<TalkChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReceiveId), groupId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getChatType), TalkChatType.GROUP_CHAT.getKey());
        queryWrapper.ne(MybatisPlusUtil.toColumns(TalkChatHistory::getSendId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReadType), WhetherEnum.DISABLE_USING.getKey());
        if (StrUtil.isNotBlank(joinTime)) {
            queryWrapper.gt(MybatisPlusUtil.toColumns(TalkChatHistory::getCreateTime), joinTime);
        }
        return count(queryWrapper);
    }

    private Map<String, String> queryUserGroupJoinTimeMap(String userId) {
        QueryWrapper<CompanyTalkGroupUser> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getUserId), userId);
        List<CompanyTalkGroupUser> memberList = companyTalkGroupUserService.list(memberQueryWrapper);
        if (CollectionUtil.isEmpty(memberList)) {
            return new HashMap<>();
        }
        return memberList.stream().collect(Collectors.toMap(
            CompanyTalkGroupUser::getGroupId,
            CompanyTalkGroupUser::getCreateTime,
            (left, right) -> left
        ));
    }

    private List<TalkChatHistory> queryGroupUnreadMessageList(String userId) {
        Map<String, String> groupJoinTimeMap = queryUserGroupJoinTimeMap(userId);
        if (CollectionUtil.isEmpty(groupJoinTimeMap)) {
            return new ArrayList<>();
        }
        List<TalkChatHistory> unreadGroupMessages = new ArrayList<>();
        groupJoinTimeMap.forEach((groupId, joinTime) -> {
            QueryWrapper<TalkChatHistory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReceiveId), groupId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getChatType), TalkChatType.GROUP_CHAT.getKey());
            queryWrapper.ne(MybatisPlusUtil.toColumns(TalkChatHistory::getSendId), userId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReadType), WhetherEnum.DISABLE_USING.getKey());
            if (StrUtil.isNotBlank(joinTime)) {
                queryWrapper.gt(MybatisPlusUtil.toColumns(TalkChatHistory::getCreateTime), joinTime);
            }
            queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TalkChatHistory::getCreateTime));
            unreadGroupMessages.addAll(list(queryWrapper));
        });
        return unreadGroupMessages;
    }

    private void fillSendStaffMation(List<TalkChatHistory> talkChatHistoryList) {
        if (CollectionUtil.isEmpty(talkChatHistoryList)) {
            return;
        }
        List<String> userIds = talkChatHistoryList.stream().map(TalkChatHistory::getSendId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(userIds)) {
            return;
        }
        List<Map<String, Object>> staffList = sysEveUserStaffService.queryUserMationList(
            Joiner.on(CommonCharConstants.COMMA_MARK).join(userIds), null);
        Map<String, Map<String, Object>> userIdToStaff = staffList.stream()
            .collect(Collectors.toMap(m -> m.get("userId").toString(), m -> m, (left, right) -> left));
        talkChatHistoryList.forEach(talkChatHistory -> {
            Map<String, Object> staff = userIdToStaff.get(talkChatHistory.getSendId());
            talkChatHistory.setSendStaffMation(staff);
        });
    }

    @Override
    public String createEntity(JSONObject jsonObject, Integer chatType) {
        return createEntity(jsonObject, chatType, WhetherEnum.DISABLE_USING.getKey());
    }

    @Override
    public String createEntity(JSONObject jsonObject, Integer chatType, Integer readType) {
        TalkChatHistory talkChatHistory = new TalkChatHistory();
        talkChatHistory.setContent(jsonObject.getStr("message"));
        talkChatHistory.setSendId(jsonObject.getStr("userId"));
        String toId = jsonObject.getStr("to");
        if (TalkChatType.GROUP_CHAT.getKey().equals(chatType)) {
            talkChatHistory.setUniqueId(toId);
        } else {
            talkChatHistory.setUniqueId(getSortString(jsonObject.getStr("userId"), toId));
        }
        talkChatHistory.setReceiveId(toId);
        talkChatHistory.setCreateTime(DateUtil.getTimeAndToString());
        talkChatHistory.setReadType(readType);
        talkChatHistory.setChatType(chatType);
        return createEntity(talkChatHistory, StrUtil.EMPTY);
    }

    /**
     * 对两个字符串进行排序，然后组装成一个字符创返回
     */
    private String getSortString(String str1, String str2) {
        List<String> list = new ArrayList<>();
        list.add(str1);
        list.add(str2);
        list.sort(String::compareTo);
        return Joiner.on(CommonCharConstants.HORIZONTAL_LINE_MARK).join(list);
    }

    @Override
    public void queryMyUnReadMessageList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<TalkChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReceiveId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getChatType), TalkChatType.PERSONAL_TO_PERSONAL.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReadType), WhetherEnum.DISABLE_USING.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TalkChatHistory::getCreateTime));
        List<TalkChatHistory> talkChatHistoryList = new ArrayList<>(list(queryWrapper));
        talkChatHistoryList.addAll(queryGroupUnreadMessageList(userId));
        talkChatHistoryList.sort((left, right) -> right.getCreateTime().compareTo(left.getCreateTime()));
        fillSendStaffMation(talkChatHistoryList);

        outputObject.setBeans(talkChatHistoryList);
        outputObject.settotal(talkChatHistoryList.size());
    }

    @Override
    public void editTalkChatHistoryToRead(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        Map<String, Object> params = inputObject.getParams();
        String sendId = params.get("sendId").toString();
        String chatType = params.containsKey("chatType") ? params.get("chatType").toString() : TalkChatType.PERSONAL_TO_PERSONAL.getChType();
        UpdateWrapper<TalkChatHistory> updateWrapper = new UpdateWrapper<>();
        if (TalkChatType.GROUP_CHAT.getChType().equals(chatType)) {
            String joinTime = queryUserGroupJoinTimeMap(userId).get(sendId);
            updateWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReceiveId), sendId);
            updateWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getChatType), TalkChatType.GROUP_CHAT.getKey());
            updateWrapper.ne(MybatisPlusUtil.toColumns(TalkChatHistory::getSendId), userId);
            if (StrUtil.isNotBlank(joinTime)) {
                updateWrapper.gt(MybatisPlusUtil.toColumns(TalkChatHistory::getCreateTime), joinTime);
            }
        } else {
            updateWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReceiveId), userId);
            updateWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getSendId), sendId);
            updateWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getChatType), TalkChatType.PERSONAL_TO_PERSONAL.getKey());
        }
        updateWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReadType), WhetherEnum.DISABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TalkChatHistory::getReadType), WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public void queryMyTalkMessageList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        Map<String, String> groupJoinTimeMap = queryUserGroupJoinTimeMap(userId);
        List<String> userGroupIds = new ArrayList<>(groupJoinTimeMap.keySet());

        // 单聊：按会话对象取最新一条
        QueryWrapper<TalkChatHistory> personalWrapper = new QueryWrapper<>();
        personalWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getChatType), TalkChatType.PERSONAL_TO_PERSONAL.getKey());
        personalWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReceiveId), userId)
                .or().eq(MybatisPlusUtil.toColumns(TalkChatHistory::getSendId), userId));
        personalWrapper.orderByDesc(MybatisPlusUtil.toColumns(TalkChatHistory::getCreateTime));
        List<TalkChatHistory> personalList = list(personalWrapper);
        Map<String, TalkChatHistory> personalLatestMap = new HashMap<>();
        for (TalkChatHistory talkChatHistory : personalList) {
            String talkUserId = StrUtil.equals(userId, talkChatHistory.getSendId())
                ? talkChatHistory.getReceiveId() : talkChatHistory.getSendId();
            personalLatestMap.putIfAbsent(talkUserId, talkChatHistory);
        }

        // 群聊：按群 id 取最新一条（含他人发送的消息）
        Map<String, TalkChatHistory> groupLatestMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(userGroupIds)) {
            QueryWrapper<TalkChatHistory> groupWrapper = new QueryWrapper<>();
            groupWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getChatType), TalkChatType.GROUP_CHAT.getKey());
            groupWrapper.in(MybatisPlusUtil.toColumns(TalkChatHistory::getReceiveId), userGroupIds);
            groupWrapper.orderByDesc(MybatisPlusUtil.toColumns(TalkChatHistory::getCreateTime));
            List<TalkChatHistory> groupList = list(groupWrapper);
            for (TalkChatHistory talkChatHistory : groupList) {
                groupLatestMap.putIfAbsent(talkChatHistory.getReceiveId(), talkChatHistory);
            }
        }

        List<TalkChatHistory> talkChatHistoryList = new ArrayList<>();
        talkChatHistoryList.addAll(personalLatestMap.values());
        talkChatHistoryList.addAll(groupLatestMap.values());
        talkChatHistoryList.sort((left, right) -> right.getCreateTime().compareTo(left.getCreateTime()));
        if (talkChatHistoryList.size() > 50) {
            talkChatHistoryList = new ArrayList<>(talkChatHistoryList.subList(0, 50));
        }
        if (CollectionUtil.isEmpty(talkChatHistoryList)) {
            return;
        }

        // 根据用户id查询员工数据
        List<String> userIds = talkChatHistoryList.stream()
            .filter(talkChatHistory -> talkChatHistory.getChatType() == TalkChatType.PERSONAL_TO_PERSONAL.getKey())
            .map(TalkChatHistory::getSendId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(userIds)) {
            List<String> receiveIds = talkChatHistoryList.stream()
                .filter(talkChatHistory -> talkChatHistory.getChatType() == TalkChatType.PERSONAL_TO_PERSONAL.getKey())
                .map(TalkChatHistory::getReceiveId).distinct().collect(Collectors.toList());
            userIds.addAll(receiveIds);
            userIds = userIds.stream().distinct().collect(Collectors.toList());
        }
        Map<String, Map<String, Object>> userMap = iAuthUserService.queryUserNameList(userIds);

        // 根据群组id 查询群组数据，对于群聊聊天，只会有receiveId
        List<String> groupIds = talkChatHistoryList.stream()
            .filter(talkChatHistory -> talkChatHistory.getChatType() == TalkChatType.GROUP_CHAT.getKey())
            .map(TalkChatHistory::getReceiveId).distinct().collect(Collectors.toList());
        Map<String, CompanyTalkGroup> groupMap = companyTalkGroupService.selectMapByIds(groupIds);

        List<Map<String, Object>> result = new ArrayList<>();
        for (TalkChatHistory talkChatHistory : talkChatHistoryList) {
            Map<String, Object> bean = new HashMap<>();
            long unreadCount = 0;

            if (talkChatHistory.getChatType() == TalkChatType.PERSONAL_TO_PERSONAL.getKey()) {
                Map<String, Object> user;
                String talkUserId;
                if (StrUtil.equals(userId, talkChatHistory.getSendId())) {
                    // 我发送的消息
                    talkUserId = talkChatHistory.getReceiveId();
                    user = userMap.get(talkChatHistory.getReceiveId());
                } else {
                    // 我接收的消息
                    talkUserId = talkChatHistory.getSendId();
                    user = userMap.get(talkChatHistory.getSendId());
                }
                if (CollectionUtil.isEmpty(user)) {
                    continue;
                }
                unreadCount = countPersonalUnread(userId, talkUserId);
                // 发送者信息
                bean.put("name", user.get("userName").toString());
                bean.put("avatar", user.get("userPhoto").toString());
                bean.put("staffId", user.get("staffId").toString());
                // 会话 id 必须使用 userId，与 WebSocket/好友列表的 id 保持一致
                bean.put("talkId", talkUserId);
                bean.put("userId", talkUserId);
            } else if (talkChatHistory.getChatType() == TalkChatType.GROUP_CHAT.getKey()) {
                // 群信息
                CompanyTalkGroup group = groupMap.get(talkChatHistory.getReceiveId());
                if (ObjectUtil.isEmpty(group)) {
                    continue;
                }
                if (group.getState() != CompanyTalkGroupState.NORMAL.getKey()) {
                    continue;
                }
                unreadCount = countGroupUnread(userId, group.getId(), groupJoinTimeMap.get(group.getId()));
                bean.put("name", group.getGroupName());
                bean.put("avatar", group.getGroupImg());
                bean.put("groupId", group.getId());
                bean.put("talkId", group.getId());
            }
            bean.put("sendId", talkChatHistory.getSendId());
            bean.put("content", talkChatHistory.getContent());
            bean.put("createTime", talkChatHistory.getCreateTime());
            bean.put("chatType", talkChatHistory.getChatType());
            bean.put("unread", unreadCount);
            result.add(bean);
        }
        result.sort((left, right) -> String.valueOf(right.get("createTime")).compareTo(String.valueOf(left.get("createTime"))));
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

}
