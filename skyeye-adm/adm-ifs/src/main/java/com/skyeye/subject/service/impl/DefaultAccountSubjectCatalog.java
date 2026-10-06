package com.skyeye.subject.service.impl;

import com.skyeye.subject.classenum.AccountSubjectType;
import com.skyeye.subject.classenum.AmountDirection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 默认会计科目（工业企业示意，编码对齐凭证模板）。
 */
final class DefaultAccountSubjectCatalog {

    private DefaultAccountSubjectCatalog() {
    }

    static List<SubjectDef> all() {
        return Arrays.asList(
            asset("1001", "库存现金", null, false, false, false, false, false, true),
            asset("1002", "银行存款", null, false, false, false, false, false, true),
            asset("1122", "应收账款", null, true, false, false, false, false, false),
            asset("1221", "其他应收款", null, false, false, false, false, false, false),
            asset("122101", "其他应收款-员工借款", "1221", false, false, false, false, false, false),
            asset("1133", "其他应收款-备用金", null, false, false, false, false, false, false),
            asset("1403", "原材料", null, false, false, true, false, false, false),
            asset("1405", "库存商品", null, false, false, true, false, false, false),
            asset("1901", "待处理财产损溢", null, false, false, false, false, false, false),

            debt("2202", "应付账款-暂估", null, false, true, false, false, false),
            debt("220201", "应付账款", "2202", false, true, false, false, false),
            debt("2221", "应交税费", null, false, false, false, false, false),
            debt("222101", "应交税费-进项税额", "2221", false, false, false, false, false),
            debt("22210101", "应交税费-销项税额", "222101", false, false, false, false, false),

            equity("4001", "实收资本", null),
            equity("4103", "本年利润", null),

            cost("5001", "生产成本", null, false, false, true, false, true),
            cost("5101", "制造费用", null, false, false, false, true, false),

            pnl("6001", "主营业务收入", AmountDirection.LOAN, false, false, false, false),
            pnl("6301", "营业外收入", AmountDirection.LOAN, false, false, false, false),
            pnl("6401", "主营业务成本", AmountDirection.BORROW, false, false, true, false),
            pnl("6601", "销售费用", AmountDirection.BORROW, false, false, false, false),
            pnl("6602", "管理费用", AmountDirection.BORROW, false, false, false, true),
            pnl("6711", "营业外支出", AmountDirection.BORROW, false, false, false, false)
        );
    }

    private static SubjectDef asset(String num, String name, String parentNum,
                                    boolean auxCustomer, boolean auxSupplier, boolean auxMaterial,
                                    boolean auxDepartment, boolean auxProject, boolean cash) {
        return new SubjectDef(num, name, AccountSubjectType.PROPERTY.getKey(), AmountDirection.BORROW.getKey(),
            parentNum, auxCustomer, auxSupplier, auxMaterial, auxDepartment, auxProject, cash);
    }

    private static SubjectDef debt(String num, String name, String parentNum,
                                   boolean auxCustomer, boolean auxSupplier, boolean auxMaterial,
                                   boolean auxDepartment, boolean auxProject) {
        return new SubjectDef(num, name, AccountSubjectType.IN_DEBT.getKey(), AmountDirection.LOAN.getKey(),
            parentNum, auxCustomer, auxSupplier, auxMaterial, auxDepartment, auxProject, false);
    }

    private static SubjectDef equity(String num, String name, String parentNum) {
        return new SubjectDef(num, name, AccountSubjectType.RIGHTS_AND_INTERESTS.getKey(), AmountDirection.LOAN.getKey(),
            parentNum, false, false, false, false, false, false);
    }

    private static SubjectDef cost(String num, String name, String parentNum,
                                   boolean auxCustomer, boolean auxSupplier, boolean auxMaterial,
                                   boolean auxDepartment, boolean auxProject) {
        return new SubjectDef(num, name, AccountSubjectType.PRIME_COST.getKey(), AmountDirection.BORROW.getKey(),
            parentNum, auxCustomer, auxSupplier, auxMaterial, auxDepartment, auxProject, false);
    }

    private static SubjectDef pnl(String num, String name, AmountDirection direction,
                                  boolean auxCustomer, boolean auxSupplier, boolean auxMaterial, boolean auxDepartment) {
        return new SubjectDef(num, name, AccountSubjectType.INCREASE_AND_DECREASE.getKey(), direction.getKey(),
            null, auxCustomer, auxSupplier, auxMaterial, auxDepartment, false, false);
    }

    @Getter
    @AllArgsConstructor
    static class SubjectDef {
        private final String num;
        private final String name;
        private final Integer type;
        private final Integer amountDirection;
        private final String parentNum;
        private final boolean auxCustomer;
        private final boolean auxSupplier;
        private final boolean auxMaterial;
        private final boolean auxDepartment;
        private final boolean auxProject;
        private final boolean cash;
    }
}
