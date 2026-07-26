package com.skyeye.equipmentinspection.classenum;

import cn.hutool.core.map.MapUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备巡检单检查结果
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentInspectionCheckResult implements SkyeyeEnumClass {

    NORMAL(1, "正常", true, true),
    ABNORMAL(2, "异常", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

    public static Map<String, Object> getMation(Integer type) {
        if (type == null) {
            return MapUtil.newHashMap();
        }
        for (EquipmentInspectionCheckResult bean : values()) {
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
