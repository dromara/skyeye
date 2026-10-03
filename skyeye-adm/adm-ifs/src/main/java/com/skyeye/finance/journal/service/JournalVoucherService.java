package com.skyeye.finance.journal.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.journal.entity.JournalVoucher;

public interface JournalVoucherService extends SkyeyeBusinessService<JournalVoucher> {

    void approveVoucher(InputObject inputObject, OutputObject outputObject);

    void postVoucher(InputObject inputObject, OutputObject outputObject);

    void reverseVoucher(InputObject inputObject, OutputObject outputObject);

    JournalVoucher createAndOptionallyPost(JournalVoucher voucher, boolean autoPost);

    void queryDetailLedger(InputObject inputObject, OutputObject outputObject);
}
