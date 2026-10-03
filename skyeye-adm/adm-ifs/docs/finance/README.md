# 财务中枢（业财一体化）落地说明

本模块实现「真正业财一体」最小闭环，对标用友/金蝶：业务事件 →（存货核算）→ 凭证模板 → 会计凭证 → 过账余额 → 结账报表。

## 文档（Phase 0）

- [00-policy.md](00-policy.md)
- [01-biz-event-dict.md](01-biz-event-dict.md)
- [02-voucher-template.md](02-voucher-template.md)

## DDL

执行：`doc/skyeyeDoc/public/docs/增量sql/202610-finance-gl.sql`  
所有业财新表含 `tenant_id`（默认 `10000`），唯一索引按租户隔离；运行时由租户拦截器自动注入/过滤。

## 后端包结构（adm-ifs）

| 包 | 能力 |
|----|------|
| `com.skyeye.finance.period` | 会计期间开账/关账 |
| `com.skyeye.finance.journal` | 会计凭证草稿/审核/过账/红字冲销/明细账 |
| `com.skyeye.finance.ledger` | 科目余额、试算平衡 |
| `com.skyeye.finance.template` | 凭证模板引擎 |
| `com.skyeye.finance.event` | 业务会计事件（幂等） |
| `com.skyeye.finance.costdomain` | 成本域 |
| `com.skyeye.finance.invaccounting` | 存货核算 + 生产领料/完工 |
| `com.skyeye.finance.report` | 三大报表、损益结转、毛利、信用校验 |
| `com.skyeye.finance.migrate` | 旧报销/借款迁移记账 |

## 已打通业务链路

1. ERP 付款审批通过 → `acceptBizAcctEvent(PAYMENT)`
2. CRM 回款审批通过 → `acceptBizAcctEvent(RECEIPT)`
3. 报销审批通过 → `EXPENSE_REIMBURSE`
4. 存货核算计价过账 → 按 `billType` 生成凭证
5. 生产领料/完工/制费 → `MfgCostAcctController`

## 前端路由（Cloud_Vue）

- `/ifs/journal/journalList` 会计凭证
- `/ifs/period/periodList` 会计期间
- `/ifs/ledger/subjectBalance` 科目余额
- `/ifs/template/templateList` 凭证模板
- `/ifs/report/financialReport` 财务报表
- `/ifs/invAccounting/invAcctList` 存货核算

## 上线检查清单

1. 执行 DDL，初始化账套期间与科目（科目编码对齐模板）
2. 确认默认模板 `PAYMENT/RECEIPT/EXPENSE_REIMBURSE` 或按账套配置
3. 手工凭证试过账 + 试算平衡
4. 走一笔付款/回款，核对事件台与凭证互查
5. 配置采购入库/销售出库模板后，用存货核算接口联调
