/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 设备维修单状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentRepairOrderState implements SkyeyeEnumClass {

    BE_DISPATCHED(1, "待派工", true, true),
    PENDING_ORDERS(2, "待接单", true, false),
    BE_SIGNED(3, "待签到", true, false),
    BE_COMPLETED(4, "待完工", true, false),
    BE_EVALUATED(5, "待评价", true, false),
    AUDIT(6, "待审核", true, false),
    COMPLATE(7, "已完工", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
