/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.notice.dao.UserMessageDao;
import com.skyeye.eve.notice.entity.UserMessage;
import com.skyeye.eve.notice.entity.UserMessageBox;
import com.skyeye.eve.notice.service.UserMessageService;
import com.skyeye.eve.websocket.service.IWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: UserMessageServiceImpl
 * @Description: 用户消息管理务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 19:12
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "用户消息管理", groupName = "内部消息模块")
public class UserMessageServiceImpl extends SkyeyeBusinessServiceImpl<UserMessageDao, UserMessage> implements UserMessageService {

    @Autowired
    private IWebSocketService iWebSocketService;

    @Override
    protected QueryWrapper<UserMessage> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<UserMessage> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(UserMessage::getReceiveId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void editMessageById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UserMessage userMessage = selectById(id);
        if (ObjectUtil.isNotEmpty(userMessage)) {
            if (userMessage.getState().equals(WhetherEnum.DISABLE_USING.getKey())) {
                // 未读状态下的消息
                UpdateWrapper<UserMessage> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(UserMessage::getState), WhetherEnum.ENABLE_USING.getKey());
                updateWrapper.set(MybatisPlusUtil.toColumns(UserMessage::getReadTime), DateUtil.getTimeAndToString());
                update(updateWrapper);
            }
        } else {
            outputObject.setreturnMessage("该消息不存在.");
        }
    }

    @Override
    public void deleteAllMessage(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        UpdateWrapper<UserMessage> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(UserMessage::getReceiveId), userId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(UserMessage::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(UserMessage::getDeleteFlag), DeleteFlagEnum.DELETED.getKey());
        update(updateWrapper);
    }

    @Override
    public void insertUserMessage(InputObject inputObject, OutputObject outputObject) {
        UserMessageBox userMessageBox = inputObject.getParams(UserMessageBox.class);
        List<UserMessage> userMessageList = userMessageBox.getUserNoticeMationList();
        log.info("userMessageList: {}", JSONUtil.toJsonStr(userMessageList));
        if (CollectionUtil.isEmpty(userMessageList)) {
            return;
        }
        String userId = null;
        for (UserMessage userMessage : userMessageList) {
            userId = userMessage.getCreateUserId();
            userMessage.setState(WhetherEnum.DISABLE_USING.getKey());
        }
        createEntity(userMessageList, userId);
        // 发送websocket消息
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserMessage userMessage : userMessageList) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userMessage.getReceiveId());
            map.put("msg", userMessage.getContent());
            map.put("itemObject", JSONUtil.toJsonStr(userMessage));
            list.add(map);
        }
        log.info("list: {}", JSONUtil.toJsonStr(list));
        iWebSocketService.sendWebSocketPointMsgToUser(list, null);
    }

    @Override
    @IgnoreTenant
    public void queryUnReadMessageCount(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<UserMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(UserMessage::getReceiveId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(UserMessage::getState), WhetherEnum.DISABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(UserMessage::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            queryWrapper.eq(CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        long count = count(queryWrapper);
        outputObject.settotal(count);
    }

}
