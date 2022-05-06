/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.filter;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * @ClassName: PropertiesUtil
 * @Description: 非spring容器管理的类可以通过此类获取配置值
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/3 15:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class PropertiesUtil implements EmbeddedValueResolverAware {

    private static StringValueResolver resolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        PropertiesUtil.resolver = resolver;
    }

    public static String getPropertiesValue(String key){
        return resolver.resolveStringValue(key);
    }
}
