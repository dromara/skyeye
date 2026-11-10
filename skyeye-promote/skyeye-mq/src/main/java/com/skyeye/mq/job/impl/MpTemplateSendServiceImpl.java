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

            WxMpTemplateMessage message = WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId(apiTemplateId)
                .build();
            List<WxMpTemplateData> dataList = buildTemplateData(map);
            message.setData(dataList);
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
        // 按 appId 取公众号 openId，避免与小程序 openId 串用
        Map<String, Object> bind = sysEveUserDao.queryUserBindMationByUserIdAndAppId(receiveId, appId);
        if (bind == null) {
            return StrUtil.EMPTY;
        }
        return MapUtil.getStr(bind, "openId");
    }

    /**
     * 固定映射：消息通知标题/内容（已解析占位符）→ 微信模板 title / content。
     * 注意：微信自 2023-05 起不再展示 first、remark，勿再用这两个字段名。
     */
    private List<WxMpTemplateData> buildTemplateData(Map<String, Object> map) {
        List<WxMpTemplateData> list = new ArrayList<>();
        String title = toPlainText(MapUtil.getStr(map, "title"));
        String content = toPlainText(MapUtil.getStr(map, "content"));
        list.add(new WxMpTemplateData("title", title));
        list.add(new WxMpTemplateData("content", content));
        log.info("mp template data titleLen={}, contentLen={}", title.length(), content.length());
        return list;
    }

    /** 站内信内容可能含 HTML，公众号模板只收纯文本 */
    private String toPlainText(String raw) {
        if (StrUtil.isBlank(raw)) {
            return StrUtil.EMPTY;
        }
        String text = raw.replaceAll("(?i)<br\\s*/?>", "\n");
        text = text.replaceAll("<[^>]+>", StrUtil.EMPTY);
        return text.replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">")
            .replace("&amp;", "&").trim();
    }

}
