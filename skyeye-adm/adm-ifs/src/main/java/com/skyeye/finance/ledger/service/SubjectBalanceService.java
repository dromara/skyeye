package com.skyeye.finance.ledger.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.journal.entity.JournalEntry;
import com.skyeye.finance.journal.entity.JournalVoucher;
import com.skyeye.finance.ledger.entity.SubjectBalance;

import java.util.List;

public interface SubjectBalanceService extends SkyeyeBusinessService<SubjectBalance> {

    void applyPosting(JournalVoucher voucher, List<JournalEntry> entries);

    void applyReversal(JournalVoucher voucher, List<JournalEntry> entries);

    void querySubjectBalanceList(InputObject inputObject, OutputObject outputObject);

    void queryTrialBalance(InputObject inputObject, OutputObject outputObject);
}
