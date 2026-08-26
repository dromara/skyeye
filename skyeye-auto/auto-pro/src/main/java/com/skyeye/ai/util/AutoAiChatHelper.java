/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.util;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.ai.IAiChatRest;
import com.skyeye.rest.platform.IPlatformBaseSettingRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 流式对话编排（Feign 调用 adm-ai，WebSocket 推送结果）。
 */
@Component
public class AutoAiChatHelper {

    @Autowired
    private IAiChatRest iAiChatRest;

    @Autowired
    private IPlatformBaseSettingRest iPlatformBaseSettingRest;

    public String loadPlatformAiRoleId() {
        Map<String, Object> bean = ExecuteFeignClient.get(() -> iPlatformBaseSettingRest.queryPlatformAiRole()).getBean();
        String roleId = bean == null || bean.get("roleId") == null ? "" : bean.get("roleId").toString();
        if (StrUtil.isBlank(roleId)) {
            throw new CustomException("请先在平台信息设置中绑定AI角色");
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
        return bean;
    }

    public String requireAnswer(Map<String, Object> params) {
        if (params.get("answer") == null) {
            throw new CustomException("生成结果不能为空");
        }
        String answer = params.get("answer").toString();
        if (StrUtil.isBlank(answer)) {
            throw new CustomException("生成结果不能为空");
        }
        return answer;
    }
}
