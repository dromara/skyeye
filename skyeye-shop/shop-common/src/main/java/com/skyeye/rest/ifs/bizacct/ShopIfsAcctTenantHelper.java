package com.skyeye.rest.ifs.bizacct;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.tenant.TenantTypeEnum;

/**
 * 商城业财落账租户：个人店（或门店无租户）统一进 {@link TenantTypeEnum#SHOP}，企业店用门店所属租户。
 */
public final class ShopIfsAcctTenantHelper {

    private ShopIfsAcctTenantHelper() {
    }

    /**
     * @param personalStore 是否个人门店
     * @param storeTenantId 门店表 tenant_id
     */
    public static String resolve(boolean personalStore, String storeTenantId) {
        if (personalStore || StrUtil.isBlank(storeTenantId)) {
            return TenantTypeEnum.SHOP.getCode();
        }
        return storeTenantId;
    }
}
