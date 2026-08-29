/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.ai;

import feign.Request;
import org.springframework.context.annotation.Bean;

/**
 * adm-ai 同步对话 Feign 超时配置，仅作用于 IAiChatRest。
 */
public class AiChatFeignConfiguration {

    @Bean
    public Request.Options aiChatFeignOptions() {
        return new Request.Options(10000, 120000);
    }
}
