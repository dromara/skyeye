/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.rest.notice.NoticeDispatchChannelLogUpdateBody;
import com.skyeye.eve.service.INoticeDispatchHistoryService;
import com.skyeye.personnel.dao.SysEveUserDao;
import com.skyeye.service.JobMateMationService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: MpTemplateSendServiceImpl
 * @Description: 公众号模板消息 MQ 消费
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/29 22:30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${topic.mp-template-send-service}",
    consumerGroup = "${topic.mp-template-send-service}",
    selectorExpression = "${spring.profiles.active}")
public class MpTemplateSendServiceImpl implements RocketMQListener<String> {

    @Autowired
    private JobMateMationService jobMateMationService;

    @Autowired
    private SysEveUserDao sysEveUserDao;

    @Autowired
    private INoticeDispatchHistoryService iNoticeDispatchHistoryService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        String channelLogId = MapUtil.getStr(map, "channelLogId");
        try {
            if (tenantEnable && map.get("tenantId") != null) {
                TenantContext.setTenantId(map.get("tenantId").toString());
            }
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);

            String appId = MapUtil.getStr(map, "appId");
            String appSecret = MapUtil.getStr(map, "appSecret");
            String apiTemplateId = MapUtil.getStr(map, "apiTemplateId");
            String openId = resolveOpenId(map);
            if (StrUtil.hasEmpty(appId, appSecret, apiTemplateId, openId)) {
                throw new IllegalArgumentException("公众号发送缺少 appId/appSecret/apiTemplateId/openId");
            }

            WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
            config.setAppId(appId);
            config.setSecret(appSecret);
            WxMpService wxMpService = new WxMpServiceImpl();
            wxMpService.setWxMpConfigStorage(config);

            WxMpTemplateMessage.WxMpTemplateMessageBuilder builder = WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId(apiTemplateId);
            applyJump(builder, map);
            WxMpTemplateMessage message = builder.build();
            message.setData(buildTemplateData(map));
            wxMpService.getTemplateMsgService().sendTemplateMsg(message);

            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
            iNoticeDispatchHistoryService.updateChannelLog(channelLogId,
                NoticeDispatchChannelLogUpdateBody.STATUS_SUCCESS, jobId, StrUtil.EMPTY);
        } catch (Exception e) {
            log.warn("mp template send failed", e);
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL,
                StrUtil.blankToDefault(e.getMessage(), StrUtil.EMPTY));
            iNoticeDispatchHistoryService.updateChannelLog(channelLogId,
                NoticeDispatchChannelLogUpdateBody.STATUS_FAIL, jobId, e.getMessage());
        }
    }

    /**
     * 跳转：H5（jumpUrl）与小程序（miniAppId + pagePath）可同时下发；微信端有小程序时优先打开小程序。
     */
    private void applyJump(WxMpTemplateMessage.WxMpTemplateMessageBuilder builder, Map<String, Object> map) {
        String pagePath = MapUtil.getStr(map, "pagePath");
        String jumpUrl = MapUtil.getStr(map, "jumpUrl");
        String miniAppId = MapUtil.getStr(map, "miniAppId");
        if (StrUtil.isNotBlank(miniAppId) && StrUtil.isNotBlank(pagePath)) {
            WxMpTemplateMessage.MiniProgram miniProgram = new WxMpTemplateMessage.MiniProgram();
            miniProgram.setAppid(miniAppId);
            miniProgram.setPagePath(pagePath);
            builder.miniProgram(miniProgram);
        }
        if (StrUtil.isNotBlank(jumpUrl)) {
            builder.url(jumpUrl);
        }
    }

    private String resolveOpenId(Map<String, Object> map) {
        String openId = MapUtil.getStr(map, "receiveAccount");
        if (StrUtil.isNotEmpty(openId)) {
            return openId;
        }
        String receiveId = MapUtil.getStr(map, "receiveId");
        String appId = MapUtil.getStr(map, "appId");
        if (StrUtil.isEmpty(receiveId) || StrUtil.isEmpty(appId)) {
            return StrUtil.EMPTY;
        }
        Map<String, Object> bind = sysEveUserDao.queryUserBindMationByUserIdAndAppId(receiveId, appId);
        if (bind == null) {
            return StrUtil.EMPTY;
        }
        return MapUtil.getStr(bind, "openId");
    }

    /**
     * 固定两字段：消息通知标题/内容（已解析占位符）→ title / content。
     * 勿用 first/remark（微信已屏蔽）。
     */
    private List<WxMpTemplateData> buildTemplateData(Map<String, Object> map) {
        List<WxMpTemplateData> list = new ArrayList<>();
        list.add(new WxMpTemplateData("title", toPlainText(MapUtil.getStr(map, "title"))));
        list.add(new WxMpTemplateData("content", toPlainText(MapUtil.getStr(map, "content"))));
        return list;
    }

    private String toPlainText(String raw) {
        if (StrUtil.isBlank(raw)) {
            return StrUtil.EMPTY;
        }
        String text = raw.replaceAll("(?i)<br\\s*/?>", " ");
        text = text.replaceAll("<[^>]+>", StrUtil.EMPTY);
        text = text.replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">")
            .replace("&amp;", "&");
        text = text.replaceAll("[\\r\\n]+", " ").replaceAll("\\s{2,}", " ");
        return text.trim();
    }

}
