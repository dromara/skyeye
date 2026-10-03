package com.skyeye.finance.period.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.period.entity.AccountPeriod;

public interface AccountPeriodService extends SkyeyeBusinessService<AccountPeriod> {

    AccountPeriod getOpenPeriod(String setOfBooksId, String periodCode);

    void assertPeriodOpen(String setOfBooksId, String periodCode);

    void closeBizPeriod(InputObject inputObject, OutputObject outputObject);

    void closeFinPeriod(InputObject inputObject, OutputObject outputObject);

    void reopenPeriod(InputObject inputObject, OutputObject outputObject);

    void initYearPeriods(InputObject inputObject, OutputObject outputObject);
}
