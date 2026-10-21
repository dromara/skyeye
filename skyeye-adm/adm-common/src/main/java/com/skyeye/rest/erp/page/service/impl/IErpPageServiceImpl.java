/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.erp.page.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.erp.page.rest.IErpPageRest;
import com.skyeye.rest.erp.page.service.IErpPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class IErpPageServiceImpl implements IErpPageService {

    @Autowired
    private IErpPageRest iErpPageRest;

    @Override
    public Map<String, Object> queryProcessFlowCount() {
        Map<String, Object> bean = ExecuteFeignClient.get(
            () -> iErpPageRest.queryProcessFlowCount(new HashMap<>())).getBean();
        if (CollectionUtil.isEmpty(bean)) {
            return Collections.emptyMap();
        }
        return bean;
    }
}
