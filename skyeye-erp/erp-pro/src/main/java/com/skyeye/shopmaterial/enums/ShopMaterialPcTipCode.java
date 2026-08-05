/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ShopMaterialPcTipCode
 * @Description: PC 商城提示编码（接口 returnCode / returnMessage，供前端识别展示）
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/5
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopMaterialPcTipCode implements SkyeyeEnumClass {

    /**
     * 商品上架方式仅同城配送，PC 无定位不可下单，提示前往移动端
     */
    LOCAL_DELIVERY_ONLY(3003, "该商品仅适合同城配送，请前往移动端使用", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
