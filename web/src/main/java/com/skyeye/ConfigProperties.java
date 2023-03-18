/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye;

import cn.hutool.core.date.DateUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: ConfigProperties
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/9 20:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
@ConfigurationProperties(prefix = "skyeye.configuation")
public class ConfigProperties {

    public ConfigProperties() {
        System.setProperty("skyeye.year", String.valueOf(DateUtil.thisYear()));
    }

    private Map<String, String> config;

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }
}
