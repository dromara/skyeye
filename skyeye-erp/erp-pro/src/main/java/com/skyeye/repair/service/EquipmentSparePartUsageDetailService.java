/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.repair.entity.EquipmentSparePartUsageDetail;

import java.util.List;

/**
 * 维修工单备件使用明细
 */
public interface EquipmentSparePartUsageDetailService extends SkyeyeBusinessService<EquipmentSparePartUsageDetail> {

    void saveLinkList(String parentId, List<EquipmentSparePartUsageDetail> detailList);

    void deleteByParentId(String parentId);

    List<EquipmentSparePartUsageDetail> selectByParentId(String parentId);

    void calcDetailPrice(List<EquipmentSparePartUsageDetail> detailList);

    void checkDetailList(String parentId, List<EquipmentSparePartUsageDetail> beans);

    void deductStockByParentId(String parentId);

}
