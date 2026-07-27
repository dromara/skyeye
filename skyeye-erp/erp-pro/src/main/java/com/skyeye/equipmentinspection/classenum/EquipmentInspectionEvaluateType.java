package com.skyeye.equipmentinspection.classenum;

import cn.hutool.core.map.MapUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 巡检评价类型（对齐工单 SealEvaluate：1 系统自动 / 2 人工）
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentInspectionEvaluateType implements SkyeyeEnumClass {

    SYSTEM(1, "系统自动", true, false),
    MANUAL(2, "人工评价", true, true);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

    public static Map<String, Object> getMation(Integer type) {
        if (type == null) {
            return MapUtil.newHashMap();
        }
        for (EquipmentInspectionEvaluateType bean : values()) {
            if (type.equals(bean.getKey())) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", bean.getKey());
                result.put("name", bean.getValue());
                return result;
            }
        }
        return MapUtil.newHashMap();
    }
}
