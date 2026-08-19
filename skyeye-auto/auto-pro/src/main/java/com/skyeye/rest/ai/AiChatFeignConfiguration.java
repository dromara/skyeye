/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.ai;

import feign.Request;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName: AiChatFeignConfiguration
 * @Description: adm-ai 同步对话 Feign 超时配置，仅作用于 IAiChatRest
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AiChatFeignConfiguration {

    @Bean
    public Request.Options aiChatFeignOptions() {
        return new Request.Options(10000, 120000);
    }
}
