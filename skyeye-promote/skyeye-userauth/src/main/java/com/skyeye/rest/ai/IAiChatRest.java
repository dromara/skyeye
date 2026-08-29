/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.ai;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * 调用 adm-ai 同步对话。
 */
@FeignClient(value = "${webroot.skyeye-adm}", configuration = {ClientConfiguration.class, AiChatFeignConfiguration.class})
public interface IAiChatRest {

    /**
     * 流式调用大模型，立即返回 chatId。
     */
    @PostMapping("/syncChatCompletion")
    String syncChatCompletion(Map<String, Object> params);
}
