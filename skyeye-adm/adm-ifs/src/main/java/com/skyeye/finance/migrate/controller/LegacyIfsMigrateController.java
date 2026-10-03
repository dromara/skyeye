package com.skyeye.finance.migrate.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.migrate.service.LegacyIfsMigrateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "旧财务迁移记账", tags = "旧财务迁移记账", modelName = "财务中枢")
public class LegacyIfsMigrateController {

    @Autowired
    private LegacyIfsMigrateService legacyIfsMigrateService;

    @ApiOperation(id = "migrateReimbursementToVoucher", value = "报销单生成会计凭证", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "报销单id", required = "required"),
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id")
    })
    @RequestMapping("/post/LegacyIfsMigrateController/migrateReimbursementToVoucher")
    public void migrateReimbursementToVoucher(InputObject inputObject, OutputObject outputObject) {
        legacyIfsMigrateService.migrateReimbursementToVoucher(inputObject, outputObject);
    }

    @ApiOperation(id = "migrateLoanBorrowToVoucher", value = "借款单生成会计凭证", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "借款单id", required = "required"),
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id")
    })
    @RequestMapping("/post/LegacyIfsMigrateController/migrateLoanBorrowToVoucher")
    public void migrateLoanBorrowToVoucher(InputObject inputObject, OutputObject outputObject) {
        legacyIfsMigrateService.migrateLoanBorrowToVoucher(inputObject, outputObject);
    }

    @ApiOperation(id = "migrateLoanRepayToVoucher", value = "还款单生成会计凭证", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "还款单id", required = "required"),
        @ApiImplicitParam(id = "setOfBooksId", name = "setOfBooksId", value = "账套id")
    })
    @RequestMapping("/post/LegacyIfsMigrateController/migrateLoanRepayToVoucher")
    public void migrateLoanRepayToVoucher(InputObject inputObject, OutputObject outputObject) {
        legacyIfsMigrateService.migrateLoanRepayToVoucher(inputObject, outputObject);
    }
}
