/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.flowable.task.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 工作流任务（与 PC 待办小部件 PendingProcessWidget 同口径）。
 */
@FeignClient(value = "${webroot.skyeye-flowable}", configuration = ClientConfiguration.class)
public interface IFlowableTaskRest {

    /** 我的待办审批 */
    @PostMapping("/activitimode008")
    String queryUserAgencyTasksListByUserId(@RequestBody Map<String, Object> params);

    /** 我发起的流程 */
    @PostMapping("/activitimode013")
    String queryStartProcessNotSubByUserId(@RequestBody Map<String, Object> params);
}
