/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.classenum;

import cn.hutool.core.map.MapUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备维修单来源单据类型（保养/巡检/点检可转维修）
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentRepairFromType implements SkyeyeEnumClass {

    MAINTAIN_ORDER(1, "保养任务", true, false),
    INSPECTION_TASK(2, "巡检任务", true, false),
    CHECK_ORDER(3, "点检单", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static Map<String, Object> getMation(Integer type) {
        if (type == null) {
            return MapUtil.newHashMap();
        }
        for (EquipmentRepairFromType bean : EquipmentRepairFromType.values()) {
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
