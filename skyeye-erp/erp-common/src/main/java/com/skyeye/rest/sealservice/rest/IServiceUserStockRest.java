/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.sealservice.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * 我的配件库存（seal-service）Feign 接口
 */
@FeignClient(value = "${webroot.skyeye-seal-service}", configuration = ClientConfiguration.class)
public interface IServiceUserStockRest {

    /**
     * 批量查询当前登录人规格库存
     *
     * @param params normsIds
     */
    @PostMapping("/queryUserStockByNormsIds")
    String queryUserStockByNormsIds(Map<String, Object> params);

    /**
     * 增减指定用户配件库存
     *
     * @param params userId、materialId、normsId、operNumber、type（1入库 2出库）
     */
    @PostMapping("/editMaterialNormsUserStock")
    String editMaterialNormsUserStock(Map<String, Object> params);

}
