/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.platform.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName: IPlatformBaseSettingRest
 * @Description: 平台基础信息 Feign
 */
@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface IPlatformBaseSettingRest {

    /**
     * 获取个人门店配额配置（所有服务可读）
     */
    @GetMapping("/post/PlatformBaseSettingController/queryPlatformPersonalStoreConfig")
    String queryPlatformPersonalStoreConfig();

}
