package com.skyeye.rest.ifs.bizacct.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "${webroot.skyeye-ifs}", configuration = ClientConfiguration.class)
public interface IFsBizAcctEventRest {

    @PostMapping("/acceptBizAcctEvent")
    String acceptBizAcctEvent(@RequestBody Map<String, Object> map);
}
