package com.skyeye.finance.report.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.constants.IfsConstants;
import com.skyeye.finance.ledger.entity.SubjectBalance;
import com.skyeye.finance.ledger.service.SubjectBalanceService;
import com.skyeye.finance.report.service.CreditControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户信用前置：按辅助核算客户汇总应收期末净额。
 * 默认 warning；strict=1 且有应收余额时直接拦截。
 */
@Service
public class CreditControlServiceImpl implements CreditControlService {

    @Autowired
    private SubjectBalanceService subjectBalanceService;

    @Override
    public void checkCustomerCredit(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String customerId = required(params, "customerId");
        String setOfBooksId = params.get("setOfBooksId") == null ? null : params.get("setOfBooksId").toString();
        String periodCode = params.get("periodCode") == null ? null : params.get("periodCode").toString();
        String amount = params.get("amount") == null ? "0" : params.get("amount").toString();

        // 按客户辅助核算汇总期末借方净额（应收类一般在借方）
        QueryWrapper<SubjectBalance> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getAuxCustomerId), customerId);
        if (StrUtil.isNotBlank(setOfBooksId)) {
            qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getSetOfBooksId), setOfBooksId);
        }
        if (StrUtil.isNotBlank(periodCode)) {
            qw.eq(MybatisPlusUtil.toColumns(SubjectBalance::getPeriodCode), periodCode);
        }
        List<SubjectBalance> list = subjectBalanceService.list(qw);
        String arBalance = "0";
        for (SubjectBalance b : list) {
            String net = CalculationUtil.subtract(
                StrUtil.blankToDefault(b.getEndDebit(), "0"),
                StrUtil.blankToDefault(b.getEndCredit(), "0"), IfsConstants.NUM_AFTER_DOT);
            arBalance = CalculationUtil.add(arBalance, net, IfsConstants.NUM_AFTER_DOT);
        }
        boolean blocked = CalculationUtil.compareTo(arBalance, "0", IfsConstants.NUM_AFTER_DOT, RoundingMode.HALF_UP) > 0
            && CalculationUtil.compareTo(amount, "0", IfsConstants.NUM_AFTER_DOT, RoundingMode.HALF_UP) > 0
            && CalculationUtil.compareTo(arBalance, "0", IfsConstants.NUM_AFTER_DOT, RoundingMode.HALF_UP) > 0;

        // 简化策略：存在应收余额且本次业务金额>0 时返回 warning；严格模式可抛异常
        boolean strict = "1".equals(String.valueOf(params.getOrDefault("strict", "0")));
        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customerId);
        result.put("arBalance", arBalance);
        result.put("allowed", !strict || CalculationUtil.compareTo(arBalance, "0", IfsConstants.NUM_AFTER_DOT, RoundingMode.HALF_UP) <= 0);
        result.put("message", blocked ? "客户存在应收账款余额，请关注信用风险" : "信用检查通过");
        if (strict && CalculationUtil.compareTo(arBalance, "0", IfsConstants.NUM_AFTER_DOT, RoundingMode.HALF_UP) > 0) {
            throw new CustomException("客户应收未清，禁止发货/下单，余额=" + arBalance);
        }
        outputObject.setBean(result);
    }

    private String required(Map<String, Object> params, String key) {
        Object v = params.get(key);
        if (v == null || StrUtil.isBlank(v.toString())) {
            throw new CustomException("缺少参数：" + key);
        }
        return v.toString();
    }
}
