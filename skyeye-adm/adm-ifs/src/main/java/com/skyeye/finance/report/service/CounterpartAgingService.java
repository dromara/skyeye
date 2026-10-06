package com.skyeye.finance.report.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.report.entity.CounterpartAging;

public interface CounterpartAgingService extends SkyeyeBusinessService<CounterpartAging> {

    void queryAging(InputObject inputObject, OutputObject outputObject);

    void queryStatement(InputObject inputObject, OutputObject outputObject);
}
