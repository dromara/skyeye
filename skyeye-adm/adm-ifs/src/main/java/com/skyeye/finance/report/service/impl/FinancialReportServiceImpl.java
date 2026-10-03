package com.skyeye.finance.report.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.constants.IfsConstants;
import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.finance.event.service.BizAcctEventService;
import com.skyeye.finance.journal.classenum.JournalVoucherState;
import com.skyeye.finance.journal.entity.JournalEntry;
import com.skyeye.finance.journal.entity.JournalVoucher;
import com.skyeye.finance.journal.service.JournalEntryService;
import com.skyeye.finance.journal.service.JournalVoucherService;
import com.skyeye.finance.ledger.entity.SubjectBalance;
import com.skyeye.finance.ledger.service.SubjectBalanceService;
import com.skyeye.finance.period.service.AccountPeriodService;
import com.skyeye.finance.report.service.FinancialReportService;
import com.skyeye.subject.classenum.AccountSubjectType;
import com.skyeye.subject.classenum.AmountDirection;
import com.skyeye.subject.entity.AccountSubject;
import com.skyeye.subject.service.IfsAccountSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 财务报表与期末结转：基于科目余额/已过账凭证汇总。
 * 科目分类依赖 AccountSubjectType + 科目编码前缀（如 6 收入、6401 成本）。
 */
@Service
public class FinancialReportServiceImpl implements FinancialReportService {

    @Autowired
    private SubjectBalanceService subjectBalanceService;

    @Autowired
    private IfsAccountSubjectService ifsAccountSubjectService;

    @Autowired
    private JournalVoucherService journalVoucherService;

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private AccountPeriodService accountPeriodService;

    @Autowired
    private BizAcctEventService bizAcctEventService;

    /** 资产负债表：资产 / 负债 / 权益，净额按科目借贷方向轧差 */
    @Override
    public void queryBalanceSheet(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        List<SubjectBalance> balances = listBalances(params);
        Map<String, AccountSubject> subjectMap = loadSubjects(balances);
        List<Map<String, Object>> assets = new ArrayList<>();
        List<Map<String, Object>> liabilities = new ArrayList<>();
        List<Map<String, Object>> equity = new ArrayList<>();
        String assetTotal = "0";
        String liabilityTotal = "0";
        String equityTotal = "0";
        for (SubjectBalance b : balances) {
            AccountSubject s = subjectMap.get(b.getSubjectId());
            if (s == null) {
                continue;
            }
            Map<String, Object> row = toBalanceRow(b, s);
            if (AccountSubjectType.PROPERTY.getKey().equals(s.getType())) {
                assets.add(row);
                assetTotal = CalculationUtil.add(assetTotal, netBalance(b, s), IfsConstants.NUM_AFTER_DOT);
            } else if (AccountSubjectType.IN_DEBT.getKey().equals(s.getType())) {
                liabilities.add(row);
                liabilityTotal = CalculationUtil.add(liabilityTotal, netBalance(b, s), IfsConstants.NUM_AFTER_DOT);
            } else if (AccountSubjectType.RIGHTS_AND_INTERESTS.getKey().equals(s.getType())) {
                equity.add(row);
                equityTotal = CalculationUtil.add(equityTotal, netBalance(b, s), IfsConstants.NUM_AFTER_DOT);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("assets", assets);
        result.put("liabilities", liabilities);
        result.put("equity", equity);
        result.put("assetTotal", assetTotal);
        result.put("liabilityTotal", liabilityTotal);
        result.put("equityTotal", equityTotal);
        outputObject.setBean(result);
    }

    /**
     * 利润表简版：收入取本期贷方（6 开头或贷方科目），成本取本期借方（成本类/5/64/66）。
     * 净利润 = 收入合计 - 成本合计。
     */
    @Override
    public void queryIncomeStatement(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        List<SubjectBalance> balances = listBalances(params);
        Map<String, AccountSubject> subjectMap = loadSubjects(balances);
        List<Map<String, Object>> incomes = new ArrayList<>();
        List<Map<String, Object>> costs = new ArrayList<>();
        String incomeTotal = "0";
        String costTotal = "0";
        for (SubjectBalance b : balances) {
            AccountSubject s = subjectMap.get(b.getSubjectId());
            if (s == null) {
                continue;
            }
            String num = StrUtil.blankToDefault(s.getNum(), "");
            Map<String, Object> row = toBalanceRow(b, s);
            if (AccountSubjectType.INCREASE_AND_DECREASE.getKey().equals(s.getType())
                && (num.startsWith("6") || AmountDirection.LOAN.getKey().equals(s.getAmountDirection()))) {
                incomes.add(row);
                incomeTotal = CalculationUtil.add(incomeTotal, b.getPeriodCredit(), IfsConstants.NUM_AFTER_DOT);
            } else if (AccountSubjectType.INCREASE_AND_DECREASE.getKey().equals(s.getType())
                || AccountSubjectType.PRIME_COST.getKey().equals(s.getType())
                || num.startsWith("64") || num.startsWith("66") || num.startsWith("5")) {
                costs.add(row);
                costTotal = CalculationUtil.add(costTotal, b.getPeriodDebit(), IfsConstants.NUM_AFTER_DOT);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("incomes", incomes);
        result.put("costs", costs);
        result.put("incomeTotal", incomeTotal);
        result.put("costTotal", costTotal);
        result.put("netProfit", CalculationUtil.subtract(incomeTotal, costTotal, IfsConstants.NUM_AFTER_DOT));
        outputObject.setBean(result);
    }

    /** 现金流量简版（间接法思路）：现金/银行存款科目（cashFlag 或 1001/1002）本期借=流入、贷=流出 */
    @Override
    public void queryCashFlow(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        List<SubjectBalance> balances = listBalances(params);
        Map<String, AccountSubject> subjectMap = loadSubjects(balances);
        String cashIn = "0";
        String cashOut = "0";
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SubjectBalance b : balances) {
            AccountSubject s = subjectMap.get(b.getSubjectId());
            if (s == null) {
                continue;
            }
            boolean cash = Integer.valueOf(1).equals(s.getCashFlag())
                || StrUtil.startWith(s.getNum(), "1001")
                || StrUtil.startWith(s.getNum(), "1002");
            if (!cash) {
                continue;
            }
            rows.add(toBalanceRow(b, s));
            cashIn = CalculationUtil.add(cashIn, b.getPeriodDebit(), IfsConstants.NUM_AFTER_DOT);
            cashOut = CalculationUtil.add(cashOut, b.getPeriodCredit(), IfsConstants.NUM_AFTER_DOT);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("rows", rows);
        result.put("cashIn", cashIn);
        result.put("cashOut", cashOut);
        result.put("netCash", CalculationUtil.subtract(cashIn, cashOut, IfsConstants.NUM_AFTER_DOT));
        outputObject.setBean(result);
    }

    /**
     * 期末损益结转：汇总本期损益得净利润，抛 PERIOD_CLOSE 事件由模板生成结转凭证。
     * 须先配置 PERIOD_CLOSE 模板；期间需开放。
     */
    @Override
    public void periodProfitClose(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String setOfBooksId = required(params, "setOfBooksId");
        String periodCode = required(params, "periodCode");
        accountPeriodService.assertPeriodOpen(setOfBooksId, periodCode);

        List<SubjectBalance> balances = listBalances(params);
        Map<String, AccountSubject> subjectMap = loadSubjects(balances);
        String incomeTotal = "0";
        String costTotal = "0";
        for (SubjectBalance b : balances) {
            AccountSubject s = subjectMap.get(b.getSubjectId());
            if (s == null) {
                continue;
            }
            String num = StrUtil.blankToDefault(s.getNum(), "");
            if (AccountSubjectType.INCREASE_AND_DECREASE.getKey().equals(s.getType())
                && (num.startsWith("6") || AmountDirection.LOAN.getKey().equals(s.getAmountDirection()))) {
                incomeTotal = CalculationUtil.add(incomeTotal, b.getPeriodCredit(), IfsConstants.NUM_AFTER_DOT);
            } else if (AccountSubjectType.INCREASE_AND_DECREASE.getKey().equals(s.getType())
                || AccountSubjectType.PRIME_COST.getKey().equals(s.getType())
                || num.startsWith("64") || num.startsWith("66") || num.startsWith("5")) {
                costTotal = CalculationUtil.add(costTotal, b.getPeriodDebit(), IfsConstants.NUM_AFTER_DOT);
            }
        }
        String netProfit = CalculationUtil.subtract(incomeTotal, costTotal, IfsConstants.NUM_AFTER_DOT);

        Map<String, Object> event = new HashMap<>();
        event.put("eventType", BizAcctEventType.PERIOD_CLOSE.getKey());
        event.put("sourceType", BizAcctEventType.PERIOD_CLOSE.getKey());
        event.put("sourceId", setOfBooksId + "-" + periodCode);
        event.put("sourceNo", periodCode);
        event.put("setOfBooksId", setOfBooksId);
        event.put("voucherDate", DateUtil.getYmdTimeAndToString());
        event.put("amount", netProfit.replace("-", ""));
        event.put("netProfit", netProfit);
        event.put("summary", "期末损益结转-" + periodCode);
        Map<String, Object> result = bizAcctEventService.acceptEvent(event);
        result.put("netProfit", netProfit);
        outputObject.setBean(result);
    }

    /** 毛利：已过账 SALES_OUT 凭证中，贷方 6001 为收入、借方 6401 为成本 */
    @Override
    public void queryGrossProfit(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String setOfBooksId = required(params, "setOfBooksId");
        String periodCode = required(params, "periodCode");
        QueryWrapper<JournalVoucher> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getSetOfBooksId), setOfBooksId);
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getPeriodCode), periodCode);
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getState), JournalVoucherState.POSTED.getKey());
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getEventType), BizAcctEventType.SALES_OUT.getKey());
        List<JournalVoucher> vouchers = journalVoucherService.list(qw);
        String revenue = "0";
        String cost = "0";
        for (JournalVoucher v : vouchers) {
            List<JournalEntry> entries = journalEntryService.selectByPId(v.getId());
            for (JournalEntry e : entries) {
                AccountSubject subject = ifsAccountSubjectService.selectById(e.getSubjectId());
                String subjectNum = subject == null ? "" : subject.getNum();
                if (AmountDirection.LOAN.getKey().equals(e.getDirection())
                    && StrUtil.startWith(subjectNum, "6001")) {
                    revenue = CalculationUtil.add(revenue, e.getAmount(), IfsConstants.NUM_AFTER_DOT);
                }
                if (AmountDirection.BORROW.getKey().equals(e.getDirection())
                    && StrUtil.startWith(subjectNum, "6401")) {
                    cost = CalculationUtil.add(cost, e.getAmount(), IfsConstants.NUM_AFTER_DOT);
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("revenue", revenue);
        result.put("cost", cost);
        result.put("grossProfit", CalculationUtil.subtract(revenue, cost, IfsConstants.NUM_AFTER_DOT));
        outputObject.setBean(result);
    }

    private List<SubjectBalance> listBalances(Map<String, Object> params) {
        QueryWrapper<SubjectBalance> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getSetOfBooksId), required(params, "setOfBooksId"));
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getPeriodCode), required(params, "periodCode"));
        return subjectBalanceService.list(qw);
    }

    private Map<String, AccountSubject> loadSubjects(List<SubjectBalance> balances) {
        List<String> ids = balances.stream().map(SubjectBalance::getSubjectId).distinct().collect(Collectors.toList());
        Map<String, AccountSubject> map = new HashMap<>();
        for (String id : ids) {
            AccountSubject s = ifsAccountSubjectService.selectById(id);
            if (s != null) {
                map.put(id, s);
            }
        }
        return map;
    }

    private Map<String, Object> toBalanceRow(SubjectBalance b, AccountSubject s) {
        Map<String, Object> row = new HashMap<>();
        row.put("subjectId", s.getId());
        row.put("subjectNum", s.getNum());
        row.put("subjectName", s.getName());
        row.put("beginDebit", b.getBeginDebit());
        row.put("beginCredit", b.getBeginCredit());
        row.put("periodDebit", b.getPeriodDebit());
        row.put("periodCredit", b.getPeriodCredit());
        row.put("endDebit", b.getEndDebit());
        row.put("endCredit", b.getEndCredit());
        row.put("net", netBalance(b, s));
        return row;
    }

    /** 借方科目：期末借-贷；贷方科目：期末贷-借 */
    private String netBalance(SubjectBalance b, AccountSubject s) {
        if (AmountDirection.BORROW.getKey().equals(s.getAmountDirection())) {
            return CalculationUtil.subtract(b.getEndDebit(), b.getEndCredit(), IfsConstants.NUM_AFTER_DOT);
        }
        return CalculationUtil.subtract(b.getEndCredit(), b.getEndDebit(), IfsConstants.NUM_AFTER_DOT);
    }

    private String required(Map<String, Object> params, String key) {
        Object v = params.get(key);
        if (v == null || StrUtil.isBlank(v.toString())) {
            throw new CustomException("缺少参数：" + key);
        }
        return v.toString();
    }
}
