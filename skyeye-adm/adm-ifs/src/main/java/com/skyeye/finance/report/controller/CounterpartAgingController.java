package com.skyeye.finance.report.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.report.service.CounterpartAgingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "往来账龄", tags = "往来账龄", modelName = "财务中枢")
public class CounterpartAgingController {

    @Autowired
    private CounterpartAgingService counterpartAgingService;

    @ApiOperation(id = "queryCounterpartAging", value = "往来账龄", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "periodCode", name = "periodCode", value = "期间", required = "required")})
    @RequestMapping("/post/CounterpartAgingController/queryCounterpartAging")
    public void queryCounterpartAging(InputObject inputObject, OutputObject outputObject) {
        counterpartAgingService.queryAging(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCounterpartStatement", value = "往来对账单", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "periodCode", name = "periodCode", value = "期间", required = "required"),
        @ApiImplicitParam(id = "side", name = "side", value = "ar应收/ap应付"),
        @ApiImplicitParam(id = "partnerId", name = "partnerId", value = "客户或供应商id", required = "required")})
    @RequestMapping("/post/CounterpartAgingController/queryCounterpartStatement")
    public void queryCounterpartStatement(InputObject inputObject, OutputObject outputObject) {
        counterpartAgingService.queryStatement(inputObject, outputObject);
    }
}
