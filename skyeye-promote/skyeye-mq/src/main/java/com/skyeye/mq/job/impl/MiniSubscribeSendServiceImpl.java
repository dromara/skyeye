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
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: MiniSubscribeSendServiceImpl
 * @Description: 小程序订阅消息 MQ 消费
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/29 22:30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${topic.mini-subscribe-send-service}",
    consumerGroup = "${topic.mini-subscribe-send-service}",
    selectorExpression = "${spring.profiles.active}")
public class MiniSubscribeSendServiceImpl implements RocketMQListener<String> {

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
                throw new IllegalArgumentException("小程序发送缺少 appId/appSecret/apiTemplateId/openId");
            }

            WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
            config.setAppid(appId);
            config.setSecret(appSecret);
            WxMaService wxMaService = new WxMaServiceImpl();
            wxMaService.setWxMaConfig(config);

            List<WxMaSubscribeMessage.MsgData> dataList = buildMsgData(map);
            String pagePath = MapUtil.getStr(map, "pagePath");
            // 小程序订阅消息只支持小程序内路径，忽略 http(s)
            if (StrUtil.isNotBlank(pagePath)) {
                String lower = pagePath.trim().toLowerCase();
                if (lower.startsWith("http://") || lower.startsWith("https://")) {
                    pagePath = StrUtil.EMPTY;
                }
            }
            WxMaSubscribeMessage message = WxMaSubscribeMessage.builder()
                .toUser(openId)
                .templateId(apiTemplateId)
                .page(pagePath)
                .data(dataList)
                .build();
            wxMaService.getMsgService().sendSubscribeMsg(message);

            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
            iNoticeDispatchHistoryService.updateChannelLog(channelLogId,
                NoticeDispatchChannelLogUpdateBody.STATUS_SUCCESS, jobId, StrUtil.EMPTY);
        } catch (Exception e) {
            log.warn("mini subscribe send failed", e);
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
        // 按 appId 取小程序 openId，避免与公众号 openId 串用
        Map<String, Object> bind = sysEveUserDao.queryUserBindMationByUserIdAndAppId(receiveId, appId);
        if (bind == null) {
            return StrUtil.EMPTY;
        }
        return MapUtil.getStr(bind, "openId");
    }

    /**
     * 固定映射：消息通知标题/内容（已解析占位符）→ thing1 / thing2。
     * 小程序订阅消息模板请使用 thing1、thing2 两个字段承接。
     */
    private List<WxMaSubscribeMessage.MsgData> buildMsgData(Map<String, Object> map) {
        List<WxMaSubscribeMessage.MsgData> list = new ArrayList<>();
        list.add(new WxMaSubscribeMessage.MsgData("thing1", toPlainText(MapUtil.getStr(map, "title"))));
        list.add(new WxMaSubscribeMessage.MsgData("thing2", toPlainText(MapUtil.getStr(map, "content"))));
        return list;
    }

    /** 小程序订阅消息字段同样不支持 HTML/换行，&lt;br/&gt; 转空格 */
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
