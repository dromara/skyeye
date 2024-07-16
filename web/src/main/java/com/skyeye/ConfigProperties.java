/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.skyeye.common.filter.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ConfigProperties
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/9 20:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class ConfigProperties {

    @Value("${spring.cloud.nacos.config.server-addr}")
    private String serverAddr;

    @Value("${spring.cloud.nacos.config.namespace}")
    private String namespace;

    @Value("${spring.application.name}.${spring.cloud.nacos.config.file-extension}")
    private String dataId;

    @Value("${spring.cloud.nacos.config.group}")
    private String group;

    public Map<String, String> bindPropertiesToObject(String env, String... prefix) {
        try {
            // 创建 Nacos 客户端
            Properties properties = new Properties();
            properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
            properties.setProperty(PropertyKeyConst.NAMESPACE, namespace);
            // 获取配置服务
            ConfigService configService = NacosFactory.createConfigService(properties);

            // 获取配置
            String content = configService.getConfig(dataId, group, 5000);

            // 使用SnakeYAML解析配置内容为Map
            Yaml yaml = new Yaml();
            ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());
            Map<String, Object> configMap = yaml.load(stream);
            Map<String, String> result = new HashMap<>();
            loadYamlConfig(result, configMap, prefix);

            Map<String, String> temp = new HashMap<>();
            String zuulApiKey = StrUtil.isEmpty(env) ? "${skyeye.zuulApi}" : "${skyeye." + env + ".zuulApi}";
            String zuulApi = PropertiesUtil.getPropertiesValue(zuulApiKey);
            temp.put(zuulApiKey, zuulApi);
            temp.put("${skyeye.year}", String.valueOf(DateUtil.thisYear()));

            result.forEach((key, value) -> {
                temp.forEach((k, v) -> {
                    if (value.contains(k)) {
                        result.put(key, value.replace(k, v));
                    }
                });
            });
            return result;
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadYamlConfig(Map<String, String> result, Map<String, Object> configMap, String... keys) {
        List<String> keyList = Arrays.asList(keys).stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(keyList)) {
            configMap.forEach((key, value) -> {
                result.put(key, value.toString());
            });
            return;
        }
        String key = keyList.get(0);
        if (configMap.containsKey(key)) {
            Object value = configMap.get(key);
            if (value instanceof Map) {
                if (keyList.size() == 1) {
                    loadYamlConfig(result, (Map<String, Object>) value, StrUtil.EMPTY);
                } else {
                    loadYamlConfig(result, (Map<String, Object>) value, Arrays.copyOfRange(keys, 1, keys.length));
                }
            } else {
                result.put(key, value.toString());
            }
        }
    }

    public Map<String, String> getConfig(String env) {
        Map<String, String> map = bindPropertiesToObject(env, "skyeye", "configuation", "config", env);
        return map;
    }

}
