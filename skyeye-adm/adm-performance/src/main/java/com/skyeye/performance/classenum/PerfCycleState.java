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
public enum PerfCycleState implements SkyeyeEnumClass {

    NOT_STARTED("notStarted", "未开始", true, true),
    PROCESSING("processing", "进行中", true, false),
    CLOSED("closed", "已关闭", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

    public static Map<String, Object> getMation(String type) {
        for (PerfCycleState bean : PerfCycleState.values()) {
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
