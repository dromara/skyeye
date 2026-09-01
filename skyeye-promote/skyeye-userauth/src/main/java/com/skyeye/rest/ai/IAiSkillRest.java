/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.ai;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * 调用 adm-ai 技能配置。
 */
@FeignClient(value = "${webroot.skyeye-adm}", contextId = "iAiSkillRest", configuration = {ClientConfiguration.class, AiChatFeignConfiguration.class})
public interface IAiSkillRest {

    @PostMapping("/queryEnabledAiSkillMatchList")
    String queryEnabledAiSkillMatchList(Map<String, Object> params);
}
