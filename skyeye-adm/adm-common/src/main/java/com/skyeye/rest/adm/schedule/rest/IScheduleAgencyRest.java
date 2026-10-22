/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.adm.schedule.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 日程待办（与 PC 待办小部件 myagency001 同口径）。
 * contextId 避免与其它 skyeye-adm Feign 冲突。
 */
@FeignClient(value = "${webroot.skyeye-adm}", contextId = "admIScheduleAgencyRest",
    configuration = ClientConfiguration.class)
public interface IScheduleAgencyRest {

    @PostMapping("/myagency001")
    String queryMyAgencyList(@RequestBody Map<String, Object> params);
}
