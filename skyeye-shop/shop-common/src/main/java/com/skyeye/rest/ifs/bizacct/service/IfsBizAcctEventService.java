package com.skyeye.rest.ifs.bizacct.service;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.exception.PeriodClosedException;

import java.util.Map;

public interface IfsBizAcctEventService {

    Map<String, Object> acceptBizAcctEvent(Map<String, Object> map);

    /**
     * 业务完成后推财务事件；失败由财务事件台记录，不打断业务。
     * 会计期间已关闭时抛 {@link PeriodClosedException}，由业务侧回滚。
     */
    default void pushIfsBizAcctEvent(Map<String, Object> map) {
        try {
            Map<String, Object> result = acceptBizAcctEvent(map);
            throwIfPeriodClosed(result);
        } catch (PeriodClosedException ex) {
            throw ex;
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

    default void throwIfPeriodClosed(Map<String, Object> result) {
        if (result != null && PeriodClosedException.ERROR_CODE.equals(String.valueOf(result.get("errorCode")))) {
            Object msg = result.get("errorMsg");
            throw new PeriodClosedException(msg == null ? "会计期间已关闭" : msg.toString());
        }
    }
}
