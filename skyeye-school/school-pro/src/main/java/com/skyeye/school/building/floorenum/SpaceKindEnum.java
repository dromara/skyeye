package com.skyeye.school.building.floorenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 房间空间类型：普通 / 楼梯 / 电梯
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SpaceKindEnum implements SkyeyeEnumClass {

    ROOM(0, "普通房间", true, true),
    STAIR(1, "楼梯", true, false),
    ELEVATOR(2, "电梯", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
