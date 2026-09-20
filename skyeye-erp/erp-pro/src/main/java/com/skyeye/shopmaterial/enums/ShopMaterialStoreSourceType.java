/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ShopMaterialStoreSourceType
 * @Description: 个人门店商品来源（挂在门店商品关系上，不是企业门店自制标识）
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/20
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopMaterialStoreSourceType implements SkyeyeEnumClass {

    PERSONAL_SELF(1, "自建", true, true),
    PLATFORM(2, "平台货源", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
