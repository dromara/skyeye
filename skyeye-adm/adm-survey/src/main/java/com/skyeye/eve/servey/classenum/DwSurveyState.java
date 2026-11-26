package com.skyeye.eve.servey.classenum;

import cn.hutool.core.map.MapUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 问卷状态：设计 / 执行中 / 结束
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DwSurveyState implements SkyeyeEnumClass {

    DESIGN(0, "设计", true, true),
    RUNNING(1, "执行中", true, false),
    END(2, "结束", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

    public static String getName(Integer type) {
        if (type == null) {
            return StringUtils.EMPTY;
        }
        for (DwSurveyState bean : DwSurveyState.values()) {
            if (type.equals(bean.getKey())) {
                return bean.getValue();
            }
        }
        return StringUtils.EMPTY;
    }

    public static Map<String, Object> getMation(Integer type) {
        for (DwSurveyState bean : DwSurveyState.values()) {
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
