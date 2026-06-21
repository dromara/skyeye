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
 * @ClassName: EquipmentInspectionAbnormalFlag
 * @Description: 设备巡检-是否异常
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentInspectionAbnormalFlag implements SkyeyeEnumClass {

    NO(0, "否", true, true),
    YES(1, "是", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static Map<String, Object> getMation(Integer type) {
        for (EquipmentInspectionAbnormalFlag bean : EquipmentInspectionAbnormalFlag.values()) {
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