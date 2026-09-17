/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.platform.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.rest.platform.rest.IPlatformBaseSettingRest;
import com.skyeye.rest.platform.service.IPlatformBaseSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: IPlatformBaseSettingServiceImpl
 * @Description: 平台基础信息调用封装实现
 */
@Slf4j
@Service
public class IPlatformBaseSettingServiceImpl implements IPlatformBaseSettingService {

    private static final int DEFAULT_MAX_PERSONAL_STORE_PER_MEMBER = 3;

    private static final String KEY_MAX_PERSONAL_STORE_PER_MEMBER = "maxPersonalStorePerMember";

    @Autowired
    private IPlatformBaseSettingRest iPlatformBaseSettingRest;

    @Override
    public Integer getMaxPersonalStorePerMember() {
        try {
            ResultEntity resultEntity = ExecuteFeignClient.get(() -> iPlatformBaseSettingRest.queryPlatformPersonalStoreConfig());
            Map<String, Object> bean = resultEntity.getBean();
            if (bean == null || ObjectUtil.isEmpty(bean.get(KEY_MAX_PERSONAL_STORE_PER_MEMBER))) {
                return DEFAULT_MAX_PERSONAL_STORE_PER_MEMBER;
            }
            String value = bean.get(KEY_MAX_PERSONAL_STORE_PER_MEMBER).toString();
            if (StrUtil.isBlank(value)) {
                return DEFAULT_MAX_PERSONAL_STORE_PER_MEMBER;
            }
            return NumberUtil.parseInt(value);
        } catch (Exception e) {
            log.warn("获取个人门店配额失败，使用默认值 {}: {}", DEFAULT_MAX_PERSONAL_STORE_PER_MEMBER, e.getMessage());
            return DEFAULT_MAX_PERSONAL_STORE_PER_MEMBER;
        }
    }

}
