/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.service;

import com.skyeye.base.business.service.SkyeyeLinkDataService;
import com.skyeye.sparepart.entity.EquipmentSparePartApplyLink;

import java.util.List;

/**
 * 设备备件-申领明细
 */
public interface EquipmentSparePartApplyLinkService extends SkyeyeLinkDataService<EquipmentSparePartApplyLink> {

    String calcOrderAllTotalPrice(List<EquipmentSparePartApplyLink> applyLinkList);

}
