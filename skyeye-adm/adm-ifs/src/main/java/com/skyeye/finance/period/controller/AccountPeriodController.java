package com.skyeye.finance.period.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.period.entity.AccountPeriod;
import com.skyeye.finance.period.service.AccountPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "会计期间管理", tags = "会计期间管理", modelName = "财务中枢")
public class AccountPeriodController {

    @Autowired
    private AccountPeriodService accountPeriodService;

    @ApiOperation(id = "queryAccountPeriodList", value = "分页查询会计期间", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AccountPeriodController/queryAccountPeriodList")
    public void queryAccountPeriodList(InputObject inputObject, OutputObject outputObject) {
        accountPeriodService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAccountPeriod", value = "新增/编辑会计期间", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AccountPeriod.class)
    @RequestMapping("/post/AccountPeriodController/writeAccountPeriod")
    public void writeAccountPeriod(InputObject inputObject, OutputObject outputObject) {
        accountPeriodService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAccountPeriod", value = "删除会计期间", method = "DELETE", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AccountPeriodController/deleteAccountPeriod")
    public void deleteAccountPeriod(InputObject inputObject, OutputObject outputObject) {
        accountPeriodService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "initYearAccountPeriods", value = "初始化年度会计期间", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id", required = "required"),
        @ApiImplicitParam(id = "year", name = "year", value = "年度", required = "required")
    })
    @RequestMapping("/post/AccountPeriodController/initYearAccountPeriods")
    public void initYearAccountPeriods(InputObject inputObject, OutputObject outputObject) {
        accountPeriodService.initYearPeriods(inputObject, outputObject);
    }

    @ApiOperation(id = "closeBizAccountPeriod", value = "业务关账", method = "POST", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AccountPeriodController/closeBizAccountPeriod")
    public void closeBizAccountPeriod(InputObject inputObject, OutputObject outputObject) {
        accountPeriodService.closeBizPeriod(inputObject, outputObject);
    }

    @ApiOperation(id = "closeFinAccountPeriod", value = "财务关账", method = "POST", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AccountPeriodController/closeFinAccountPeriod")
    public void closeFinAccountPeriod(InputObject inputObject, OutputObject outputObject) {
        accountPeriodService.closeFinPeriod(inputObject, outputObject);
    }

    @ApiOperation(id = "reopenAccountPeriod", value = "反开账", method = "POST", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AccountPeriodController/reopenAccountPeriod")
    public void reopenAccountPeriod(InputObject inputObject, OutputObject outputObject) {
        accountPeriodService.reopenPeriod(inputObject, outputObject);
    }
}
