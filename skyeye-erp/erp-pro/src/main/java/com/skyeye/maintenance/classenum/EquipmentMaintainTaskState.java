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
 * @Description: 设备保养任务状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentMaintainTaskState implements SkyeyeEnumClass {

    PENDING(1, "待执行", "#1890ff", true, true),
    IN_PROGRESS(2, "执行中", "#faad14", true, false),
    COMPLETED(3, "已完成", "#52c41a", true, false),
    CANCELLED(4, "已取消", "#faad14", true, false),
    TIMEOUT(5, "已超时", "#faad14", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

    public static Map<String, Object> getMation(Integer type) {
        if (type == null) {
            return MapUtil.newHashMap();
        }
        for (EquipmentMaintainTaskState bean : EquipmentMaintainTaskState.values()) {
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
