/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 备件领用明细-领用原因
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentSparePartRequisitionReason implements SkyeyeEnumClass {

    LOOSE(1, "松动", true, false),
    AIR_LEAK(2, "漏气", true, false),
    FRACTURE(3, "断裂", true, false),
    MALFUNCTION(4, "失灵", true, false),
    WEAR(5, "磨损", true, false),
    DAMAGE(6, "损坏", true, false),
    RETROFIT(7, "改造", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
