package com.skyeye.finance.period.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AccountPeriodState implements SkyeyeEnumClass {

    OPEN(1, "开账", true, true),
    BIZ_CLOSED(2, "业务关闭", true, false),
    FIN_CLOSED(3, "财务关闭", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}
