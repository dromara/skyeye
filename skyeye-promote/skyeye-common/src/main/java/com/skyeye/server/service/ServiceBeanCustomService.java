/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.server.entity.ServiceBeanCustom;

/**
 * @ClassName: ServiceBeanCustomService
 * @Description: 自定义服务管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/1/6 22:43
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ServiceBeanCustomService extends SkyeyeBusinessService<ServiceBeanCustom> {

    void queryServiceBeanCustom(InputObject inputObject, OutputObject outputObject);

    void queryServiceBeanCustomAiFormAssist(InputObject inputObject, OutputObject outputObject);

    void editServiceBeanCustomAiFormAssist(InputObject inputObject, OutputObject outputObject);

    ServiceBeanCustom selectServiceBeanCustom(String appId, String className);

    void deleteServiceBeanCustom(String appId, String className);

}
