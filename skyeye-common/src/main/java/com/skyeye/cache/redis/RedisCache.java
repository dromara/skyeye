/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.cache.redis;

import com.gexin.fastjson.JSON;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.JedisClientService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @ClassName: RedisCache
 * @Description: redis缓存工具类
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/3 20:30
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class RedisCache {

    private static Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

    @Autowired
    private JedisClientService jedisClient;

    /**
     * 从Redis缓存中获取数据
     *
     * @param key 缓存key
     * @param loader 缓存中没有时，从此方法中获取
     * @param seconds 缓存时间，单位：秒
     * @return
     */
    public List<Map<String, Object>> getList(String key, Function<String, Object> loader, int seconds){
        String value = getData(key, loader, seconds);
        if(value == null){
            return null;
        }
        return JSONArray.fromObject(value);
    }

    /**
     * 从Redis缓存中获取数据
     *
     * @param key 缓存key
     * @param loader 缓存中没有时，从此方法中获取
     * @param seconds 缓存时间，单位：秒
     * @return
     */
    public Map<String, Object> getMap(String key, Function<String, Object> loader, int seconds){
        String value = getData(key, loader, seconds);
        if(value == null){
            return null;
        }
        return JSONObject.fromObject(value);
    }

    /**
     * 从Redis缓存中获取数据
     *
     * @param key 缓存key
     * @param loader 缓存中没有时，从此方法中获取
     * @param seconds 缓存时间，单位：秒
     * @return
     */
    public String getString(String key, Function<String, Object> loader, int seconds){
        return getData(key, loader, seconds);
    }

    private String getData(String key, Function<String, Object> loader, int seconds){
        String value = jedisClient.get(key);
        if(ToolUtil.isBlank(value)) {
            LOGGER.info("get data mation from function, key is {}", key);
            value = JSON.toJSONString(loader.apply(key));
            if(value != null) {
                jedisClient.set(key, value, seconds);
            }
        } else {
            LOGGER.info("get data mation from redis cache, key is {}", key);
        }
        if(value == null){
            return null;
        }
        return value;
    }

}
