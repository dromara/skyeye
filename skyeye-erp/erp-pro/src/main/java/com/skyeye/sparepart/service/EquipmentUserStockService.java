/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.sparepart.entity.EquipmentUserStock;

import java.util.List;
import java.util.Map;

/**
 * 设备维修-我的备件库存
 */
public interface EquipmentUserStockService extends SkyeyeBusinessService<EquipmentUserStock> {

    void editMaterialNormsUserStock(String userId, String materialId, String normsId, String operNumber, int type);

    void queryMyPartsNumByNormsId(InputObject inputObject, OutputObject outputObject);

    EquipmentUserStock queryUserStock(String userId, String normsId);

    Map<String, EquipmentUserStock> queryUserStock(String userId, List<String> normsIds);

}
