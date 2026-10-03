package com.skyeye.finance.ledger.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.ledger.service.SubjectBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "科目余额管理", tags = "科目余额管理", modelName = "财务中枢")
public class SubjectBalanceController {

    @Autowired
    private SubjectBalanceService subjectBalanceService;

    @ApiOperation(id = "querySubjectBalanceList", value = "查询科目余额表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "periodCode", name = "periodCode", value = "期间", required = "required"),
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id")
    })
    @RequestMapping("/post/SubjectBalanceController/querySubjectBalanceList")
    public void querySubjectBalanceList(InputObject inputObject, OutputObject outputObject) {
        subjectBalanceService.querySubjectBalanceList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTrialBalance", value = "试算平衡", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "periodCode", name = "periodCode", value = "期间", required = "required")
    })
    @RequestMapping("/post/SubjectBalanceController/queryTrialBalance")
    public void queryTrialBalance(InputObject inputObject, OutputObject outputObject) {
        subjectBalanceService.queryTrialBalance(inputObject, outputObject);
    }
}
