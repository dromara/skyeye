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
                line(1, AmountDirection.BORROW, "amount", "{summary}", "220201", "2202"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1002", "1001")),
            def(BizAcctEventType.PAYABLE_CONFIRM, "应付确认",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "2202", "220201")),
            def(BizAcctEventType.RECEIVABLE_CONFIRM, "应收确认",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "1122"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "6001")),
            def(BizAcctEventType.RECEIPT, "客户回款",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "1002", "1001"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1122")),
            def(BizAcctEventType.EXPENSE_REIMBURSE, "费用报销",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "6602", "6601"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1002", "1001")),
            // 费用申请审批通过：先挂账，报销付款时再冲其他应付款
            def(BizAcctEventType.EXPENSE_APPLY, "费用申请",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "6602", "6601"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "2241")),
            def(BizAcctEventType.LOAN_BORROW, "借款",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "1221", "122101", "1133"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1002", "1001")),
            def(BizAcctEventType.LOAN_REPAY, "还款",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "1002", "1001"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1221", "122101", "1133")),
            def(BizAcctEventType.PURCHASE_IN, "采购入库暂估",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "2202", "220201")),
            // 票到冲暂估：无税额时 taxAmount=0 该行跳过，仍借暂估贷正式应付
            def(BizAcctEventType.PURCHASE_INVOICE, "采购发票校验",
                line(1, AmountDirection.BORROW, "amountExTax", "{summary}", "2202"),
                line(2, AmountDirection.BORROW, "taxAmount", "{summary}", "222101", "2221"),
                line(3, AmountDirection.LOAN, "amount", "{summary}", "220201", "2202")),
            def(BizAcctEventType.PURCHASE_RETURN, "采购退货",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "2202", "220201"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1405", "1403")),
            def(BizAcctEventType.SALES_OUT, "销售出库",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "6401"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "1405", "1403")),
            // 开票不再确认收入/应收，避免与应收事项或出库重复记 1122；无税额时事件跳过凭证
            def(BizAcctEventType.SALES_INVOICE, "销售开票",
                line(1, AmountDirection.BORROW, "taxAmount", "{summary}", "1122"),
                line(2, AmountDirection.LOAN, "taxAmount", "{summary}", "22210101", "2221")),
            def(BizAcctEventType.SALES_RETURN, "销售退货",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "6401")),
            def(BizAcctEventType.OTHER_IN, "其他入库",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "6301", "6001")),
            def(BizAcctEventType.OTHER_OUT, "其他出库",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "6711", "6602", "6401"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1405", "1403")),
            def(BizAcctEventType.PROD_PICK, "生产领料",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "5001"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "1403", "1405")),
            def(BizAcctEventType.PROD_RETURN, "生产退料",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "1403", "1405"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "5001")),
            def(BizAcctEventType.PROD_FINISH, "完工入库",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "5001")),
            def(BizAcctEventType.MFG_OVERHEAD, "制造费用归集",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "5001"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "5101", "6602")),
            def(BizAcctEventType.STOCKTAKE, "盘点盈亏",
                line(1, AmountDirection.BORROW, "profitAmount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "profitAmount", "{summary}", "1901"),
                line(3, AmountDirection.BORROW, "lossAmount", "{summary}", "1901"),
                line(4, AmountDirection.LOAN, "lossAmount", "{summary}", "1405", "1403")),
            def(BizAcctEventType.TRANSFER, "库存调拨",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1405", "1403")),
            def(BizAcctEventType.RETAIL_OUT, "零售出库",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "6401"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "1405", "1403")),
            def(BizAcctEventType.RETAIL_RETURN, "零售退货",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "6401")),
            def(BizAcctEventType.STORE_PICK, "门店申领",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "6601", "6602", "6711"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "1405", "1403")),
            def(BizAcctEventType.STORE_RETURN, "门店退货",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "6601", "6602", "6301")),
            def(BizAcctEventType.STORE_MATERIAL_RETURN, "门店物料退货",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "6601", "6602", "5001")),
            def(BizAcctEventType.SEAL_PICK, "配件申领",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "6601", "6401", "6711"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "1405", "1403")),
            def(BizAcctEventType.MATERIAL_RETURN, "物料退货",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "1403", "1405"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "5001", "6602")),
            def(BizAcctEventType.EXCHANGE_OUT, "采购换货出库",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "2202", "220201"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1405", "1403")),
            def(BizAcctEventType.EXCHANGE_IN, "销售换货入库",
                line(1, AmountDirection.BORROW, "costAmount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "costAmount", "{summary}", "6401")),
            def(BizAcctEventType.STOCK_LOAN, "借出出库",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "1221", "122101"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1405", "1403")),
            def(BizAcctEventType.STOCK_RETURN, "归还入库",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "1405", "1403"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1221", "122101")),
            def(BizAcctEventType.SHOP_SETTLE, "商城结算可提现",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "6601", "6401"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "2241", "2202")),
            def(BizAcctEventType.SHOP_WITHDRAW, "商城提现打款",
                line(1, AmountDirection.BORROW, "amount", "{summary}", "2241", "2202"),
                line(2, AmountDirection.LOAN, "amount", "{summary}", "1002", "1001"))
        );
    }

    private static TemplateDef def(BizAcctEventType type, String name, LineDef... lines) {
        return new TemplateDef(type.getKey(), name, Arrays.asList(lines));
    }

    private static LineDef line(int lineNo, AmountDirection direction, String amountExpr, String summaryTpl,
                                String... subjectNums) {
        return new LineDef(lineNo, direction.getKey(), amountExpr, summaryTpl, subjectNums);
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
        private final String[] subjectNums;
    }
}
