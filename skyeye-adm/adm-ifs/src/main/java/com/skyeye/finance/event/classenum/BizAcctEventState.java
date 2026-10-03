package com.skyeye.finance.event.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum BizAcctEventState implements SkyeyeEnumClass {

    PENDING(1, "待处理", true, true),
    SUCCESS(2, "成功", true, false),
    FAILED(3, "失败", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}
