/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.notice.classenum.NoticeChannelEnum;
import com.skyeye.eve.notice.classenum.NoticeDispatchChannelStatusEnum;
import com.skyeye.eve.notice.entity.NoticeDispatchChannelLog;
import com.skyeye.eve.notice.entity.NoticeDispatchHistory;
import com.skyeye.eve.notice.service.NoticeDispatchChannelLogService;
import com.skyeye.eve.notice.service.NoticeDispatchHistoryService;
import com.skyeye.eve.notice.service.NoticeDispatchService;
import com.skyeye.eve.notice.util.NoticeDispatchConfigHelper;
import com.skyeye.eve.notice.util.NoticeTemplateRender;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.rest.notice.NoticeDispatchBody;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.eve.service.ISystemFoundationSettingsService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.pro.rest.IPlatformBaseSettingRest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: NoticeDispatchServiceImpl
 * @Description: 多渠道通知分发。多租户合并平台类型/渠道账号与租户模板；单租户仅读系统基础设置
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/29 22:30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
public class NoticeDispatchServiceImpl implements NoticeDispatchService {

    @Autowired
    private ISystemFoundationSettingsService iSystemFoundationSettingsService;

    @Autowired
    private IPlatformBaseSettingRest iPlatformBaseSettingRest;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private NoticeDispatchHistoryService noticeDispatchHistoryService;

    @Autowired
    private NoticeDispatchChannelLogService noticeDispatchChannelLogService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void dispatchNotice(InputObject inputObject, OutputObject outputObject) {
        NoticeDispatchBody body = inputObject.getParams(NoticeDispatchBody.class);
        dispatch(body);
    }

    @Override
    public void dispatch(NoticeDispatchBody body) {
        if (body == null || StrUtil.isEmpty(body.getTypeCode()) || CollectionUtil.isEmpty(body.getReceiveUserIds())) {
            throw new CustomException("通知类型编码与接收人不能为空.");
        }
        Map<String, Object> settings = iSystemFoundationSettingsService.querySystemFoundationSettingsList();
        Object tenantConfig = settings == null ? null : settings.get("noticeDispatchConfig");
        // 多租户：平台类型/渠道账号 + 租户模板；单租户：全部走系统基础设置
        Map<String, Object> config = tenantEnable
            ? NoticeDispatchConfigHelper.mergePlatformAndTenant(loadPlatformNoticeConfig(), tenantConfig)
            : NoticeDispatchConfigHelper.parseConfig(tenantConfig);
        Map<String, Object> noticeType = NoticeDispatchConfigHelper.findType(config, body.getTypeCode());
        if (ObjectUtil.isEmpty(noticeType)) {
            throw new CustomException("通知类型不存在或已禁用: " + body.getTypeCode());
        }
        List<Integer> channels = CollectionUtil.isNotEmpty(body.getChannels())
            ? body.getChannels()
            : NoticeDispatchConfigHelper.enabledChannels(noticeType);
        List<String> receiveUserIds = body.getReceiveUserIds().stream()
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Map<String, Object> params = body.getParams() == null ? new HashMap<>() : new HashMap<>(body.getParams());
        String createUserId = StrUtil.blankToDefault(body.getCreateUserId(), CommonConstants.ADMIN_USER_ID);
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        String typeCode = MapUtil.getStr(noticeType, "codeNum");
        String typeName = MapUtil.getStr(noticeType, "name");
        String titleHint = StrUtil.blankToDefault(MapUtil.getStr(params, "title"), typeName);

        NoticeDispatchHistory history = noticeDispatchHistoryService.createHistory(
            typeCode, typeName, titleHint, body.getObjectData(), receiveUserIds.size(), createUserId);
        String historyId = history.getId();

        if (CollectionUtil.isEmpty(channels)) {
            log.warn("notice type {} has no enabled channels", body.getTypeCode());
            noticeDispatchChannelLogService.createLog(historyId, NoticeChannelEnum.SITE.getKey(),
                StrUtil.EMPTY, StrUtil.EMPTY, titleHint, StrUtil.EMPTY,
                NoticeDispatchChannelStatusEnum.SKIPPED.getKey(), "未配置可用默认渠道", createUserId);
            noticeDispatchHistoryService.refreshHistoryStatus(historyId);
            return;
        }

        for (String receiveUserId : receiveUserIds) {
            Map<String, Object> userMation = safeQueryUser(receiveUserId);
            Map<String, Object> extras = body.getReceiverExtras() == null
                ? new HashMap<>()
                : body.getReceiverExtras().getOrDefault(receiveUserId, new HashMap<>());
            Map<String, Object> userParams = new HashMap<>(params);
            if (!userParams.containsKey("userName") && userMation != null) {
                userParams.put("userName", userMation.getOrDefault("userName", StrUtil.EMPTY));
            }
            for (Integer channel : channels) {
                try {
                    dispatchOneChannel(config, noticeType, channel, receiveUserId, userMation, extras,
                        userParams, createUserId, body.getObjectData(), tenantId, historyId);
                } catch (Exception e) {
                    log.error("dispatch channel {} for user {} failed", channel, receiveUserId, e);
                    noticeDispatchChannelLogService.createLog(historyId, channel, receiveUserId, StrUtil.EMPTY,
                        titleHint, StrUtil.EMPTY, NoticeDispatchChannelStatusEnum.FAIL.getKey(),
                        e.getMessage(), createUserId);
                }
            }
        }
        noticeDispatchHistoryService.refreshHistoryStatus(historyId);
    }

    private void dispatchOneChannel(Map<String, Object> config, Map<String, Object> noticeType, Integer channel,
                                    String receiveUserId, Map<String, Object> userMation, Map<String, Object> extras,
                                    Map<String, Object> params, String createUserId, String objectData,
                                    String tenantId, String historyId) {
        NoticeChannelEnum channelEnum = NoticeChannelEnum.getByKey(channel);
        if (channelEnum == null) {
            noticeDispatchChannelLogService.createLog(historyId, channel, receiveUserId, StrUtil.EMPTY,
                StrUtil.EMPTY, StrUtil.EMPTY, NoticeDispatchChannelStatusEnum.SKIPPED.getKey(),
                "不支持的通知渠道", createUserId);
            return;
        }
        Map<String, Object> template = NoticeDispatchConfigHelper.findTemplate(noticeType, channel);
        if (ObjectUtil.isEmpty(template)) {
            noticeDispatchChannelLogService.createLog(historyId, channel, receiveUserId, StrUtil.EMPTY,
                StrUtil.EMPTY, StrUtil.EMPTY, NoticeDispatchChannelStatusEnum.SKIPPED.getKey(),
                "未启用该渠道模板", createUserId);
            return;
        }
        String title = NoticeTemplateRender.render(MapUtil.getStr(template, "title"), params);
        String content = NoticeTemplateRender.render(MapUtil.getStr(template, "content"), params);
        String receiveAccount = resolveReceiveAccount(channelEnum, receiveUserId, userMation, extras);
        if ((NoticeChannelEnum.EMAIL.equals(channelEnum) || NoticeChannelEnum.SMS.equals(channelEnum))
            && StrUtil.isEmpty(receiveAccount)) {
            noticeDispatchChannelLogService.createLog(historyId, channel, receiveUserId, StrUtil.EMPTY,
                title, content, NoticeDispatchChannelStatusEnum.SKIPPED.getKey(),
                "接收账号为空", createUserId);
            return;
        }
        Map<String, Object> channelConfig = NoticeDispatchConfigHelper.findChannelConfig(config, channel);
        if (NoticeChannelEnum.SMS.equals(channelEnum)
            && (ObjectUtil.isEmpty(channelConfig) || StrUtil.isEmpty(MapUtil.getStr(channelConfig, "refId")))) {
            noticeDispatchChannelLogService.createLog(historyId, channel, receiveUserId, receiveAccount,
                title, content, NoticeDispatchChannelStatusEnum.SKIPPED.getKey(),
                "未配置可用短信渠道账号", createUserId);
            return;
        }
        if ((NoticeChannelEnum.MP.equals(channelEnum) || NoticeChannelEnum.MINI_PROGRAM.equals(channelEnum))
            && (ObjectUtil.isEmpty(channelConfig)
            || StrUtil.isBlank(MapUtil.getStr(channelConfig, "appId"))
            || StrUtil.isBlank(MapUtil.getStr(channelConfig, "appSecret")))) {
            noticeDispatchChannelLogService.createLog(historyId, channel, receiveUserId, receiveAccount,
                title, content, NoticeDispatchChannelStatusEnum.SKIPPED.getKey(),
                "未配置可用" + channelEnum.getValue() + "渠道账号", createUserId);
            return;
        }

        NoticeDispatchChannelLog channelLog = noticeDispatchChannelLogService.createLog(
            historyId, channel, receiveUserId, receiveAccount, title, content,
            NoticeDispatchChannelStatusEnum.PROCESSING.getKey(), StrUtil.EMPTY, createUserId);

        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("typeCode", MapUtil.getStr(noticeType, "codeNum"));
            payload.put("channel", channel);
            payload.put("receiveId", receiveUserId);
            payload.put("receiveAccount", receiveAccount);
            payload.put("title", title);
            payload.put("content", content);
            payload.put("remark", StrUtil.blankToDefault(MapUtil.getStr(noticeType, "remark"), title));
            payload.put("objectData", objectData);
            payload.put("createUserId", createUserId);
            payload.put("params", params);
            payload.put("apiTemplateId", MapUtil.getStr(template, "apiTemplateId"));
            payload.put("smsTemplateCode", MapUtil.getStr(template, "smsTemplateCode"));
            payload.put("pagePath", MapUtil.getStr(template, "pagePath"));
            payload.put("historyId", historyId);
            payload.put("channelLogId", channelLog.getId());
            if (tenantEnable) {
                payload.put("tenantId", tenantId);
            }
            if (ObjectUtil.isNotEmpty(channelConfig)) {
                payload.put("appId", MapUtil.getStr(channelConfig, "appId"));
                payload.put("appSecret", MapUtil.getStr(channelConfig, "appSecret"));
                payload.put("refId", MapUtil.getStr(channelConfig, "refId"));
            }

            int jobType = resolveJobType(channelEnum);
            payload.put("type", jobType);
            if (NoticeChannelEnum.EMAIL.equals(channelEnum)) {
                payload.put("email", receiveAccount);
            }
            if (NoticeChannelEnum.SMS.equals(channelEnum)) {
                enrichSmsPayload(payload, channelConfig, template, receiveAccount, params);
            }

            JobMateMation jobMateMation = new JobMateMation();
            jobMateMation.setJsonStr(JSONUtil.toJsonStr(payload));
            jobMateMation.setUserId(createUserId);
            iJobMateMationService.sendMQProducer(jobMateMation);
        } catch (Exception e) {
            log.error("dispatch channel {} for user {} failed after log created", channel, receiveUserId, e);
            noticeDispatchChannelLogService.updateChannelResult(
                channelLog.getId(), NoticeDispatchChannelStatusEnum.FAIL.getKey(), null, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void enrichSmsPayload(Map<String, Object> payload, Map<String, Object> channelConfig,
                                  Map<String, Object> template, String mobile, Map<String, Object> params) {
        Map<String, Object> smsMessage = new HashMap<>();
        smsMessage.put("mobile", mobile);
        smsMessage.put("channelId", channelConfig == null ? null : MapUtil.getStr(channelConfig, "refId"));
        smsMessage.put("apiTemplateId", MapUtil.getStr(template, "apiTemplateId"));
        List<Map<String, Object>> templateParams = new ArrayList<>();
        Object paramsObj = template.get("params");
        List<String> keys = new ArrayList<>();
        if (paramsObj instanceof List) {
            for (Object key : (List<?>) paramsObj) {
                keys.add(key.toString());
            }
        } else {
            keys.addAll(params.keySet());
        }
        for (String key : keys) {
            Map<String, Object> kv = new HashMap<>();
            kv.put("key", key);
            kv.put("value", params.get(key));
            templateParams.add(kv);
        }
        smsMessage.put("templateParams", templateParams);
        payload.put("content", JSONUtil.toJsonStr(smsMessage));
        payload.put("renderContent", NoticeTemplateRender.render(MapUtil.getStr(template, "content"), params));
    }

    private int resolveJobType(NoticeChannelEnum channelEnum) {
        switch (channelEnum) {
            case SITE:
                return MqConstants.JobMateMationJobType.SITE_MESSAGE_SEND.getJobType();
            case EMAIL:
                return MqConstants.JobMateMationJobType.ORDINARY_MAIL_DELIVERY.getJobType();
            case SMS:
                return MqConstants.JobMateMationJobType.SMS_SEND.getJobType();
            case MP:
                return MqConstants.JobMateMationJobType.MP_TEMPLATE_SEND.getJobType();
            case MINI_PROGRAM:
                return MqConstants.JobMateMationJobType.MINI_SUBSCRIBE_SEND.getJobType();
            case CHAT:
                return MqConstants.JobMateMationJobType.CHAT_MESSAGE_SEND.getJobType();
            default:
                throw new CustomException("不支持的通知渠道");
        }
    }

    private String resolveReceiveAccount(NoticeChannelEnum channelEnum, String receiveUserId,
                                         Map<String, Object> userMation, Map<String, Object> extras) {
        switch (channelEnum) {
            case SITE:
            case CHAT:
                // 站内信/聊天无独立账号，展示用姓名（详情页也会用 receiveUserMation）
                String userName = MapUtil.getStr(userMation, "userName");
                if (StrUtil.isBlank(userName)) {
                    userName = MapUtil.getStr(userMation, "name");
                }
                return StrUtil.blankToDefault(userName, receiveUserId);
            case EMAIL:
                return firstNonEmpty(extras, "email", userMation, "email");
            case SMS:
                return firstNonEmpty(extras, "phone", userMation, "phone");
            case MP:
                return firstNonEmpty(extras, "mpOpenId", extras, "openId");
            case MINI_PROGRAM:
                return firstNonEmpty(extras, "miniOpenId", extras, "openId");
            default:
                return StrUtil.EMPTY;
        }
    }

    private String firstNonEmpty(Map<String, Object> first, String firstKey,
                                 Map<String, Object> second, String secondKey) {
        if (first != null && first.get(firstKey) != null && StrUtil.isNotEmpty(first.get(firstKey).toString())) {
            return first.get(firstKey).toString();
        }
        if (second != null && second.get(secondKey) != null && StrUtil.isNotEmpty(second.get(secondKey).toString())) {
            return second.get(secondKey).toString();
        }
        return StrUtil.EMPTY;
    }

    private Map<String, Object> safeQueryUser(String userId) {
        try {
            return iAuthUserService.queryDataMationById(userId);
        } catch (Exception e) {
            log.warn("query user {} failed", userId, e);
            return new HashMap<>();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadPlatformNoticeConfig() {
        try {
            Map<String, Object> result = ExecuteFeignClient.get(() -> iPlatformBaseSettingRest.queryPlatformNoticeDispatchConfig()).getBean();
            if (CollectionUtil.isEmpty(result)) {
                return NoticeDispatchConfigHelper.defaultPlatformConfig();
            }
            return result;
        } catch (Exception e) {
            log.warn("load platform notice config failed, fallback to defaults", e);
            return NoticeDispatchConfigHelper.defaultPlatformConfig();
        }
    }

}
