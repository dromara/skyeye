package com.skyeye.equipmentcheck.classenum;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: EquipmentCheckItemResult
 * @Description: 设备点检明细结果枚举类
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentCheckItemResult implements SkyeyeEnumClass {

    NORMAL("normal", "正常", true, true),
    ABNORMAL("abnormal", "异常", true, false),
    OTHER("other", "其他", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

    public static Map<String, Object> getMation(String type) {
        if (StrUtil.isBlank(type)) {
            return MapUtil.newHashMap();
        }
        for (EquipmentCheckItemResult bean : values()) {
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

