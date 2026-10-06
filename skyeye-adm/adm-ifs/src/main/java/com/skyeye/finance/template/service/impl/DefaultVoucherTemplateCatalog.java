package com.skyeye.finance.template.service.impl;

import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.subject.classenum.AmountDirection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 默认凭证模板目录（工业企业示意科目）。科目按编码匹配，可多候选。
 */
final class DefaultVoucherTemplateCatalog {

    private DefaultVoucherTemplateCatalog() {
    }

    static List<TemplateDef> all() {
        return Arrays.asList(
            def(BizAcctEventType.PAYMENT, "供应商付款",
                line(1, AmountDirection.BORROW, "amount", "{summary}", true, false, "220201", "2202"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, false, "1002", "1001")),
            def(BizAcctEventType.RECEIPT, "客户回款",
                line(1, AmountDirection.BORROW, "amount", "{summary}", false, false, "1002", "1001"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, true, "1122")),
            def(BizAcctEventType.EXPENSE_REIMBURSE, "费用报销",
                line(1, AmountDirection.BORROW, "amount", "{summary}", false, false, "6602", "6601"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, false, "1002", "1001")),
            def(BizAcctEventType.LOAN_BORROW, "借款",
                line(1, AmountDirection.BORROW, "amount", "{summary}", false, false, "1221", "122101", "1133"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, false, "1002", "1001")),
            def(BizAcctEventType.LOAN_REPAY, "还款",
                line(1, AmountDirection.BORROW, "amount", "{summary}", false, false, "1002", "1001"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, false, "1221", "122101", "1133")),
            def(BizAcctEventType.PURCHASE_IN, "采购入库暂估",
                line(1, AmountDirection.BORROW, "amount", "{summary}", false, false, "1405", "1403"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", true, false, "2202", "220201")),
            // 票到冲暂估：无税额时 taxAmount=0 该行跳过，仍借暂估贷正式应付
            def(BizAcctEventType.PURCHASE_INVOICE, "采购发票校验",
                line(1, AmountDirection.BORROW, "amountExTax", "{summary}", true, false, "2202"),
                line(2, AmountDirection.BORROW, "taxAmount", "{summary}", false, false, "222101", "2221"),
                line(3, AmountDirection.LOAN, "amount", "{summary}", true, false, "220201", "2202")),
            def(BizAcctEventType.PURCHASE_RETURN, "采购退货",
                line(1, AmountDirection.BORROW, "amount", "{summary}", true, false, "2202", "220201"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, false, "1405", "1403")),
            def(BizAcctEventType.SALES_OUT, "销售出库",
                line(1, AmountDirection.BORROW, "amount", "{summary}", false, true, "1122"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, false, "6001")),
            def(BizAcctEventType.SALES_RETURN, "销售退货",
                line(1, AmountDirection.BORROW, "amount", "{summary}", false, false, "6001"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, true, "1122")),
            def(BizAcctEventType.OTHER_IN, "其他入库",
                line(1, AmountDirection.BORROW, "amount", "{summary}", false, false, "1405", "1403"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, false, "6301", "6001")),
            def(BizAcctEventType.OTHER_OUT, "其他出库",
                line(1, AmountDirection.BORROW, "amount", "{summary}", false, false, "6711", "6602", "6401"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, false, "1405", "1403")),
            def(BizAcctEventType.PROD_PICK, "生产领料",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", false, false, "5001"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", false, false, "1403", "1405")),
            def(BizAcctEventType.PROD_RETURN, "生产退料",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", false, false, "1403", "1405"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", false, false, "5001")),
            def(BizAcctEventType.PROD_FINISH, "完工入库",
                line(1, AmountDirection.BORROW, "amount", "{summary}", false, false, "1405", "1403"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, false, "5001")),
            def(BizAcctEventType.MFG_OVERHEAD, "制造费用归集",
                line(1, AmountDirection.BORROW, "amount", "{summary}", false, false, "5001"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", false, false, "5101", "6602"))
        );
    }

    private static TemplateDef def(BizAcctEventType type, String name, LineDef... lines) {
        return new TemplateDef(type.getKey(), name, Arrays.asList(lines));
    }

    private static LineDef line(int lineNo, AmountDirection direction, String amountExpr, String summaryTpl,
                                boolean auxSupplier, boolean auxCustomer, String... subjectNums) {
        return new LineDef(lineNo, direction.getKey(), amountExpr, summaryTpl, auxSupplier, auxCustomer, subjectNums);
    }

    @Getter
    @AllArgsConstructor
    static class TemplateDef {
        private final String eventType;
        private final String name;
        private final List<LineDef> lines;
    }

    @Getter
    @AllArgsConstructor
    static class LineDef {
        private final Integer lineNo;
        private final Integer direction;
        private final String amountExpr;
        private final String summaryTpl;
        private final boolean auxSupplier;
        private final boolean auxCustomer;
        private final String[] subjectNums;
    }
}
