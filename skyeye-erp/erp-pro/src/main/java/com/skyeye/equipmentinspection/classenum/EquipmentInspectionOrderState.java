package com.skyeye.equipmentinspection.classenum;

import cn.hutool.core.map.MapUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备巡检单状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentInspectionOrderState implements SkyeyeEnumClass {

    BE_DISPATCHED(1, "待派工", true, true),
    PENDING_ORDERS(2, "待接单", true, false),
    BE_EXECUTED(3, "待填报", true, false),
    BE_AUDITED(4, "待完工", true, false),
    COMPLETED(5, "已完成", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

    public static Map<String, Object> getMation(Integer type) {
        if (type == null) {
            return MapUtil.newHashMap();
        }
        for (EquipmentInspectionOrderState bean : values()) {
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
