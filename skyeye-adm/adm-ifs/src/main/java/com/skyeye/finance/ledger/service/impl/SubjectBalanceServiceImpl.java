package com.skyeye.finance.ledger.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.finance.constants.IfsConstants;
import com.skyeye.finance.journal.entity.JournalEntry;
import com.skyeye.finance.journal.entity.JournalVoucher;
import com.skyeye.finance.ledger.dao.SubjectBalanceDao;
import com.skyeye.finance.ledger.entity.SubjectBalance;
import com.skyeye.finance.ledger.service.SubjectBalanceService;
import com.skyeye.subject.classenum.AmountDirection;
import com.skyeye.subject.entity.AccountSubject;
import com.skyeye.subject.service.IfsAccountSubjectService;
import com.skyeye.rest.crm.customer.service.ICrmCustomerService;
import com.skyeye.rest.erp.supplier.service.IErpSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科目余额：按账套+期间+科目+辅助核算维度汇总本期发生，期末=期初+本期。
 * 过账累加，冲销走反向金额（applyReversal）或由冲销凭证再次过账。
 */
@Service
@SkyeyeService(name = "科目余额管理", groupName = "财务中枢")
public class SubjectBalanceServiceImpl extends SkyeyeBusinessServiceImpl<SubjectBalanceDao, SubjectBalance>
    implements SubjectBalanceService {

    @Autowired
    private IfsAccountSubjectService ifsAccountSubjectService;

    @Autowired
    private ICrmCustomerService iCrmCustomerService;

    @Autowired
    private IErpSupplierService iErpSupplierService;

    @Override
    public void applyPosting(JournalVoucher voucher, List<JournalEntry> entries) {
        applyDelta(voucher, entries, false);
    }

    @Override
    public void applyReversal(JournalVoucher voucher, List<JournalEntry> entries) {
        applyDelta(voucher, entries, true);
    }

    /**
     * @param reverse true 时金额取负，用于直接冲回；一般冲销走新凭证过账即可
     */
    private void applyDelta(JournalVoucher voucher, List<JournalEntry> entries, boolean reverse) {
        if (CollectionUtil.isEmpty(entries)) {
            return;
        }
        String userId = InputObject.getLogParamsStatic() != null
            ? String.valueOf(InputObject.getLogParamsStatic().getOrDefault("id", ""))
            : "";
        for (JournalEntry entry : entries) {
            SubjectBalance balance = findOrCreate(voucher, entry, userId);
            String amount = StrUtil.blankToDefault(entry.getAmount(), "0");
            if (reverse) {
                amount = CalculationUtil.subtract("0", amount, IfsConstants.NUM_AFTER_DOT);
            }
            // 借方进本期借方，贷方进本期贷方
            if (AmountDirection.BORROW.getKey().equals(entry.getDirection())) {
                balance.setPeriodDebit(CalculationUtil.add(balance.getPeriodDebit(), amount, IfsConstants.NUM_AFTER_DOT));
            } else {
                balance.setPeriodCredit(CalculationUtil.add(balance.getPeriodCredit(), amount, IfsConstants.NUM_AFTER_DOT));
            }
            recalculateEnd(balance);
            updateById(balance);
        }
    }

    /** 同一辅助核算组合共用一条余额；空辅助项存空串以便唯一匹配 */
    private SubjectBalance findOrCreate(JournalVoucher voucher, JournalEntry entry, String userId) {
        String cust = StrUtil.blankToDefault(entry.getAuxCustomerId(), "");
        String sup = StrUtil.blankToDefault(entry.getAuxSupplierId(), "");
        String mat = StrUtil.blankToDefault(entry.getAuxMaterialId(), "");
        String dept = StrUtil.blankToDefault(entry.getAuxDepartmentId(), "");
        String proj = StrUtil.blankToDefault(entry.getAuxProjectId(), "");
        String depot = StrUtil.blankToDefault(entry.getAuxDepotId(), "");

        QueryWrapper<SubjectBalance> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getSetOfBooksId), voucher.getSetOfBooksId());
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getPeriodCode), voucher.getPeriodCode());
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getSubjectId), entry.getSubjectId());
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getAuxCustomerId), cust);
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getAuxSupplierId), sup);
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getAuxMaterialId), mat);
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getAuxDepartmentId), dept);
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getAuxProjectId), proj);
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getAuxDepotId), depot);
        SubjectBalance balance = getOne(qw);
        if (ObjectUtil.isNotEmpty(balance)) {
            return balance;
        }
        // 首次过账该维度：期初为 0（期初结转由关账/结转流程另行写入）
        balance = new SubjectBalance();
        balance.setId(ToolUtil.getSurFaceId());
        balance.setSetOfBooksId(voucher.getSetOfBooksId());
        balance.setPeriodCode(voucher.getPeriodCode());
        balance.setSubjectId(entry.getSubjectId());
        AccountSubject subject = ifsAccountSubjectService.selectById(entry.getSubjectId());
        if (ObjectUtil.isNotEmpty(subject)) {
            balance.setSubjectNum(subject.getNum());
        }
        balance.setBeginDebit("0");
        balance.setBeginCredit("0");
        balance.setPeriodDebit("0");
        balance.setPeriodCredit("0");
        balance.setEndDebit("0");
        balance.setEndCredit("0");
        balance.setAuxCustomerId(cust);
        balance.setAuxSupplierId(sup);
        balance.setAuxMaterialId(mat);
        balance.setAuxDepartmentId(dept);
        balance.setAuxProjectId(proj);
        balance.setAuxDepotId(depot);
        createEntity(balance, userId);
        return selectById(balance.getId());
    }

    /** 期末借/贷 = 期初 + 本期（不做方向轧差，净额在报表侧按科目方向计算） */
    private void recalculateEnd(SubjectBalance balance) {
        String endDebit = CalculationUtil.add(balance.getBeginDebit(), balance.getPeriodDebit(), IfsConstants.NUM_AFTER_DOT);
        String endCredit = CalculationUtil.add(balance.getBeginCredit(), balance.getPeriodCredit(), IfsConstants.NUM_AFTER_DOT);
        balance.setEndDebit(endDebit);
        balance.setEndCredit(endCredit);
    }

    @Override
    public void querySubjectBalanceList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        QueryWrapper<SubjectBalance> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getSetOfBooksId), params.get("setOfBooksId"));
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getPeriodCode), params.get("periodCode"));
        if (params.get("subjectId") != null && StrUtil.isNotBlank(params.get("subjectId").toString())) {
            qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getSubjectId), params.get("subjectId"));
        }
        List<SubjectBalance> list = list(qw);
        iCrmCustomerService.setName(list, "auxCustomerId", "auxCustomerName");
        iErpSupplierService.setName(list, "auxSupplierId", "auxSupplierName");
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    /** 试算平衡：本期借方合计 vs 本期贷方合计 */
    @Override
    public void queryTrialBalance(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        QueryWrapper<SubjectBalance> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getSetOfBooksId), params.get("setOfBooksId"));
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getPeriodCode), params.get("periodCode"));
        List<SubjectBalance> list = list(qw);
        iCrmCustomerService.setName(list, "auxCustomerId", "auxCustomerName");
        iErpSupplierService.setName(list, "auxSupplierId", "auxSupplierName");
        String debit = "0";
        String credit = "0";
        for (SubjectBalance b : list) {
            debit = CalculationUtil.add(debit, b.getPeriodDebit(), IfsConstants.NUM_AFTER_DOT);
            credit = CalculationUtil.add(credit, b.getPeriodCredit(), IfsConstants.NUM_AFTER_DOT);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("periodDebitTotal", debit);
        result.put("periodCreditTotal", credit);
        result.put("balanced", CalculationUtil.compareTo(debit, credit, IfsConstants.NUM_AFTER_DOT, java.math.RoundingMode.HALF_UP) == 0);
        result.put("rows", list);
        outputObject.setBean(result);
        outputObject.settotal(list.size());
    }
}
