package com.skyeye.performance.classenum;

import cn.hutool.core.map.MapUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 绩效考核方案类型：KPI / OKR / 360
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PerfSchemeType implements SkyeyeEnumClass {

    KPI("kpi", "KPI考核", true, true),
    OKR("okr", "OKR", true, false),
    DEGREE360("360", "360评估", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

    public static Map<String, Object> getMation(String type) {
        for (PerfSchemeType bean : PerfSchemeType.values()) {
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
