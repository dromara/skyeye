package com.skyeye.performance.classenum;

import cn.hutool.core.map.MapUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PerfCycleType implements SkyeyeEnumClass {

    MONTH("month", "月度", true, false),
    QUARTER("quarter", "季度", true, true),
    YEAR("year", "年度", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

    public static Map<String, Object> getMation(String type) {
        for (PerfCycleType bean : PerfCycleType.values()) {
            if (bean.getKey().equals(type)) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", bean.getKey());
                result.put("name", bean.getValue());
                return result;
            }
        }
        return MapUtil.newHashMap();
    }
}
