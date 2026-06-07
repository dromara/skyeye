/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.classenum;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EquipmentInspectionFrequencyType
 * @Description: 设备巡检频率
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentInspectionFrequencyType implements SkyeyeEnumClass {

    DAY(1, "日检", true, true),
    WEEK(2, "周检", true, false),
    MONTH(3, "月检", true, false),
    QUARTER(4, "季检", true, false),
    YEAR(5, "年检", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static Integer parseKey(Object raw) {
        if (raw == null) {
            return DAY.getKey();
        }
        if (raw instanceof Number) {
            return ((Number) raw).intValue();
        }
        String text = StrUtil.trim(raw.toString());
        if (StrUtil.isBlank(text)) {
            return DAY.getKey();
        }
        if (StrUtil.isNumeric(text)) {
            return Integer.parseInt(text);
        }
        switch (text.toLowerCase()) {
            case "week":
                return WEEK.getKey();
            case "month":
                return MONTH.getKey();
            case "quarter":
                return QUARTER.getKey();
            case "year":
                return YEAR.getKey();
            case "day":
            default:
                return DAY.getKey();
        }
    }

}
