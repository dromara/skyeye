package com.skyeye.finance.event.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.event.entity.BizAcctEvent;

import java.util.Map;

public interface BizAcctEventService extends SkyeyeBusinessService<BizAcctEvent> {

    void acceptEvent(InputObject inputObject, OutputObject outputObject);

    Map<String, Object> acceptEvent(Map<String, Object> params);

    void retryFailedEvent(InputObject inputObject, OutputObject outputObject);
}
