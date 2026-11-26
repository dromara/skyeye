package com.skyeye.performance.classenum;

import cn.hutool.core.map.MapUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 指标数据来源：手动 / 考勤 / 问卷 / 计件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PerfItemSourceType implements SkyeyeEnumClass {

    MANUAL("manual", "手动打分", true, true),
    CHECKWORK("checkwork", "考勤", true, false),
    SURVEY("survey", "问卷", true, false),
    PIECEWORK("piecework", "计件", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

    public static Map<String, Object> getMation(String type) {
        for (PerfItemSourceType bean : PerfItemSourceType.values()) {
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
