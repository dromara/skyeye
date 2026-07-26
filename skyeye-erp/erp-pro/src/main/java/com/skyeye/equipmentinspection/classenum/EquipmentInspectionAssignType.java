package com.skyeye.equipmentinspection.classenum;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 巡检员指派方式
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentInspectionAssignType implements SkyeyeEnumClass {

    MANUAL("manual", "手动指派", true, true),
    AUTO("auto", "自动指派，不做区分", true, false),
    BY_AREA("byArea", "按区域指派(自动)", true, false),
    BY_SKILL("bySkill", "按技能指派(自动)", true, false),
    BY_ORDER_TYPE("byOrderType", "按工单类型指派(自动)", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

    public static Map<String, Object> getMation(String type) {
        if (StrUtil.isBlank(type)) {
            return MapUtil.newHashMap();
        }
        for (EquipmentInspectionAssignType bean : values()) {
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
