/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.classenum;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import com.skyeye.other.service.impl.OtherOutLetsServiceImpl;
import com.skyeye.pick.service.impl.PatchOutLetServiceImpl;
import com.skyeye.pick.service.impl.RequisitionOutLetServiceImpl;
import com.skyeye.product.service.impl.ProductLeadOutStockServiceImpl;
import com.skyeye.purchase.service.impl.PurchaseExchangesServiceImpl;
import com.skyeye.purchase.service.impl.PurchaseReturnsServiceImpl;
import com.skyeye.retail.service.impl.RetailOutLetServiceImpl;
import com.skyeye.seal.service.impl.SalesOutLetServiceImpl;
import com.skyeye.shop.service.impl.ShopOutLetsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: DepotOutFromType
 * @Description: 仓库出库单来源单据类型
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 10:58
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DepotOutFromType implements SkyeyeEnumClass {

    PURCHASE_RETURNS(1, "采购退货单", PurchaseReturnsServiceImpl.class.getName(), true, false),
    SEAL_OUTLET(2, "销售出库单", SalesOutLetServiceImpl.class.getName(), true, false),
    RETAIL_OUTLET(3, "零售出库单", RetailOutLetServiceImpl.class.getName(), true, false),
    OTHER_OUTLET(4, "其他出库单", OtherOutLetsServiceImpl.class.getName(), true, false),
    REQUISITION_OUTLET(5, "领料出库单", RequisitionOutLetServiceImpl.class.getName(), true, false),
    PATCH_OUTLET(6, "补料出库单", PatchOutLetServiceImpl.class.getName(), true, false),
    SEAL_APPLY(7, "配件申领单", "com.skyeye.accessory.service.impl.SealApplyServiceImpl", true, false),
    SHOP_OUTLET(8, "门店申领单", ShopOutLetsServiceImpl.class.getName(), true, false),
    PURCHASE_EXCHANGE(9, "采购换货单", PurchaseExchangesServiceImpl.class.getName(), true, false),
    LOANOUT(10, "借出出库单", ProductLeadOutStockServiceImpl.class.getName(), true, false),
    EQUIPMENT_SPARE_PART_APPLY(11, "设备备件申领单", "com.skyeye.sparepart.service.impl.EquipmentSparePartApplyServiceImpl", true, false);

    private Integer key;

    private String value;

    private String idKey;

    private Boolean show;

    private Boolean isDefault;

    public static Integer getItemKey(String idKey) {
        for (DepotOutFromType bean : DepotOutFromType.values()) {
            if (StrUtil.equals(idKey, bean.getIdKey())) {
                return bean.getKey();
            }
        }
        return null;
    }

    public static String getItemIdKey(Integer key) {
        for (DepotOutFromType bean : DepotOutFromType.values()) {
            if (key == bean.getKey()) {
                return bean.getIdKey();
            }
        }
        return StrUtil.EMPTY;
    }

    public static List<String> getAllIdKeys() {
        return Arrays.stream(DepotOutFromType.values()).map(DepotOutFromType::getIdKey).collect(Collectors.toList());
    }

}
