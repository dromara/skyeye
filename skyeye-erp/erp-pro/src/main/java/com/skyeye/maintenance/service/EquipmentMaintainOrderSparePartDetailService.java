/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.maintenance.entity.EquipmentMaintainOrderSparePartDetail;

import java.util.List;

/**
 * @Description: 设备保养单备件领用明细服务接口
 */
public interface EquipmentMaintainOrderSparePartDetailService extends SkyeyeBusinessService<EquipmentMaintainOrderSparePartDetail> {

    /**
     * 保存备件子表（仅执行中编辑时由上层在 sparePartDetailList != null 时调用）。
     */
    void saveLinkList(String parentId, List<EquipmentMaintainOrderSparePartDetail> detailList);

    /**
     * 完成任务时按当前登录人扣减备件库存。
     */
    void deductStockByParentId(String parentId);

    void deleteByParentId(String parentId);

    List<EquipmentMaintainOrderSparePartDetail> selectByParentId(String parentId);

}
