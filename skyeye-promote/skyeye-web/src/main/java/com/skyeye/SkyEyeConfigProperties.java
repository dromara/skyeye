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
import com.skyeye.common.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class SkyEyeConfigProperties {

    @Value("${spring.cloud.nacos.config.server-addr}")
    private String serverAddr;

    @Value("${spring.cloud.nacos.config.namespace}")
    private String namespace;

    @Value("${spring.application.config}.${spring.cloud.nacos.config.file-extension}")
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
            // 从配置映射中直接获取zuulApi值
            String zuulApiKey = StrUtil.isEmpty(env) ? "skyeye.zuulApi" : "skyeye." + env + ".zuulApi";
            String zuulApi = getValueFromConfigMap(configMap, zuulApiKey);
            String springProfileActive = PropertiesUtil.getPropertiesValue("${spring.profiles.active}");
            zuulApi = zuulApi.replace("${spring.profiles.active}", springProfileActive);

            // 保存用于替换的键值对
            temp.put("${skyeye.zuulApi}", zuulApi != null ? zuulApi : "");
            if (!StrUtil.isEmpty(env)) {
                temp.put("${skyeye." + env + ".zuulApi}", zuulApi != null ? zuulApi : "");
            }
            temp.put("${skyeye.year}", String.valueOf(DateUtil.thisYear()));
            temp.put("${spring.profiles.active}", springProfileActive);

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

    /**
     * 从配置映射中获取嵌套的配置值
     *
     * @param configMap 配置映射
     * @param path      配置路径，如 "skyeye.zuulApi"
     * @return 配置值，如果不存在则返回null
     */
    private String getValueFromConfigMap(Map<String, Object> configMap, String path) {
        String[] keys = path.split("\\.");
        Object current = configMap;

        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
                if (current == null) {
                    return null;
                }
            } else {
                return null;
            }
        }

        return current.toString();
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
        log.info("开始从Nacos获取配置信息，环境参数: {}, 如果看到此日志说明没有命中缓存", env);
        long startTime = System.currentTimeMillis();
        Map<String, String> map = bindPropertiesToObject(env, "skyeye", "configuation", "config", env);
        log.info("获取配置信息完成，环境参数: {}, 耗时: {}ms", env, System.currentTimeMillis() - startTime);
        return map;
    }

}
