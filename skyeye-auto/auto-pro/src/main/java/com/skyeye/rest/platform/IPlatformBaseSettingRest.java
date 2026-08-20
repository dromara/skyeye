/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.platform;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName: IPlatformBaseSettingRest
 * @Description: 调用平台基础信息设置
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface IPlatformBaseSettingRest {

    /**
     * 查询平台绑定的 AI 角色
     *
     * @return 平台统一 JSON 字符串
     */
    @GetMapping("/queryPlatformAiRole")
    String queryPlatformAiRole();
}
