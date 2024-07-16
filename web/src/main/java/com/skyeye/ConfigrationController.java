/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName: ConfigrationController
 * @Description: 获取配置信息返回给前台
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/9 20:01
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
public class ConfigrationController {

    @Autowired
    private ConfigProperties configProperties;

    @GetMapping(value = "/getConfigRation")
    public Map<String, String> getConfigRation(@RequestParam("env") String env) {
        return configProperties.getConfig(env);
    }

}
