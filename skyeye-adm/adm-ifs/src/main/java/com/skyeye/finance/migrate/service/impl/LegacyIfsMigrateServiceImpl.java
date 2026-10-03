package com.skyeye.finance.migrate.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.finance.event.service.BizAcctEventService;
import com.skyeye.finance.migrate.service.LegacyIfsMigrateService;
import com.skyeye.loan.entity.LoanBorrow;
import com.skyeye.loan.entity.LoanRepay;
import com.skyeye.loan.service.LoanBorrowService;
import com.skyeye.loan.service.LoanRepayService;
import com.skyeye.reimbursement.entity.Reimbursement;
import com.skyeye.reimbursement.service.ReimbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 旧 IFS 费用/借款单据对接新凭证引擎。
 * 不改旧单据主流程，仅提供显式迁移记账入口（须配置对应 eventType 模板）。
 */
@Service
public class LegacyIfsMigrateServiceImpl implements LegacyIfsMigrateService {

    @Autowired
    private BizAcctEventService bizAcctEventService;

    @Autowired
    private ReimbursementService reimbursementService;

    @Autowired
    private LoanBorrowService loanBorrowService;

    @Autowired
    private LoanRepayService loanRepayService;

    /** 报销单 → EXPENSE_REIMBURSE */
    @Override
    public void migrateReimbursementToVoucher(InputObject inputObject, OutputObject outputObject) {
        String id = required(inputObject, "id");
        Reimbursement bean = reimbursementService.selectById(id);
        if (bean == null) {
            throw new CustomException("报销单不存在");
        }
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", BizAcctEventType.EXPENSE_REIMBURSE.getKey());
        event.put("sourceType", "IFS_REIMBURSEMENT");
        event.put("sourceId", bean.getId());
        event.put("sourceNo", bean.getOddNumber());
        event.put("amount", StrUtil.blankToDefault(bean.getPrice(), "0"));
        event.put("departmentId", bean.getDepartmentId());
        event.put("voucherDate", DateUtil.getYmdTimeAndToString());
        event.put("summary", "费用报销-" + bean.getOddNumber());
        event.put("setOfBooksId", inputObject.getParams().get("setOfBooksId"));
        outputObject.setBean(bizAcctEventService.acceptEvent(event));
    }

    /** 借款单 → LOAN_BORROW */
    @Override
    public void migrateLoanBorrowToVoucher(InputObject inputObject, OutputObject outputObject) {
        String id = required(inputObject, "id");
        LoanBorrow bean = loanBorrowService.selectById(id);
        if (bean == null) {
            throw new CustomException("借款单不存在");
        }
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", BizAcctEventType.LOAN_BORROW.getKey());
        event.put("sourceType", "IFS_LOAN_BORROW");
        event.put("sourceId", bean.getId());
        event.put("sourceNo", bean.getOddNumber());
        event.put("amount", StrUtil.blankToDefault(bean.getPrice(), "0"));
        event.put("voucherDate", DateUtil.getYmdTimeAndToString());
        event.put("summary", "借款-" + bean.getOddNumber());
        event.put("setOfBooksId", inputObject.getParams().get("setOfBooksId"));
        outputObject.setBean(bizAcctEventService.acceptEvent(event));
    }

    /** 还款单 → LOAN_REPAY */
    @Override
    public void migrateLoanRepayToVoucher(InputObject inputObject, OutputObject outputObject) {
        String id = required(inputObject, "id");
        LoanRepay bean = loanRepayService.selectById(id);
        if (bean == null) {
            throw new CustomException("还款单不存在");
        }
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", BizAcctEventType.LOAN_REPAY.getKey());
        event.put("sourceType", "IFS_LOAN_REPAY");
        event.put("sourceId", bean.getId());
        event.put("sourceNo", bean.getOddNumber());
        event.put("amount", StrUtil.blankToDefault(bean.getPrice(), "0"));
        event.put("voucherDate", DateUtil.getYmdTimeAndToString());
        event.put("summary", "还款-" + bean.getOddNumber());
        event.put("setOfBooksId", inputObject.getParams().get("setOfBooksId"));
        outputObject.setBean(bizAcctEventService.acceptEvent(event));
    }

    private String required(InputObject inputObject, String key) {
        Object v = inputObject.getParams().get(key);
        if (v == null || StrUtil.isBlank(v.toString())) {
            throw new CustomException("缺少参数：" + key);
        }
        return v.toString();
    }
}
