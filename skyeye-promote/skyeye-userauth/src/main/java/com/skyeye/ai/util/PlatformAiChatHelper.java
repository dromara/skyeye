/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.util;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.ai.IAiChatRest;
import com.skyeye.tenant.service.PlatformBaseSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 平台 AI 流式对话编排（Feign 调用 adm-ai）。
 */
@Component
public class PlatformAiChatHelper {

    @Autowired
    private IAiChatRest iAiChatRest;

    @Autowired
    private PlatformBaseSettingService platformBaseSettingService;

    public String loadPlatformAiRoleId() {
        String roleId = platformBaseSettingService.getOaAiRoleId();
        if (StrUtil.isBlank(roleId)) {
            throw new CustomException("请先在平台信息设置中绑定办公OA的AI角色");
        }
        return roleId;
    }

    public Map<String, Object> startStreamingChat(String content, String bizType) {
        return startStreamingChat(content, bizType, null);
    }

    public Map<String, Object> startStreamingChat(String content, String bizType, Map<String, Object> extraParams) {
        String roleId = loadPlatformAiRoleId();
        Map<String, Object> chatParams = new HashMap<>();
        chatParams.put("content", content);
        chatParams.put("bizType", bizType);
        chatParams.put("roleId", roleId);
        chatParams.put("saveChat", 0);
        if (extraParams != null && !extraParams.isEmpty()) {
            chatParams.putAll(extraParams);
        }
        Map<String, Object> chatBean = ExecuteFeignClient.get(() -> iAiChatRest.syncChatCompletion(chatParams)).getBean();
        if (chatBean == null || chatBean.get("id") == null) {
            throw new CustomException("启动AI生成失败");
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("chatId", chatBean.get("id").toString());
        bean.put("streaming", true);
        if (chatBean.get("apiKeyId") != null) {
            bean.put("apiKeyId", chatBean.get("apiKeyId").toString());
        }
        return bean;
    }
}
