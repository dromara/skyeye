package com.skyeye.finance.report.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.report.service.FinancialReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "财务报表", tags = "财务报表", modelName = "财务中枢")
public class FinancialReportController {

    @Autowired
    private FinancialReportService financialReportService;

    @ApiOperation(id = "queryBalanceSheet", value = "资产负债表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "periodCode", name = "periodCode", value = "期间", required = "required")
    })
    @RequestMapping("/post/FinancialReportController/queryBalanceSheet")
    public void queryBalanceSheet(InputObject inputObject, OutputObject outputObject) {
        financialReportService.queryBalanceSheet(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIncomeStatement", value = "利润表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "periodCode", name = "periodCode", value = "期间", required = "required")
    })
    @RequestMapping("/post/FinancialReportController/queryIncomeStatement")
    public void queryIncomeStatement(InputObject inputObject, OutputObject outputObject) {
        financialReportService.queryIncomeStatement(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCashFlow", value = "现金流量表(间接法简版)", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "periodCode", name = "periodCode", value = "期间", required = "required")
    })
    @RequestMapping("/post/FinancialReportController/queryCashFlow")
    public void queryCashFlow(InputObject inputObject, OutputObject outputObject) {
        financialReportService.queryCashFlow(inputObject, outputObject);
    }

    @ApiOperation(id = "periodProfitClose", value = "期末损益结转", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "periodCode", name = "periodCode", value = "期间", required = "required")
    })
    @RequestMapping("/post/FinancialReportController/periodProfitClose")
    public void periodProfitClose(InputObject inputObject, OutputObject outputObject) {
        financialReportService.periodProfitClose(inputObject, outputObject);
    }

    @ApiOperation(id = "queryGrossProfit", value = "毛利分析(销售出库)", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "periodCode", name = "periodCode", value = "期间", required = "required")
    })
    @RequestMapping("/post/FinancialReportController/queryGrossProfit")
    public void queryGrossProfit(InputObject inputObject, OutputObject outputObject) {
        financialReportService.queryGrossProfit(inputObject, outputObject);
    }
}
