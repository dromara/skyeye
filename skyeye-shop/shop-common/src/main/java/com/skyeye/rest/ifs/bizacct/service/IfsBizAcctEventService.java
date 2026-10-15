package com.skyeye.rest.ifs.bizacct.service;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.tenant.context.TenantContext;

import java.util.Map;

public interface IfsBizAcctEventService {

    Map<String, Object> acceptBizAcctEvent(Map<String, Object> map);

    /**
     * 业务完成后推财务事件；失败由财务事件台记录，不打断业务。
     */
    default void pushIfsBizAcctEvent(Map<String, Object> map) {
        try {
            acceptBizAcctEvent(map);
        } catch (Exception ignored) {
        }
    }

    /**
     * 按指定租户推财务事件（临时切换 TenantContext，供 Feign 透传；结束后还原）。
     * 个人店请传 {@code TenantTypeEnum.SHOP}；企业店传门店所属租户。
     */
    default void pushIfsBizAcctEvent(Map<String, Object> map, String tenantId) {
        if (StrUtil.isBlank(tenantId)) {
            pushIfsBizAcctEvent(map);
            return;
        }
        String oldTenantId = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(tenantId);
            pushIfsBizAcctEvent(map);
        } finally {
            TenantContext.setTenantId(oldTenantId);
        }
    }
}
