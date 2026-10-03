package com.skyeye.finance.report.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.report.service.CreditControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "信用控制", tags = "信用控制", modelName = "财务中枢")
public class CreditControlController {

    @Autowired
    private CreditControlService creditControlService;

    @ApiOperation(id = "checkCustomerCredit", value = "客户信用校验(财务反哺)", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "customerId", name = "customerId", value = "客户id", required = "required"),
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id"),
        @ApiImplicitParam(id = "periodCode", name = "periodCode", value = "期间"),
        @ApiImplicitParam(id = "amount", name = "amount", value = "本次业务金额"),
        @ApiImplicitParam(id = "strict", name = "strict", value = "严格模式1=拦截")
    })
    @RequestMapping("/post/CreditControlController/checkCustomerCredit")
    public void checkCustomerCredit(InputObject inputObject, OutputObject outputObject) {
        creditControlService.checkCustomerCredit(inputObject, outputObject);
    }
}
