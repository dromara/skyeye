package com.skyeye.finance.report.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.constants.IfsConstants;
import com.skyeye.finance.journal.classenum.JournalVoucherState;
import com.skyeye.finance.journal.entity.JournalEntry;
import com.skyeye.finance.journal.entity.JournalVoucher;
import com.skyeye.finance.journal.service.JournalEntryService;
import com.skyeye.finance.journal.service.JournalVoucherService;
import com.skyeye.finance.ledger.entity.SubjectBalance;
import com.skyeye.finance.ledger.service.SubjectBalanceService;
import com.skyeye.finance.report.dao.CounterpartAgingDao;
import com.skyeye.finance.report.entity.CounterpartAging;
import com.skyeye.finance.report.service.CounterpartAgingService;
import com.skyeye.rest.crm.customer.service.ICrmCustomerService;
import com.skyeye.rest.erp.supplier.service.IErpSupplierService;
import com.skyeye.subject.classenum.AmountDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 往来账龄/对账单：按科目余额的客户、供应商辅助核算汇总期末净额，账龄按本期最早发生日粗分档。
 */
@Service
@SkyeyeService(name = "往来账龄", groupName = "财务中枢")
public class CounterpartAgingServiceImpl extends SkyeyeBusinessServiceImpl<CounterpartAgingDao, CounterpartAging> implements CounterpartAgingService {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD);

    @Autowired
    private SubjectBalanceService subjectBalanceService;

    @Autowired
    private JournalVoucherService journalVoucherService;

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private ICrmCustomerService iCrmCustomerService;

    @Autowired
    private IErpSupplierService iErpSupplierService;

    @Override
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        queryAging(inputObject, outputObject);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        return buildAgingRows(resolveParams(inputObject));
    }

    @Override
    public void queryAging(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> rows = buildAgingRows(resolveParams(inputObject));
        outputObject.setBeans(rows);
        outputObject.settotal(rows.size());
    }

    private List<Map<String, Object>> buildAgingRows(Map<String, Object> params) {
        String setOfBooksId = required(params, "setOfBooksId");
        String periodCode = toPeriod(required(params, "periodCode"));
        boolean ar = !"ap".equals(String.valueOf(params.getOrDefault("side", "ar")));
        String prefix = ar ? "1122" : "2202";

        QueryWrapper<SubjectBalance> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getSetOfBooksId), setOfBooksId);
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getPeriodCode), periodCode);
        qw.likeRight(MybatisPlusUtil.toColumns(SubjectBalance::getSubjectNum), prefix);
        List<SubjectBalance> balances = subjectBalanceService.list(qw);

        Map<String, String> netMap = new HashMap<>();
        for (SubjectBalance b : balances) {
            String partnerId = ar ? b.getAuxCustomerId() : b.getAuxSupplierId();
            if (StrUtil.isBlank(partnerId)) {
                continue;
            }
            String net = ar
                ? CalculationUtil.subtract(nz(b.getEndDebit()), nz(b.getEndCredit()), IfsConstants.NUM_AFTER_DOT)
                : CalculationUtil.subtract(nz(b.getEndCredit()), nz(b.getEndDebit()), IfsConstants.NUM_AFTER_DOT);
            netMap.put(partnerId, CalculationUtil.add(nz(netMap.get(partnerId)), net, IfsConstants.NUM_AFTER_DOT));
        }

        Map<String, String> oldest = oldestDateByPartner(setOfBooksId, periodCode, ar);
        Map<String, String> nameMap = loadNames(ar, new ArrayList<>(netMap.keySet()));
        LocalDate asOf = YearMonth.parse(periodCode).atEndOfMonth();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Map.Entry<String, String> e : netMap.entrySet()) {
            if (CalculationUtil.compareTo(e.getValue(), "0", IfsConstants.NUM_AFTER_DOT, RoundingMode.HALF_UP) == 0) {
                continue;
            }
            String date = oldest.get(e.getKey());
            long days = 0;
            if (StrUtil.isNotBlank(date) && date.length() >= 10) {
                days = ChronoUnit.DAYS.between(LocalDate.parse(date.substring(0, 10), DAY), asOf);
                if (days < 0) {
                    days = 0;
                }
            }
            Map<String, Object> row = new HashMap<>();
            row.put("partnerId", e.getKey());
            row.put("partnerName", nameMap.getOrDefault(e.getKey(), e.getKey()));
            row.put("balance", e.getValue());
            row.put("oldestDate", date);
            row.put("overdueDays", days);
            row.put("d0_30", days <= 30 ? e.getValue() : "0");
            row.put("d31_60", days >= 31 && days <= 60 ? e.getValue() : "0");
            row.put("d61_90", days >= 61 && days <= 90 ? e.getValue() : "0");
            row.put("d90p", days > 90 ? e.getValue() : "0");
            rows.add(row);
        }
        return rows;
    }

    @Override
    public void queryStatement(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = resolveParams(inputObject);
        String setOfBooksId = required(params, "setOfBooksId");
        String periodCode = toPeriod(required(params, "periodCode"));
        boolean ar = !"ap".equals(String.valueOf(params.getOrDefault("side", "ar")));
        String partnerId = required(params, "partnerId");

        QueryWrapper<JournalVoucher> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getSetOfBooksId), setOfBooksId);
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getPeriodCode), periodCode);
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getState), JournalVoucherState.POSTED.getKey());
        List<JournalVoucher> vouchers = journalVoucherService.list(qw);
        List<Map<String, Object>> rows = new ArrayList<>();
        String debitTotal = "0";
        String creditTotal = "0";
        for (JournalVoucher v : vouchers) {
            List<JournalEntry> entries = journalEntryService.selectByPId(v.getId());
            for (JournalEntry e : entries) {
                String aux = ar ? e.getAuxCustomerId() : e.getAuxSupplierId();
                if (!partnerId.equals(aux)) {
                    continue;
                }
                Map<String, Object> row = new HashMap<>();
                row.put("oddNumber", v.getOddNumber());
                row.put("voucherDate", v.getVoucherDate());
                row.put("sourceNo", v.getSourceNo());
                row.put("summary", e.getSummary());
                row.put("direction", e.getDirection());
                row.put("amount", e.getAmount());
                if (AmountDirection.BORROW.getKey().equals(e.getDirection())) {
                    row.put("debitAmount", e.getAmount());
                    row.put("creditAmount", "");
                    debitTotal = CalculationUtil.add(debitTotal, nz(e.getAmount()), IfsConstants.NUM_AFTER_DOT);
                } else {
                    row.put("debitAmount", "");
                    row.put("creditAmount", e.getAmount());
                    creditTotal = CalculationUtil.add(creditTotal, nz(e.getAmount()), IfsConstants.NUM_AFTER_DOT);
                }
                rows.add(row);
            }
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("debitTotal", debitTotal);
        bean.put("creditTotal", creditTotal);
        bean.put("balance", ar
            ? CalculationUtil.subtract(debitTotal, creditTotal, IfsConstants.NUM_AFTER_DOT)
            : CalculationUtil.subtract(creditTotal, debitTotal, IfsConstants.NUM_AFTER_DOT));
        bean.put("side", ar ? "ar" : "ap");
        bean.put("partnerId", partnerId);
        outputObject.setBean(bean);
        outputObject.setBeans(rows);
        outputObject.settotal(rows.size());
    }

    private Map<String, String> oldestDateByPartner(String setOfBooksId, String periodCode, boolean ar) {
        QueryWrapper<JournalVoucher> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getSetOfBooksId), setOfBooksId);
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getPeriodCode), periodCode);
        qw.eq(MybatisPlusUtil.toColumns(JournalVoucher::getState), JournalVoucherState.POSTED.getKey());
        List<JournalVoucher> vouchers = journalVoucherService.list(qw);
        Map<String, String> oldest = new HashMap<>();
        for (JournalVoucher v : vouchers) {
            List<JournalEntry> entries = journalEntryService.selectByPId(v.getId());
            for (JournalEntry e : entries) {
                String partnerId = ar ? e.getAuxCustomerId() : e.getAuxSupplierId();
                if (StrUtil.isBlank(partnerId) || StrUtil.isBlank(v.getVoucherDate())) {
                    continue;
                }
                String cur = oldest.get(partnerId);
                if (cur == null || v.getVoucherDate().compareTo(cur) < 0) {
                    oldest.put(partnerId, v.getVoucherDate());
                }
            }
        }
        return oldest;
    }

    private Map<String, String> loadNames(boolean ar, List<String> ids) {
        Map<String, String> map = new HashMap<>();
        if (CollectionUtil.isEmpty(ids)) {
            return map;
        }
        String joined = ids.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.joining(","));
        if (StrUtil.isBlank(joined)) {
            return map;
        }
        List<Map<String, Object>> list = ar
            ? iCrmCustomerService.queryCustomerListByIds(joined)
            : iErpSupplierService.querySupplierListByIds(joined);
        if (CollectionUtil.isEmpty(list)) {
            return map;
        }
        for (Map<String, Object> item : list) {
            Object id = item.get("id");
            Object name = item.get("name");
            if (id != null) {
                map.put(id.toString(), name == null ? id.toString() : name.toString());
            }
        }
        return map;
    }

    private Map<String, Object> resolveParams(InputObject inputObject) {
        Map<String, Object> params = new HashMap<>();
        if (inputObject.getParams() != null) {
            params.putAll(inputObject.getParams());
        }
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        if (pageInfo != null) {
            putIfAbsent(params, "setOfBooksId", pageInfo.getCustomParamsMapStr("setOfBooksId"));
            putIfAbsent(params, "periodCode", pageInfo.getCustomParamsMapStr("periodCode"));
            putIfAbsent(params, "side", pageInfo.getCustomParamsMapStr("side"));
            putIfAbsent(params, "partnerId", pageInfo.getCustomParamsMapStr("partnerId"));
        }
        return params;
    }

    private void putIfAbsent(Map<String, Object> params, String key, String value) {
        if (StrUtil.isBlank(value)) {
            return;
        }
        Object current = params.get(key);
        if (current == null || StrUtil.isBlank(current.toString())) {
            params.put(key, value);
        }
    }

    private String required(Map<String, Object> params, String key) {
        Object v = params.get(key);
        if (v == null || StrUtil.isBlank(v.toString())) {
            throw new CustomException("缺少参数：" + key);
        }
        return v.toString();
    }

    private String toPeriod(String val) {
        return val.length() >= 7 ? val.substring(0, 7) : val;
    }

    private String nz(String val) {
        return StrUtil.blankToDefault(val, "0");
    }
}
