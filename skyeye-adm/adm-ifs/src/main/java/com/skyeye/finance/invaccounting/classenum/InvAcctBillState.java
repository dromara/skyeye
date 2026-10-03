package com.skyeye.finance.invaccounting.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum InvAcctBillState implements SkyeyeEnumClass {

    DRAFT(1, "草稿", true, true),
    VALUED(2, "已计价", true, false),
    VOUCHERED(3, "已生成凭证", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}
