package com.skyeye.rest.crm.receivable.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "${webroot.skyeye-crm}", configuration = ClientConfiguration.class)
public interface ICrmReceivableRest {

    /**
     * 应收事项总笔数（财务流程板统计）
     */
    @PostMapping("/queryReceivableCount")
    String queryReceivableCount();
}
