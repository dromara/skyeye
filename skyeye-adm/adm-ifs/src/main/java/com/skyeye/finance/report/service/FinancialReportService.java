package com.skyeye.finance.report.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface FinancialReportService {

    void queryBalanceSheet(InputObject inputObject, OutputObject outputObject);

    void queryIncomeStatement(InputObject inputObject, OutputObject outputObject);

    void queryCashFlow(InputObject inputObject, OutputObject outputObject);

    void periodProfitClose(InputObject inputObject, OutputObject outputObject);

    void queryGrossProfit(InputObject inputObject, OutputObject outputObject);
}
