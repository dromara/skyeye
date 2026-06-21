/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.classenum;

import cn.hutool.core.map.MapUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionRunStatus
 * @Description: 设备巡检-设备运行状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentInspectionRunStatus implements SkyeyeEnumClass {

    NORMAL(1, "正常运行", true, true),
    DEGRADED(2, "带病运行", true, false),
    UNDER_REPAIR(3, "维修中", true, false),
    STANDBY(4, "备用", true, false),
    DISABLED(5, "停用", true, false),
    SCRAPPED(6, "报废", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static Map<String, Object> getMation(Integer type) {
        for (EquipmentInspectionRunStatus bean : EquipmentInspectionRunStatus.values()) {
            if (type != null && type.equals(bean.getKey())) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", bean.getKey());
                result.put("name", bean.getValue());
                return result;
            }
        }
        return MapUtil.newHashMap();
    }

}