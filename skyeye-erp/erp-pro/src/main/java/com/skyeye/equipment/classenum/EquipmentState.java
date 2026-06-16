package com.skyeye.equipment.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EquipmentState
 * @Description: 设备状态枚举类
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentState implements SkyeyeEnumClass {

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
