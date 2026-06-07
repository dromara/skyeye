/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EquipmentInspectionRunStatus
 * @Description: 设备巡检-设备运行状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentInspectionRunStatus implements SkyeyeEnumClass {

    NORMAL(1, "正常运行", true, true),
    DEGRADED(2, "带病运行", true, false),
    UNDER_REPAIR(3, "维修中", true, false),
    STANDBY(4, "备用", true, false),
    DISABLED(5, "停用", true, false),
    SCRAPPED(6, "报废", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
