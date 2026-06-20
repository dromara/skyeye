/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 设备维修-个人备件库存出入库类型
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentUserStockPutOutType implements SkyeyeEnumClass {

    PUT(1, "入库", true, true),
    OUT(2, "出库", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
