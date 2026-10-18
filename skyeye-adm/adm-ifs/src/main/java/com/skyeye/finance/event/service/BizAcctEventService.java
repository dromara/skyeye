package com.skyeye.finance.event.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exception.PeriodClosedException;
import com.skyeye.finance.event.entity.BizAcctEvent;

import java.util.Map;

public interface BizAcctEventService extends SkyeyeBusinessService<BizAcctEvent> {

    void acceptEvent(InputObject inputObject, OutputObject outputObject);

    Map<String, Object> acceptEvent(Map<String, Object> params);

    void retryFailedEvent(InputObject inputObject, OutputObject outputObject);

    /**
     * 受理业财事件；会计期间已关闭时抛 {@link PeriodClosedException}，供审批回调回滚业务。
     */
    default void acceptEventRequireOpen(Map<String, Object> params) {
        Map<String, Object> result = acceptEvent(params);
        if (result != null && PeriodClosedException.ERROR_CODE.equals(String.valueOf(result.get("errorCode")))) {
            Object msg = result.get("errorMsg");
            throw new PeriodClosedException(msg == null ? "会计期间已关闭" : msg.toString());
        }
    }
}
