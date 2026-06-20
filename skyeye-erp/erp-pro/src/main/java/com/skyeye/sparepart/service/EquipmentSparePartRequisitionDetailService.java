/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.service;

import com.skyeye.base.business.service.SkyeyeLinkDataService;
import com.skyeye.sparepart.entity.EquipmentSparePartRequisitionDetail;

import java.util.List;

/**
 * 备件领用明细
 */
public interface EquipmentSparePartRequisitionDetailService extends SkyeyeLinkDataService<EquipmentSparePartRequisitionDetail> {

    String calcOrderAllTotalPrice(List<EquipmentSparePartRequisitionDetail> detailList);

    List<EquipmentSparePartRequisitionDetail> selectByPIds(List<String> pIds);

}
