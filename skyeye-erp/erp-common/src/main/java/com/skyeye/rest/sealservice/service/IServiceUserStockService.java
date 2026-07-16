/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.sealservice.service;

import java.util.List;
import java.util.Map;

/**
 * 我的配件库存公共服务（调用 seal-service）
 */
public interface IServiceUserStockService {

    /**
     * 查询当前登录人规格库存
     */
    Map<String, Map<String, Object>> queryUserStock(List<String> normsIds);

    /**
     * 增减配件库存
     */
    void editMaterialNormsUserStock(String userId, String materialId, String normsId, String operNumber, int type);

}
