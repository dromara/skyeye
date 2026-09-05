/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.pro.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 调用平台基础信息设置（skyeye-pro / userauth）
 */
@FeignClient(value = "${webroot.skyeye-pro}", contextId = "admIPlatformBaseSettingRest", configuration = ClientConfiguration.class)
public interface IPlatformBaseSettingRest {

    /**
     * 查询平台 AI 技能编码规则配置
     */
    @GetMapping("/queryPlatformAiSkillCodeRule")
    String queryPlatformAiSkillCodeRule();
}
