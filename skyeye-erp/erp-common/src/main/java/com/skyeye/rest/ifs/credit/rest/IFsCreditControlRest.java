package com.skyeye.rest.ifs.credit.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "${webroot.skyeye-ifs}", configuration = ClientConfiguration.class)
public interface IFsCreditControlRest {

    @PostMapping("/checkCustomerCredit")
    String checkCustomerCredit(@RequestBody Map<String, Object> map);
}
