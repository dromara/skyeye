package com.skyeye.rest.erp.supplier.rest;


import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IErpSupplierRest {

    /**
     * 根据ids获取供应商信息
     *
     * @param params 入参，ids 主键ids
     */
    @PostMapping("/querySupplierListByIds")
    String querySupplierListByIds(@RequestBody Map<String, Object> params);

}
