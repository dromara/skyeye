package com.skyeye.rest.ifs.bizacct.service;

import com.skyeye.exception.PeriodClosedException;

import java.util.Map;

public interface IfsBizAcctEventService {

    Map<String, Object> acceptBizAcctEvent(Map<String, Object> map);

    /**
     * 业务审批后推财务事件；失败由财务事件台记录，不打断业务。
     * 会计期间已关闭时抛 {@link PeriodClosedException}，由业务侧回滚审批。
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

    default void throwIfPeriodClosed(Map<String, Object> result) {
        if (result != null && PeriodClosedException.ERROR_CODE.equals(String.valueOf(result.get("errorCode")))) {
            Object msg = result.get("errorMsg");
            throw new PeriodClosedException(msg == null ? "会计期间已关闭" : msg.toString());
        }
    }
}
