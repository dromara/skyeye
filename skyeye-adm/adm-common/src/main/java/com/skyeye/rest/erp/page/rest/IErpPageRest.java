/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.erp.page.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * ERP 首页/流程板统计（与 PC 仪表盘 erppage005 同口径）。
 */
@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IErpPageRest {

    /**
     * 业务流程各节点单据数量。bean 的 key 为节点 code（如 E01 待出库、E03 待入库）。
     */
    @PostMapping("/erppage005")
    String queryProcessFlowCount(@RequestBody Map<String, Object> params);
}
