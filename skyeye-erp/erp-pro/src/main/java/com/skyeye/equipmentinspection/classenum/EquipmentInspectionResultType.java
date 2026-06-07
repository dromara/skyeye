/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EquipmentInspectionResultType
 * @Description: 设备巡检结果
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentInspectionResultType implements SkyeyeEnumClass {

    NORMAL(1, "正常", true, true),
    ABNORMAL(2, "异常", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
