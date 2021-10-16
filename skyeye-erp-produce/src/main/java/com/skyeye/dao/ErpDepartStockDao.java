/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ErpDepartStockDao {

    public List<Map<String, Object>> queryMaterialReserveList(Map<String, Object> params) throws Exception;

    public List<Map<String, Object>> queryMaterialNormsMationDetailsById(@Param("materialId") String materialId,
        @Param("departMentId") String departMentId) throws Exception;

}
