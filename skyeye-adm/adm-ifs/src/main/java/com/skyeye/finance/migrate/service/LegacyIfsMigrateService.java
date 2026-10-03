package com.skyeye.finance.migrate.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface LegacyIfsMigrateService {

    void migrateReimbursementToVoucher(InputObject inputObject, OutputObject outputObject);

    void migrateLoanBorrowToVoucher(InputObject inputObject, OutputObject outputObject);

    void migrateLoanRepayToVoucher(InputObject inputObject, OutputObject outputObject);
}
