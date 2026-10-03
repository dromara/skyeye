package com.skyeye.finance.event.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum BizAcctEventType implements SkyeyeEnumClass {

    PURCHASE_IN("purchaseIn", "采购入库暂估", true, false),
    PURCHASE_INVOICE("purchaseInvoice", "采购发票校验", true, false),
    PURCHASE_RETURN("purchaseReturn", "采购退货", true, false),
    SALES_OUT("salesOut", "销售出库", true, false),
    SALES_RETURN("salesReturn", "销售退货", true, false),
    PAYABLE_CONFIRM("payableConfirm", "应付确认", true, false),
    PAYMENT("payment", "供应商付款", true, false),
    RECEIVABLE_CONFIRM("receivableConfirm", "应收确认", true, false),
    RECEIPT("receipt", "客户回款", true, false),
    TRANSFER("transfer", "库存调拨", true, false),
    STOCKTAKE("stocktake", "盘点盈亏", true, false),
    OTHER_IN("otherIn", "其他入库", true, false),
    OTHER_OUT("otherOut", "其他出库", true, false),
    PROD_PICK("prodPick", "生产领料", true, false),
    PROD_RETURN("prodReturn", "生产退料", true, false),
    PROD_FINISH("prodFinish", "完工入库", true, false),
    MFG_OVERHEAD("mfgOverhead", "制造费用归集", true, false),
    EXPENSE_REIMBURSE("expenseReimburse", "费用报销", true, false),
    LOAN_BORROW("loanBorrow", "借款", true, false),
    LOAN_REPAY("loanRepay", "还款", true, false),
    MANUAL("manual", "手工凭证", true, true),
    PERIOD_CLOSE("periodClose", "期末损益结转", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

}
