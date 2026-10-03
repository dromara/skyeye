package com.skyeye.finance.journal.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum JournalVoucherState implements SkyeyeEnumClass {

    DRAFT(1, "草稿", true, true),
    APPROVED(2, "已审核", true, false),
    POSTED(3, "已过账", true, false),
    VOIDED(4, "已作废", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}
