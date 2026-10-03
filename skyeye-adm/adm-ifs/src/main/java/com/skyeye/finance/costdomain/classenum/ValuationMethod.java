package com.skyeye.finance.costdomain.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ValuationMethod implements SkyeyeEnumClass {

    MOVING_AVG(1, "移动加权平均", true, true),
    FIFO(2, "先进先出", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}
