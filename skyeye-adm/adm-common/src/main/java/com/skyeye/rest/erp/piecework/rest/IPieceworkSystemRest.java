package com.skyeye.rest.erp.piecework.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IPieceworkSystemRest {

    /**
     * 按员工与月份区间查询计件（绩效取分）
     */
    @PostMapping("/queryPieceworkByStaffAndMonths")
    String queryPieceworkByStaffAndMonths(@RequestParam("staffId") String staffId,
                                          @RequestParam(value = "farmId", required = false) String farmId,
                                          @RequestParam("startMonth") String startMonth,
                                          @RequestParam("endMonth") String endMonth);
}
