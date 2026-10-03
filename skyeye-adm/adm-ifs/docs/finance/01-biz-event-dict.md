# 标准业务会计事项字典

| 编码 | 名称 | 来源域 | 驱动单据 | 是否存货核算 |
|------|------|--------|----------|--------------|
| PURCHASE_IN | 采购入库（暂估） | ERP | 采购入库单 | 是 |
| PURCHASE_INVOICE | 采购发票校验 | ERP | 供应商发票 | 否 |
| PURCHASE_RETURN | 采购退货 | ERP | 采购退货单 | 是 |
| SALES_OUT | 销售出库（收入+成本） | ERP | 销售出库单 | 是 |
| SALES_RETURN | 销售退货 | ERP | 销售退货单 | 是 |
| PAYABLE_CONFIRM | 应付确认 | ERP | 应付事项 | 否 |
| PAYMENT | 供应商付款 | ERP | 付款单 | 否 |
| RECEIVABLE_CONFIRM | 应收确认 | CRM | 应收事项 | 否 |
| RECEIPT | 客户回款 | CRM | 回款单 | 否 |
| TRANSFER | 库存调拨 | ERP | 调拨单 | 是 |
| STOCKTAKE | 盘点盈亏 | ERP | 盘点单 | 是 |
| OTHER_IN | 其他入库 | ERP | 其他入库 | 是 |
| OTHER_OUT | 其他出库 | ERP | 其他出库 | 是 |
| PROD_PICK | 生产领料 | ERP | 领料单 | 是 |
| PROD_RETURN | 生产退料 | ERP | 退料单 | 是 |
| PROD_FINISH | 完工入库 | ERP | 加工入库 | 是 |
| MFG_OVERHEAD | 制造费用归集 | ERP | 费用归集 | 否 |
| EXPENSE_REIMBURSE | 费用报销 | IFS | 报销单 | 否 |
| LOAN_BORROW | 借款 | IFS | 借款单 | 否 |
| LOAN_REPAY | 还款 | IFS | 还款单 | 否 |
| MANUAL | 手工凭证 | IFS | 手工录入 | 否 |
| PERIOD_CLOSE | 期末损益结转 | IFS | 结账 | 否 |
