/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.classenum;

import cn.hutool.core.map.MapUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 保养计划频次
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MaintenancePlanFrequency implements SkyeyeEnumClass {

    DAILY(1, "每天", true, true),
    WEEKLY(2, "每周", true, false),
    MONTHLY(3, "每月", true, false),
    CUSTOM(99, "自定义", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static Map<String, Object> getMation(Integer type) {
        if (type == null) {
            return MapUtil.newHashMap();
        }
        for (MaintenancePlanFrequency bean : MaintenancePlanFrequency.values()) {
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
