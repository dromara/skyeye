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
import com.skyeye.eve.notice.service.NoticeMiniSubscribeService;
import com.skyeye.eve.notice.util.NoticeDispatchConfigHelper;
import com.skyeye.eve.notice.util.NoticeTemplateRender;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.rest.notice.NoticeDispatchBody;
import com.skyeye.eve.rest.notice.NoticeMiniSubscribeConsumeBody;
import com.skyeye.eve.rest.notice.NoticeMiniSubscribeSaveBody;
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

    @Autowired
    private NoticeMiniSubscribeService noticeMiniSubscribeService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void dispatchNotice(InputObject inputObject, OutputObject outputObject) {
        NoticeDispatchBody body = inputObject.getParams(NoticeDispatchBody.class);
        dispatch(body);
    }

    @Override
    public void queryMiniSubscribeTemplateList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> rows =
            NoticeDispatchConfigHelper.listMiniSubscribeTemplates(loadMergedNoticeConfig());
        String userId = MapUtil.getStr(InputObject.getLogParamsStatic(), "id");
        Set<String> templateIds = rows.stream()
            .map(row -> MapUtil.getStr(row, "templateId"))
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.toSet());
        Set<String> accepted = noticeMiniSubscribeService.listAcceptedTemplateIds(userId, templateIds);
        for (Map<String, Object> row : rows) {
            String templateId = MapUtil.getStr(row, "templateId");
            row.put("subscribed", StrUtil.isNotBlank(templateId) && accepted.contains(templateId));
        }
        outputObject.setBeans(rows);
        outputObject.settotal(rows.size());
    }

    @Override
    public void saveMiniSubscribeStatus(InputObject inputObject, OutputObject outputObject) {
        NoticeMiniSubscribeSaveBody body = inputObject.getParams(NoticeMiniSubscribeSaveBody.class);
        String userId = MapUtil.getStr(InputObject.getLogParamsStatic(), "id");
        if (StrUtil.isBlank(userId)) {
            throw new CustomException("未登录，无法保存订阅状态.");
        }
        noticeMiniSubscribeService.upsertStatuses(userId, body == null ? null : body.getList());
    }

    @Override
    public void consumeMiniSubscribeStatus(InputObject inputObject, OutputObject outputObject) {
        NoticeMiniSubscribeConsumeBody body = inputObject.getParams(NoticeMiniSubscribeConsumeBody.class);
        if (body == null) {
            return;
        }
        noticeMiniSubscribeService.consumeAccept(body.getUserId(), body.getTemplateId());
    }

    @Override
    public void dispatch(NoticeDispatchBody body) {
        if (body == null || StrUtil.isEmpty(body.getTypeCode()) || CollectionUtil.isEmpty(body.getReceiveUserIds())) {
            throw new CustomException("通知类型编码与接收人不能为空.");
        }
        Map<String, Object> config = loadMergedNoticeConfig();
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
                        userParams, createUserId, body.getObjectData(), tenantId, historyId, body);
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
                                    String tenantId, String historyId, NoticeDispatchBody body) {
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
            String pagePath = resolveRenderedPath(body == null ? null : body.getMiniPagePath(),
                MapUtil.getStr(params, "pagePath"),
                MapUtil.getStr(template, "pagePath"), params);
            String resolvedJumpUrl = resolveRenderedPath(body == null ? null : body.getJumpUrl(),
                MapUtil.getStr(params, "jumpUrl"),
                MapUtil.getStr(template, "h5Url"), params);
            String pcPath = resolveRenderedPath(body == null ? null : body.getPcPath(),
                MapUtil.getStr(params, "pcPath"), MapUtil.getStr(template, "pcPath"), params);
            String mobilePath = resolveRenderedPath(body == null ? null : body.getMobilePath(),
                MapUtil.getStr(params, "mobilePath"), MapUtil.getStr(template, "mobilePath"), params);
            // 平台/单租户配置的跳转域名：相对路径自动拼接；小程序 pagePath 不拼
            String h5Domain = NoticeDispatchConfigHelper.getJumpDomain(config, "h5Domain");
            String pcDomain = NoticeDispatchConfigHelper.getJumpDomain(config, "pcDomain");
            resolvedJumpUrl = NoticeDispatchConfigHelper.joinWithDomain(h5Domain, resolvedJumpUrl);
            pcPath = NoticeDispatchConfigHelper.joinWithDomain(pcDomain, pcPath);
            mobilePath = NoticeDispatchConfigHelper.joinWithDomain(h5Domain, mobilePath);
            String resolvedObjectData = objectData;
            if (NoticeChannelEnum.SITE.equals(channelEnum)) {
                resolvedObjectData = mergeSiteJumpObjectData(objectData, pcPath, mobilePath, resolvedJumpUrl, pagePath);
            }

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("typeCode", MapUtil.getStr(noticeType, "codeNum"));
            payload.put("channel", channel);
            payload.put("receiveId", receiveUserId);
            payload.put("receiveAccount", receiveAccount);
            payload.put("title", title);
            payload.put("content", content);
            payload.put("remark", StrUtil.blankToDefault(MapUtil.getStr(noticeType, "remark"), title));
            payload.put("objectData", resolvedObjectData);
            payload.put("createUserId", createUserId);
            payload.put("params", params);
            payload.put("apiTemplateId", MapUtil.getStr(template, "apiTemplateId"));
            payload.put("smsTemplateCode", MapUtil.getStr(template, "smsTemplateCode"));
            String channelKey = channel == null ? StrUtil.EMPTY : String.valueOf(channel);
            payload.put("wxTitleField", NoticeDispatchConfigHelper.defaultWxTitleField(
                channelKey, MapUtil.getStr(template, "wxTitleField")));
            payload.put("wxContentField", NoticeDispatchConfigHelper.defaultWxContentField(
                channelKey, MapUtil.getStr(template, "wxContentField")));
            payload.put("pagePath", pagePath);
            payload.put("jumpUrl", resolvedJumpUrl);
            payload.put("pcPath", pcPath);
            payload.put("mobilePath", mobilePath);
            String resolvedMiniAppId = StrUtil.blankToDefault(
                body == null ? null : body.getMiniAppId(), MapUtil.getStr(params, "miniAppId"));
            if (StrUtil.isBlank(resolvedMiniAppId)) {
                Map<String, Object> miniCfg = NoticeDispatchConfigHelper.findChannelConfig(
                    config, NoticeChannelEnum.MINI_PROGRAM.getKey());
                if (ObjectUtil.isNotEmpty(miniCfg)) {
                    resolvedMiniAppId = MapUtil.getStr(miniCfg, "appId");
                }
            }
            payload.put("miniAppId", resolvedMiniAppId);
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

    private String firstNonBlank(String first, String second) {
        return StrUtil.isNotBlank(first) ? first : StrUtil.blankToDefault(second, StrUtil.EMPTY);
    }

    /**
     * 业务入参 > params > 模板字段，均支持 {占位} 渲染
     */
    private String resolveRenderedPath(String bodyValue, String paramsValue, String templateValue,
                                       Map<String, Object> params) {
        String raw = firstNonBlank(bodyValue, firstNonBlank(paramsValue, templateValue));
        if (StrUtil.isBlank(raw)) {
            return StrUtil.EMPTY;
        }
        return StrUtil.blankToDefault(NoticeTemplateRender.render(raw, params), StrUtil.EMPTY).trim();
    }

    @SuppressWarnings("unchecked")
    private String mergeSiteJumpObjectData(String objectData, String pcPath, String mobilePath,
                                           String h5Url, String pagePath) {
        // 跳转字段放最前，避免 object_data 列过长被截断时丢跳转
        Map<String, Object> map = new LinkedHashMap<>();
        putIfNotBlank(map, "pcPath", pcPath);
        putIfNotBlank(map, "mobilePath", mobilePath);
        putIfNotBlank(map, "h5Url", h5Url);
        putIfNotBlank(map, "pagePath", pagePath);
        if (StrUtil.isNotBlank(objectData)) {
            try {
                Object parsed = JSONUtil.toBean(objectData, null);
                if (parsed instanceof Map) {
                    for (Map.Entry<String, Object> entry : ((Map<String, Object>) parsed).entrySet()) {
                        if (!map.containsKey(entry.getKey())) {
                            map.put(entry.getKey(), entry.getValue());
                        }
                    }
                } else {
                    map.put("raw", objectData);
                }
            } catch (Exception e) {
                map.put("raw", objectData);
            }
        }
        if (map.isEmpty()) {
            return StrUtil.blankToDefault(objectData, StrUtil.EMPTY);
        }
        return JSONUtil.toJsonStr(map);
    }

    private void putIfNotBlank(Map<String, Object> map, String key, String value) {
        if (StrUtil.isNotBlank(value)) {
            map.put(key, value);
        }
    }

    private Map<String, Object> safeQueryUser(String userId) {
        try {
            return iAuthUserService.queryDataMationById(userId);
        } catch (Exception e) {
            log.warn("query user {} failed", userId, e);
            return new HashMap<>();
        }
    }

    /** 多租户：平台类型/渠道账号 + 租户模板；单租户：系统基础设置 */
    private Map<String, Object> loadMergedNoticeConfig() {
        Map<String, Object> settings = iSystemFoundationSettingsService.querySystemFoundationSettingsList();
        Object tenantConfig = settings == null ? null : settings.get("noticeDispatchConfig");
        return tenantEnable
            ? NoticeDispatchConfigHelper.mergePlatformAndTenant(loadPlatformNoticeConfig(), tenantConfig)
            : NoticeDispatchConfigHelper.parseConfig(tenantConfig);
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
