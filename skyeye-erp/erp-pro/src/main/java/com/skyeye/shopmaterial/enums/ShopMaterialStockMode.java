/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ShopMaterialStockMode
 * @Description: 门店商品库存模式
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopMaterialStockMode implements SkyeyeEnumClass {

    NORMAL(1, "普通商品库存", true, true),
    DEPOT_LINK(2, "商品关联仓库存", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
