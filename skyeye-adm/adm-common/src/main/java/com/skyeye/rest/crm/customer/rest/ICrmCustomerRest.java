package com.skyeye.rest.crm.customer.rest;


import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "${webroot.skyeye-crm}", configuration = ClientConfiguration.class)
public interface ICrmCustomerRest {

    /**
     * 根据ids获取客户信息
     *
     * @param params 入参，ids 主键ids
     */
    @PostMapping("/queryCustomerListByIds")
    String queryCustomerListByIds(@RequestBody Map<String, Object> params);

}
