package com.skyeye.rest.erp.farm.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IFarmRest {

    /**
     * 批量查询车间信息
     */
    @PostMapping("/queryFarmByIds")
    String queryFarmByIds(@RequestParam("farmIds") String farmIds);
}
