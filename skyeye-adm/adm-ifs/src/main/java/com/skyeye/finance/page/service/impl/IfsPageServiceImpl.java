package com.skyeye.finance.page.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.feeapplication.service.FeeApplicationService;
import com.skyeye.finance.costdomain.service.CostDomainService;
import com.skyeye.finance.event.service.BizAcctEventService;
import com.skyeye.finance.invaccounting.service.InvAcctBillService;
import com.skyeye.finance.journal.service.JournalVoucherService;
import com.skyeye.finance.ledger.service.SubjectBalanceService;
import com.skyeye.finance.page.service.IfsPageService;
import com.skyeye.finance.period.service.AccountPeriodService;
import com.skyeye.finance.template.service.VoucherTemplateService;
import com.skyeye.subject.service.IfsAccountSubjectService;
import com.skyeye.loan.entity.LoanBorrow;
import com.skyeye.loan.entity.LoanRepay;
import com.skyeye.loan.service.LoanBorrowService;
import com.skyeye.loan.service.LoanRepayService;
import com.skyeye.receivepayment.service.ReceivePaymentService;
import com.skyeye.reimbursement.entity.Reimbursement;
import com.skyeye.reimbursement.service.ReimbursementService;
import com.skyeye.rest.crm.receivable.service.ICrmReceivableService;
import com.skyeye.rest.erp.payable.service.IErpPayableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongSupplier;

/**
 * 财务流程板节点数量：主账簿/收付/存货为当前租户总笔数；
 * G02/G03/G04（报销、借款、还款）仅统计当前登录人自己申报的单据，与列表「我创建的」口径一致。
 * F05/F06/J02 为操作入口，前端 hideCount 不展示数字，本接口不返回这些 key；
 * H02/H03 分别通过 Feign 调用 CRM/ERP 数量接口。
 */
@Service
public class IfsPageServiceImpl implements IfsPageService {

    @Autowired
    private IfsAccountSubjectService ifsAccountSubjectService;

    @Autowired
    private AccountPeriodService accountPeriodService;

    @Autowired
    private VoucherTemplateService voucherTemplateService;

    @Autowired
    private BizAcctEventService bizAcctEventService;

    @Autowired
    private JournalVoucherService journalVoucherService;

    @Autowired
    private SubjectBalanceService subjectBalanceService;

    @Autowired
    private FeeApplicationService feeApplicationService;

    @Autowired
    private ReimbursementService reimbursementService;

    @Autowired
    private LoanBorrowService loanBorrowService;

    @Autowired
    private LoanRepayService loanRepayService;

    @Autowired
    private ReceivePaymentService receivePaymentService;

    @Autowired
    private ICrmReceivableService iCrmReceivableService;

    @Autowired
    private IErpPayableService iErpPayableService;

    @Autowired
    private CostDomainService costDomainService;

    @Autowired
    private InvAcctBillService invAcctBillService;

    @Override
    public void queryProcessFlowCount(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> bean = new HashMap<>();
        // 主账簿
        bean.put("F00", safeCount(() -> ifsAccountSubjectService.count()));
        bean.put("F01", safeCount(() -> accountPeriodService.count()));
        bean.put("F02", safeCount(() -> voucherTemplateService.count()));
        bean.put("F07", safeCount(() -> bizAcctEventService.count()));
        bean.put("F03", safeCount(() -> journalVoucherService.count()));
        bean.put("F04", safeCount(() -> subjectBalanceService.count()));
        // 费用 / 借款
        bean.put("G01", safeCount(() -> feeApplicationService.count()));
        bean.put("G02", safeCount(() -> countMine(reimbursementService, Reimbursement::getCreateId)));
        bean.put("G03", safeCount(() -> countMine(loanBorrowService, LoanBorrow::getCreateId)));
        bean.put("G04", safeCount(() -> countMine(loanRepayService, LoanRepay::getCreateId)));
        // 收付：收付款在 IFS；应收/应付走 CRM/ERP
        bean.put("H01", safeCount(() -> receivePaymentService.count()));
        bean.put("H02", safeCount(() -> iCrmReceivableService.queryReceivableCount()));
        bean.put("H03", safeCount(() -> iErpPayableService.queryPayableCount()));
        // 存货
        bean.put("J00", safeCount(() -> costDomainService.count()));
        bean.put("J01", safeCount(() -> invAcctBillService.count()));
        outputObject.setBean(bean);
    }

    private <T> long countMine(SkyeyeBusinessService<T> service, SFunction<T, ?> userColumn) {
        String userId = currentUserId();
        if (userId.isEmpty()) {
            return 0L;
        }
        QueryWrapper<T> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(userColumn), userId);
        return service.count(qw);
    }

    private String currentUserId() {
        Map<String, Object> user = InputObject.getLogParamsStatic();
        if (user == null || user.get("id") == null) {
            return "";
        }
        return user.get("id").toString();
    }

    private long safeCount(LongSupplier counter) {
        try {
            return counter.getAsLong();
        } catch (Exception ex) {
            // 表不存在或跨服务异常时不阻断整板
            return 0L;
        }
    }
}
