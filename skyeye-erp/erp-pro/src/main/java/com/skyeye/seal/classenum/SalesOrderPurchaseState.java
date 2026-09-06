/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.seal.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: SalesOrderPurchaseState
 * @Description: 销售订单采购状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/31 11:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SalesOrderPurchaseState implements SkyeyeEnumClass {

    NOT_NEED(1, "无需采购", "purple", true, true),
    NEED(2, "待采购", "blue", true, false),
    PARTIAL(3, "部分采购", "orange", true, false),
    COMPLATE(4, "全部采购", "green", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
