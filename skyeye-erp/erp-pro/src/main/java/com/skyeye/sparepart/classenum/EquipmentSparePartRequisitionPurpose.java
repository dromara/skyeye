/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 备件领用/申领目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentSparePartRequisitionPurpose implements SkyeyeEnumClass {

    EQUIPMENT_REPAIR(1, "设备维修", true, true),
    EQUIPMENT_MAINTENANCE(2, "设备保养", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
