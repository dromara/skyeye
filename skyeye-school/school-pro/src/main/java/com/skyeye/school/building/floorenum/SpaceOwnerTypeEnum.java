package com.skyeye.school.building.floorenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 空间轮廓归属：楼栋或房间
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SpaceOwnerTypeEnum implements SkyeyeEnumClass {

    BUILDING(1, "楼栋", true, true),
    ROOM(2, "房间", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
