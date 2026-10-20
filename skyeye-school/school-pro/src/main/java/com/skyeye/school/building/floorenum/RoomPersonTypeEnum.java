package com.skyeye.school.building.floorenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 房间人员类型：教职工或学生
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RoomPersonTypeEnum implements SkyeyeEnumClass {

    STAFF(1, "教职工", true, true),
    STUDENT(2, "学生", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
