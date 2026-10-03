package com.skyeye.rest.erp.payable.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IErpPayableRest {

    /**
     * 应付事项总笔数（财务流程板统计）
     */
    @PostMapping("/queryPayableCount")
    String queryPayableCount();
}
